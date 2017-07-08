package com.example.tapiwa.collabo;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "personalNotes.db";
    public static final String NOTES_TABLE_NAME = "notes";
    public static final String NOTES_COLUMN_TITLE = "title";
    public static final String NOTES_COLUMN_CONTENT = "contents";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table notes" +
                        "(title text,contents text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(db);
    }


    public boolean insertNote (String title, String contents) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("contents", contents);
        db.insert("notes", null, contentValues);
        return true;
    }

    public String getNoteContents(String title) {


        // TODO: 7/6/17 fix displayNote 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from notes where title = "+title+" ", null);
        String contents = res.getColumnName(1);

        return contents;
    }

    public Cursor getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notes", null);
        return res;
    }


    // TODO: 7/6/17 fix this part
    public boolean updateNote (String title, String contents) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("contents", contents);
     //   db.update("notes", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }


   //// TODO: 7/6/17 fix this part
    public Integer deleteNote (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllTitles() {

        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notes", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(NOTES_COLUMN_TITLE)));
            res.moveToNext();
        }
        return array_list;
    }
}
