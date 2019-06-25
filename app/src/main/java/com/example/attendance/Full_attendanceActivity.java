package com.example.attendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Full_attendanceActivity extends AppCompatActivity {

    private TextView rollView, percentageView, marksView, presentView, absentView;
    private NonScrollableListView nonScrollableListView;
    private List<Single_student_data> listData = new ArrayList<>(0);
    private String table_name, row_id;
    private Database_helper database_helper = new Database_helper(this);
    private Full_attendance_adapter full_attendance_adapter;
    private String roll;
    private boolean changeMade = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_attendance_of_single_student);
        getSupportActionBar().setTitle("Full Attendance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Initialize();

        full_attendance_adapter = new Full_attendance_adapter(this,R.layout.single_day_presence_gesign, listData );
        //listView.setAdapter(full_attendance_adapter);
        nonScrollableListView.setAdapter(full_attendance_adapter);

        nonScrollableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String col_name = listData.get(position).getCol_name();
                final String previous_presence = listData.get(position).getPresence();
                //build alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Full_attendanceActivity.this);
                if(previous_presence.equals("Absent")){
                    builder.setTitle("Set Absent -> Present");
                } else {
                    builder.setTitle("Set Present -> Absent");
                }
                builder.setMessage("This will toggle the previous attendance.");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeMade = true;
                        if( previous_presence.equals("Absent")){
                            //update database
                            Database_helper database_helper = new Database_helper(Full_attendanceActivity.this);
                            database_helper.toggleAttendance(table_name, roll, col_name, "P");
                            database_helper.close();
                            listData.clear();
                            Initialize();
                            full_attendance_adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        } else {
                            //update database
                            Database_helper database_helper = new Database_helper(Full_attendanceActivity.this);
                            database_helper.toggleAttendance(table_name, roll, col_name, "A");
                            database_helper.close();
                            listData.clear();
                            Initialize();
                            full_attendance_adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
        });
    }

    private void Initialize() {
        InitializeAppData();
        InitializeUI();
        InitializeUIData();

    }

    private void InitializeAppData() {
        Intent intent = getIntent();
        table_name = intent.getStringExtra("table_name");
        row_id = intent.getStringExtra("row_id");
        //Log.d("tagextra", "intent row_id: "+ row_id+" table_name: "+ table_name);
    }

    private void InitializeUI() {
        rollView = findViewById(R.id.full_roll_id);
        percentageView = findViewById(R.id.full_percentage_id);
        marksView = findViewById(R.id.full_marks_id);
        presentView = findViewById(R.id.full_present_id);
        absentView = findViewById(R.id.full_absent_id);

        //listView = findViewById(R.id.full_list_view_id);
        nonScrollableListView = findViewById(R.id.full_list_view_id);
    }

    private void InitializeUIData() {
        Cursor cursor = database_helper.fullAttendance(table_name, row_id);
        Integer number_of_column = cursor.getColumnCount();
        if (cursor != null) {
            while(cursor.moveToNext()){
                roll = cursor.getString(cursor.getColumnIndex("roll_no"));
                String p_att =cursor.getString(cursor.getColumnIndex("p_att"));
                String marks = cursor.getString(cursor.getColumnIndex("marks"));
                String attend = cursor.getString(cursor.getColumnIndex("attend"));
                String Total_class = cursor.getString(cursor.getColumnIndex("total_class"));
                Integer attendInt = Integer.parseInt(attend);
                Integer total_class = Integer.parseInt(Total_class);
                Integer absent = total_class - attendInt;
                rollView.setText("Roll No: "+ roll);
                percentageView.setText("Percentage: "+ p_att+ "%");
                marksView.setText("Obtained marks: "+ marks);
                presentView.setText("Present: "+ attend);
                absentView.setText("Absent: "+ absent);
                for(int i=number_of_column-total_class; i<number_of_column; i++){
                    String col_name = cursor.getColumnName(i);
                    String cycle = col_name.substring(col_name.indexOf("cycle_")+6, col_name.indexOf("_day"));
                    String day = col_name.substring(col_name.indexOf("day_")+4, col_name.indexOf("_date"));
                    String date = col_name.substring(col_name.indexOf("date_")+5);
                    String cycle_day = cycle + day;
                    //second period check
                    if (col_name.startsWith("second_")){
                        cycle_day = cycle_day + " 2nd P.";
                    }
                    String pre = cursor.getString(i);
                    //Log.d("hellotag"," pre : "+ pre);
                    String presence;
                    if(pre.equals("P")){
                        presence = "Present";
                    }else {
                        presence = "Absent";
                    }
                    listData.add(new Single_student_data(cycle_day, date, presence, col_name));
                }
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(changeMade){
            //pass with proper information
            Intent intent = new Intent(Full_attendanceActivity.this, AttendanceActivity.class);
            Bundle bund = new Bundle();
            //process table name
            String course_id = table_name;
            course_id = course_id.replace("attendance_table_","");
            bund.putString("Course_ID",course_id);
            intent.putExtras(bund);
            startActivityForResult(intent,0);
        } else {
            super.onBackPressed();
        }
    }
}
