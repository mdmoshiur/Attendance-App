package com.example.attendance;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;



public class DriveServiceHelper {

    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;

    public static final String TAG = "MainActivity.this";

    public DriveServiceHelper(Drive driveService){
        this.mDriveService = driveService;
    }

    //upload to drive
    public Task<String> uploadDBfile(final java.io.File dbFile){
        return Tasks.call(mExecutor, new Callable<String>() {
            @Override
            public String call() throws Exception {

                //search for file
                FileList result = mDriveService.files().list()
                        .setQ("name = 'AttendanceApp.db' and mimeType = 'application/db' and trashed = false")
                        .setSpaces("drive")
                        .setFields("files (id, name)")
                        .execute();
                String fileId = null;
                if(result.getFiles().size() > 0){
                    fileId = result.getFiles().get(0).getId();
                }
                Log.d(TAG, "fileID in function parameter: "+ fileId);
                //not deleted or trashed file
                //Log.d(TAG, "fileID trashed or not: "+ isFileExists(fileID));
                //if (!isFileExists(fileID)){
                if(fileId == null){
                    File metadata = new File()
                            .setParents(Collections.singletonList("root"))
                            .setMimeType("application/db")
                            .setName("AttendanceApp.db");

                    FileContent fileContent = new FileContent("application/db", dbFile);
                    File googleFile = mDriveService.files().create(metadata, fileContent).execute();
                    if (googleFile == null) {
                        throw new IOException("Null result when uploading file :(");
                    }

                    return googleFile.getId();
                } else {
                    //retrieve previously uploaded file
                    //File metadata = mDriveService.files().get(fileID).execute();
                    File metadata = new File().setName("AttendanceApp.db");
                    FileContent fileContent = new FileContent("application/db", dbFile);
                    File googleFile = mDriveService.files().update(fileId, metadata, fileContent).execute();
                    if (googleFile == null) {
                        throw new IOException("Null result when updating file :(");
                    }

                    return googleFile.getId();
                }

            }
        });
    }

    public Intent createFilePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/db");
        return intent;
    }

    public Task<Void> downloadFileUsingStorageAccessFramework(final Uri uri){
        return Tasks.call(mExecutor, new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                uri.getPath();
                File file = mDriveService.files().get(uri.getPath()).execute();
                Log.d(TAG, "path: "+ uri.getPath());
                Log.d(TAG, "fileId: "+ file.getId());
                return null;
            }
        });
    }

    //restore database file
    public Task<Void> downloadDBFile(final java.io.File saveFileLocation){
        return Tasks.call(mExecutor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                //search for file
                FileList result = mDriveService.files().list()
                        .setQ("name = 'AttendanceApp.db' and mimeType = 'application/db' and trashed = false")
                        .setSpaces("drive")
                        .setFields("files (id, name)")
                        .execute();
                String fileId = null;
                if(result.getFiles().size() > 0){
                    fileId = result.getFiles().get(0).getId();
                }
                Log.d(TAG, "fileID in function parameter: "+ fileId);
                if( fileId != null){
                    // Retrieve the metadata as a File object.
                    OutputStream outputStream = new FileOutputStream(saveFileLocation);
                    mDriveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
                } else {
                    Log.d(TAG, "There is no database file found ");
                }

                return null;
            }
        });
    }

}
