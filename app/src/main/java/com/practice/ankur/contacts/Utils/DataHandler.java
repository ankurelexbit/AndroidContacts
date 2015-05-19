package com.practice.ankur.contacts.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by ankur on 5/18/15.
 */
public class DataHandler {

    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String IMAGE = "image";
    public static final String CONTENTID = "_id";
    public static final String TABLE_NAME = "contactlist";
    public static final String DB_NAME = "contacts";
    public static final int DB_VERSION = 1;
    public static final String TABLE_CREATE = "create table if not exists " + TABLE_NAME + " " +
            " (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " name VARCHAR NOT NULL," +
            " number VARCHAR NOT NULL," +
            " image VARCHAR NULL);";
    DatabaseHelper dbhelper;
    Context ctx;
    SQLiteDatabase db;

    public DataHandler(Context ctx){
        this.ctx = ctx;
        dbhelper = new DatabaseHelper(ctx);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context ctx){
            super(ctx, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try{
                db.execSQL(TABLE_CREATE);
            }catch (SQLException e){
                e.printStackTrace();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            onCreate(db);
        }
    }

    public DataHandler open(){
        db = dbhelper.getWritableDatabase();
        dbhelper.onCreate(db);
        return this;
    }

    public void close(){
        dbhelper.close();
    }

    public long insert(long _id, String name, String number, String image){
        ContentValues content = new ContentValues();
        content.put(CONTENTID, _id);
        content.put(NAME, name);
        content.put(NUMBER, number);
        content.put(IMAGE, image);
        return db.insertWithOnConflict(TABLE_NAME, null, content, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public Cursor getAll(){
        return db.query(TABLE_NAME, new String[]{CONTENTID, NAME, NUMBER, IMAGE}, null, null, null, null, null);
    }

    public String getImageUriFromNumber(String number){
        Cursor cursor = db.query(TABLE_NAME, new String[] {IMAGE}, "number = ?" , new String[]{number}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getString(0);
        }else
            return null;
    }

    public void setImageUriFromNumber(String number, Uri imageUri){
        ContentValues content = new ContentValues();
        content.put(IMAGE, imageUri.toString());
        db.update(TABLE_NAME, content, NUMBER + " = ?", new String[]{number});
    }
}
