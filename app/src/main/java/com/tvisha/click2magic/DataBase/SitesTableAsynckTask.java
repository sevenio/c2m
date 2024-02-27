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
import com.tvisha.click2magic.api.post.SitesInfo;
import com.tvisha.click2magic.api.post.model.ActiveChat;

import org.json.JSONArray;
import org.json.JSONObject;

public class SitesTableAsynckTask extends AsyncTask<Object, String, Boolean> {
    DataBaseContext dbContext;
    Context context;
    protected SQLiteDatabase db;
    int type=0;
    boolean isSiteAdded=false;
    public static final String TABLE_NAME = DataBaseValues.SitesTable.TABLE_NAME;

    




    @Override
    protected void onPostExecute(Boolean result) {
        if(HandlerHolder.mainActivityUiHandler!=null)
        {
            HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.SITES_SYNC_COMPLETED);
        }
        if(HandlerHolder.homeFragmentUiHandler!=null)
        {
            HandlerHolder.homeFragmentUiHandler.sendEmptyMessage(Values.RecentList.SITES_SYNC_COMPLETED);
        }

        if(HandlerHolder.agentsFragmentUiHandler!=null)
        {
            HandlerHolder.agentsFragmentUiHandler.sendEmptyMessage(Values.RecentList.SITES_SYNC_COMPLETED);
        }

        if(HandlerHolder.archiveFragmentHandler!=null)
        {
            HandlerHolder.archiveFragmentHandler.sendEmptyMessage(Values.RecentList.SITES_SYNC_COMPLETED);
        }

        Helper.getInstance().LogDetails("SitesTableAsynckTask ArchiveChatsTableAsynckTask onPostExecute","called");



    }

    @Override
    protected  Boolean doInBackground(Object... params) {
        boolean status=false;
        context =(Context) params[0];
        type =(int) params[1];
        if(context!=null){
            dbContext = new DataBaseContext(context);
            if(dbContext!=null){
                db=dbContext.getDatabase();
                if(db!=null){
                    db.execSQL(DataBaseValues.SitesTable.CREATE_SITES_TABLE);
                }
            }

        }
        JSONArray jsonArray = (JSONArray) params[2];
        if(type==Values.Action.INSERT) {

            for (int k = 0; k < jsonArray.length(); k++) {
                JSONObject jsonObject = jsonArray.optJSONObject(k);
                if (jsonObject != null) {
                    Helper.getInstance().LogDetails("SitesTableAsynckTask addMessage", jsonObject.toString());
                    if (context != null) {
                        insertSites(jsonObject);
                    }
                }

            }
        }else
        {
            if(jsonArray!=null && jsonArray.length()>0){
                checkSites(jsonArray);
            }
            else
            {
                clearDb();
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


    public void insertSites(JSONObject jsonObject){

        try {

            if(jsonObject!=null) {

                long status = 0;
                Cursor cursor = null;
                ContentValues data = new ContentValues();
                data.put(DataBaseValues.SitesTable.site_id, jsonObject.optString("site_id"));
                data.put(DataBaseValues.SitesTable.site_name, jsonObject.optString("site_name"));
                data.put(DataBaseValues.SitesTable.site_token,jsonObject.optString("site_token"));
                data.put(DataBaseValues.SitesTable.account_id, jsonObject.optString("account_id"));
                data.put(DataBaseValues.SitesTable.updated_at, Utilities.getCurrentDateTimeNew());

                int site_id=jsonObject.optInt("site_id");

                String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + DataBaseValues.SitesTable.site_id + " = " + site_id;

                try {
                    cursor = db.rawQuery(query, null);
                    if (cursor != null && cursor.getCount() > 0) {
                        if (cursor.moveToNext()) {


                            int result = db.update(TABLE_NAME, data, DataBaseValues.SitesTable.site_id + " = " + site_id, null);
                            if (result > 0) {
                                Helper.getInstance().LogDetails("SitesTableAsynckTask insertSites", "updated true");

                            } else {
                                Helper.getInstance().LogDetails("SitesTableAsynckTask insertSites", "updated false");
                            }
                        }
                    } else {
                        data.put(DataBaseValues.SitesTable.created_at, Utilities.getCurrentDateTimeNew());
                        status = db.insert(TABLE_NAME, null, data);
                        if(HandlerHolder.archiveFragmentHandler!=null)
                        {
                            HandlerHolder.archiveFragmentHandler.obtainMessage(Values.RecentList.SITE_ADDED,jsonObject).sendToTarget();
                        }

                        if(HandlerHolder.homeFragmentUiHandler!=null)
                        {
                            HandlerHolder.homeFragmentUiHandler.obtainMessage(Values.RecentList.SITE_ADDED,jsonObject).sendToTarget();
                        }

                        if(HandlerHolder.agentsFragmentUiHandler!=null)
                        {
                            HandlerHolder.agentsFragmentUiHandler.obtainMessage(Values.RecentList.SITE_ADDED,jsonObject).sendToTarget();
                        }

                        if(HandlerHolder.mainActivityUiHandler!=null)
                        {
                            HandlerHolder.mainActivityUiHandler.obtainMessage(Values.RecentList.SITE_ADDED,jsonObject).sendToTarget();
                        }

                        if (status > 0) {
                            Helper.getInstance().LogDetails("SitesTableAsynckTask insertSites", "inserted true");
                        } else {
                            Helper.getInstance().LogDetails("SitesTableAsynckTask insertSites", "inserted true");
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void checkSites(JSONArray jsonArray){

        Helper.getInstance().LogDetails("SitesTableAsynckTask checkChats","method called");


        Cursor cursor = null;
        try{

            String query= "SELECT * FROM " +TABLE_NAME;

            Helper.getInstance().LogDetails("SitesTableAsynckTask checkChats","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{
                    String siteId= cursor.getString(cursor.getColumnIndex(DataBaseValues.SitesTable.site_id));
                    checkSiteExists(Integer.parseInt(siteId),jsonArray);

                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("SitesTableAsynckTask checkChats","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("SitesTableAsynckTask checkChats","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }


    }

    private void checkSiteExists(int site_id,JSONArray jsonArray)
    {
        Helper.getInstance().LogDetails("SitesTableAsynckTask ","checkChatExists called");
        boolean isExists=false;
        for(int k = 0;k < jsonArray.length();k++){
            JSONObject jsonObject=jsonArray.optJSONObject(k);
            if(jsonObject!=null ){

                if(context!=null)
                {

                    int siteId=jsonObject.optInt("site_id");
                    if(siteId==site_id){
                        Helper.getInstance().LogDetails("SitesTableAsynckTask ","checkChatExists true");
                        isExists=true;
                        break;
                    }


                }
            }

        }
        if(!isExists)
        {
            deleteSite(site_id);
        }

    }

    public void deleteSite(int siteId){

        Helper.getInstance().LogDetails("SitesTableAsynckTask ","deleteActiveChat called");

        int delete = db.delete(TABLE_NAME, DataBaseValues.SitesTable.site_id+" = "+siteId, null);
        if(HandlerHolder.archiveFragmentHandler!=null)
        {
            HandlerHolder.archiveFragmentHandler.obtainMessage(Values.RecentList.SITE_DELETED,siteId).sendToTarget();
        }

        if(HandlerHolder.homeFragmentUiHandler!=null)
        {
            HandlerHolder.homeFragmentUiHandler.obtainMessage(Values.RecentList.SITE_DELETED,siteId).sendToTarget();
        }

        if(HandlerHolder.agentsFragmentUiHandler!=null)
        {
            HandlerHolder.agentsFragmentUiHandler.obtainMessage(Values.RecentList.SITE_DELETED,siteId).sendToTarget();
        }

        if(HandlerHolder.mainActivityUiHandler!=null)
        {
            HandlerHolder.mainActivityUiHandler.obtainMessage(Values.RecentList.SITE_DELETED,siteId).sendToTarget();
        }


    }

    public void clearDb(){
        //delete from
        try{
            String query="delete from "+ TABLE_NAME;
            Helper.getInstance().LogDetails("SitesTableAsynckTask clearDb","query "+query);
            db.execSQL(query);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
