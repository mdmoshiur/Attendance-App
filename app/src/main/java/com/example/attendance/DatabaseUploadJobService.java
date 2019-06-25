package com.example.attendance;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import static com.example.attendance.MainActivity.mainActivity;
public class DatabaseUploadJobService extends JobService {
    private static final String TAG = "jobservice";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG,"Job started");
        doInBackground(params);
        return true;
    }

    private void doInBackground(final JobParameters params){

        new Thread(new Runnable() {
            @Override
            public void run() {
                /*
                for(int i = 0; i<10; i++){
                    if(jobCancelled){
                        return;
                    }
                    Log.d(TAG," run: "+ i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                         e.printStackTrace();
                    }
                }
                */
                mainActivity.connectToDrive(true);
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
