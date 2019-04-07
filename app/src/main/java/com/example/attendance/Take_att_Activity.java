package com.example.attendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Take_att_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        Intent intent = getIntent();
        String course_name = intent.getStringExtra("course_name");
        String series_dept = intent.getStringExtra("series_dept");
        String roll = intent.getStringExtra("roll");
        Log.d("extra", course_name+", "+series_dept+", "+roll);
    }
}
