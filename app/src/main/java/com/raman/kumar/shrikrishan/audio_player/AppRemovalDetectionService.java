package com.raman.kumar.shrikrishan.audio_player;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class AppRemovalDetectionService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
//        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Service continues running until explicitly stopped
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // This method is called when the app is removed from recent apps
        // Cancel all notifications related to this app
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
        System.out.println("ghfhfh    App removed from recent apps");
        // Stop the service after detecting the app removal
        stopSelf();

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We are not using binding in this service
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }
}

