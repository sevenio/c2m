package com.tvisha.click2magic.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.api.post.model.CannedResponse;
import com.tvisha.click2magic.api.post.model.Collateral;
import com.tvisha.click2magic.api.post.model.Image;
import com.tvisha.click2magic.api.post.model.Link;

import java.util.ArrayList;
import java.util.List;


public class SiteAssetsTable extends BaseTable  {

    public static final String TABLE_NAME = DataBaseValues.SiteAssetsTable.TABLE_NAME;
    public SQLiteDatabase db = database;
    private Context context;


    public SiteAssetsTable(Context context){
        super(context);
        this.context=context;
        if(db!=null){
            db.execSQL(DataBaseValues.SiteAssetsTable.CREATE_SITE_ASSESTS_TABLE);
        }


    }

    public void insertAssest(Image model){
        long status=0;

        Cursor cursor = null;
        ContentValues data = new ContentValues();


        data.put(DataBaseValues.SiteAssetsTable.asset_id, model.getAssetId());
        data.put(DataBaseValues.SiteAssetsTable.title, model.getTitle());
        data.put(DataBaseValues.SiteAssetsTable.path, model.getPath());
        data.put(DataBaseValues.SiteAssetsTable.site_id, model.getSiteId());
        data.put(DataBaseValues.SiteAssetsTable.added_by, model.getAddedBy());
        data.put(DataBaseValues.SiteAssetsTable.type, model.getType());
        data.put(DataBaseValues.SiteAssetsTable.status, model.getStatus());
        data.put(DataBaseValues.SiteAssetsTable.user_name, model.getUserName());



        data.put(DataBaseValues.SiteAssetsTable.updated_at, Utilities.getCurrentDateTimeNew());

        String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+DataBaseValues.SiteAssetsTable.site_id+" = "+model.getSiteId()+" AND "+DataBaseValues.SiteAssetsTable.asset_id+" = "+model.getAssetId();


        try{
            cursor = db.rawQuery(query,null);
            if(cursor!=null && cursor.getCount()>0)
            {

                if(cursor.moveToNext()){

                    int result= db.update(TABLE_NAME,data,DataBaseValues.SiteAssetsTable.site_id+" = "+model.getSiteId()+" AND "+DataBaseValues.SiteAssetsTable.asset_id+" = "+model.getAssetId(),null);
                    if(result>0)
                    {
                        Helper.getInstance().LogDetails("insertAssest","updated true");

                    }
                    else
                    {
                        Helper.getInstance().LogDetails("insertAssest","updated false");
                    }

                }
            }else{
                data.put(DataBaseValues.SiteAssetsTable.created_at, Utilities.getCurrentDateTimeNew());
                status = db.insert(TABLE_NAME, null, data);
                if(status>0){
                    Helper.getInstance().LogDetails("insertAssest","inserted true");
                }else
                {
                    Helper.getInstance().LogDetails("insertAssest","inserted true");
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    public List<Image> getImageAssetList(int site_id){

        Helper.getInstance().LogDetails("getAssetList","method called"+site_id);

        List<Image> list=new ArrayList<>();
        int type=Values.AssetType.IMAGES;


        Cursor cursor = null;
        String query;
        try{

            // query= "SELECT * FROM " +TABLE_NAME + " WHERE " + DataBaseValues.SiteAssetsTable.type+" = "+type;

            query= "SELECT * FROM " +
                    TABLE_NAME +
                    " WHERE " + DataBaseValues.SiteAssetsTable.type+" = "+type+" AND "+DataBaseValues.SiteAssetsTable.site_id+ " IN "+
                    "("+site_id+")";

         /*   String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " +
                    DataBaseValues.SiteAssetsTable.site_id+ " = " +site_id+" AND "+DataBaseValues.SiteAssetsTable.type+" = "+type;*/


            Helper.getInstance().LogDetails("getAssetList","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{

                        Image model = new Image();

                        model.setSiteId(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.site_id)));
                        model.setAssetId(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.asset_id)));
                        model.setTitle(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.title)));
                        model.setPath(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.path)));
                        model.setAddedBy(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.added_by)));

                        model.setUserName(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.user_name)));
                        model.setType(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.type)));
                        model.setStatus(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.status)));

                        list.add(model);
                        Helper.getInstance().LogDetails("getAgentList", "called" + model.getAssetId() + " " + model.getSiteId());


                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getAssetList","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getAssetList","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  list;
    }

    public List<Link> getLinkAssetList(int site_id){



        List<Link> list=new ArrayList<>();
        int type=Values.AssetType.LINKS;

        Helper.getInstance().LogDetails("getLinkAssetList","method called"+site_id+" "+type);


        Cursor cursor = null;
        String query;
        try{

             //query= "SELECT * FROM " +TABLE_NAME + " WHERE " + DataBaseValues.SiteAssetsTable.type+" = "+type;

            query= "SELECT * FROM " +
                    TABLE_NAME +
                    " WHERE " + DataBaseValues.SiteAssetsTable.type+" = "+type+" AND "+DataBaseValues.SiteAssetsTable.site_id+ " IN "+
                    "("+site_id+")";

            /*String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " +
                    DataBaseValues.SiteAssetsTable.site_id+ " = " +site_id+" AND "+DataBaseValues.SiteAssetsTable.type+" = "+type;
            */

            Helper.getInstance().LogDetails("getLinkAssetList","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor !=null && cursor.moveToFirst()){

                do{

                        Link model = new Link();

                        model.setSiteId(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.site_id)));
                        model.setAssetId(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.asset_id)));
                        model.setTitle(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.title)));
                        model.setPath(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.path)));
                        model.setAddedBy(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.added_by)));

                        model.setUserName(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.user_name)));
                        model.setType(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.type)));
                        model.setStatus(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.status)));

                        list.add(model);
                        Helper.getInstance().LogDetails("getLinkAssetList", "called" + model.getAssetId() + " " + model.getSiteId());


                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getLinkAssetList","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getLinkAssetList","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  list;
    }

    public List<Collateral> getCollateralAssetList(int site_id){

        Helper.getInstance().LogDetails("getCollateralAssetList","method called"+site_id);

        List<Collateral> list=new ArrayList<>();
        int type=Values.AssetType.COLLATERAL;


        Cursor cursor = null;
        String query;
        try{

            // query= "SELECT * FROM " +TABLE_NAME + " WHERE " + DataBaseValues.SiteAssetsTable.type+" = "+type;

            query= "SELECT * FROM " +
                    TABLE_NAME +
                    " WHERE " + DataBaseValues.SiteAssetsTable.type+" = "+type+" AND "+DataBaseValues.SiteAssetsTable.site_id+ " IN "+
                    "("+site_id+")";
/*


            String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " +
                    DataBaseValues.SiteAssetsTable.site_id+ " = " +site_id+" AND "+DataBaseValues.SiteAssetsTable.type+" = "+type;
*/


            Helper.getInstance().LogDetails("getCollateralAssetList","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{

                        Collateral model = new Collateral();
                        model.setSiteId(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.site_id)));
                        model.setAssetId(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.asset_id)));
                        model.setTitle(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.title)));
                        model.setPath(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.path)));
                        model.setAddedBy(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.added_by)));

                        model.setUserName(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.user_name)));
                        model.setType(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.type)));
                        model.setStatus(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.status)));

                        list.add(model);
                        Helper.getInstance().LogDetails("getCollateralAssetList", "called" + model.getAssetId() + " " + model.getSiteId());


                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getCollateralAssetList","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getCollateralAssetList","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  list;
    }

    public List<CannedResponse> getCannedAssetList(int site_id){

        Helper.getInstance().LogDetails("getCannedAssetList","method called"+site_id);

        List<CannedResponse> list=new ArrayList<>();
        int type=Values.AssetType.CANNEDRESPONSES;


        Cursor cursor = null;
        String query;
        try{

            // query= "SELECT * FROM " +TABLE_NAME + " WHERE " + DataBaseValues.SiteAssetsTable.type+" = "+type;
            query= "SELECT * FROM " +
                    TABLE_NAME +
                    " WHERE " + DataBaseValues.SiteAssetsTable.type+" = "+type+" AND "+DataBaseValues.SiteAssetsTable.site_id+ " IN "+
                    "("+site_id+")";

           /* String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " +
                    DataBaseValues.SiteAssetsTable.site_id+ " = " +site_id+" AND "+DataBaseValues.SiteAssetsTable.type+" = "+type;*/


            Helper.getInstance().LogDetails("getCannedAssetList","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{

                        CannedResponse model = new CannedResponse();

                        model.setSiteId(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.site_id)));
                        model.setAssetId(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.asset_id)));
                        model.setTitle(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.title)));
                        model.setPath(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.path)));
                        model.setAddedBy(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.added_by)));

                        model.setUserName(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.user_name)));
                        model.setType(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.type)));
                        model.setStatus(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.status)));

                        list.add(model);
                        Helper.getInstance().LogDetails("getCannedAssetList", "called" + model.getAssetId() + " " + model.getSiteId());


                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getCannedAssetList","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getCannedAssetList","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  list;
    }

    public void clearDb(int type){
        //delete from
        try{
            String query="delete from "+ TABLE_NAME + " WHERE " +
                    DataBaseValues.SiteAssetsTable.type+ " = " +type;
            Helper.getInstance().LogDetails("clearDb","query "+query);
            db.execSQL(query);
        }catch (Exception e){
            e.printStackTrace();
        }

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
    public List<Image> getImageAssetList1(int site_id){

        Helper.getInstance().LogDetails("getAssetList","method called"+site_id);

        List<Image> list=new ArrayList<>();
        int type=Values.AssetType.IMAGES;


        Cursor cursor = null;
        try{

            String query= "SELECT * FROM " +TABLE_NAME + " WHERE " + DataBaseValues.SiteAssetsTable.type+" = "+type;

         /*   String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " +
                    DataBaseValues.SiteAssetsTable.site_id+ " = " +site_id+" AND "+DataBaseValues.SiteAssetsTable.type+" = "+type;*/


            Helper.getInstance().LogDetails("getAssetList","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{

                    boolean isPresent=false;

                    String sid=cursor.getString(cursor.getColumnIndex(DataBaseValues.AgentsTable.site_id));
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
                    }



                    if(isPresent) {


                        Image model = new Image();

                        model.setSiteId(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.site_id)));
                        model.setAssetId(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.asset_id)));
                        model.setTitle(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.title)));
                        model.setPath(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.path)));
                        model.setAddedBy(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.added_by)));

                        model.setUserName(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.user_name)));
                        model.setType(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.type)));
                        model.setStatus(cursor.getString(cursor.getColumnIndex(DataBaseValues.SiteAssetsTable.status)));

                        list.add(model);
                        Helper.getInstance().LogDetails("getAgentList", "called" + model.getAssetId() + " " + model.getUserName());
                    }

                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getAssetList","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getAssetList","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  list;
    }

}
