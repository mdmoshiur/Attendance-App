package com.example.attendance;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity {
    private List<DataUser> dataUsers;
    private ListView listView;
    private FloatingActionButton floatingActionButton;
    private String TABLE_NAME;
    private Student_adapter student_adapter;

    private Database_helper database_helper = new Database_helper(this);

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AttendanceActivity.this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        getSupportActionBar().setTitle("Summary Attendance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        Initialize();
        //onclick listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("tag", "item clicked "+ position);
            }
        });

        //onLongClickListener
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu popupMenu = new PopupMenu(AttendanceActivity.this, view);
                popupMenu.inflate(R.menu.student_popup_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.remove_student_id:
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(AttendanceActivity.this);
                                builder1.setTitle("Are you sure?");
                                builder1.setMessage("This permanently remove the student roll from database.");
                                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String id = dataUsers.get(position).getRowId().trim();
                                        database_helper.deleteRow(TABLE_NAME,id);
                                        finish();
                                        startActivity(getIntent());
                                    }
                                })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Log.d("Tag", " which: "+ which);
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog dialog1 = builder1.create();
                                dialog1.show();
                                return true;
                            default:
                                return false;
                        }

                    }
                });
                return true;
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
                final EditText cycle = mView.findViewById(R.id.cycle_id);
                final EditText day = mView.findViewById(R.id.day_id);
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
                        String Cycle;
                        String Day;

                        if(date.getText()!=null && cycle.getText() != null && day.getText() != null){
                            Date = date.getText().toString().trim();
                            Cycle = cycle.getText().toString().trim();
                            Day = day.getText().toString().trim();
                            String new_col = "cycle_"+Cycle+"_day_"+ Day +"_date_"+ Date;
                            //remove all spaces
                            new_col = new_col.replaceAll("\\s","");
                            Intent intent = new Intent(AttendanceActivity.this, Take_att_Activity.class);
                            intent.putExtra("table_name", TABLE_NAME);
                            intent.putExtra("new_col_name", new_col);
                            startActivity(intent);
                        } else {
                            Toast.makeText(AttendanceActivity.this,"Please fill up all !!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
    }//finish of oncreate()

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.summary_attendance_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.app_bar_search_id:
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        student_adapter.getFilter().filter(newText);
                        return false;
                    }
                });
                return true;
            case R.id.add_new_student_id:
                AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.insert_roll_layout, null);
                builder.setView(mView);
                final AlertDialog dialog = builder.create();
                dialog.show();

                //process for edit text
                final EditText roll = mView.findViewById(R.id.roll_edit_id);
                Button mButton = mView.findViewById(R.id.add_button_id);
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(roll.getText()!= null){
                            String rolls = roll.getText().toString().trim();
                            database_helper.AddStudent(TABLE_NAME, rolls);
                            //student_adapter.notifyDataSetChanged();
                            dialog.dismiss();
                            finish();
                            startActivity(getIntent());

                        } else {
                            Toast.makeText(AttendanceActivity.this, "Please insert roll first!....",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //database_helper.AddStudent(TABLE_NAME);
                return true;
            case R.id.filter_by_id:
                return true;
            case R.id.highest_att_id:
                Collections.sort(dataUsers, new Comparator<DataUser>() {
                    @Override
                    public int compare(DataUser o1, DataUser o2) {
                        Double p1 = Double.parseDouble(o1.getPofattendance());
                        Double p2 = Double.parseDouble(o2.getPofattendance());
                        return p2.compareTo(p1);
                    }
                });
                student_adapter.notifyDataSetChanged();
                return true;
            case R.id.lowest_att_id:
                Collections.sort(dataUsers, new MyComparetor());
                student_adapter.notifyDataSetChanged();
                return true;
            case R.id.summary_id:

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
        dataUsers = new ArrayList<>(0);
        Cursor cursor = database_helper.AttendacneSummary(table_name);
        int latest_col = cursor.getColumnCount()-1;
        if (cursor.getCount()!=0){
            while (cursor.moveToNext()){
                String row_id = cursor.getString(cursor.getColumnIndex("id"));
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
                dataUsers.add(new DataUser(row_id, roll, marks, p_att, r));
            }
        }
        student_adapter = new Student_adapter(this, R.layout.single_student_designview, dataUsers);
        listView.setAdapter(student_adapter);

    }

    //comparator
    public class MyComparetor implements Comparator<DataUser> {
        @Override
        public int compare(DataUser o1, DataUser o2) {
            Double p1 = Double.parseDouble(o1.getPofattendance());
            Double p2 = Double.parseDouble(o2.getPofattendance());
            return p1.compareTo(p2);
        }
    }


}
