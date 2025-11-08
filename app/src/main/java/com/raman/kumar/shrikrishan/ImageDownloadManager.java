package com.raman.kumar.shrikrishan;

import android.Manifest;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;

public class ImageDownloadManager {

    public static void downloadImage(Context context, String imageUrl) {
        try {
            // Check permission for Android below 10 (Q)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                        (android.app.Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1
                );
            }

            Uri uri = Uri.parse(imageUrl);
            String fileName = uri.getLastPathSegment();
            if (fileName == null || !fileName.contains(".")) {
                fileName = "image_" + System.currentTimeMillis() + ".jpg";
            }

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle("Downloading Image");
            request.setDescription("Saving ");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.allowScanningByMediaScanner();

            // Destination (will work for all Android versions)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = dm.enqueue(request);

            // ✅ Toast when download starts
            Toast.makeText(context, "Downloading", Toast.LENGTH_SHORT).show();


            // Register BroadcastReceiver
            String finalFileName = fileName;
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                @Override
                public void onReceive(Context ctx, Intent intent) {
                    long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (id == downloadId) {
                        // ✅ Toast when download completes
                        Toast.makeText(ctx, "Download completed", Toast.LENGTH_LONG).show();
                        showBannerNotification(context, finalFileName);
                        context.unregisterReceiver(this);
                    }
                }
            };

            ContextCompat.registerReceiver(context, onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), ContextCompat.RECEIVER_EXPORTED);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static void showBannerNotification(Context context, String fileName) {
        String channelId = "download_channel";
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Download Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for image downloads");
            manager.createNotificationChannel(channel);
        }

        // ✅ Get file path and content URI using FileProvider
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);

        Intent openImageIntent = new Intent(Intent.ACTION_VIEW);
        openImageIntent.setDataAndType(fileUri, "image/*");
        openImageIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                openImageIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle("Download Complete")
                .setContentText("Saved in Downloads")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent); // 👈 Opens image on tap

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
