package com.example.attendance;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class new_course_activity extends AppCompatActivity implements View.OnClickListener {

    private EditText Series_name,Dept_name,Section, Course_name, Start, End, Others;
    private Button button;

    Database_helper database_helper;
    /*
    //this variable for testing
    String mtable_name, mothers_roll;
    int mstrat, mend;
    private Handler mHandler = new Handler();
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_course);
        getSupportActionBar().setTitle("Create new Course");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Series_name = findViewById(R.id.series_name_id);
        Dept_name = findViewById(R.id.dept_name_id);
        Section = findViewById(R.id.section_name_id);
        Course_name = findViewById(R.id.course_name_id);
        Start = findViewById(R.id.start_roll_id);
        End = findViewById(R.id.end_roll_id);
        Others = findViewById(R.id.others_roll_id);
        button = findViewById(R.id.create_buttun_id);

        database_helper = new Database_helper(this);
        SQLiteDatabase sqLiteDatabase = database_helper.getWritableDatabase();

        button.setOnClickListener(this);

    }
    /*
    //background thread
    class MyRunnable implements Runnable {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(new_course_activity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            database_helper.create_attendance_table(mtable_name, mstrat, mend, mothers_roll);
            database_helper.insertRow(mtable_name, mstrat, mend, mothers_roll);
        }
    }
    */

    @Override
    public void onClick(View v) {
        try {
            String series = Series_name.getText().toString().trim();
            String dept = Dept_name.getText().toString().trim();
            String section = Section.getText().toString().trim();
            String course_name = Course_name.getText().toString().trim();
            String start = Start.getText().toString().trim();
            String end = End.getText().toString().trim();
            String others = null;
            if(Others.getText()!= null){
                 others = Others.getText().toString().trim();
            }

            //create buttun is clicked
            Integer starting_roll = Integer.parseInt(start);
            Integer ending_roll = Integer.parseInt(end);
            //Integer others_roll = Integer.parseInt(others);

            if (v.getId()==R.id.create_buttun_id){
                // textView.setText(" "+series+dept+section+course_name);
                long row_id = database_helper.insertData(series,dept,section,course_name,starting_roll,ending_roll,others);
                if(row_id!=-1){
                    ///String table_name = course_name + dept + series + section + starting_roll;
                    //eliminate space for table name
                    //table_name = table_name.replaceAll("\\s","");
                    String table_name = "attendance_table_"+row_id;
                    //Log.d("tag",table_name);

                    database_helper.create_attendance_table(table_name);
                    database_helper.insertRow(table_name, starting_roll, ending_roll, others);

                    //Toast.makeText(getApplicationContext(),"Row  is successfully inserted",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(new_course_activity.this, MainActivity.class);
                    startActivity(intent);

                    /*
                    // performance issues
                    mtable_name = table_name;
                    mstrat = starting_roll;
                    mend = ending_roll;
                    mothers_roll = others;


                    //call for other thread
                    MyRunnable runnable = new MyRunnable();
                    new Thread(runnable).start();
                    /*
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            database_helper.create_attendance_table(mtable_name, mstrat, mend, mothers_roll);
                            database_helper.insertRow(mtable_name, mstrat, mend, mothers_roll);
                        }
                    }).start();
                    */

                } else {
                    Toast.makeText(getApplicationContext(),"Row  is not inserted",Toast.LENGTH_SHORT).show();
                }

            }
        } catch (Exception e){
            Toast.makeText(getApplicationContext(),"Exception is found ",Toast.LENGTH_SHORT).show();
        }
    }
}

