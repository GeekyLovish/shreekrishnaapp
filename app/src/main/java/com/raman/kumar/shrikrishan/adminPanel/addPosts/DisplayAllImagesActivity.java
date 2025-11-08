package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Gallery;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.modals.gallary.getGallary.GalleryData;
import com.raman.kumar.modals.gallary.getGallary.GalleryModal;
import com.raman.kumar.modals.pictureByParts.getPictureByPart.Datum;
import com.raman.kumar.shrikrishan.MainActivity;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.loginfiles.LoginActivity;
import com.raman.kumar.shrikrishan.model.GetImagesResponse;
import com.raman.kumar.shrikrishan.model.LoginResponse;
import com.raman.kumar.shrikrishan.model.PostImageResponse;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisplayAllImagesActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    // Folder path for Firebase Storage.
    String Storage_Path = "All_Image_Uploads/";

    // Root Database Name for Firebase Database.
    public static final String Database_Path = "All_Image_Uploads_Database";

    // Creating URI.
    Uri FilePathUri;

    AllImagesAdapter allImagesAdapter;
    // Image request code for onActivityResult() .
    int Image_Request_Code = 7;

    ProgressDialog progressDialog;
    List<GalleryData> list = new ArrayList<>();

    private int currentPage = 1;
    private final int perPageLimit = 200;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_all_images);
        initView();



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0
                                && totalItemCount >= perPageLimit) {
                            currentPage++;
                            getAllImages();
                        }
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllImages();
    }

//    private void getAllImages() {
//        progressDialog = new ProgressDialog(DisplayAllImagesActivity.this);
//        progressDialog.setMessage("Loading Images...");
//        progressDialog.show();
//        list.clear();
//        Call<GalleryModal> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .getAllAmritGalleryImages(currentPage,perPageLimit);
//
//        call.enqueue(new Callback<GalleryModal>() {
//            @Override
//            public void onResponse(Call<GalleryModal> call, Response<GalleryModal> response) {
//                GalleryModal imagesResponse = response.body();
//                if (response.isSuccessful()) {
//                    progressDialog.dismiss();
//                    if (imagesResponse.getStatus()) {
//                        list.clear();
//                        list.addAll(imagesResponse.getGalleryData());
//                        if (!list.isEmpty()){
//                Collections.sort(list, new Comparator<GalleryData>() {
////                    DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    @Override
//                    public int compare(GalleryData lhs, GalleryData rhs) {
//                        try {
//                            return getTimeStamp(rhs.getCreatedAt()).compareTo(getTimeStamp(lhs.getCreatedAt()));
////                            return f.parse(lhs.getCreatedAt()).compareTo(f.parse(rhs.getCreatedAt()));
//                        } catch (Exception e) {
//                            throw new IllegalArgumentException(e);
//                        }
//                    }
//                });
//                    allImagesAdapter = new AllImagesAdapter(DisplayAllImagesActivity.this, list);
//                    recyclerView.setAdapter(allImagesAdapter);
//                }
//                progressDialog.dismiss();
//
//                    } else {
//                        Toast.makeText(DisplayAllImagesActivity.this, imagesResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else
//                {
//                    progressDialog.dismiss();
//                    Toast.makeText(DisplayAllImagesActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GalleryModal> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Failed to load!", Toast.LENGTH_LONG).show();
//                Log.d("error","Message:"+t.getMessage());
//                progressDialog.dismiss();
//            }
//        });
//
//    }


private void getAllImages() {
    if (isLoading || isLastPage) return;

    isLoading = true;
    if (currentPage == 1) {
        progressDialog = new ProgressDialog(DisplayAllImagesActivity.this);
        progressDialog.setMessage("Loading Images...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    Call<GalleryModal> call = RetrofitClient
            .getInstance()
            .getApi()
            .getAllAmritGalleryImages(currentPage, perPageLimit);

    call.enqueue(new Callback<GalleryModal>() {
        @Override
        public void onResponse(Call<GalleryModal> call, Response<GalleryModal> response) {
            isLoading = false;
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (response.isSuccessful() && response.body() != null) {
                GalleryModal imagesResponse = response.body();

                if (imagesResponse.getStatus()) {
                    List<GalleryData> newItems = imagesResponse.getGalleryData();

                    if (newItems != null && !newItems.isEmpty()) {
                        // Sort newItems
                        Collections.sort(newItems, new Comparator<GalleryData>() {
                            @Override
                            public int compare(GalleryData lhs, GalleryData rhs) {
                                try {
                                    return getTimeStamp(rhs.getCreatedAt()).compareTo(getTimeStamp(lhs.getCreatedAt()));
                                } catch (Exception e) {
                                    throw new IllegalArgumentException(e);
                                }
                            }
                        });

                        int start = list.size();
                        list.addAll(newItems);

                        if (allImagesAdapter == null) {
                            allImagesAdapter = new AllImagesAdapter(DisplayAllImagesActivity.this, list);
                            recyclerView.setAdapter(allImagesAdapter);
                        } else {
                            allImagesAdapter.notifyItemRangeInserted(start, newItems.size());
                        }

                        // If fewer items than expected are returned, it's the last page
                        if (newItems.size() < perPageLimit) {
                            isLastPage = true;
                        }

                    } else {
                        isLastPage = true;
                    }
                } else {
                    Toast.makeText(DisplayAllImagesActivity.this, imagesResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(DisplayAllImagesActivity.this, "Response error: " + response.message(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<GalleryModal> call, Throwable t) {
            isLoading = false;
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(getApplicationContext(), "Failed to load: " + t.getMessage(), Toast.LENGTH_LONG).show();
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
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return 0L;
        } else
            return 0L;

    }


    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

// Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(new LinearLayoutManager(DisplayAllImagesActivity.this));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
}
