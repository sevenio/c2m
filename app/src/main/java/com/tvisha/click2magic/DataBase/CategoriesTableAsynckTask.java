package com.tvisha.click2magic.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.api.post.ActiveAgent;
import com.tvisha.click2magic.api.post.model.Category;

import org.json.JSONArray;
import org.json.JSONObject;

public class CategoriesTableAsynckTask extends AsyncTask<Object, String, Boolean> {
    DataBaseContext dbContext;
    Context context;
    protected SQLiteDatabase db;
    int type=0;
    public static final String TABLE_NAME = DataBaseValues.CategoriesTable.TABLE_NAME;

    




    @Override
    protected void onPostExecute(Boolean result) {

       /* if(HandlerHolder.archiveFragmentHandler!=null)
        {
            HandlerHolder.archiveFragmentHandler.sendEmptyMessage(Values.RecentList.CATEGORIES_SYNC_COMPLETED);
        }*/
        Helper.getInstance().LogDetails("CategoriesTableAsynckTask activeAgentsTableAsynckTask onPostExecute","called");

    }

    @Override
    protected  Boolean doInBackground(Object... params) {
        boolean status=false;
        context =(Context) params[0];
        if(context!=null){
            dbContext = new DataBaseContext(context);
            if(dbContext!=null){
                db=dbContext.getDatabase();
                if(db!=null){
                    db.execSQL(DataBaseValues.CategoriesTable.CREATE_CATEGORIES_TABLE);
                }
            }

        }

        type=(int) params[1];
        JSONArray jsonArray =(JSONArray) params[2];
        if(type==Values.Action.INSERT)
        {
            for(int k = 0;k < jsonArray.length();k++){
                JSONObject jsonObject=jsonArray.optJSONObject(k);
                if(jsonObject!=null ){
                    Helper.getInstance().LogDetails("CategoriesTableAsynckTask addMessage",jsonObject.toString());
                    if(context!=null)
                    {
                        Category category=setCategoryData(jsonObject);
                        insertCategory(category);
                    }


                }

            }
        }
        else if(type==Values.Action.DELETE){
            if(jsonArray!=null && jsonArray.length()>0){
                checkCategories(jsonArray);
            }
            else
            {
                clearDb();
            }
        }


        return status;
    }

    public Category setCategoryData(JSONObject jsonObject) {

        Helper.getInstance().LogDetails("CategoriesTableAsynckTask  setActiveUserData", jsonObject.toString());



        Category category = null;
        try {
            if (jsonObject != null) {
                category = new Category();
                category.setId(jsonObject.optString(DataBaseValues.CategoriesTable.id));
                category.setStatus(jsonObject.optString(DataBaseValues.CategoriesTable.status));
                category.setCategoryName(jsonObject.optString(DataBaseValues.CategoriesTable.category_name));
                category.setCreatedAt(jsonObject.optString(DataBaseValues.CategoriesTable.created_at));
                category.setUpdatedAt(jsonObject.optString(DataBaseValues.CategoriesTable.updated_at));

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return category;
    }
    @Override
    protected void onPreExecute() {
        if(context!=null){
            dbContext = new DataBaseContext(context);
            if(dbContext!=null){
                db=dbContext.getDatabase();
            }
        }

    }

    @Override
    protected void onProgressUpdate(String... text) {
    }

    public void insertCategory(Category model){
        long status=0;

        Cursor cursor = null;
        ContentValues data = new ContentValues();

        data.put(DataBaseValues.CategoriesTable.id, model.getId());
        data.put(DataBaseValues.CategoriesTable.category_name, model.getCategoryName());
        data.put(DataBaseValues.CategoriesTable.status, model.getStatus());

        data.put(DataBaseValues.AgentsTable.updated_at, Utilities.getCurrentDateTimeNew());

      //  String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+DataBaseValues.AgentsTable.agent_id+" = "+activeAgent.getAgentId()+" AND "+DataBaseValues.AgentsTable.site_id+" = "+activeAgent.getSiteId();
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+DataBaseValues.CategoriesTable.id+" = "+model.getId();


        try{
            cursor = db.rawQuery(query,null);
            if(cursor!=null && cursor.getCount()>0)
            {

                if(cursor.moveToNext()){


                    //int result= db.update(TABLE_NAME,data,DataBaseValues.AgentsTable.agent_id+" = "+activeAgent.getAgentId()+" AND "+DataBaseValues.AgentsTable.site_id+" = "+activeAgent.getSiteId(),null);
                    int result= db.update(TABLE_NAME,data,DataBaseValues.CategoriesTable.id+" = "+model.getId(),null);
                    if(result>0)
                    {
                        Helper.getInstance().LogDetails("CategoriesTableAsynckTask insertAgent","updated true"+model.getId()+" "+model.getCategoryName());

                    }
                    else
                    {
                        Helper.getInstance().LogDetails("CategoriesTableAsynckTask insertAgent","updated false");
                    }
                }
            }else{
                data.put(DataBaseValues.CategoriesTable.created_at, Utilities.getCurrentDateTimeNew());
                status = db.insert(TABLE_NAME, null, data);
                if(status>0){
                    Helper.getInstance().LogDetails("CategoriesTableAsynckTask insertAgent","inserted true");
                }
                else
                {
                    Helper.getInstance().LogDetails("CategoriesTableAsynckTask insertAgent","inserted true");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void checkCategories(JSONArray jsonArray){

        Helper.getInstance().LogDetails("CategoriesTableAsynckTask checkCategories","method called");


        Cursor cursor = null;
        try{

            String query= "SELECT * FROM " +TABLE_NAME;

            Helper.getInstance().LogDetails("CategoriesTableAsynckTask checkCategories","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{
                    String id= cursor.getString(cursor.getColumnIndex(DataBaseValues.CategoriesTable.id));
                    checkCategoryExists(Integer.parseInt(id),jsonArray);

                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("CategoriesTableAsynckTask checkCategories","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("CategoriesTableAsynckTask checkCategories","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }


    }

    private void checkCategoryExists(int id,JSONArray jsonArray)
    {
        Helper.getInstance().LogDetails("CategoriesTableAsynckTask ","checkCategoryExists called");
        boolean isExists=false;
        for(int k = 0;k < jsonArray.length();k++){
            JSONObject jsonObject=jsonArray.optJSONObject(k);
            if(jsonObject!=null ){

                if(context!=null)
                {
                    int cid=jsonObject.optInt(DataBaseValues.CategoriesTable.id);
                    if(cid==id){
                        Helper.getInstance().LogDetails("CategoriesTableAsynckTask ","checkCategoryExists true");
                        isExists=true;
                        break;
                    }


                }
            }

        }
        if(!isExists)
        {
            deleteCategory(id);
        }

    }

    public void deleteCategory(int id){

        Helper.getInstance().LogDetails("CategoriesTableAsynckTask ","deleteCategory called");

        int delete = db.delete(TABLE_NAME, DataBaseValues.CategoriesTable.id+" = "+id, null);

    }

    public void clearDb(){
        //delete from
        try{
            String query="delete from "+ TABLE_NAME;
            Helper.getInstance().LogDetails("clearDb","query "+query);
            db.execSQL(query);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    
}
