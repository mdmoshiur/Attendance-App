package com.example.attendance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.attendance.MainActivity.mainActivity;

public class Take_att_Activity extends AppCompatActivity {
    private NonScrollableListView listView;
    private Take_att_adapter take_att_adapter;
    private Button button;
    List<Take_att_data_node> list = new ArrayList<>(0);
    private String table_name, new_col_name;
    Database_helper database_helper = new Database_helper(this);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.take_attendance_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //actionbarUp button action
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.search_button_id:
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
            case R.id.set_all_present:
                setAll(1);
                take_att_adapter.notifyDataSetChanged();
                return true;
            case R.id.set_all_absent:
                setAll(0);
                take_att_adapter.notifyDataSetChanged();
                return true;
            case R.id.save_id:
                button.performClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setAll(int value) {
        int s= list.size();
        for(int i=0;i<s;i++){
            list.get(i).setCheckValue(value);
        }
    }

    @Override
    public void onBackPressed() {
        //pass with proper information
        Intent intent = new Intent(Take_att_Activity.this, AttendanceActivity.class);
        Bundle bund = new Bundle();
        //process table name
        String course_id = table_name;
        course_id = course_id.replace("attendance_table_","");
        bund.putString("Course_ID",course_id);
        intent.putExtras(bund);
        startActivityForResult(intent,0);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_attendance);
        getSupportActionBar().setTitle("Take today's Attendance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Initialize();


        take_att_adapter = new Take_att_adapter(this,R.layout.sample_take_attendance, list);
        listView.setAdapter(take_att_adapter);

        //button process
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Take_att_Activity.this);
                builder.setTitle("Are you want to save this?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Take_att_Activity.this, "Today's  attendance is stored.", Toast.LENGTH_SHORT).show();
                        database_helper.AddColumn(table_name, new_col_name);
                        database_helper.InsertTodaysAtt(table_name, new_col_name, list);
                        mainActivity.scheduleJob();
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

    private void Initialize() {
        InitializeAppData();
        InitializeUI();
        InitializeUIData();
    }

    private void InitializeAppData() {
        Intent intent = getIntent();
        table_name = intent.getStringExtra("table_name");
        String col_name = intent.getStringExtra("new_col_name");
        col_name = col_name.replaceAll("/","_");
        new_col_name = col_name;
        //Log.d("tag",table_name+" / "+ new_col_name);

    }

    private void InitializeUI() {
         listView = findViewById(R.id.listview_for_take_att_id);
        button = findViewById(R.id.save_button_id);
    }

    private void InitializeUIData() {
        Cursor cursor = database_helper.AttendacneSummary(table_name);
        if (cursor.getCount()!=0){
            while (cursor.moveToNext()) {
                String roll = cursor.getString(cursor.getColumnIndex("roll_no"));
                String p_att = cursor.getString(cursor.getColumnIndex("p_att"));
                list.add(new Take_att_data_node(roll, p_att,0));
            }
        }
    }


}
