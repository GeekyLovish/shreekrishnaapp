package com.raman.kumar.shrikrishan.apiNetworking;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.util.Log;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

public class AppUpdateHelper {

    private final Context context;
    private final Activity activity;
    private final AppUpdateManager appUpdateManager;
    private static final int REQUEST_CODE_UPDATE = 123;
    private static final String TAG = "AppUpdateHelper";

    public AppUpdateHelper(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.appUpdateManager = AppUpdateManagerFactory.create(context);
    }

    public void checkForUpdate() {
        appUpdateManager.getAppUpdateInfo()
                .addOnSuccessListener(appUpdateInfo -> {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                            appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        startUpdateFlow(appUpdateInfo);
                    } else {
                        Log.w(TAG, "No Update available");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking for updates", e));
    }

    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    activity,
                    REQUEST_CODE_UPDATE
            );
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Failed to start update flow", e);
            Toast.makeText(context, "Failed to start update flow.", Toast.LENGTH_SHORT).show();
        }
    }

    public void resumeUpdateIfNeeded() {
        appUpdateManager.getAppUpdateInfo()
                .addOnSuccessListener(appUpdateInfo -> {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS &&
                            appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        startUpdateFlow(appUpdateInfo);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to get update info", e));
    }
}
