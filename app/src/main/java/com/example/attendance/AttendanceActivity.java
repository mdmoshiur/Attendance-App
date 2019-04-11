package com.example.attendance;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
    private Student_adapter  student_adapter;

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
                            Cycle_day = "";
                        }

                        if(date.getText()!=null){
                            Date = date.getText().toString().trim();
                            String new_col = Date +"_"+ Cycle_day;
                            //remove all spaces
                            new_col = new_col.replaceAll("\\s","");
                            Intent intent = new Intent(AttendanceActivity.this, Take_att_Activity.class);
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
        List<DataUser> dataUsers = new ArrayList<>(0);
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));
        dataUsers.add(new DataUser("1503113", "8", "90%", "R: PPPAPPPPPP"));

        listView.setAdapter(new Student_adapter(this, R.layout.single_student_designview, dataUsers));

    }


}
