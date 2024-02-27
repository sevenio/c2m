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
import com.tvisha.click2magic.api.post.model.Image;

import org.json.JSONArray;
import org.json.JSONObject;

public class SiteAssetsTableAsynckTask extends AsyncTask<Object, String, Boolean> {
    DataBaseContext dbContext;
    Context context;
    protected SQLiteDatabase db;
    int type=0;
    int assetType=0;

    public static final String TABLE_NAME = DataBaseValues.SiteAssetsTable.TABLE_NAME;

    




    @Override
    protected void onPostExecute(Boolean result) {
       /* if(HandlerHolder.mainActivityUiHandler!=null)
        {
            HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.ACTIVE_AGENTS_SYNC_COMPLETED);
        }
*/

        Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask activeAgentsTableAsynckTask onPostExecute","called");

    }

    @Override
    protected  Boolean doInBackground(Object... params) {
        boolean status=false;
        context =(Context) params[0];
        type=(int) params[1];
        assetType=(int) params[2];
        if(context!=null){
            dbContext = new DataBaseContext(context);
            if(dbContext!=null){
                db=dbContext.getDatabase();
                if(db!=null){
                    db.execSQL(DataBaseValues.SiteAssetsTable.CREATE_SITE_ASSESTS_TABLE);
                }
            }

        }


        JSONArray jsonArray =(JSONArray) params[3];
        if(type==Values.Action.INSERT) {
            for (int k = 0; k < jsonArray.length(); k++) {
                JSONObject jsonObject = jsonArray.optJSONObject(k);
                if (jsonObject != null) {
                    Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask addMessage", jsonObject.toString()+" "+assetType);
                    if (context != null) {

                        insertAssest(jsonObject);
                    }

                }
            }
        }
        else if(type==Values.Action.DELETE){
            if(jsonArray!=null && jsonArray.length()>0){
                checkAssest(jsonArray);
            }
        }

        return status;
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
    public void insertAssest(JSONObject  jsonObject){
        long status=0;

        Cursor cursor = null;
        ContentValues data = new ContentValues();


        data.put(DataBaseValues.SiteAssetsTable.asset_id, jsonObject.optString(DataBaseValues.SiteAssetsTable.asset_id));
        data.put(DataBaseValues.SiteAssetsTable.title, jsonObject.optString(DataBaseValues.SiteAssetsTable.title));
        data.put(DataBaseValues.SiteAssetsTable.path, jsonObject.optString(DataBaseValues.SiteAssetsTable.path));
        data.put(DataBaseValues.SiteAssetsTable.site_id, jsonObject.optString(DataBaseValues.SiteAssetsTable.site_id));
        data.put(DataBaseValues.SiteAssetsTable.added_by, jsonObject.optString(DataBaseValues.SiteAssetsTable.added_by));
        data.put(DataBaseValues.SiteAssetsTable.type, jsonObject.optString(DataBaseValues.SiteAssetsTable.type));
        data.put(DataBaseValues.SiteAssetsTable.status, jsonObject.optString(DataBaseValues.SiteAssetsTable.status));
        data.put(DataBaseValues.SiteAssetsTable.user_name, jsonObject.optString(DataBaseValues.SiteAssetsTable.user_name));

        String siteId=jsonObject.optString(DataBaseValues.SiteAssetsTable.site_id);
        String assestId=jsonObject.optString(DataBaseValues.SiteAssetsTable.asset_id);
        String type=jsonObject.optString(DataBaseValues.SiteAssetsTable.type);


        if(siteId.length()==3 && siteId.startsWith(",") && siteId.endsWith(","))
        {
            siteId=siteId.substring(1,2);
            data.put(DataBaseValues.SiteAssetsTable.site_id, siteId);
        }





        data.put(DataBaseValues.SiteAssetsTable.updated_at, Utilities.getCurrentDateTimeNew());

        String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+DataBaseValues.SiteAssetsTable.type+" = "+type+" AND "+DataBaseValues.SiteAssetsTable.asset_id+" = "+assestId;

        Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask insertAssest"," query "+query +" "+assetType);
        try{
            cursor = db.rawQuery(query,null);
            if(cursor!=null && cursor.getCount()>0)
            {

                if(cursor.moveToNext()){


                    int result= db.update(TABLE_NAME,data,DataBaseValues.SiteAssetsTable.type+" = "+type+"  AND  "+DataBaseValues.SiteAssetsTable.asset_id+" = "+assestId,null);
                    if(result>0)
                    {
                        Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask insertAssest","updated true "+assetType);

                    }
                    else
                    {
                        Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask insertAssest","updated false "+assetType);
                    }

                }
            }else{
                data.put(DataBaseValues.SiteAssetsTable.created_at, Utilities.getCurrentDateTimeNew());
                status = db.insert(TABLE_NAME, null, data);
                if(status>0){
                    Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask insertAssest","inserted true "+assetType);
                }else
                {
                    Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask insertAssest","inserted true " +assetType);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    public void checkAssest(JSONArray jsonArray){

        Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask checkAssest","method called"+assetType);


        Cursor cursor = null;
        try{

            String query= "SELECT * FROM " +TABLE_NAME+" WHERE "+DataBaseValues.SiteAssetsTable.type+" = "+assetType;

            Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask checkAssest","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor!=null && cursor.getCount()>0  && cursor.moveToFirst()){
                Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask checkAssest","c== "+cursor.getCount()+"");
                do{
                    String id= cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.asset_id));
                    String type= cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.type));
                    String site_id= cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.site_id));
                    checkAssetExists(Integer.parseInt(id),Integer.parseInt(type),site_id,jsonArray);

                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask checkAssest","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask checkAssest","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }


    }

    private void checkAssetExists(int id,int type,String site_id ,JSONArray jsonArray)
    {
        Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask ","checkAssetExists called");
        boolean isExists=false;
        for(int k = 0;k < jsonArray.length();k++){
            JSONObject jsonObject=jsonArray.optJSONObject(k);
            if(jsonObject!=null ){

                if(context!=null)
                {
                    int cid=jsonObject.optInt(DataBaseValues.SiteAssetsTable.asset_id);
                    int typ=jsonObject.optInt(DataBaseValues.SiteAssetsTable.type);
                    String siteId=jsonObject.optString(DataBaseValues.SiteAssetsTable.site_id);
                    if(cid==id && typ==type ){
                        Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask ","checkAssetExists true");
                        isExists=true;
                        break;
                    }


                }
            }

        }
        if(!isExists)
        {
            deleteAsset(id,type);
        }

    }

    public void deleteAsset(int id,int type){

        Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask ","deleteAsset called");

        int delete = db.delete(TABLE_NAME, DataBaseValues.SiteAssetsTable.asset_id+" = "+id+" AND "+DataBaseValues.SiteAssetsTable.type+" = "+type, null);

    }
}
