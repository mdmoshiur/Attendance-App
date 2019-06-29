package com.example.attendance;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class Info {
    private static MainActivity mainActivity;
    private static DriveServiceHelper mDriveServiceHelper;

    public Info(MainActivity activity, DriveServiceHelper driveServiceHelper) {
        this.mainActivity = activity;
        this.mDriveServiceHelper = driveServiceHelper;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public static DriveServiceHelper getmDriveServiceHelper() {
        return mDriveServiceHelper;
    }
}
