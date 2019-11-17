package com.example.attendance;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.example.attendance.Database_helper.db_name;


public class DriveServiceHelper {

    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;

    public static final String TAG = "MainActivity.this";

    public DriveServiceHelper(Drive driveService){
        this.mDriveService = driveService;
    }

    private String createFolder(String folder_name) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(folder_name);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setParents(Collections.singletonList("root"));

        File folder = mDriveService.files().create(fileMetadata).execute();
        return folder.getId();
    }

    //upload to drive
    public Task<String> uploadDBfile(final java.io.File dbFile){
        return Tasks.call(mExecutor, new Callable<String>() {
            @Override
            public String call() throws Exception {

                //at first search folder exists or not
                // Retrive the metadata as a File object.
                FileList result = mDriveService.files().list()
                        .setQ("mimeType = 'application/vnd.google-apps.folder' and name = 'Attendance App'and trashed = false ")
                        .setSpaces("drive")
                        .execute();
                String folderID = null;
                if (result.getFiles().size() > 0) {
                    folderID = result.getFiles().get(0).getId();
                } else {
                    folderID = createFolder("Attendance App");
                }
                //search for file
                FileList files_result = mDriveService.files().list()
                        .setQ("'" + folderID + "' in parents and name = 'AttendanceApp.db' and mimeType = 'application/db' and trashed = false")
                        .setSpaces("drive")
                        .setFields("files (id, name)")
                        .execute();
                String fileId = null;
                if (files_result.getFiles().size() > 0) {
                    fileId = files_result.getFiles().get(0).getId();
                }
                Log.d(TAG, "fileID in function parameter: " + fileId);
                //not deleted or trashed file
                //Log.d(TAG, "fileID trashed or not: "+ isFileExists(fileID));
                //if (!isFileExists(fileID)){
                if (fileId == null) {
                    File metadata = new File()
                            .setParents(Collections.singletonList(folderID))
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/db");
        return intent;
    }


    /*
    public Task<Void> downloadFileUsingStorageAccessFramework(final Context context, final Uri uri){
        return Tasks.call(mExecutor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Log.d(TAG, "inside");
                InputStream fileInputStream =context.getContentResolver().openInputStream(uri);
                final String output_dbfilename = context.getDatabasePath(db_name).toString();
                // Open the empty db as the output stream
                OutputStream output = new FileOutputStream(output_dbfilename);

                // Transfer bytes from the inputfile to the outputfile
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fileInputStream.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }

                // Close the streams
                output.flush();
                output.close();
                fileInputStream.close();
                return null;
            }
        });

    }

    */


    //restore database file
    public Task<Void> download_Replace_DBFile(final Context context, final java.io.File dbFile, final String fileName, final Uri uri){
        return Tasks.call(mExecutor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                //upload old database file
                //goto attendance app folder
                FileList result = mDriveService.files().list()
                        .setQ("mimeType = 'application/vnd.google-apps.folder' and name = 'Attendance App'and trashed = false ")
                        .setSpaces("drive")
                        .execute();
                String folderID = null;
                if (result.getFiles().size() > 0) {
                    folderID = result.getFiles().get(0).getId();
                } else {
                    folderID = createFolder("Attendance App");
                }
                File metadata = new File()
                        .setParents(Collections.singletonList(folderID))
                        .setMimeType("application/db")
                        .setName(fileName);

                FileContent fileContent = new FileContent("application/db", dbFile);
                File googleFile = mDriveService.files().create(metadata, fileContent).execute();
                if (googleFile == null) {
                    throw new IOException("Null result when uploading file :(");
                } else {
                    Log.d(TAG,"file not uploaded");
                }
                // finished old file uploading

                // now download selected db file and replace
                InputStream fileInputStream = context.getContentResolver().openInputStream(uri);
                final String output_dbfilename = context.getDatabasePath(db_name).toString();
                // Open the empty db as the output stream
                OutputStream output = new FileOutputStream(output_dbfilename);

                // Transfer bytes from the inputfile to the outputfile
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fileInputStream.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }

                // Close the streams
                output.flush();
                output.close();
                fileInputStream.close();

                return null;
            }
        });
    }

    /* previous easy version
    //restore database file
    public Task<Void> downloadDBFile(final java.io.File dbFile, final String fileName, final java.io.File saveFileLocation){
        return Tasks.call(mExecutor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                //upload old database file
                File metadata = new File()
                        .setParents(Collections.singletonList("root"))
                        .setMimeType("application/db")
                        .setName(fileName);

                FileContent fileContent = new FileContent("application/db", dbFile);
                File googleFile = mDriveService.files().create(metadata, fileContent).execute();
                if (googleFile == null) {
                    throw new IOException("Null result when uploading file :(");
                } else {
                    Log.d(TAG,"file not uploaded");
                }

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
    */
}
