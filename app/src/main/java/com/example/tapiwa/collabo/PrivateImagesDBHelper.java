package com.example.tapiwa.collabo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class PrivateImagesDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "personalNotes.db";
    public static final String IMAGES_TABLE_NAME = "personalImages";
    public static final String IMAGES_COLUMN_TITLE = "images";


    public PrivateImagesDBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table personalImages" +
                        "(id integer autoincrement primary key, imageUri text)"

                //// TODO: 7/9/17 check if text is the right type of text to be entered here
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS personalImages");
        onCreate(db);
    }

    public void addImage (String imageUri){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("imageUri", imageUri);
        db.insert("personalImages", null, contentValues);
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
            array_list.add(res.getString(res.getColumnIndex(IMAGES_COLUMN_TITLE)));
            res.moveToNext();
        }
        return array_list;
    }
}
