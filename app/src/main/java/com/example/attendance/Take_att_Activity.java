package com.example.attendance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Take_att_Activity extends AppCompatActivity {
    private ListView listView;
    private Button button;
    List<Take_att_data_node> list = new ArrayList<>(0);
    private String table_name, new_col_name;
    Database_helper database_helper = new Database_helper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_attendance);

        Initialize();
        //create a new column

        listView.setAdapter(new Take_att_adapter(this,R.layout.sample_take_attendance, list));

        //button process
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Take_att_Activity.this);
                builder.setMessage("Are you want to save this?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Take_att_Activity.this, "Toaday's attendance is stored.", Toast.LENGTH_SHORT).show();
                        database_helper.AddColumn(table_name, new_col_name);
                        database_helper.InsertTodaysAtt(table_name, new_col_name, list);
                        startActivity(new Intent(Take_att_Activity.this, AttendanceActivity.class));
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
        new_col_name = "day_"+col_name;
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
