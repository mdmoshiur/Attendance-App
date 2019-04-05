package com.example.attendance;

import android.content.ContentValues;
import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class Database_helper extends SQLiteOpenHelper {
    private static final String db_name = "attendance";
    private static final String table_name = "courses";
    private static final String series = "series";
    private static final String dept = "dept";
    private static final String section = "section";
    private static final String course_name = "course_name";
    private static final String starting_roll= "starting_roll";
    private static final String ending_roll= "ending_roll";
    private static final String others_roll ="others_roll";
    private static final int version = 3;
    private static final String create_table ="CREATE TABLE "+table_name+"(course_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "series TEXT," +
            "dept TEXT," +
            "section TEXT," +
            "course_name TEXT," +
            "starting_roll INTEGER," +
            "ending_roll INTEGER," +
            "others_roll TEXT" +
            ");";
    private static final String drop_tabe = "DROP TABLE IF EXISTS "+table_name;
    private static final String display_query = "SELECT * FROM "+table_name;

    // table for attendance
    //private static final String attendance_table = "Create table"


    private Context context;

    public Database_helper(Context context) {
        super(context, db_name, null, version);
        this.context=context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Toast.makeText(context,"onCreate method is called",Toast.LENGTH_SHORT).show();
            db.execSQL(create_table);
        } catch (Exception e) {
            Toast.makeText(context,"Exception in onCreate method",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            Toast.makeText(context,"onUpgrade is called",Toast.LENGTH_SHORT).show();
            db.execSQL(drop_tabe);
            onCreate(db);
        } catch (Exception e){
            Toast.makeText(context,"Exception in onUpgrade method",Toast.LENGTH_SHORT).show();
        }

    }

    public long insertData(String Series, String Dept, String Section, String Course_name, int Start,int End,String Others){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(series,Series);
        contentValues.put(dept,Dept);
        contentValues.put(section,Section);
        contentValues.put(course_name,Course_name);
        contentValues.put(starting_roll,Start);
        contentValues.put(ending_roll,End);
        contentValues.put(others_roll,Others);
        long row_id = sqLiteDatabase.insert(table_name,null,contentValues);
        return row_id;
    }

    public Cursor display_courses() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor =sqLiteDatabase.rawQuery(display_query, null);
        return cursor;
    }

    //create table name
    private static String tble_name_for_attendance ="initial";
    public void  setTable_name(String table_name){
        tble_name_for_attendance = table_name;
    }

    public void create_attendance_table(String tble_name, int start, int end, String others){
        //create table for new course attendance
         setTable_name(tble_name);
         String create_table_query ="CREATE TABLE "+ tble_name_for_attendance + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "roll_no INTEGER, "
                + "p_att DOUBLE "
                + ");";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL(create_table_query);
    }

    public void insertRow(String tble_name, int start, int end, String others) {
        setTable_name(tble_name);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for(int i=start; i<= end; i++){
            contentValues.put("roll_no",i);
            contentValues.put("p_att", 0.0);
            sqLiteDatabase.insert(tble_name_for_attendance, null,contentValues);
        }
        String[] others_roll={};
        if(!others.isEmpty()){
            others_roll = others.split("\\s*,\\s*");
        }
        int len = others_roll.length;
        Integer[] others_roll_int = new Integer[len];
        for(int i=0;i<len;i++){
            others_roll_int[i] = Integer.parseInt(others_roll[i]);
            contentValues.put("roll_no",others_roll_int[i]);
            contentValues.put("p_att", 0.0);
            sqLiteDatabase.insert(tble_name_for_attendance, null,contentValues);
        }
    }



}