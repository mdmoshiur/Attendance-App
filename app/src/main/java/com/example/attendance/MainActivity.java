package com.example.attendance;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

//import database name form db helper class
import static com.example.attendance.Database_helper.db_name;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Course_adapter.MyOnClickListener {
    private static final String TAG = "MainActivity.this";
    public static final int jobID = 112821;
    public static MainActivity mainActivity;
    //modification start here
    ArrayList<Course_data> courses = new ArrayList<>();
    //variables for courses
    private RecyclerView mrecyclerView;
    private Course_adapter madapter;
    private RecyclerView.LayoutManager mlayoutManager;
    private TextView header_name, header_email;
    private ImageView headerImageView;
    private ImageButton imageButton;

    //for google sign in and drive api
    private static final int RC_SIGN_IN = 0;
    private static final int RC_OPEN_DOCUMENT = 1;

    //for rest api
    private DriveServiceHelper mDriveServiceHelper;
    private String mFileId = null;
    //database helper class object
    private Database_helper database_helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //create arraylist for courses
        loadCourseData();

        mrecyclerView = findViewById(R.id.course_recyclerview_id);
        mrecyclerView.setHasFixedSize(true);
        mlayoutManager = new LinearLayoutManager(this);
        madapter = new Course_adapter(courses);
        madapter.setMyOnClickListener(this);
        mrecyclerView.setLayoutManager(mlayoutManager);
        mrecyclerView.setAdapter(madapter);
       // registerForContextMenu(mrecyclerView);

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
                //saveData();
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            }
        });

        itemTouchHelper.attachToRecyclerView(mrecyclerView);

        //finish move code

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
        View headerView = navigationView.getHeaderView(0);
        header_name = headerView.findViewById(R.id.header_name_id);
        header_email =  headerView.findViewById(R.id.header_email_id);
        headerImageView =  headerView.findViewById(R.id.header_imageView_id);
        imageButton = (ImageButton) headerView.findViewById(R.id.header_image_button_id);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        //set last sign in data
        if (account != null){
            updateHeaderUI(account);
        }
        //imageButton onclicklistener
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSignInpopup();
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

    }

    private boolean isInternetAvailable(){
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    private void createSignInpopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.activity_login, null);
        builder.setView(mView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        final SignInButton signInButton = mView.findViewById(R.id.google_sign_in_button_id);
        final Button sign_out_button = mView.findViewById(R.id.sign_out_button_id);
        final Button signIn_other_account = mView.findViewById(R.id.sign_in_other_account_id);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        if(account != null){
            signInButton.setVisibility(View.GONE);
            sign_out_button.setVisibility(View.VISIBLE);
            signIn_other_account.setVisibility(View.VISIBLE);
        } else {
            signInButton.setVisibility(View.VISIBLE);
            sign_out_button.setVisibility(View.GONE);
            signIn_other_account.setVisibility(View.GONE);
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                alertDialog.dismiss();
            }
        });
        sign_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                alertDialog.dismiss();
            }
        });
        signIn_other_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                signIn();
                alertDialog.dismiss();
            }
        });

    }

    //google sign in
    private void signIn() {
        if (!isInternetAvailable()){
            Toast.makeText(MainActivity.this, "No internet", Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG, "Start sign in");
        GoogleSignInClient GoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(GoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }


    //recover drive service helper account
    private void setDriveServiceHelper(){
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        if(googleSignInAccount != null){
            // Use the authenticated account to sign in to the Drive service.
            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingOAuth2(
                            MainActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
            credential.setSelectedAccount(googleSignInAccount.getAccount());
            Drive googleDriveService =
                    new Drive.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new GsonFactory(),
                            credential)
                            .setApplicationName("Attendance App")
                            .build();

            // The DriveServiceHelper encapsulates all REST API and SAF functionality.
            // Its instantiation is required before handling any onClick actions.
            mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
        }
    }

    //drive backup
    public void backupDB(){
        if (! isInternetAvailable()){
            Toast.makeText(MainActivity.this, "No internet available", Toast.LENGTH_SHORT).show();
        }
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        if (account == null){
            Toast.makeText(MainActivity.this, "please sign in first...",Toast.LENGTH_LONG).show();
        } else {
            setDriveServiceHelper();
            Log.d(TAG, "mDriveservice helper i am here now: "+mDriveServiceHelper);
            if (mDriveServiceHelper != null){
                //Log.d(TAG, "mDriveservice helper:"+mDriveServiceHelper);
                File dbFile = getDBFile();
                mDriveServiceHelper.uploadDBfile(dbFile)
                        .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String fileId) {
                                //mFileId = fileId;
                                Toast.makeText(MainActivity.this, "Database uploaded successfully :)",Toast.LENGTH_SHORT).show();
                                //Log.d(TAG, "database file successfully uploaded fileId : "+fileId);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, " failed to upload database file!");
                            }
                        });
            }

        }
    }

    private File getDBFile(){
        final String dbFilePath = MainActivity.this.getDatabasePath(db_name).toString();
        File dbFile = new File(dbFilePath);
        return dbFile;
    }

    //filepicker
    private void openFilePicker(){
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Opening file picker.");

            Intent pickerIntent = mDriveServiceHelper.createFilePickerIntent();

            // The result of the SAF Intent is handled in onActivityResult.
            startActivityForResult(pickerIntent, RC_OPEN_DOCUMENT);
        }
    }

    //restore database file
    public void restoreDB(){
        if (! isInternetAvailable()){
            Toast.makeText(MainActivity.this, "No internet available", Toast.LENGTH_SHORT).show();
        }else {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
            if (account == null){
                Toast.makeText(MainActivity.this, "please sign in first...",Toast.LENGTH_LONG).show();
            } else {
                //String timeStamp = new SimpleDateFormat("dd_MM_yyyy").format(Calendar.getInstance().getTime());
                //String dbOldFileName = "Att_App_OldDB_" +timeStamp+".db";
                setDriveServiceHelper();
                openFilePicker();
                /*
                if(mDriveServiceHelper != null){
                    mDriveServiceHelper.downloadDBFile(getDBFile(), dbOldFileName, new File(MainActivity.this.getDatabasePath(db_name).toString()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //restart activity
                                    finish();
                                    startActivity(getIntent());
                                    Toast.makeText(MainActivity.this,"Database restored successfully!!",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this,"Failed to restore !!",Toast.LENGTH_SHORT).show();

                                }
                            });
                }
                */
            }
        }
    }



    private void signOut(){
        GoogleSignInClient mGoogleSignInClient = buildGoogleSignInClient();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //
                        headerImageView.setImageResource(R.mipmap.ic_launcher_round);
                        header_name.setText("@name");
                        header_email.setText("@email address");
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
                if (resultCode == Activity.RESULT_OK && data != null) {
                    // The Task returned from this call is always completed, no need to attach
                    // a listener.
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(data);
                    handleSignInForUIData(task);

                }
                break;

            case RC_OPEN_DOCUMENT:
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    Log.d(TAG," i am now at request code here");
                    Log.d(TAG,"selected file uri: " + uri);
                    if (uri != null && mDriveServiceHelper != null) {
                        String timeStamp = new SimpleDateFormat("dd_MM_yyyy").format(Calendar.getInstance().getTime());
                        String dbOldFileName = "Att_App_OldDB_" +timeStamp+".db";
                        mDriveServiceHelper.download_Replace_DBFile(MainActivity.this, getDBFile(), dbOldFileName, uri)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //restart activity
                                        finish();
                                        startActivity(getIntent());
                                        Toast.makeText(MainActivity.this, "Database Imported :)", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, " Import failed", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                } else {
                    Log.d(TAG, "Unable to download file.");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void handleSignInForUIData(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            if (account != null){
                updateHeaderUI(account);
            }

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleAccount) {
                        // Use the authenticated account to sign in to the Drive service.
                        GoogleAccountCredential credential =
                                GoogleAccountCredential.usingOAuth2(
                                        MainActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
                        credential.setSelectedAccount(googleAccount.getAccount());
                        Drive googleDriveService =
                                new Drive.Builder(
                                        AndroidHttp.newCompatibleTransport(),
                                        new GsonFactory(),
                                        credential)
                                        .setApplicationName("Attendance App")
                                        .build();

                        // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                        // Its instantiation is required before handling any onClick actions.
                        mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
                    }
                });
    }

    private void updateHeaderUI(GoogleSignInAccount account){
        String personName = account.getDisplayName();
        //String personGivenName = account.getGivenName();
        //String personFamilyName = account.getFamilyName();
        String personEmail = account.getEmail();
        //String personId = account.getId();
        Uri personPhoto = account.getPhotoUrl();
        header_name.setText(personName);
        header_email.setText(personEmail);
        Glide.with(this).load(personPhoto).into(headerImageView);
    }

    private void loadCourseData() {
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
                courses.add(new Course_data(course_id, course_name,series_dept,roll));
            }
        }
        cursor.close();
    }
    /*
    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("course_list", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(courses);
        editor.putString("My_changes", json);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("course_list", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("My_changes", null);
        Type type = new TypeToken<ArrayList<Course_data>>() {}.getType();
        courses = gson.fromJson(json, type);

        if(courses == null){
            loadCourseData();
        }
    }

    */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //create an alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Are you want to exit the app?");
            builder.setPositiveButton("Yes", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //exit the app
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory(Intent.CATEGORY_HOME);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);
                }
            })
                    .setNegativeButton("No", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.addnewclassid) {
            startActivity(new Intent(MainActivity.this, new_course_activity.class));
        } else if (id == R.id.homeid) {
            startActivity(new Intent(MainActivity.this,MainActivity.class));
        } else if (id == R.id.uploadid) {
            backupDB();
        } else if (id == R.id.restore_id){
            createConfirmDialog();
        } else if (id == R.id.contactid) {

        } else if (id == R.id.shareid) {
            //cancelJob();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //confirmation dialog to restore database
    private void createConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Are you know what is happening?")
                .setMessage("If you confirm, the present database will be stored as an old database in google drive and the selected backup database will be restored in the app." +
                        "\n\nBe sure what you actually want?")
                .setPositiveButton("Confirm", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //restore database file
                        restoreDB();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void scheduleJob(){
        ComponentName componentName  = new ComponentName(MainActivity.this, DatabaseUploadJobService.class);
        //put extra info
        //setDriveServiceHelper();
        //Info mInfo = new Info(mainActivity, mDriveServiceHelper);
        //Gson g = new Gson();
        //String json = g.toJson(mInfo);

        //PersistableBundle bundle = new PersistableBundle();
        //bundle.putString("mInfo", json);
        JobInfo jobInfo = new JobInfo.Builder(jobID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                //.setExtras(bundle)
                .build();
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(jobInfo);
        if(resultCode == JobScheduler.RESULT_SUCCESS){
            Log.d("jobservice","Job scheduled finished");
        } else {
            Log.d("jobservice","Job scheduling failed");
        }
    }

    public void cancelJob(){
        JobScheduler jobScheduler =(JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(jobID);
        Log.d("jobservice","Job cancelled");
    }

    @Override
    public void onClick(View view, int position) {
        Course_data clickedItem = courses.get(position);
        Intent intent = new Intent(this,AttendanceActivity.class);
        //Log.d("tag", "id: "+ clickedItem.get_course_id());
        Bundle bund = new Bundle();
        bund.putString("Course_ID",clickedItem.get_course_id());
        intent.putExtras(bund);
        startActivityForResult(intent,0);
    }


    @Override
    public void onLongClick(View view, final int position) {
        //Log.d("tag","onlongClick is triggered ");

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
                    case R.id.deleteid:
                        database_helper.deleteCourse(courses.get(position).get_course_id());
                        //give trigger
                        //scheduleJob();
                        finish();
                        startActivity(getIntent());
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

}
