package com.raman.kumar.shrikrishan;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by Dell- on 8/30/2018.
 */

public class Music extends Service implements MediaPlayer.OnCompletionListener {
    MediaPlayer mediaPlayer=new MediaPlayer();
    boolean isPrepared = false;

    //// TEstes de servico
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
     final String fileDescriptor = bundle.getString("ServiceFileDescriptor");


        Thread t = new Thread(){
            public void run(){
                // NOTE: The next line will vary depending on the data type for the file
                // descriptor. I'm assuming that it's an int.

                try {
                    if(!mediaPlayer.isPlaying()){
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(fileDescriptor);
                        mediaPlayer.prepare();
                        isPrepared = true;
                        mediaPlayer.start();
                       // mediaPlayer.setOnCompletionListener(this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();


        return Service.START_NOT_STICKY;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        info(" started");
    }
    @Override
    public void onDestroy() {
        stop();
        info(" stopped");
    }
    @Override
    public void onStart(Intent intent, int startid) {
        info("Servico started!");
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public void info(String txt) {
        Toast toast = Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG);
        toast.show();
    }
    //// Fim testes de servico

    public Music(FileDescriptor fileDescriptor){
        mediaPlayer = new MediaPlayer();
        try{
            mediaPlayer.setDataSource(fileDescriptor);
            mediaPlayer.prepare();
            isPrepared = true;
            mediaPlayer.setOnCompletionListener(this);
        } catch(Exception ex){
            throw new RuntimeException("Couldn't load music, uh oh!");
        }
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        synchronized(this){
            isPrepared = false;
        }
    }
    public Music() {
      //  super("Music");
    }
    public void play() {
        if(mediaPlayer.isPlaying()) return;
        try{
            synchronized(this){
                if(!isPrepared){
                    mediaPlayer.prepare();
                }
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
            }
        } catch(IllegalStateException ex){
            ex.printStackTrace();
        } catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void stop() {
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            synchronized (this) {
                isPrepared = false;
            }
        }
    }

    public void switchTracks(){
        mediaPlayer.seekTo(0);
        mediaPlayer.pause();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public boolean isLooping() {
        return mediaPlayer.isLooping();
    }

    public void setLooping(boolean isLooping) {
        mediaPlayer.setLooping(isLooping);
    }

    public void setVolume(float volumeLeft, float volumeRight) {
        mediaPlayer.setVolume(volumeLeft, volumeRight);
    }

    public String getDuration() {
        return String.valueOf((int)(mediaPlayer.getDuration()/1000));
    }
    public void dispose() {
        if(mediaPlayer.isPlaying()){
            stop();
        }
        mediaPlayer.release();
    }
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                int time = Integer.parseInt(params[0])*1000;

                Thread.sleep(time);
                resp = "Slept for " + params[0] + " seconds";
            } catch (InterruptedException e) {
                e.printStackTrace();
                resp = e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
         //   finalResult.setText(result);
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getApplicationContext(),
                    "ProgressDialog",
                    "Loading audio");
        }


        @Override
        protected void onProgressUpdate(String... text) {
          //  finalResult.setText(text[0]);

        }
    }


}