package com.tvisha.click2magic.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.api.post.ActiveAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AgentsTable extends BaseTable  {

    public static final String TABLE_NAME = DataBaseValues.AgentsTable.TABLE_NAME;
    public SQLiteDatabase db = database;
    private Context context;


    public AgentsTable(Context context){
        super(context);
        this.context=context;

        if(db!=null){
            db.execSQL(DataBaseValues.AgentsTable.CREATE_AGENTS_TABLE);
        }


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

        String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+DataBaseValues.AgentsTable.agent_id+" = "+activeAgent.getAgentId()+" AND "+DataBaseValues.AgentsTable.site_id+" = "+activeAgent.getSiteId();


        try{
            cursor = db.rawQuery(query,null);
            if(cursor!=null && cursor.getCount()>0)
            {

                if(cursor.moveToNext()){


                    int result= db.update(TABLE_NAME,data,DataBaseValues.AgentsTable.agent_id+" = "+activeAgent.getAgentId()+" AND "+DataBaseValues.AgentsTable.site_id+" = "+activeAgent.getSiteId(),null);
                    if(result>0)
                    {
                        Helper.getInstance().LogDetails("insertAgent","updated true");

                    }
                    else
                    {
                        Helper.getInstance().LogDetails("insertAgent","updated false");
                    }

                }
            }else{
                data.put(DataBaseValues.AgentsTable.created_at, Utilities.getCurrentDateTimeNew());
                status = db.insert(TABLE_NAME, null, data);
                if(status>0){
                    Helper.getInstance().LogDetails("insertAgent","inserted true");
                }else
                {
                    Helper.getInstance().LogDetails("insertAgent","inserted true");
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }



    public int getTheActiveAgentCount(int site_id) {
        Cursor cursor = null;
        Helper.getInstance().LogDetails("getTheActiveAgentCount ",site_id+" ");
        try {
            String count_query = "select count(id) as count from "+TABLE_NAME+" where user_site_id ='"+site_id+"' and is_online = 1 ";
            cursor = selectByManualQuery(count_query);
            if (cursor != null && cursor.moveToFirst()) {

                int val = cursor.getInt(cursor.getColumnIndex("count"));
                Helper.getInstance().LogDetails("getTheActiveAgentCount count",val+" ");
                cursor.close();
                return val;
            }
        }catch (Exception e){
            Helper.getInstance().LogDetails("getTheAgentCount ",e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }
        finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return 0;
    }
    public int getTheAgentCount(int site_id) {
        Helper.getInstance().LogDetails("getTheAgentCount ",site_id+" ");
        Cursor cursor = null;
        try {
            String count_query = "select count(id) as count from "+TABLE_NAME+" where user_site_id ='"+site_id+"'";
            cursor = selectByManualQuery(count_query);
            if (cursor != null && cursor.moveToFirst()) {

                int val = cursor.getInt(cursor.getColumnIndex("count"));
                Helper.getInstance().LogDetails("getTheAgentCount count",val+" ");
                cursor.close();
                return val;
            }
        }catch (Exception e){
            Helper.getInstance().LogDetails("getTheAgentCount ",e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }
        finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return 0;
    }
    public List<ActiveAgent> getAgentList(int site_id){

        Helper.getInstance().LogDetails("getAgentList","method called"+site_id);

        List<ActiveAgent> activeAgentList=new ArrayList<>();


        Cursor cursor = null;
        try{

           // String query= "SELECT * FROM " +TABLE_NAME;

            String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " +
                    DataBaseValues.AgentsTable.user_site_id+ " = " +site_id;


            Helper.getInstance().LogDetails("getAgentList","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{

                    boolean isPresent=true;


               /*     String sid=cursor.getString(cursor.getColumnIndex(DataBaseValues.AgentsTable.site_id));
                    if(sid!=null && !sid.trim().isEmpty())
                    {
                        String[] separated = sid.replace(" ","").split(",");
                        if(separated!=null && separated.length>0){
                            for(int i=0;i<separated.length;i++){
                                if(separated[i]!=null && !separated[i].isEmpty())
                                {
                                    if(site_id==(Integer.parseInt(separated[i].trim()))){
                                        isPresent=true;
                                        break;
                                    }
                                }

                            }
                        }
                    }*/
                    if(isPresent){
                        ActiveAgent model=new ActiveAgent();
                        model.setSiteId(cursor.getString(cursor.getColumnIndex(DataBaseValues.AgentsTable.site_id)));
                        model.setAgentId(cursor.getString(cursor.getColumnIndex(DataBaseValues.AgentsTable.agent_id)));
                        model.setAccountId(cursor.getString(cursor.getColumnIndex(DataBaseValues.AgentsTable.account_id)));
                        model.setUserId(cursor.getString(cursor.getColumnIndex(DataBaseValues.AgentsTable.user_id)));
                        model.setTmUserId(cursor.getString(cursor.getColumnIndex(DataBaseValues.AgentsTable.tm_user_id)));

                        model.setUserName(cursor.getString(cursor.getColumnIndex(DataBaseValues.AgentsTable.user_name)));
                        model.setIsOnline(cursor.getString(cursor.getColumnIndex(DataBaseValues.AgentsTable.is_online)));
                        model.setActive_chat_count(cursor.getString(cursor.getColumnIndex(DataBaseValues.AgentsTable.active_chat_count)));
                        model.setRole(cursor.getString(cursor.getColumnIndex(DataBaseValues.AgentsTable.role)));
                        model.setUser_site_id(cursor.getString(cursor.getColumnIndex(DataBaseValues.AgentsTable.user_site_id)));
                        activeAgentList.add(model);
                        Helper.getInstance().LogDetails("getAgentList","called"+model.getAgentId()+" "+model.getUserName());
                    }



                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getAgentList","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getAgentList","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  activeAgentList;
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

    public void agentOnlineOfflineStatus(JSONObject jsonObject){
        try
        {
            //{"agent_active":4,"user_name":"AgentDM","user_id":11,"tm_user_id":3720,"agent_staus":1,"site_id":"4"}
            if(jsonObject!=null ){
                int user_id=jsonObject.optInt("user_id");
                int  agent_staus=jsonObject.optInt("agent_staus");
                int  siteId=jsonObject.optInt("site_id");

                long status=0;
                Cursor cursor = null;
                ContentValues data = new ContentValues();


                data.put(DataBaseValues.AgentsTable.is_online, agent_staus);
                data.put(DataBaseValues.AgentsTable.updated_at, Utilities.getCurrentDateTimeNew());
                String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+DataBaseValues.AgentsTable.user_id+" = "+user_id+" AND "+DataBaseValues.AgentsTable.user_site_id+" = "+siteId;

                cursor = db.rawQuery(query,null);
                if(cursor!=null && cursor.getCount()>0)
                {

                    if(cursor.moveToNext()){


                        int result= db.update(TABLE_NAME,data,DataBaseValues.AgentsTable.user_id+" = "+user_id+" AND "+DataBaseValues.AgentsTable.user_site_id+" = "+siteId,null);
                        if(result>0)
                        {
                            Helper.getInstance().LogDetails("SyncData agentOnlineOfflineStatus","updated true");

                        }
                        else
                        {
                            Helper.getInstance().LogDetails("SyncData agentOnlineOfflineStatus","updated false");
                        }

                    }
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
