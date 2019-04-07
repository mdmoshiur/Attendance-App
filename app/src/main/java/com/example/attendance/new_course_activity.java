package com.example.attendance;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class new_course_activity extends AppCompatActivity implements View.OnClickListener {

    private EditText Series_name,Dept_name,Section, Course_name, Start, End, Others;
    private Button button;
    private TextView textView;

    Database_helper database_helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_course);

        Series_name = findViewById(R.id.series_name_id);
        Dept_name = findViewById(R.id.dept_name_id);
        Section = findViewById(R.id.section_name_id);
        Course_name = findViewById(R.id.course_name_id);
        Start = findViewById(R.id.start_roll_id);
        End = findViewById(R.id.end_roll_id);
        Others = findViewById(R.id.others_roll_id);
        button = findViewById(R.id.create_buttun_id);
        textView = findViewById(R.id.textid);

        database_helper = new Database_helper(this);
        SQLiteDatabase sqLiteDatabase = database_helper.getWritableDatabase();

        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        try {
            String series = Series_name.getText().toString().trim();
            String dept = Dept_name.getText().toString().trim();
            String section = Section.getText().toString().trim();
            String course_name = Course_name.getText().toString().trim();
            String start = Start.getText().toString().trim();
            String end = End.getText().toString().trim();
            String others = Others.getText().toString().trim();
            //create buttun is clicked
            Integer starting_roll = Integer.parseInt(start);
            Integer ending_roll = Integer.parseInt(end);
            //Integer others_roll = Integer.parseInt(others);

            if (v.getId()==R.id.create_buttun_id){
                // textView.setText(" "+series+dept+section+course_name);
                long row_id = database_helper.insertData(series,dept,section,course_name,starting_roll,ending_roll,others);
                if(row_id!=-1){
                    //Toast.makeText(getApplicationContext(),"Row  is successfully inserted",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),"Row  is not inserted",Toast.LENGTH_SHORT).show();
                }

                String table_name = course_name + dept + series + section + starting_roll;
                //eliminate space for table name
                table_name = table_name.replaceAll("\\s","");
                //Log.d("tag",table_name);
                database_helper.create_attendance_table(table_name, starting_roll, ending_roll, others);
                database_helper.insertRow(table_name, starting_roll, ending_roll, others);

            }
        } catch (Exception e){
            Toast.makeText(getApplicationContext(),"Exception is found ",Toast.LENGTH_SHORT).show();
        }
    }
}

