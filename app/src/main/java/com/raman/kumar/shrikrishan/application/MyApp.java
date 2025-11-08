package com.raman.kumar.shrikrishan.application;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.client.Firebase;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.raman.kumar.shrikrishan.AppOpenManager;
import com.raman.kumar.shrikrishan.audio_player.MusicPlayerService;

import java.io.File;
import java.io.IOException;

public class MyApp extends Application {
    private static MyApp mInstance;
    private int activityCount = 0;

    private MusicPlayerService musicPlayerService;
    private boolean isServiceBound = false;
    private AppOpenManager appOpenManager;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.MusicPlayerBinder binder = (MusicPlayerService.MusicPlayerBinder) service;
            musicPlayerService = binder.getService();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicPlayerService = null;
            isServiceBound = false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, initializationStatus -> {});
        appOpenManager = new AppOpenManager(this);

        FirebaseCrashlytics.getInstance();
        Firebase.setAndroidContext(this);
        FirebaseMessaging.getInstance().subscribeToTopic("krishna");
        mInstance = this;

        if ( isExternalStorageWritable() ) {

            File appDirectory = new File( Environment.getExternalStorageDirectory() + "/MyPersonalAppFolder" );
            File logDirectory = new File( appDirectory + "/log" );
            File logFile = new File( logDirectory, "logcat" + System.currentTimeMillis() + ".txt" );

            // create app folder
            if ( !appDirectory.exists() ) {
                appDirectory.mkdir();
            }

            // create log folder
            if ( !logDirectory.exists() ) {
                logDirectory.mkdir();
            }

            // clear the previous logcat and then write the new one to the file
            try {
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile);
            } catch ( IOException e ) {
                e.printStackTrace();
            }

        } else if ( isExternalStorageReadable() ) {
            // only readable
        } else {
            // not accessible
        }



        // Bind service once for the whole app
        Intent intent = new Intent(this, MusicPlayerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);




        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activityCount++;
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d("MyApplication", " pehle App is in the background, all activities destroyed." + activityCount + activity);
                activityCount--;
                if (activityCount == 0) {
                    // All activities are destroyed; the app is in the background
                    Log.d("MyApplication", "App is in the background, all activities destroyed.");
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (notificationManager != null) {
                        notificationManager.cancelAll();
                    }
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {}

            @Override
            public void onActivityResumed(Activity activity) {}

            @Override
            public void onActivityPaused(Activity activity) {

                Log.d("MyApplication", " pehle App is swiped away from recent tasks.");
                if (activityCount == 0) {
                    // The app is in the background (no active activity)
                    Log.d("MyApplication", "App is swiped away from recent tasks.");
                }
            }


            @Override
            public void onActivityStopped(Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
        });



    }
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {
            return true;
        }
        return false;
    }

    public static synchronized MyApp getApplication() {
        return mInstance;
    }


    public MusicPlayerService getMusicPlayerService() {
        return isServiceBound ? musicPlayerService : null;
    }

    @Override
    public void onTerminate() {
        stopAndUnbindService();
        super.onTerminate();

    }

    private void stopAndUnbindService() {
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
        }
        Intent intent = new Intent(this, MusicPlayerService.class);
        stopService(intent);
    }





}