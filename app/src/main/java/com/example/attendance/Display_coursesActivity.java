package com.example.attendance;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;

public class Display_coursesActivity extends AppCompatActivity {
    ArrayList<Course_card> courses = new ArrayList<>();
    //variables for courses
    private RecyclerView recyclerView;
    private Course_adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    //database helper class object
    private Database_helper database_helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_courses);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //display courses

        //create arraylist for courses
        database_helper = new Database_helper(this);
        Cursor cursor = database_helper.display_courses();
        if(cursor.getCount() != 0){
            while (cursor.moveToNext()){
                String course_name = cursor.getString(cursor.getColumnIndex("course_name"));
                String series = cursor.getString(cursor.getColumnIndex("series"));
                String dept = cursor.getString(cursor.getColumnIndex("dept"));
                String section = cursor.getString(cursor.getColumnIndex("section"));
                String starting_roll = cursor.getString(cursor.getColumnIndex("starting_roll"));
                String ending_roll = cursor.getString(cursor.getColumnIndex("ending_roll"));

                String series_dept = dept +"-"+series+", Section: "+ section;
                String roll = "Roll: " + starting_roll+"-"+ending_roll;
                //add to arraylist
                courses.add(new Course_card(course_name,series_dept,roll));
            }
        }
        cursor.close();

        recyclerView = findViewById(R.id.course_recyclerview_id);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new Course_adapter(courses);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //move items
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,@NonNull RecyclerView.ViewHolder draggedItem,@NonNull RecyclerView.ViewHolder targetItem) {
                int dragged_position = draggedItem.getAdapterPosition();
                int target_position = targetItem.getAdapterPosition();
                Collections.swap(courses, dragged_position,  target_position);
                adapter.notifyItemMoved(dragged_position, target_position);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);

        //finish move code

    }

}
