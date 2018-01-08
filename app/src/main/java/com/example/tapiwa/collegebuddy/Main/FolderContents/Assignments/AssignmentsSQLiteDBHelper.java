package com.example.tapiwa.collegebuddy.Main.FolderContents.Assignments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class AssignmentsSQLiteDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Assignments.db";
    public static final String ASSIGNMENTS_TABLE_NAME = "dueDates";
    public static final String ASSIGNMENTS_COLUMN_TITLE = "title";
    public static final String ASSIGNMENTS_COLUMN_DUE_DATES = "dateDue";
    public static final String ASSIGNMENTS_COLUMN_CLASS = "class";


    public AssignmentsSQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table dueDates" +
                        "(id integer primary key, class text, title text, dateDue text)"

                //// TODO: 7/9/17 check if text is the right type of text to be entered here
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS dueDates");
        onCreate(db);
    }

    public void insertDueDate (String classtype, String title, String dueDate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ASSIGNMENTS_COLUMN_CLASS, classtype);
        contentValues.put(ASSIGNMENTS_COLUMN_TITLE, title);
        contentValues.put(ASSIGNMENTS_COLUMN_DUE_DATES, dueDate);
        db.insert(ASSIGNMENTS_TABLE_NAME, null, contentValues);
    }


    // TODO: 7/6/17 fix this part
    public boolean updateDueDate(String classtype, String title, String dueDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ASSIGNMENTS_COLUMN_CLASS, classtype);
        contentValues.put(ASSIGNMENTS_COLUMN_TITLE, title);
        contentValues.put(ASSIGNMENTS_COLUMN_DUE_DATES, dueDate);
        String [] whereArgs = {title, classtype};
        db.update(ASSIGNMENTS_TABLE_NAME, contentValues, "title = ? and class = ?", whereArgs);
        return true;
    }

   //// TODO: 7/6/17 fix this part
    public void deleteAssignment(String classname, String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        String [] whereArgs = {classname, title};
        db.delete(ASSIGNMENTS_TABLE_NAME, "class = ? and title = ?", whereArgs);
    }

    public ArrayList<Assignment> searchDate(String className, String title) {

        ArrayList<Assignment> arr = new ArrayList<Assignment>();

        String [] args = {className, title + '%'};
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor res = sqLiteDatabase
                .rawQuery("select * from personalNotes where class = ? and title like ?", args);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            Assignment assignment = new Assignment(
                    res.getString(res.getColumnIndex(ASSIGNMENTS_COLUMN_TITLE)),
                    res.getString(res.getColumnIndex(ASSIGNMENTS_COLUMN_DUE_DATES))
            );
            arr.add(assignment);
            res.moveToNext();
        }

        return arr;
    }

    public ArrayList<Assignment> getAllDates(String classType) {

        ArrayList<Assignment> arr = new ArrayList<Assignment>();

            SQLiteDatabase db = this.getReadableDatabase();
            String[] args = {classType};
            Cursor res = db.rawQuery("select * from dueDates where class = ?", args);
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                Assignment assignment = new Assignment(
                        res.getString(res.getColumnIndex(ASSIGNMENTS_COLUMN_TITLE)),
                        res.getString(res.getColumnIndex(ASSIGNMENTS_COLUMN_DUE_DATES))
                );
                arr.add(assignment);
                res.moveToNext();
            }
        return arr;
    }

}
