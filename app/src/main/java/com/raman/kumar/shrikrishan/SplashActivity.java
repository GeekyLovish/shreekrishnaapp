package com.raman.kumar.shrikrishan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.raman.kumar.shrikrishan.apiNetworking.AppUpdateHelper;
import com.raman.kumar.shrikrishan.util.PrefHelper;

public class SplashActivity extends AppCompatActivity {

    PrefHelper prefHelper;
    AppUpdateHelper appUpdateHelper;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        // Initialize AppUpdateHelper
        appUpdateHelper = new AppUpdateHelper(this, this);

        // Check for app updates
        appUpdateHelper.checkForUpdate();

        // Firebase messaging setup
        FirebaseMessaging.getInstance().subscribeToTopic("krishna");

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String refreshedToken;
                if (!task.isSuccessful()) {
                    refreshedToken = task.getException().getMessage();
                    Log.w("FCM TOKEN Failed", task.getException());
                } else {
                    refreshedToken = task.getResult();
                    Log.e("FCM TOKEN", refreshedToken);
                }

                // Store Firebase token
                prefHelper = new PrefHelper(getApplicationContext());
                prefHelper.setFirebaseToken(refreshedToken);
            }
        });
        requestNotificationPermission(); // 🔥 Request permission on startup
        // Delay to move to MainActivity after splash
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 2500);
    }



    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
                    Toast.makeText(this, "Please allow notifications in Settings.", Toast.LENGTH_LONG).show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
                }
            }
        }
    }


    // 🔥 Handle Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "✅ Notification permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "❌ Notification permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
