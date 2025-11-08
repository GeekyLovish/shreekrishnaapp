package com.raman.kumar.shrikrishan.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.raman.kumar.modals.pictureByParts.pictureByPart.Datum;
import com.raman.kumar.modals.pictureByParts.pictureByPart.PictureByPartModal;
import com.raman.kumar.shrikrishan.Adapter.GalleryAdapter1;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.ShareImageFragment;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryActivity1 extends AppCompatActivity implements GalleryAdapter1.ShareFacebook {

    private RecyclerView recyclerView;
    private GalleryAdapter1 mAdapter;
    private List<Datum> imagesList = new ArrayList<>();
    private ProgressDialog progress;
    private String id;
    private ProgressDialog finalProgressDialog;
    private int currentPage = 1;
    private final int perPageLimit = 30; // Items per page
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (getIntent().getExtras() != null) {
            id = getIntent().getStringExtra("id");
        }

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        recyclerView = findViewById(R.id.recycler_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(getIntent().getStringExtra("title"));

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        progress = new ProgressDialog(this);
        getAllImages();



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        currentPage++; // Increment the page number
                        getAllImages(); // Load next page of data
                    }
                }
            }
        });


    }



    public void getAllImages() {
        if (isLoading) return;

        isLoading = true;

        // Show progress dialog only for the first page
        if (currentPage == 1) {
            showProgressDialog();
        }

        // Pass the current page and limit to the API
        Call<PictureByPartModal> call = RetrofitClient.getInstance()
                .getApi()
                .getBySection(id, currentPage, perPageLimit);

        call.enqueue(new Callback<PictureByPartModal>() {
            @Override
            public void onResponse(Call<PictureByPartModal> call, Response<PictureByPartModal> response) {
                isLoading = false;

                // Dismiss progress dialog for the first page
                if (currentPage == 1) {
                    progress.dismiss();
                }

                if (response.isSuccessful() && response.body() != null) {
                    PictureByPartModal imagesResponse = response.body();

                    if (imagesResponse.getStatus()) {
                        if (currentPage == 1) {
                            imagesList.clear(); // Clear the list for the first page
                        }

                        imagesList.addAll(imagesResponse.getData());

                        if (mAdapter == null) {
                            mAdapter = new GalleryAdapter1(imagesList, GalleryActivity1.this, getSupportFragmentManager().beginTransaction());
                            mAdapter.setImageListener(GalleryActivity1.this);
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(GalleryActivity1.this, imagesResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(GalleryActivity1.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PictureByPartModal> call, Throwable t) {
                isLoading = false;
                if (currentPage == 1) {
                    progress.dismiss();
                }
                Toast.makeText(GalleryActivity1.this, "Failed to load!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showProgressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    public void shareImage(String image, String title, ProgressDialog dialog) {
        if (image != null && !image.isEmpty()) {
            // Remove progress dialog logic temporarily to see if it helps with sharing
            Bundle bundle = new Bundle();
            bundle.putString("image", image);
            bundle.putString("title", title);

            ShareImageFragment shareFragment = new ShareImageFragment();
            shareFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.wallcontainer, shareFragment, ShareImageFragment.class.getSimpleName())
                    .addToBackStack(null)
                    .commit();
        } else {
            Toast.makeText(this, "Invalid image URL", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public ProgressDialog showProgress() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Redirecting to Facebook.");
        dialog.show();
        finalProgressDialog = dialog;
        return dialog;
    }

    @Override
    public void dismissProgressDialog(ProgressDialog progressDialog) {
        // Handle any logic to dismiss progress if necessary
    }

    @Override
    public void downloadImageFromURL(String image, String title) {
        new DownloadFileFromURL().execute(image, title);
    }

    private class DownloadFileFromURL extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(GalleryActivity1.this);
            pDialog.setMessage("Downloading...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                int fileLength = connection.getContentLength();

                String root = Environment.getExternalStorageDirectory().toString();
                File directory = new File(root + "/srikrishna/images");

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String fileName = new Date().getTime() + ".jpg";
                File file = new File(directory, fileName);

                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream(file);

                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / fileLength));
                    output.write(data, 0, count);
                }

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, f_url[1], f_url[1]);

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("Download Error", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            Toast.makeText(GalleryActivity1.this, "Download Completed", Toast.LENGTH_SHORT).show();
        }
    }
}
