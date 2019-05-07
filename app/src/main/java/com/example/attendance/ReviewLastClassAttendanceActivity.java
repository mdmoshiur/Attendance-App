package com.example.attendance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ReviewLastClassAttendanceActivity extends AppCompatActivity {
    private String table_name;
    private String new_col_name;
    private ListView listView;
    private Button updateButton;
    private List<Take_att_data_node> reviewList = new ArrayList<>(0);
    private List<Take_att_data_node> previousList = new ArrayList<>(0);
    private Take_att_adapter take_att_adapter;
    private Database_helper database_helper = new Database_helper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_last_class_attendance);
        getSupportActionBar().setTitle("Review last class attendance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Initialize();
        //display list of rolls
        take_att_adapter = new Take_att_adapter(this, R.layout.sample_take_attendance, reviewList);
        listView.setAdapter(take_att_adapter);

        //update button process
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ReviewLastClassAttendanceActivity.this);
                builder.setMessage("Are you want to update last class attendance?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ReviewLastClassAttendanceActivity.this, "Last class attendance is updated.", Toast.LENGTH_SHORT).show();
                        //update last column
                        database_helper.updateLastAttendance(table_name, reviewList, previousList);
                        //finish();
                        onBackPressed();

                    }
                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Log.d("tag","checkvalue: "+ list.get(1).getIntegerCheckValue());
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.review_last_class_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.review_search_button_id:
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        take_att_adapter.getFilter().filter(newText);
                        return false;
                    }
                });
                return true;
            case R.id.take_as_second_period_id:
                takeAttendanceAsSecondPeriod();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void Initialize() {
        InitializeAppData();
        InitializeUI();
        InitializeUIData();
    }

    private void InitializeAppData() {
        Intent intent = getIntent();
        table_name = intent.getStringExtra("table_name");
        //Log.d("table_name"," table name: "+ table_name);
    }

    private void InitializeUI() {
        listView = findViewById(R.id.review_lastClass_listview_id);
        updateButton = findViewById(R.id.review_save_button_id);
    }

    private void InitializeUIData() {
        Cursor cursor = database_helper.AttendacneSummary(table_name);
        Integer lastColumnIndex = cursor.getColumnCount()-1;
        if (cursor != null){
            while (cursor.moveToNext()){
                String roll = cursor.getString(cursor.getColumnIndex("roll_no"));
                String p_att = cursor.getString((cursor.getColumnIndex("p_att")));
                String presence = cursor.getString(lastColumnIndex);
                Integer checkBoxValue;
                if(presence.equals("0")){
                    checkBoxValue = 0;
                } else {
                    checkBoxValue = 1;
                }
                reviewList.add(new Take_att_data_node(roll, p_att, checkBoxValue));
                previousList.add(new Take_att_data_node(roll, p_att, checkBoxValue));
            }
        }
        cursor.close();
    }

    private void takeAttendanceAsSecondPeriod(){
        Cursor cursor = database_helper.AllData(table_name);
        String last_col_name = cursor.getColumnName(cursor.getColumnCount()-1);
        new_col_name = "second_"+last_col_name;
        cursor.close();
        //store this attendance now
        createAlertDialog();
    }

    private void createAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ReviewLastClassAttendanceActivity.this);
        builder.setTitle("Are you sure?");
        builder.setMessage("This will be stored as today's second period's attendance.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ReviewLastClassAttendanceActivity.this, "Second period's attendance is stored.", Toast.LENGTH_SHORT).show();
                Database_helper database_helper2 = new Database_helper(ReviewLastClassAttendanceActivity.this);
                database_helper2.AddColumn(table_name, new_col_name);
                database_helper2.InsertTodaysAtt(table_name, new_col_name, reviewList);
                //finish();
                onBackPressed();

            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Log.d("tag","checkvalue: "+ list.get(1).getIntegerCheckValue());
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        //pass with proper information
        Intent intent = new Intent(ReviewLastClassAttendanceActivity.this, AttendanceActivity.class);
        Bundle bund = new Bundle();
        //process table name
        String course_id = table_name;
        course_id = course_id.replace("attendance_table_","");
        bund.putString("Course_ID",course_id);
        intent.putExtras(bund);
        startActivityForResult(intent,0);

    }
}
