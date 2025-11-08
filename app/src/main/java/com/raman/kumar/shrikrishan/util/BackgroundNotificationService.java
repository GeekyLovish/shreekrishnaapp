package com.raman.kumar.shrikrishan.util;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.raman.kumar.NotificationId;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class BackgroundNotificationService extends IntentService {

    public BackgroundNotificationService() {
        super("Service");
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private String url_my;




    @Override
    protected void onHandleIntent(Intent intent) {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (intent.hasExtra("url")) {
            url_my = intent.getStringExtra("url");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("id", "an", NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription("no sound");
            notificationChannel.setSound(null, null);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        notificationBuilder = new NotificationCompat.Builder(this, "id")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Download")
                .setContentText("Downloading Image")
                .setDefaults(0)
                .setAutoCancel(true);
        notificationManager.notify(100+NotificationId.getID(), notificationBuilder.build());

        try {
            downloadImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        initRetrofit();

    }

//    private void initRetrofit() {
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://unsplash.com/")
//                .build();
//
//        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
//
//        Call<ResponseBody> request = retrofitInterface.downloadImage("photos/YYW9shdLIwo/download?force=true");
//        try {
//
//            downloadImage(request.execute().body());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//
//        }
//    }

    private void downloadImage() throws IOException {
        int count;
        try {
            String title = "";
            URL url = new URL(url_my);
            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/srikrishna/images");

            if (!myDir.exists()) {
                myDir.mkdirs();
            }

            String name = new Date().getTime() + ".jpg";
            myDir = new File(myDir, name);

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file
            OutputStream output = new FileOutputStream(myDir);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                total += count;
                int progress = (int) ((double) (total * 100) / (double) lenghtOfFile);
                updateNotification(progress);
//                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);

            }

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(myDir.getAbsolutePath(), bmOptions);
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, title, title);
            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

//        int count;
//        byte data[] = new byte[1024 * 4];
//        long fileSize = body.contentLength();
//        InputStream inputStream = new BufferedInputStream(body.byteStream(), 1024 * 8);
//        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "journaldev-image-downloaded.jpg");
//        OutputStream outputStream = new FileOutputStream(outputFile);
//        long total = 0;
//        boolean downloadComplete = false;
//        //int totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
//
//        while ((count = inputStream.read(data)) != -1) {
//
//
//
//
//
//            outputStream.write(data, 0, count);
//            downloadComplete = true;
//        }
//        onDownloadComplete(downloadComplete);
//        outputStream.flush();
//        outputStream.close();
//        inputStream.close();

    }

    private void updateNotification(int currentProgress) {

        notificationBuilder.setProgress(100, currentProgress, false);
        notificationBuilder.setContentText("Downloaded: " + currentProgress + "%");
        //Random r = new Random();


        notificationManager.notify(100+NotificationId.getID(), notificationBuilder.build());
    }


    private void sendProgressUpdate(boolean downloadComplete) {

        Intent intent = new Intent("progress_update");
        intent.putExtra("downloadComplete", downloadComplete);
        LocalBroadcastManager.getInstance(BackgroundNotificationService.this).sendBroadcast(intent);
    }

    private void onDownloadComplete(boolean downloadComplete) {
        sendProgressUpdate(downloadComplete);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("Image Download Complete");


        notificationManager.notify(100+NotificationId.getID(), notificationBuilder.build());

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        System.out.println("dasfaksfaskfk.   background Remove");
        notificationManager.cancel(0);
    }




}
