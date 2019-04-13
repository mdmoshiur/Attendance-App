package com.example.attendance;

import android.content.ContentValues;
import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class Database_helper extends SQLiteOpenHelper {
    private static final String db_name = "attendance.db";
    private static final String table_name = "courses";
    private static final String series = "series";
    private static final String dept = "dept";
    private static final String section = "section";
    private static final String course_name = "course_name";
    private static final String starting_roll= "starting_roll";
    private static final String ending_roll= "ending_roll";
    private static final String others_roll ="others_roll";
    private static final int version = 1;
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
    private static String TABLE_NAME ="initial";
    private static String COL_NAME = "Col";
    public static void setColName(String colName) {
        COL_NAME = colName;
    }

    public void  setTable_name(String table_name){
        TABLE_NAME = table_name;
    }


    public void create_attendance_table(String tble_name, int start, int end, String others){
        //create table for new course attendance
         setTable_name(tble_name);
         String create_table_query ="CREATE TABLE "+ TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
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
            sqLiteDatabase.insert(TABLE_NAME, null,contentValues);
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
            sqLiteDatabase.insert(TABLE_NAME, null,contentValues);
        }
    }

    //retraive list item data

    public Cursor AttendacneSummary(String tble_name){
        setTable_name(tble_name);
        String summary_query = "SELECT * FROM "+ TABLE_NAME;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(summary_query,null);
        return cursor;
    }

    //create new column for new day
    public void AddColumn(String tble_name, String col_name){
        setTable_name(tble_name);
        setColName(col_name);
        String alter_table_query = "ALTER TABLE "+ TABLE_NAME + " ADD COLUMN "+ COL_NAME+ " INTEGER DEFAULT 1;";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        try {
            sqLiteDatabase.execSQL(alter_table_query);
        } catch (Exception e){
            Log.d("tag", "Exception: "+e);
        }

    }

    public void InsertTodaysAtt(String tble_name, String col_name, List<Take_att_data_node> mylist) {
        setTable_name(tble_name);
        setColName(col_name);
        int sizeOfList = mylist.size();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        for(int i= 0; i< sizeOfList; i++){
            if(mylist.get(i).getIntegerCheckValue()!= 1) {
                try{
                    sqLiteDatabase.execSQL("UPDATE "+ TABLE_NAME + " SET "+COL_NAME+ " = "+ mylist.get(i).getIntegerCheckValue()
                    +" WHERE roll_no = "+ mylist.get(i).getRoll() + ";");
                } catch (Exception e){
                    Log.d("tag", "Exeception :"+ e);
                }
            }
        }
    }
}