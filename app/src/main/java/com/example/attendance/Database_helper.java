package com.example.attendance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class Database_helper extends SQLiteOpenHelper {
    protected static final String db_name = "attendance.db";
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
            //Toast.makeText(context,"onCreate method is called",Toast.LENGTH_SHORT).show();
            db.execSQL(create_table);
        } catch (Exception e) {
            Toast.makeText(context,"Exception in onCreate method",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            //Toast.makeText(context,"onUpgrade is called",Toast.LENGTH_SHORT).show();
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

    public Cursor showSingleCourse(String row_id){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String single_course_query = "Select * from "+ table_name + " where course_id="+ row_id+";";
        return sqLiteDatabase.rawQuery(single_course_query,null);
    }

    //create table name
    private static String TABLE_NAME ="initial";
    private static String COL_NAME = "Col";
    private static void setColName(String colName) {
        COL_NAME = colName;
    }

    private void  setTable_name(String table_name){
        TABLE_NAME = table_name;
    }


    public void create_attendance_table(String tble_name){
        //create table for new course attendance
         setTable_name(tble_name);
         String create_table_query ="CREATE TABLE "+ TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "roll_no INTEGER, "
                + "p_att INTEGER DEFAULT 0, "
                 + "marks INTEGER DEFAULT 0, "
                 +"attend INTEGER DEFAULT 0  "
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
            sqLiteDatabase.insert(TABLE_NAME, null,contentValues);
        }
    }

    public void AddStudent(String tble_name, String others) {
        setTable_name(tble_name);
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String[] others_roll={};
        if(!others.isEmpty()){
            others_roll = others.split("\\s*,\\s*");
        }
        int len = others_roll.length;
        Integer[] others_roll_int = new Integer[len];
        for(int i=0;i<len;i++){
            others_roll_int[i] = Integer.parseInt(others_roll[i]);
            contentValues.put("roll_no",others_roll_int[i]);
            sqLiteDatabase.insert(TABLE_NAME, null,contentValues);
        }

    }
    private static Integer  Row_id;
    private void setRow_id(String id){
        Row_id = Integer.parseInt(id);
    }
    public void deleteRow(String tble_name, String id){
        setTable_name(tble_name);
        setRow_id(id);
        String delete_query = "Delete from "+ TABLE_NAME +" where id = "+ Row_id+ " ;";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        try {
            sqLiteDatabase.execSQL(delete_query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //delete course
    public void deleteCourse(String id) {
        setRow_id(id);
        String delete_query = "Delete from "+ table_name +" where course_id = "+ Row_id+ " ;";
        String drop_table_query = "drop table if exists attendance_table_"+ id;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        try {
            sqLiteDatabase.execSQL(delete_query);
            sqLiteDatabase.execSQL(drop_table_query);
        } catch (SQLException e) {
            e.printStackTrace();
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

    //show full attendance
    public Cursor fullAttendance(String tble, String row_id) {
        setTable_name(tble);
        setRow_id(row_id);
        String query_full = "SELECT * FROM "+ TABLE_NAME+ " Where id = "+ Row_id;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query_full,null);
        return cursor;
    }

    //create new column for new day
    public void AddColumn(String tble_name, String col_name){
        setTable_name(tble_name);
        setColName(col_name);
        String alter_table_query = "ALTER TABLE "+ TABLE_NAME + " ADD COLUMN "+ COL_NAME+ " INTEGER DEFAULT 0;";
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
        Cursor cursor = AllData(tble_name);
        Integer total_class = cursor.getColumnCount()-5;
        cursor.close();
        //Log.d("total class", "total class: " + total_class);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        for(int i= 0; i< sizeOfList; i++){
            if(mylist.get(i).getIntegerCheckValue()== 1) {
                try{
                    sqLiteDatabase.execSQL("UPDATE "+ TABLE_NAME + " SET "+COL_NAME+ " = 1 , attend = attend + 1, "
                            + " p_att = round(((attend+1)*1.0/"+total_class+")*100,2) "
                    +" WHERE roll_no = "+ mylist.get(i).getRoll() + ";");
                } catch (Exception e){

                }
            } else {
                try{
                    sqLiteDatabase.execSQL("UPDATE "+ TABLE_NAME + " set p_att = round((attend*1.0/"+total_class+")*100,2) "
                            +" WHERE roll_no = "+ mylist.get(i).getRoll() + ";");
                } catch (Exception e){

                }
            }
        }
        UpdateMarks();
    }

    public void updateLastAttendance(String tble, List<Take_att_data_node> updatedList, List<Take_att_data_node> previousList){
        setTable_name(tble);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int size = previousList.size();
        Cursor cursor = AllData(tble);
        Integer total_class = cursor.getColumnCount()-5;
        String col_name = cursor.getColumnName(cursor.getColumnCount()-1);
        setColName(col_name);
        cursor.close();
        for( int i=0;i<size;i++){
            if (updatedList.get(i).getIntegerCheckValue() != previousList.get(i).getIntegerCheckValue()){
                if(updatedList.get(i).getIntegerCheckValue()==1){
                    try{
                        sqLiteDatabase.execSQL("UPDATE "+ TABLE_NAME + " SET "+COL_NAME+ " = 1 , attend = attend + 1, "
                                + " p_att = round(((attend+1)*1.0/"+total_class+")*100,2) "
                                +" WHERE roll_no = "+ updatedList.get(i).getRoll() + ";");
                    } catch (Exception e){ }
                } else {
                    try{
                        sqLiteDatabase.execSQL("UPDATE "+ TABLE_NAME + " SET "+COL_NAME+ " = 0 , attend = attend - 1, "
                                + " p_att = round(((attend - 1)*1.0/"+total_class+")*100,2) "
                                +" WHERE roll_no = "+ updatedList.get(i).getRoll() + ";");
                    } catch (Exception e){ }
                }
                updateMarksRollwise(updatedList.get(i).getRoll());
            }
        }
    }

    public void UpdateMarks(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        try{
            sqLiteDatabase.execSQL("UPDATE "+ TABLE_NAME + " set marks = case "
                    +" when p_att >= 90.0 then 8 "
                    +" when p_att >= 85.0 then 7 "
                    +" when p_att >= 80.0 then 6 "
                    +" when p_att >= 70.0 then 5 "
                    +" when p_att >= 60.0 then 4 "
                    +" when p_att < 60.0 then 0 "
                    +" end "
                    +" where 1"
                    + ";");
        } catch (Exception e){
           //Log.d("tag", "Exeception :"+ e);
        }

    }

    public void updateMarksRollwise(String roll_string){
        Integer roll = Integer.parseInt(roll_string);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        try{
            sqLiteDatabase.execSQL("UPDATE "+ TABLE_NAME + " set marks = case "
                    +" when p_att >= 90.0 then 8 "
                    +" when p_att >= 85.0 then 7 "
                    +" when p_att >= 80.0 then 6 "
                    +" when p_att >= 70.0 then 5 "
                    +" when p_att >= 60.0 then 4 "
                    +" when p_att < 60.0 then 0 "
                    +" end "
                    +" where roll_no = "+ roll
                    + " ;");
        } catch (Exception e){
            //Log.d("tag", "Exeception :"+ e);
        }
    }

    public Cursor AllData(String tble) {
        setTable_name(tble);
        String query = "select * from "+ TABLE_NAME;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery(query, null);
    }


}