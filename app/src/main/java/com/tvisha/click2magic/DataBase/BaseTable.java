package com.tvisha.click2magic.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tvisha.click2magic.Helper.Session;


public class BaseTable extends DataBaseContext {
    public SQLiteDatabase db = database;
    DatabaseHelper databaseHelper;
    Context cnt;
    String login_user_id,tmUserId="";

    public BaseTable(Context context) {
        super(context);
        cnt = context;

        login_user_id =  Session.getUserID(context);
        tmUserId  = Session.getTmUserId(context);
    }

    public long insert(String TABLE_NAME, ContentValues values) {
        try {
            long result = db.insert(TABLE_NAME, null, values);
            if (result == -1) {
                return result;
            } else {
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
        }

        return -1;
    }

    public boolean update(String TABLE_NAME, ContentValues values, String where, String[] whereArgs) {
        try {
            long result = db.update(TABLE_NAME, values, where, whereArgs);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
        }

    }

    public boolean isgroupMemberExits(String TABLE_NAME, String where, String[] whereArgs) {
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, null, where, whereArgs, null, null, null, null);
            if(cursor!=null && cursor.moveToFirst()){
                cursor.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return false;
    }
    public boolean isExist(String TABLE_NAME, String where, String[] whereArgs) {
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, null, where, whereArgs, null, null, null, null);
            if(cursor!=null && cursor.moveToFirst()){
                cursor.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return false;
    }

    public Cursor selectWithQuery(String query) {
        try {
            return db.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
        }
        return null;
    }
    public Cursor selectWithQuery2(String query) {
        try {
            return db.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
        }
        return null;
    }

    public Cursor selectWithWhere(String TABLE_NAME, String[] columns, String where, String[] whereArgs) {
        try {
            return db.query(TABLE_NAME, columns, where, whereArgs, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
        }
        return null;
    }

    public Cursor selectWithLimit(String TABLE_NAME, String[] columns, String where, String[] whereArgs, String orderBy) {
        try {
            return db.query(TABLE_NAME, columns, where, whereArgs, null, null, orderBy, "1,20");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Cursor selectWithWhereOrderBy(String TABLE_NAME, String[] columns, String where, String[] whereArgs, String orderBy) {
        try {
            return db.query(TABLE_NAME, columns, where, whereArgs, null, null, orderBy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Cursor selectAll(String TABLE_NAME) {
        try {
            return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
        }

        return null;
    }

    public Cursor selectByManualQuery(String query) {
        try {
            return db.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
        }

        return null;
    }

    public void truncateDataBase(){
        try{
            db.delete(DataBaseValues.ChatConversation.TABLE_NAME,null,null);
            db.delete(DataBaseValues.Agent.TABLE_NAME,null,null);
            db.delete(DataBaseValues.Customer.TABLE_NAME,null,null);


        }catch (Exception e){
            e.printStackTrace();
        }
    }




}
