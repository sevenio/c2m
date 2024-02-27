package com.tvisha.click2magic.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Utilities;


public class TypingMessageTable extends BaseTable  {

    public static final String TABLE_NAME = DataBaseValues.TypingMessageTable.TABLE_NAME;
    public SQLiteDatabase db = database;
    private Context context;


    public TypingMessageTable(Context context){
        super(context);
        this.context=context;

        if(db!=null){
            db.execSQL(DataBaseValues.TypingMessageTable.CREATE_TYPING_MESSAGE_TABLE);
        }


    }

    public void insertMessage(int chatId,String message){
        long status=0;

        Cursor cursor = null;
        ContentValues data = new ContentValues();
        data.put(DataBaseValues.TypingMessageTable.chat_id, chatId);
        data.put(DataBaseValues.TypingMessageTable.message, message);

        data.put(DataBaseValues.TypingMessageTable.updated_at, Utilities.getCurrentDateTimeNew());

        String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+DataBaseValues.TypingMessageTable.chat_id+" = "+chatId;


        try{
            cursor = db.rawQuery(query,null);
            if(cursor!=null && cursor.getCount()>0)
            {

                if(cursor.moveToNext()){


                    int result= db.update(TABLE_NAME,data,DataBaseValues.TypingMessageTable.chat_id+" = "+chatId,null);
                    if(result>0)
                    {
                        Helper.getInstance().LogDetails("insertMessage","updated true");

                    }
                    else
                    {
                        Helper.getInstance().LogDetails("insertMessage","updated false");
                    }

                }
            }else{
                data.put(DataBaseValues.TypingMessageTable.created_at, Utilities.getCurrentDateTimeNew());
                status = db.insert(TABLE_NAME, null, data);
                if(status>0){
                    Helper.getInstance().LogDetails("insertMessage","inserted true");
                }else
                {
                    Helper.getInstance().LogDetails("insertMessage","inserted true");
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    public TypingMessage getTypingMessage(int chatId){

        Helper.getInstance().LogDetails("getTypingMessage","method called"+chatId);

        TypingMessage model=new TypingMessage();
        Cursor cursor = null;
        try{

            String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " +
                    DataBaseValues.TypingMessageTable.chat_id+ " = " +chatId;


            Helper.getInstance().LogDetails("getTypingMessage","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{

                    model.setChat_id(cursor.getInt(cursor.getColumnIndex(DataBaseValues.TypingMessageTable.chat_id)));
                    model.setMessage(cursor.getString(cursor.getColumnIndex(DataBaseValues.TypingMessageTable.message)));

                    Helper.getInstance().LogDetails("getTypingMessage","called"+model.getChat_id()+" "+model.getMessage());
                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getTypingMessage","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getTypingMessage","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  model;
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
