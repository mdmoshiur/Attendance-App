package com.example.attendance;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Course_adapter.MyOnClickListener {
    //modification start here
    ArrayList<Course_card> courses = new ArrayList<>();
    //variables for courses
    private RecyclerView mrecyclerView;
    private Course_adapter madapter;
    private RecyclerView.LayoutManager mlayoutManager;
    private TextView textView;

    //database helper class object
    private Database_helper database_helper;

    //modification end here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //modification start here
        //display courses

        //create arraylist for courses
        database_helper = new Database_helper(this);
        Cursor cursor = database_helper.display_courses();
        if(cursor.getCount() != 0){
            while (cursor.moveToNext()){
                String course_id = cursor.getString(cursor.getColumnIndex("course_id"));
                String course_name = cursor.getString(cursor.getColumnIndex("course_name"));
                String series = cursor.getString(cursor.getColumnIndex("series"));
                String dept = cursor.getString(cursor.getColumnIndex("dept"));
                String section = cursor.getString(cursor.getColumnIndex("section"));
                String starting_roll = cursor.getString(cursor.getColumnIndex("starting_roll"));
                String ending_roll = cursor.getString(cursor.getColumnIndex("ending_roll"));

                String series_dept = dept +"-"+series+", Section: "+ section;
                String roll = "Roll: " + starting_roll+"-"+ending_roll;
                //add to arraylist
                courses.add(new Course_card(course_id, course_name,series_dept,roll));
            }
        }
        cursor.close();

        mrecyclerView = findViewById(R.id.course_recyclerview_id);
        mrecyclerView.setHasFixedSize(true);
        mlayoutManager = new LinearLayoutManager(this);
        madapter = new Course_adapter(courses);
        madapter.setMyOnClickListener(this);
        mrecyclerView.setLayoutManager(mlayoutManager);
        mrecyclerView.setAdapter(madapter);
        registerForContextMenu(mrecyclerView);

        //modification end  here
        ///*
        //move items
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,@NonNull RecyclerView.ViewHolder draggedItem,@NonNull RecyclerView.ViewHolder targetItem) {
                int dragged_position = draggedItem.getAdapterPosition();
                int target_position = targetItem.getAdapterPosition();
                Collections.swap(courses, dragged_position,  target_position);
                madapter.notifyItemMoved(dragged_position, target_position);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            }
        });

        itemTouchHelper.attachToRecyclerView(mrecyclerView);

        //finish move code
        //*/


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.fab){
                    startActivity(new Intent(MainActivity.this,new_course_activity.class));
                }
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.addnewclassid) {
            // Handle the camera action
        } else if (id == R.id.homeid) {
            startActivity(new Intent(MainActivity.this,MainActivity.class));
        } else if (id == R.id.uploadid) {

        } else if (id == R.id.contactid) {

        } else if (id == R.id.shareid) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view, int position) {
        Course_card clickedItem = courses.get(position);
        Intent intent = new Intent(this,AttendanceActivity.class);
        //Log.d("tag", "id: "+ clickedItem.get_course_id());
        Bundle bund = new Bundle();
        bund.putString("Course_ID",clickedItem.get_course_id());
        intent.putExtras(bund);
        startActivityForResult(intent,0);
    }

    //modification

   /*
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.course_pop_up_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.updateid:
                Toast.makeText(MainActivity.this,"update is selected",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.backupid:
                Toast.makeText(MainActivity.this,"backup is selected",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.deleteid:
                Toast.makeText(MainActivity.this,"delete is selected",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }
    //finish here
    */

    @Override
    public void onLongClick(View view, final int position) {
        Log.d("tag","onlongClick is triggered ");

        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.inflate(R.menu.course_pop_up_menu);
        //getMenuInflater().inflate(R.menu.course_pop_up_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.updateid:
                        Toast.makeText(MainActivity.this,"update is selected "+ position,Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.backupid:
                        Toast.makeText(MainActivity.this,"backup is selected ",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.deleteid:
                        Toast.makeText(MainActivity.this,"delete is selected ",Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }


}
