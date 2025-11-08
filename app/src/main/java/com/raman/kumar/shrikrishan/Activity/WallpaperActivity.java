package com.raman.kumar.shrikrishan.Activity;

import static com.raman.kumar.shrikrishan.util.ShimmerHelper.startShimmer;
import static com.raman.kumar.shrikrishan.util.ShimmerHelper.stopShimmer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.modals.comments.whoLikes.Datum;
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
import com.raman.kumar.shrikrishan.Pojo.ImagesData;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.ShareImageFragment;
import com.raman.kumar.shrikrishan.apiNetworking.APICalls;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.util.Comment;
import com.raman.kumar.shrikrishan.util.CommonListeners;
import com.raman.kumar.shrikrishan.util.CustomBottomSheetDialog;
import com.raman.kumar.shrikrishan.util.PrefHelper;
import com.raman.kumar.shrikrishan.util.ShimmerLay;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WallpaperActivity extends AppCompatActivity implements /*WallpaperAdapter.ShareFacebook*/ GalleryAdapter.ShareFacebook, OnGalleryAdapterListener {
    RecyclerView recyclerView;
    public CommonListeners commonListeners;
    private GalleryAdapter mAdapter;
    //    private WallpaperAdapter mAdapter;
    private List<ImagesData> imagesList = new ArrayList<>();
    private List<Comment> commentList = new ArrayList<>();
    ProgressDialog progress;
    ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private ProgressDialog finalProgressDialog;
    private static String TAG = ShareImageFragment.class.getName();
    String backToActivity = "", url, title;
    FirebaseAuth mAuth;
    private PrefHelper prefHelper;

    List<GalleryData> list = new ArrayList<>();

    private int currentPage = 1;
    private final int perPageLimit = 30; // Set how many items per page you want to load
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


    //RecyclerView.LayoutManager mLayoutManager;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        prefHelper = new PrefHelper(this);
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (getIntent().hasExtra("Notification")) {
            Intent intent = new Intent(this, CommentActivity.class);
            intent.putExtra("post_id", getIntent().getStringExtra("post_id"));
            intent.putExtra("post_image", getIntent().getStringExtra("post_image"));
            intent.putExtra("section", "amrit");
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
                        new PhotoFullPopupWindow(WallpaperActivity.this, R.layout.popup_photo_full, getWindow().getDecorView().findViewById(android.R.id.content), url, null);
                    }
                }, 2000);
            }
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Shri Krishan Amrit");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);


        recyclerView.setItemAnimator(new DefaultItemAnimator());
        progress = new ProgressDialog(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
//        // Create a callbackManager to handle the login responses.
        callbackManager = CallbackManager.Factory.create();


        Intent intentData = getIntent();

        if (intentData != null) {
            String from = intentData.getStringExtra("FROM") != null ? intentData.getStringExtra("FROM") : "";
            String postId = intentData.getStringExtra("post_id") != null ? intentData.getStringExtra("post_id") : "";
            String imageUrl = intentData.getStringExtra("image_url") != null ? intentData.getStringExtra("image_url") : "";
            String likedByMeType = intentData.getStringExtra("likedByMeType") != null ? intentData.getStringExtra("likedByMeType") : "";
            String likesByTypes = intentData.getStringExtra("likesByTypes") != null ? intentData.getStringExtra("likesByTypes") : "";
            String totalLikesStr = intentData.getStringExtra("total_likes") != null ? intentData.getStringExtra("total_likes") : "";
            if (Objects.equals(from, "Notification")) {
                GalleryData data = new GalleryData();
                data.setId(Integer.parseInt(postId));
                data.setUrl(imageUrl);
                LikedByMeType type = new LikedByMeType();
                type.setType(likedByMeType);
                data.setLikedByMe((!likedByMeType.isEmpty() && likedByMeType != null) ? true : false);
                if (!likesByTypes.isEmpty() && likesByTypes != null) {
                    List<String> values = new ArrayList();
                    if (likesByTypes.contains("love")) values.add("love");
                    if (likesByTypes.contains("like")) values.add("like");

                    data.setLikesTypes(values);
                }
                data.setLikedByMeType(type);
                data.setLikesCount(Integer.parseInt((totalLikesStr != null && !totalLikesStr.isEmpty()) ? totalLikesStr : "0"));
                FullScreenDialogFragment dialogFragment = new FullScreenDialogFragment(
                        data, prefHelper.getuId(), this
                );
                commonListeners=dialogFragment.getListener();
                dialogFragment.show(getSupportFragmentManager(), "FullScreenDialogFragment");
            }
        }

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
                        currentPage++; // Increment page number
                        getAllImages(); // Load more data
                    }
                }
            }
        });

    }

    public void openLikesPopup(List<Datum> userPostLikesList) {
        final View contentView = View.inflate(WallpaperActivity.this, R.layout.users_likes_list_layout, null);
        CustomBottomSheetDialog variantModelBottomSheet = new CustomBottomSheetDialog(contentView);
        variantModelBottomSheet.show(getSupportFragmentManager(), "BottomSheet Fragment");

        RecyclerView likeRecyclerView = contentView.findViewById(R.id.likeRecyclerView);
        likeRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        likeRecyclerView.setHasFixedSize(true);
        LikesAdapter likesAdapter = new LikesAdapter(getApplicationContext(), userPostLikesList);
        likeRecyclerView.setAdapter(likesAdapter);
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
                        new PhotoFullPopupWindow(WallpaperActivity.this, R.layout.popup_photo_full, getWindow().getDecorView().findViewById(android.R.id.content), url, null);
                    }
                }, 2000);
            }
        }


        if (intent.hasExtra("Notification")) {
            Intent intentclass = new Intent(this, CommentActivity.class);
            intentclass.putExtra("post_id", intent.getStringExtra("post_id"));
            intentclass.putExtra("post_image", intent.getStringExtra("post_image"));
            intentclass.putExtra("section", "amrit");
            startActivity(intentclass);

        }


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }

//    public void getAllImages() {
//        showProgressDialog();
//
//        Call<GalleryModal> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .getAmrit(Extensions.getBearerToken());
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
//                            @Override
//                            public int compare(GalleryData lhs, GalleryData rhs) {
//                                try {
//                                    return getTimeStamp(rhs.getUpdatedAt()).compareTo(getTimeStamp(lhs.getUpdatedAt()));
//                                } catch (Exception e) {
//                                    throw new IllegalArgumentException(e);
//                                }
//                            }
//                        });
//
//                mAdapter = new GalleryAdapter(list, WallpaperActivity.this, url, "wallpaper");
//
//                recyclerView.setAdapter(mAdapter);
//                        progress.dismiss();
//
//                    } else {
//                        Toast.makeText(WallpaperActivity.this, imagesResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else
//                {
//                    progress.dismiss();
//                    Toast.makeText(WallpaperActivity.this, response.message(), Toast.LENGTH_SHORT).show();
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
//    }


    public void getAllImages() {
        if (isLoading) return; // Prevent multiple simultaneous requests

        isLoading = true;

        // Show progress dialog only for the first page
        if (currentPage == 1) {
            startShimmer(WallpaperActivity.this, ShimmerLay.POSTLAY);
        }

        // API call with page and limit parameters
        Call<GalleryModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .getAmrit(Extensions.getBearerToken(), currentPage, perPageLimit);

        call.enqueue(new Callback<GalleryModal>() {
            @Override
            public void onResponse(Call<GalleryModal> call, Response<GalleryModal> response) {
                isLoading = false;

                // Dismiss progress dialog for the first page
                if (currentPage == 1) {
                    stopShimmer(WallpaperActivity.this, ShimmerLay.POSTLAY);
                }

                if (response.isSuccessful() && response.body() != null) {
                    GalleryModal imagesResponse = response.body();

                    if (imagesResponse.getStatus()) {
                        if (currentPage == 1) {
                            list.clear(); // Clear the list for the first page
                        }

                        list.addAll(imagesResponse.getGalleryData());

                        // Sort the list by updated time if needed
                        Collections.sort(list, new Comparator<GalleryData>() {
                            @Override
                            public int compare(GalleryData lhs, GalleryData rhs) {
                                try {
                                    return getTimeStamp(rhs.getUpdatedAt()).compareTo(getTimeStamp(lhs.getUpdatedAt()));
                                } catch (Exception e) {
                                    throw new IllegalArgumentException(e);
                                }
                            }
                        });

                        // Update the adapter if it's the first time, otherwise notify data change
                        if (mAdapter == null) {
                            mAdapter = new GalleryAdapter(list, WallpaperActivity.this, url, "wallpaper", WallpaperActivity.this);
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(WallpaperActivity.this, imagesResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WallpaperActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GalleryModal> call, Throwable t) {
                isLoading = false;
                if (currentPage == 1) {
                    stopShimmer(WallpaperActivity.this, ShimmerLay.POSTLAY);
                }
                Toast.makeText(getApplicationContext(), "Failed to load!", Toast.LENGTH_LONG).show();
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


    public void showProgressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    private FacebookCallback<Sharer.Result> callback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {
            Log.v(TAG, "Successfully posted");
            // Write some code to do some operations when you shared content successfully.
        }

        @Override
        public void onCancel() {
            Log.v(TAG, "Sharing cancelled");
            // Write some code to do some operations when you cancel sharing content.
        }

        @Override
        public void onError(FacebookException error) {
            Log.v(TAG, error.getMessage());
            // Write some code to do some operations when some error occurs while sharing content.
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Call callbackManager.onActivityResult to pass login result to the LoginManager via callbackManager.
        callbackManager.onActivityResult(requestCode, resultCode, data);
        try {
            if (commonListeners != null) {
                commonListeners.onActivityResults(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            Log.d("Exception",e.getLocalizedMessage());
        }
    }


    @Override
    public void shareImage(String image, String title, ProgressDialog newProgressDialog) {

        Bundle bundle = new Bundle();
        bundle.putString("image", image);
        bundle.putString("title", title);

        Fragment fragment = new ShareImageFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.wallcontainer, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
        // newProgressDialog.dismiss();
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
    protected void onResume() {
        super.onResume();
        if (finalProgressDialog != null) {
            finalProgressDialog.dismiss();
            IntentFilter filter = new IntentFilter("com.yourapp.REFRESH_SECRET");
            LocalBroadcastManager.getInstance(this).registerReceiver(refreshReceiver, filter);
        }
        if(mAdapter!=null) {
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(refreshReceiver);
    }


    @Override
    public void dismissProgressDialog(ProgressDialog progressDialog) {
        progressDialog.dismiss();
    }

    @Override
    public ProgressDialog showDialoadingProgress() {

        return null;
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
        APICalls.peopleWhoLikes(prefHelper, post_id, "wallpaper", this);
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
            try {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pDialog.dismiss();
                    }
                }, 2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //pDialog.dismiss();
            // Displaying downloaded image into image view
            // Reading image path from sdcard
            //  String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
            // setting downloaded into image view
            // my_image.setImageDrawable(Drawable.createFromPath(imagePath));
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
