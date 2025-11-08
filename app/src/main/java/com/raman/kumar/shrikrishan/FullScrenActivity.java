package com.raman.kumar.shrikrishan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.raman.kumar.shrikrishan.util.PrefHelper;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;

public class FullScrenActivity extends AppCompatActivity {
    boolean wallpaper = false;

    String backToActivity = "", url;

    PrefHelper prefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_scren);
        Button button = (Button) findViewById(R.id.setAsWallpaper);
        ImageView bgImage = (ImageView) findViewById(R.id.backgroundImage);
        setTitle("Set Wallpaper");

        prefHelper = new PrefHelper(getApplicationContext());

        if (getIntent().hasExtra("url")) {
            url = getIntent().getStringExtra("url");
        }



        Picasso.get()
                .load(url)
                .into(bgImage);





        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!wallpaper) {
                    //  setWallpaper(url);
                    new Player()
                            .execute(url);
                    Toast.makeText(getApplicationContext(), "Wallpaper set",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Wallpaper already set",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
//    private void showDownloadDialog(Context context, String imageUrl) {
//        new AlertDialog.Builder(context)
//                .setTitle("Download Image")
//                .setMessage("Do you want to download this image?")
//                .setPositiveButton("Download", (dialog, which) -> {
//                    ImageDownloadManager.downloadImage(context, imageUrl);
//                })
//                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
//                .show();
//    }



    class Player extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progress;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            Boolean prepared = false;
            try {

                Bitmap bitmap = null;
                try {
                    URL url = new URL(params[0]);
                    bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    System.out.println(e);
                }
                WallpaperManager myWallpaperManager
                        = WallpaperManager.getInstance(getApplication());

                try {
                    myWallpaperManager.setBitmap(bitmap);
                    wallpaper = true;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                prepared = true;
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                Log.d("IllegarArgument", e.getMessage());

                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            //     if (mProgressDialog.isShowing()) {
            //         mProgressDialog.cancel();
            //     }

        }

//        public Player() {
//            mProgressDialog = new ProgressDialog(AudioActivity.this);
//        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            //  this.mProgressDialog.setMessage("Buffering...");
            //  this.mProgressDialog.show();

        }
    }

    @Override
    public void onBackPressed() {

        if (backToActivity != null && !backToActivity.isEmpty()) {
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        } else {
            super.onBackPressed();
        }

    }
}
