package com.tvisha.click2magic.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.api.post.ActiveAgent;
import com.tvisha.click2magic.api.post.model.Category;

import java.util.ArrayList;
import java.util.List;


public class CategoriesTable extends BaseTable  {

    public static final String TABLE_NAME = DataBaseValues.CategoriesTable.TABLE_NAME;
    public SQLiteDatabase db = database;
    private Context context;


    public CategoriesTable(Context context){
        super(context);
        this.context=context;

        if(db!=null){
            db.execSQL(DataBaseValues.CategoriesTable.CREATE_CATEGORIES_TABLE);
        }


    }

    public List<Category> getCategoriesList(){

        Helper.getInstance().LogDetails("getCategoriesList","method called");

        List<Category> list=new ArrayList<>();


        Cursor cursor = null;
        try{

            // String query= "SELECT * FROM " +TABLE_NAME;

            String query= "SELECT * FROM " +TABLE_NAME;


            Helper.getInstance().LogDetails("getCategoriesList","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{

                        Category model=new Category();
                        model.setId(cursor.getString(cursor.getColumnIndex(DataBaseValues.CategoriesTable.id)));
                        model.setCategoryName(cursor.getString(cursor.getColumnIndex(DataBaseValues.CategoriesTable.category_name)));
                        model.setStatus(cursor.getString(cursor.getColumnIndex(DataBaseValues.CategoriesTable.status)));
                        model.setCreatedAt(cursor.getString(cursor.getColumnIndex(DataBaseValues.CategoriesTable.created_at)));
                        model.setUpdatedAt(cursor.getString(cursor.getColumnIndex(DataBaseValues.CategoriesTable.updated_at)));


                    list.add(model);
                        Helper.getInstance().LogDetails("getCategoriesList","called"+model.getId()+" "+model.getCategoryName());




                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getCategoriesList","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getCategoriesList","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  list;
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
