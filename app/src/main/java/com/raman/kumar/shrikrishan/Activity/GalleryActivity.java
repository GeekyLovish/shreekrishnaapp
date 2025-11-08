package com.raman.kumar.shrikrishan.Activity;

import static com.raman.kumar.shrikrishan.util.ShimmerHelper.startShimmer;
import static com.raman.kumar.shrikrishan.util.ShimmerHelper.stopShimmer;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.modals.gallary.getGallary.GalleryData;
import com.raman.kumar.modals.gallary.getGallary.GalleryModal;
import com.raman.kumar.modals.gallary.getGallary.LikedByMeType;
import com.raman.kumar.shrikrishan.Adapter.GalleryAdapter;
import com.raman.kumar.shrikrishan.Adapter.LikesAdapter;
import com.raman.kumar.shrikrishan.CommentFiles.CommentActivity;
import com.raman.kumar.shrikrishan.FullScreenDialogFragment;
import com.raman.kumar.shrikrishan.MainActivity;
import com.raman.kumar.shrikrishan.OnGalleryAdapterListener;
import com.raman.kumar.shrikrishan.PhotoFullPopupWindow;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.ShareImageFragment;
import com.raman.kumar.shrikrishan.apiNetworking.APICalls;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.util.CommonListeners;
import com.raman.kumar.shrikrishan.util.CustomBottomSheetDialog;
import com.raman.kumar.shrikrishan.util.PrefHelper;
import com.raman.kumar.shrikrishan.util.ShimmerLay;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GalleryActivity extends AppCompatActivity implements GalleryAdapter.ShareFacebook, WallpaperAdapter.ShareFacebook, OnGalleryAdapterListener {
    RecyclerView recyclerView;

    public CommonListeners commonListeners;
    private GalleryAdapter mAdapter;
    private List<String> urlImage = new ArrayList<>();
    public ProgressDialog progress;
    public ProgressDialog finalProgressDialog;
    String backToActivity = "", url, title;
    RelativeLayout wallcontainer;
    PrefHelper prefHelper;
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyManager;
    int id = 1;
    int counter = 0;
    List<GalleryData> list = new ArrayList<>();
    ArrayList<AsyncTask<String, String, Void>> arr;
    private int currentPage = 1;
    private final int perPageLimit = 40;
    private boolean isLoading = false;


    private BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.yourapp.REFRESH_SECRET".equals(intent.getAction())) {
                // Refresh the secret or update UI here
                currentPage = 1;
                getAllImages();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("com.yourapp.REFRESH_SECRET");
        LocalBroadcastManager.getInstance(this).registerReceiver(refreshReceiver, filter);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(refreshReceiver);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        Intent intentData = getIntent();
        prefHelper = new PrefHelper(this);
        wallcontainer = findViewById(R.id.wallcontainer);
        if (getIntent().hasExtra("Notification")) {
            Intent intent = new Intent(this, CommentActivity.class);
            intent.putExtra("post_id", getIntent().getStringExtra("post_id"));
            intent.putExtra("post_image", getIntent().getStringExtra("post_image"));
            intent.putExtra("section", "gallery");
            startActivity(intent);
        }

        if (getIntent().hasExtra("backToActivity")) {
            url = getIntent().getStringExtra("url");
            title = getIntent().getStringExtra("title");
            backToActivity = getIntent().getStringExtra("backToActivity");
            if (!url.equals("")) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new PhotoFullPopupWindow(GalleryActivity.this, R.layout.popup_photo_full, getWindow().getDecorView().findViewById(android.R.id.content), url, null);
                    }
                }, 2000);
            }
        }


        if (intentData != null) {
            String from = intentData.getStringExtra("FROM") != null ? intentData.getStringExtra("FROM") : "";
            String postId = intentData.getStringExtra("post_id") != null ? intentData.getStringExtra("post_id") : "";
            String imageUrl = intentData.getStringExtra("image_url") != null ? intentData.getStringExtra("image_url") : "";
            String likedByMeType = intentData.getStringExtra("likedByMeType") != null ? intentData.getStringExtra("likedByMeType") : "";
            String likesByTypes = intentData.getStringExtra("likesByTypes") != null ? intentData.getStringExtra("likesByTypes") : "";
            String totalLikesStr = intentData.getStringExtra("total_likes") != null ? intentData.getStringExtra("total_likes") : "";
            if (Objects.equals(from, "Notification")) {
//                 Use the string as needed
                GalleryData data = new GalleryData();
                data.setId(Integer.parseInt(postId));
                data.setUrl(imageUrl);
                LikedByMeType type = new LikedByMeType();
                type.setType(likedByMeType);
                data.setLikedByMeType(type);
                data.setLikedByMe((!likedByMeType.isEmpty() && likedByMeType != null) ? true : false);
                if (!likesByTypes.isEmpty() && likesByTypes != null) {
                    List<String> values = new ArrayList();
                    if (likesByTypes.contains("love")) values.add("love");
                    if (likesByTypes.contains("like")) values.add("like");

                    data.setLikesTypes(values);
                }
                data.setLikesCount(Integer.parseInt((totalLikesStr != null && !totalLikesStr.isEmpty()) ? totalLikesStr : "0"));
                FullScreenDialogFragment dialogFragment = new FullScreenDialogFragment(
                        data, prefHelper.getuId(), this
                );
                commonListeners=dialogFragment.getListener();
                dialogFragment.show(getSupportFragmentManager(), "FullScreenDialogFragment");
            }
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Gallery");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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
                        currentPage++;
                        getAllImages();
                    }
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }

//    public void getAllImages(final GalleryAdapter.ShareFacebook shareFacebook) {
//        showProgressDialog();
//
//        Call<GalleryModal> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .getGallery(Extensions.getBearerToken());
//
//        call.enqueue(new Callback<GalleryModal>() {
//            @Override
//            public void onResponse(Call<GalleryModal> call, Response<GalleryModal> response) {
//                GalleryModal imagesResponse = response.body();
//                if (response.isSuccessful()) {
//                    progress.dismiss();
//                    if (imagesResponse.getStatus()) {
//                        list.clear();
//                        list.addAll(imagesResponse.getGalleryData());
//
//                        Collections.sort(list, new Comparator<GalleryData>() {
//                            //                    DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            @Override
//                            public int compare(GalleryData lhs, GalleryData rhs) {
//                                try {
//                                    return getTimeStamp(rhs.getUpdatedAt()).compareTo(getTimeStamp(lhs.getUpdatedAt()));
////                            return f.parse(lhs.getCreatedAt()).compareTo(f.parse(rhs.getCreatedAt()));
//                                } catch (Exception e) {
//                                    throw new IllegalArgumentException(e);
//                                }
//                            }
//                        });
//
////                Collections.reverse(list);
//                        Log.e("list",list.get(0).getCreatedAt());
//                        mAdapter = new GalleryAdapter(list, GalleryActivity.this, url, "gallery");
//                        mAdapter.setImageListner(shareFacebook);
//                        recyclerView.setAdapter(mAdapter);
//                        progress.dismiss();
//
//                    } else {
//                        Toast.makeText(GalleryActivity.this, imagesResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else
//                {
//                    progress.dismiss();
//                    Toast.makeText(GalleryActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GalleryModal> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Failed to load!", Toast.LENGTH_LONG).show();
//                Log.d("error","Message:"+t.getMessage());
//                progress.dismiss();
//            }
//        });
//
//    }


    public void getAllImages() {
        if (isLoading) return;

        isLoading = true;

        // Show progress dialog only for the first page
        if (currentPage == 1) {
            startShimmer(GalleryActivity.this, ShimmerLay.POSTLAY);
        }

        Call<GalleryModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .getGallery(Extensions.getBearerToken(), currentPage, perPageLimit);

        call.enqueue(new Callback<GalleryModal>() {
            @Override
            public void onResponse(Call<GalleryModal> call, Response<GalleryModal> response) {
                isLoading = false;
                if (currentPage == 1) {
                    stopShimmer(GalleryActivity.this, ShimmerLay.POSTLAY);
                }

                if (response.isSuccessful() && response.body() != null) {
                    GalleryModal imagesResponse = response.body();
                    if (imagesResponse.getStatus()) {
                        if (currentPage == 1) {
                            list.clear();
                        }
                        list.addAll(imagesResponse.getGalleryData());

                        Collections.sort(list, (lhs, rhs) -> {
                            try {
                                return getTimeStamp(rhs.getUpdatedAt()).compareTo(getTimeStamp(lhs.getUpdatedAt()));
                            } catch (Exception e) {
                                throw new IllegalArgumentException(e);
                            }
                        });

                        if (mAdapter == null) {
                            mAdapter = new GalleryAdapter(list, GalleryActivity.this, url, "gallery", GalleryActivity.this);
//                            mAdapter.setImageListner(shareFacebook);
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }

                    } else {
                        Toast.makeText(GalleryActivity.this, imagesResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(GalleryActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GalleryModal> call, Throwable t) {
                isLoading = false;
                if (currentPage == 1) {
                    stopShimmer(GalleryActivity.this, ShimmerLay.POSTLAY);
                }
                Toast.makeText(getApplicationContext(), "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error", "Message:" + t.getMessage());
            }
        });
    }

    Long getTimeStamp(String date) {
//        2020-12-31 21:17:56
        if (!date.isEmpty()) {
            DateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate;
            try {
                newDate = spf.parse(date);
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(newDate.getTime());
                return cal.getTimeInMillis();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0L;
        } else
            return 0L;

    }

    public void openLikesPopup(List<com.raman.kumar.modals.comments.whoLikes.Datum> userPostLikesList) {


        final View contentView = View.inflate(GalleryActivity.this, R.layout.users_likes_list_layout, null);
        CustomBottomSheetDialog variantModelBottomSheet = new CustomBottomSheetDialog(contentView);
        variantModelBottomSheet.show(getSupportFragmentManager(), "BottomSheet Fragment");

        RecyclerView likeRecyclerView = contentView.findViewById(R.id.likeRecyclerView);
        likeRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        likeRecyclerView.setHasFixedSize(true);
        LikesAdapter likesAdapter = new LikesAdapter(getApplicationContext(), userPostLikesList);
        likeRecyclerView.setAdapter(likesAdapter);
    }


    public void showProgressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    @Override
    public void shareImage(String image, String title, ProgressDialog dialog) {
        Bundle bundle = new Bundle();
        bundle.putString("image", image);
        bundle.putString("title", title);
        Fragment fragment = new ShareImageFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.wallcontainer, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
    }

    @Override
    public ProgressDialog showProgress() {
        ProgressDialog dialog = new ProgressDialog(this);
        //  progressDialog.setTitle("Loading");
        dialog.setMessage("Redirecting to facebook.");
        // progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
        dialog.show();
        finalProgressDialog = dialog;
        return dialog;
    }

    @Override
    public void dismissProgressDialog(ProgressDialog progressDialog) {
    }

    @Override
    public ProgressDialog showDialoadingProgress() {

        return null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("backToActivity")) {
            url = intent.getStringExtra("url");
            title = getIntent().getStringExtra("title");
            backToActivity = intent.getStringExtra("backToActivity");
            if (!url.equals("")) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new PhotoFullPopupWindow(GalleryActivity.this, R.layout.popup_photo_full, getWindow().getDecorView().findViewById(android.R.id.content), url, null);
                    }
                }, 2000);
            }
        }


        if (intent.hasExtra("Notification")) {
            Intent intentclass = new Intent(this, CommentActivity.class);
            intentclass.putExtra("post_id", intent.getStringExtra("post_id"));
            intentclass.putExtra("post_image", intent.getStringExtra("post_image"));
            intentclass.putExtra("section", "gallery");
            startActivity(intentclass);

        }


    }

    @Override
    public void downloadImageFromURL(String image, String title) {
        new DownloadFileFromURL().execute(image, title);
    }

    @Override
    public void onRefresh(GalleryData data) {
        int position = findPositionById(list, data.getId());
        if (position > -1) {
            list.get(position).setLikesCount(data.getLikesCount());
            list.get(position).setLikedByMe(data.getLikedByMe());
            list.get(position).setLikedByMeType(data.getLikedByMeType());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void openLikesPopup(String post_id) {
        APICalls.peopleWhoLikes(prefHelper, post_id, "gallery", this);
    }

    int findPositionById(List<GalleryData> list, int id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id) {
                return i; // return position
            }
        }
        return -1; // not found
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        public static final int progress_bar_type = 0;
        private ProgressDialog pDialog;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
            pDialog = showDownloadingProgress(pDialog);
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                String title = f_url[1];
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();
                String root = "";//Environment.getExternalStorageDirectory().toString().replace(":",".");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                    root = getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/";
                } else {
                    root = Environment.getExternalStorageDirectory().toString() + "/";
                }

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
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

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

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String myDir) {
            // dismiss the dialog after the file was downloaded
            //dismissDialog(progress_bar_type);
            // File file=new File(myDir);

            pDialog.setProgress(100);
            pDialog.setMessage("Download Completed");
            pDialog.setCancelable(true);
            //pDialog.dismiss();
            // Displaying downloaded image into image view
            // Reading image path from sdcard
            //  String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
            // setting downloaded into image view
            // my_image.setImageDrawable(Drawable.createFromPath(imagePath));

            try {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pDialog.dismiss();
                    }
                }, 2000);
                Toast.makeText(GalleryActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public ProgressDialog showDownloadingProgress(ProgressDialog dialog) {
        dialog = new ProgressDialog(this);
        //  progressDialog.setTitle("Loading");
        dialog.setMessage("Downloading..");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        // progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
        dialog.show();
        return dialog;
    }

    private void downloadImagesToSdCard(String downloadUrl, String imageName) {
        FileOutputStream fos;
        InputStream inputStream = null;

        try {
            URL url = new URL(downloadUrl);
            /* making a directory in sdcard */
            String sdCard = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(sdCard + "/srikrishna/images");

            /* if specified not exist create new */
            if (!myDir.exists()) {
                myDir.mkdir();
                Log.v("", "inside mkdir");
            }

            /* checks the file and if it already exist delete */
            String fname = imageName;
            File file = new File(myDir, fname);
            Log.d("file===========path", "" + file);
            if (file.exists())
                file.delete();

            /* Open a connection */
            URLConnection ucon = url.openConnection();

            HttpURLConnection httpConn = (HttpURLConnection) ucon;
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            inputStream = httpConn.getInputStream();

            /*
             * if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
             * inputStream = httpConn.getInputStream(); }
             */

            fos = new FileOutputStream(file);
            // int totalSize = httpConn.getContentLength();
            // int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, bufferLength);
                // downloadedSize += bufferLength;
                // Log.i("Progress:", "downloadedSize:" + downloadedSize +
                // "totalSize:" + totalSize);
            }
            inputStream.close();
            fos.close();
            Log.d("test", "Image Saved in sdcard..");
        } catch (IOException io) {
            inputStream = null;
            fos = null;
            io.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

    private class ImageDownloader extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... param) {
            downloadImagesToSdCard(param[0], "Image" + counter + ".png");
            return null;
        }

        protected void onProgressUpdate(String... values) {
        }

        @Override
        protected void onPreExecute() {
            Log.i("Async-Example", "onPreExecute Called");
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i("Async-Example", "onPostExecute Called");

            float len = urlImage.size();
            // When the loop is finished, updates the notification
            if (counter >= len - 1) {
                mBuilder.setContentTitle("Done.");
                mBuilder.setContentText("Download complete")
                        // Removes the progress bar
                        .setProgress(0, 0, false);
                mNotifyManager.notify(id, mBuilder.build());
                Toast.makeText(GalleryActivity.this, "Downloaded Successfully!", Toast.LENGTH_SHORT).show();
            } else {
                int per = (int) (((counter + 1) / len) * 100f);
                Log.i("Counter", "Counter : " + counter + ", per : " + per);
                mBuilder.setContentText("Downloaded (" + per + "/100");
                mBuilder.setProgress(100, per, false);
                // Displays the progress bar for the first time.
                mNotifyManager.notify(id, mBuilder.build());
            }
            counter++;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (commonListeners != null) {
                commonListeners.onActivityResults(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            Log.d("Exception",e.getLocalizedMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (commonListeners != null) {
                commonListeners.onRequestPermissionResult(requestCode, permissions, grantResults);
            }
        } catch (Exception e) {
        }
    }
}

