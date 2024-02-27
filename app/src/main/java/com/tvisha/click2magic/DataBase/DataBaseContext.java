package com.tvisha.click2magic.DataBase;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by koti on 6/6/17.
 */

public class DataBaseContext {
    protected SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private Context mContext;

    public DataBaseContext(Context context) {
        try{
            this.mContext = context;
            dbHelper = DatabaseHelper.getInstance(mContext);
            open();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void  open() throws SQLException {
        try{
            if (dbHelper == null){
                dbHelper = DatabaseHelper.getInstance(mContext);
            }
            database = dbHelper.getWritableDatabase();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public  SQLiteDatabase getDatabase(){
        if(database!=null)
        {
            return database;
        }
        else
        {
            return null;
        }
    }


    public void close() {
        try{
            dbHelper.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
