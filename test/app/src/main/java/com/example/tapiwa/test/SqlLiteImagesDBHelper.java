package com.example.tapiwa.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SqlLiteImagesDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "new.db";
    public static final String IMAGES_TABLE_NAME = "images";
    public static final String IMAGES_COLUMN_URI = "image";


    public SqlLiteImagesDBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table images" +
                        "(id integer primary key, imageUri text)"

                //// TODO: 7/9/17 check if text is the right type of text to be entered here
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS images");
        onCreate(db);
    }

    public void addImage (String imageUri){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("imageUri", imageUri);
        db.insert("images", null, contentValues);
    }

    public String getImage(String imageUri) {

        // TODO: 7/6/17 fix displayNote 
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from images", null);

        
        //// TODO: 7/10/17 look for a better way to implement this
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if (res.getString(res.getColumnIndex(IMAGES_COLUMN_URI)).equals(imageUri)) {
                return res.getString(res.getColumnIndex(IMAGES_COLUMN_URI));
            }
            res.moveToNext();
        }
        return null;
    }


   //// TODO: 7/6/17 fix this part
    public void deleteImage (String imageUri) {

        SQLiteDatabase db = this.getReadableDatabase();
        String [] whereArgs = {imageUri};

        db.delete("images", "title = ?", whereArgs);
    }

    public ArrayList<String> getAllImages() {

        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from images", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(IMAGES_COLUMN_URI)));
            res.moveToNext();
        }
        return array_list;
    }
}
