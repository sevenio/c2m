package com.tvisha.click2magic.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.api.post.ActiveAgent;
import com.tvisha.click2magic.api.post.model.Data;


import org.json.JSONArray;
import org.json.JSONObject;

public class ActiveAgentsTableAsynckTask extends AsyncTask<Object, String, Boolean> {
    DataBaseContext dbContext;
    Context context;
    protected SQLiteDatabase db;

    public static final String TABLE_NAME = DataBaseValues.AgentsTable.TABLE_NAME;

    




    @Override
    protected void onPostExecute(Boolean result) {
        if(HandlerHolder.mainActivityUiHandler!=null)
        {
            HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.ACTIVE_AGENTS_SYNC_COMPLETED);
        }

        if(HandlerHolder.agentsFragmentUiHandler!=null)
        {
            HandlerHolder.agentsFragmentUiHandler.sendEmptyMessage(Values.RecentList.ACTIVE_AGENTS_SYNC_COMPLETED);
        }
        if(HandlerHolder.archiveFragmentHandler!=null)
        {
            HandlerHolder.archiveFragmentHandler.sendEmptyMessage(Values.RecentList.ACTIVE_AGENTS_SYNC_COMPLETED);
        }
        Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask activeAgentsTableAsynckTask onPostExecute","called");

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
                    db.execSQL(DataBaseValues.AgentsTable.CREATE_AGENTS_TABLE);
                }
            }

        }


        JSONArray jsonArray =(JSONArray) params[1];
        for(int k = 0;k < jsonArray.length();k++){
            JSONObject jsonObject=jsonArray.optJSONObject(k);
            if(jsonObject!=null ){
                    Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask addMessage",jsonObject.toString());
                    if(context!=null)
                    {
                        ActiveAgent  activeAgent=setActiveAgentData(jsonObject);
                        insertAgent(activeAgent);
                    }


            }

        }

        return status;
    }

    public ActiveAgent setActiveAgentData(JSONObject jsonObject) {

        Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask  setActiveUserData", jsonObject.toString());


        ActiveAgent activeAgent = null;
        try {
            if (jsonObject != null) {
                activeAgent = new ActiveAgent();

                activeAgent.setAgentId(jsonObject.optString(DataBaseValues.AgentsTable.agent_id));
                activeAgent.setAccountId(jsonObject.optString(DataBaseValues.AgentsTable.account_id));
                activeAgent.setSiteId(jsonObject.optString(DataBaseValues.AgentsTable.site_id));
                activeAgent.setIsOnline(jsonObject.optString(DataBaseValues.AgentsTable.is_online));
                activeAgent.setUserId(jsonObject.optString(DataBaseValues.AgentsTable.user_id));
                activeAgent.setUserName(jsonObject.optString(DataBaseValues.AgentsTable.user_name));
                activeAgent.setRole(jsonObject.optString(DataBaseValues.AgentsTable.role));
                activeAgent.setTmUserId(jsonObject.optString(DataBaseValues.AgentsTable.tm_user_id));
                activeAgent.setActive_chat_count(jsonObject.optString(DataBaseValues.AgentsTable.active_chat_count));
                activeAgent.setUser_site_id(jsonObject.optString(DataBaseValues.AgentsTable.user_site_id));



            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return activeAgent;
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

    public void insertAgent(ActiveAgent activeAgent){
        long status=0;

        Cursor cursor = null;
        ContentValues data = new ContentValues();

        data.put(DataBaseValues.AgentsTable.site_id, activeAgent.getSiteId());
        data.put(DataBaseValues.AgentsTable.agent_id, activeAgent.getAgentId());
        data.put(DataBaseValues.AgentsTable.account_id, activeAgent.getAccountId());
        data.put(DataBaseValues.AgentsTable.active_chat_count, activeAgent.getActive_chat_count());
        data.put(DataBaseValues.AgentsTable.user_name, activeAgent.getUserName());

        data.put(DataBaseValues.AgentsTable.user_id, activeAgent.getUserId());
        data.put(DataBaseValues.AgentsTable.role, activeAgent.getRole());
        data.put(DataBaseValues.AgentsTable.tm_user_id, activeAgent.getTmUserId());
        data.put(DataBaseValues.AgentsTable.is_online, activeAgent.getIsOnline());
        data.put(DataBaseValues.AgentsTable.user_site_id, activeAgent.getUser_site_id());
        data.put(DataBaseValues.AgentsTable.updated_at, Utilities.getCurrentDateTimeNew());

      //  String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+DataBaseValues.AgentsTable.agent_id+" = "+activeAgent.getAgentId()+" AND "+DataBaseValues.AgentsTable.site_id+" = "+activeAgent.getSiteId();
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+DataBaseValues.AgentsTable.agent_id+" = "+activeAgent.getAgentId()+" AND "+DataBaseValues.AgentsTable.user_site_id+" = "+activeAgent.getUser_site_id();


        try{
            cursor = db.rawQuery(query,null);
            if(cursor!=null && cursor.getCount()>0)
            {

                if(cursor.moveToNext()){


                    //int result= db.update(TABLE_NAME,data,DataBaseValues.AgentsTable.agent_id+" = "+activeAgent.getAgentId()+" AND "+DataBaseValues.AgentsTable.site_id+" = "+activeAgent.getSiteId(),null);
                    int result= db.update(TABLE_NAME,data,DataBaseValues.AgentsTable.agent_id+" = "+activeAgent.getAgentId()+" AND "+DataBaseValues.AgentsTable.user_site_id+" = "+activeAgent.getUser_site_id(),null);
                    if(result>0)
                    {
                        Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask insertAgent","updated true"+activeAgent.getAgentId()+" "+activeAgent.getIsOnline());

                    }
                    else
                    {
                        Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask insertAgent","updated false");
                    }

                }
            }else{
                data.put(DataBaseValues.AgentsTable.created_at, Utilities.getCurrentDateTimeNew());
                status = db.insert(TABLE_NAME, null, data);
                if(status>0){
                    Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask insertAgent","inserted true");
                }
                else
                {
                    Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask insertAgent","inserted true");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    
}
