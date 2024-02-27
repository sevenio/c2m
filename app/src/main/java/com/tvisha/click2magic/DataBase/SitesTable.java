package com.tvisha.click2magic.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.api.post.SitesInfo;

import java.util.ArrayList;
import java.util.List;


public class SitesTable extends BaseTable  {

    public static final String TABLE_NAME = DataBaseValues.SitesTable.TABLE_NAME;
    public SQLiteDatabase db = database;
    private Context context;


    public SitesTable(Context context){
        super(context);
        this.context=context;

        if(db!=null){
            db.execSQL(DataBaseValues.SitesTable.CREATE_SITES_TABLE);
        }


    }

    public void insertSites(SitesInfo sitesInfo){

        try {

            if(sitesInfo!=null) {

                long status = 0;
                Cursor cursor = null;
                ContentValues data = new ContentValues();
                data.put(DataBaseValues.SitesTable.site_id, sitesInfo.getSiteId());
                data.put(DataBaseValues.SitesTable.site_name, sitesInfo.getSiteName());
                data.put(DataBaseValues.SitesTable.site_token, sitesInfo.getSiteToken());
                data.put(DataBaseValues.SitesTable.account_id, sitesInfo.getAccountId());
                data.put(DataBaseValues.SitesTable.updated_at, Utilities.getCurrentDateTimeNew());

                String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + DataBaseValues.SitesTable.site_id + " = " + sitesInfo.getSiteId();

                try {
                    cursor = db.rawQuery(query, null);
                    if (cursor != null && cursor.getCount() > 0) {
                        if (cursor.moveToNext()) {


                            int result = db.update(TABLE_NAME, data, DataBaseValues.SitesTable.site_id + " = " + sitesInfo.getSiteId(), null);
                            if (result > 0) {
                                Helper.getInstance().LogDetails("insertSites", "updated true");

                            } else {
                                Helper.getInstance().LogDetails("insertSites", "updated false");
                            }
                        }
                    } else {
                        data.put(DataBaseValues.SitesTable.created_at, Utilities.getCurrentDateTimeNew());
                        status = db.insert(TABLE_NAME, null, data);
                        if (status > 0) {
                            Helper.getInstance().LogDetails("insertSites", "inserted true");
                        } else {
                            Helper.getInstance().LogDetails("insertSites", "inserted true");
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
    public List<SitesInfo> getSitesOfUser(String siteIds,boolean isSelf){
        String sid="";

        if(!isSelf){

            String[] separated = siteIds.trim().split(",");
            if(separated!=null && separated.length>0)
            {
                for(int i=0;i<separated.length;i++){
                    if(separated[i]!=null && !separated[i].trim().isEmpty())
                    {
                        if(sid.trim().isEmpty()){
                            sid=sid+separated[i];
                        }
                        else
                        {
                            sid=sid+","+separated[i];
                        }

                    }
                }
            }

        }
        else
        {
            sid=siteIds;
        }



        //SELECT * FROM Customers WHERE City IN ('Paris','London');
        Helper.getInstance().LogDetails("getSitesOfUser","method called"+siteIds);

        List<SitesInfo> sitesInfoList=new ArrayList<>();


        Cursor cursor = null;
        String query="";
        try{

            if(isSelf)
            {
                query= "SELECT * FROM " +TABLE_NAME;
            }
            else
            {
                query= "SELECT * FROM " +TABLE_NAME +" WHERE site_id  IN "+"("+sid+")";
            }


            Helper.getInstance().LogDetails("getSitesOfUser","method called query"+query);

            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{
                    SitesInfo model=new SitesInfo();
                    model.setSiteId(cursor.getString(cursor.getColumnIndex(DataBaseValues.SitesTable.site_id)));
                    model.setSiteName(cursor.getString(cursor.getColumnIndex(DataBaseValues.SitesTable.site_name)));
                    model.setSiteToken(cursor.getString(cursor.getColumnIndex(DataBaseValues.SitesTable.site_token)));
                    model.setAccountId(cursor.getString(cursor.getColumnIndex(DataBaseValues.SitesTable.account_id)));
                    model.setPresent(true);

                    sitesInfoList.add(model);

                    Helper.getInstance().LogDetails("getSitesOfUser","called"+model.getSiteId()+" "+model.getSiteName());
                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getSitesOfUser","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getSitesOfUser","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  sitesInfoList;

    }
    public List<SitesInfo> getSites(){

        Helper.getInstance().LogDetails("getSites","method called");

        List<SitesInfo> sitesInfoList=new ArrayList<>();


        Cursor cursor = null;
        try{

            String query= "SELECT * FROM " +TABLE_NAME;

            Helper.getInstance().LogDetails("getSites","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{
                    SitesInfo model=new SitesInfo();
                    model.setSiteId(cursor.getString(cursor.getColumnIndex(DataBaseValues.SitesTable.site_id)));
                    model.setSiteName(cursor.getString(cursor.getColumnIndex(DataBaseValues.SitesTable.site_name)));
                    model.setSiteToken(cursor.getString(cursor.getColumnIndex(DataBaseValues.SitesTable.site_token)));
                    model.setAccountId(cursor.getString(cursor.getColumnIndex(DataBaseValues.SitesTable.account_id)));

                    sitesInfoList.add(model);

                    Helper.getInstance().LogDetails("getSites","called"+model.getSiteId()+" "+model.getSiteName());
                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getSites","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getSites","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  sitesInfoList;
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
