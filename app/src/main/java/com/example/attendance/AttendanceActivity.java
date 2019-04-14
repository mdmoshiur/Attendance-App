package com.example.attendance;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity {
    
    private ListView listView;
    private FloatingActionButton floatingActionButton;
    private String TABLE_NAME;

    private Database_helper database_helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        
        Initialize();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("tag", "item clicked "+ position);
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if(v.getId()==R.id.fab_add_id)
                //build alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_view_design, null);
                builder.setView(mView);
                final AlertDialog dialog = builder.create();
                dialog.show();

                //process for edit text
                final EditText date = mView.findViewById(R.id.date_id);
                final EditText cycle_day = mView.findViewById(R.id.day_id);
                Button mButton = mView.findViewById(R.id.submit_button);

                //open date picker
                date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //dialog.dismiss();
                        //code for date picker
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(AttendanceActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                date.setText(dayOfMonth+"/"+(month+1)+"/"+year);

                            }
                        },year, month, day);
                        datePickerDialog.show();
                    }
                });

                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String Date;
                        String Cycle_day;
                        if(cycle_day.getText()!= null){
                            Cycle_day = cycle_day.getText().toString().trim();
                        } else {
                            Cycle_day = "0";
                        }

                        if(date.getText()!=null){
                            Date = date.getText().toString().trim();
                            String new_col = Date +"_"+ Cycle_day;
                            //remove all spaces
                            new_col = new_col.replaceAll("\\s","");
                            Intent intent = new Intent(AttendanceActivity.this, Take_att_Activity.class);
                            intent.putExtra("table_name", TABLE_NAME);
                            intent.putExtra("new_col_name", new_col);
                            startActivity(intent);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(AttendanceActivity.this,"Please enter date!!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
    }

    private void Initialize() {
        InitializeAppData();
        InitializeUI();
        InitializeUIData();
    }

    private void InitializeAppData() {

    }

    private void InitializeUI() {
        listView = findViewById(R.id.list_id);
        floatingActionButton = findViewById(R.id.fab_add_id);
    }

    private void InitializeUIData() {
        String table_name = "attendance_table_";
        Bundle bundle = this.getIntent().getExtras();
        String course_id = bundle.getString("Course_ID");
        table_name = table_name + course_id;
        TABLE_NAME = table_name;
        //Log.d("TAG", "table name: "+ table_name);
        List<DataUser> dataUsers = new ArrayList<>(0);
        database_helper = new Database_helper(this);
        Cursor cursor = database_helper.AttendacneSummary(table_name);
        int latest_col = cursor.getColumnCount()-1;
        if (cursor.getCount()!=0){
            while (cursor.moveToNext()){
                String roll = cursor.getString(cursor.getColumnIndex("roll_no"));
                String p_att =cursor.getString(cursor.getColumnIndex("p_att"));
                String marks = cursor.getString(cursor.getColumnIndex("marks"));
                StringBuilder recent = new StringBuilder();
                recent.append("R: ");
                for(int i = latest_col; i>4 && latest_col-i <= 7; i--){
                    if(cursor.getString(i).equals("1")){
                        recent.append("P");
                    }
                    else
                        recent.append("A");
                }
                String r = recent.toString();
                //add to list item
                dataUsers.add(new DataUser(roll,"M: "+marks,p_att+"%", r));
            }
        }

        listView.setAdapter(new Student_adapter(this, R.layout.single_student_designview, dataUsers));

    }


}
