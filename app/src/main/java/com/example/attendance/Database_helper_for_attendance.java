package com.example.attendance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database_helper_for_attendance extends SQLiteOpenHelper {
    private static final String db_name = "Present_absent";
    private static final int version = 1;
    private static final String table_name = "";
    private static final String create_table_query ="CREATE TABLE "+ table_name + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "roll_no INTEGER, "
            + "p_att DOUBLE "
            + ");";

    //constructor
    private Context context;
    public Database_helper_for_attendance(Context context) {
        super(context, db_name, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(create_table_query);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
