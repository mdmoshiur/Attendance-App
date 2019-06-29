package com.example.attendance;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.gson.Gson;

import java.io.File;
import java.util.Collections;

public class DatabaseUploadJobService extends JobService {
    private static final String TAG = "jobservice";
    private boolean jobCancelled = false;

    //private Info mInfo;
    private DriveServiceHelper mDriveServiceHelper;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG,"Job started to execute");
        doInBackground(params);
        return true;
    }

    private void doInBackground(final JobParameters params){

        new Thread(new Runnable() {
            @Override
            public void run() {
                /*
                String json = params.getExtras().getString("mInfo");
                Gson g = new Gson();
                mInfo = g.fromJson(json, Info.class);
                Log.d(TAG, "mainActivity: "+ mInfo.getMainActivity());
                Log.d(TAG, "google account: "+ mInfo.getmDriveServiceHelper());
                String databasePath = getDatabasePath("attendance.db").toString();
                Log.d(TAG, "database path: "+ databasePath);
                */

                //set drive account
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                if(account != null){
                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    getApplicationContext(), Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(account.getAccount());
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

                    //prepare database file now
                    final String dbFilePath = getDatabasePath("attendance.db").toString();
                    File dbFile = new File(dbFilePath);

                    //now upload db file in drive
                    mDriveServiceHelper.uploadDBfile(dbFile)
                            .addOnSuccessListener(new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    Log.d(TAG, "file uploaded successfully :) :) ");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "oops database upload failed :( ");
                                }
                            });
                } else {
                    Log.d(TAG, "you are not logged in !");
                }
                Log.d(TAG, "Job is finished");
                jobFinished(params,false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
}
