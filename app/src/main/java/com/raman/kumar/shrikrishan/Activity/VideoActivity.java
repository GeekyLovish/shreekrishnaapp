package com.raman.kumar.shrikrishan.Activity;

import static com.raman.kumar.shrikrishan.util.ShimmerHelper.startShimmer;
import static com.raman.kumar.shrikrishan.util.ShimmerHelper.stopShimmer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.raman.kumar.modals.video.GetVideoModal.Datum;
import com.raman.kumar.modals.video.GetVideoModal.GetVideoModal;
import com.raman.kumar.shrikrishan.Adapter.VideoAdapter;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.util.ShimmerLay;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dell- on 8/4/2018.
 * <p>
 * <p>
 * Code Modification and Fixing by Sarangal on 12 JAN 2020
 */

public class VideoActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressDialog progress;
    private VideoAdapter mAdapter;
    private List<Datum> videoList = new ArrayList<>();
    String backToActivity = "", url_notification = "-" + "";
    AdView adView;
    private int currentPage = 1;
    private final int perPageLimit = 20; // Adjust the number of items per page as needed
    private boolean isLoading = false;



    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity_lay);

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        if (getIntent().hasExtra("backToActivity")) {
            url_notification = getIntent().getStringExtra("url");
            backToActivity = getIntent().getStringExtra("backToActivity");

        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Videos");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        progress = new ProgressDialog(this);

        getVideoLinks();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // Check if the user has reached the bottom
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        currentPage++; // Increment the page number
                        getVideoLinks(); // Load more videos
                    }
                }
            }
        });



    }

//    private void getVideoLinks() {
//        showProgressDialog();
//
//        Call<GetVideoModal> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .getVideos();
//
//        call.enqueue(new Callback<GetVideoModal>() {
//            @Override
//            public void onResponse(Call<GetVideoModal> call, Response<GetVideoModal> response) {
//                GetVideoModal videoResponse = response.body();
//                if (response.isSuccessful()) {
//                    progress.dismiss();
//                    if (videoResponse.getStatus()) {
//                        videoList.addAll(videoResponse.getData());
//                        Collections.reverse(videoList);
//                        mAdapter = new VideoAdapter(videoList, VideoActivity.this, getSupportFragmentManager()
//                                .beginTransaction());
//                        recyclerView.setAdapter(mAdapter);
//                        progress.dismiss();
//                    } else {
//                        progress.dismiss();
//                        Toast.makeText(VideoActivity.this, videoResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    progress.dismiss();
//                    Toast.makeText(VideoActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GetVideoModal> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
//                progress.dismiss();
//            }
//        });
//
//   }



    public void getVideoLinks() {
        if (isLoading) return; // Prevent multiple simultaneous requests

        isLoading = true;

        // Show progress dialog only for the first page
        if (currentPage == 1) {
            startShimmer(VideoActivity.this, ShimmerLay.VIDEOSLAY);
        }

        // API call with page and limit parameters
        Call<GetVideoModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .getVideos(currentPage, perPageLimit); // Pass page and limit

        call.enqueue(new Callback<GetVideoModal>() {
            @Override
            public void onResponse(Call<GetVideoModal> call, Response<GetVideoModal> response) {
                isLoading = false;

                // Dismiss progress dialog for the first page
                if (currentPage == 1) {
                    stopShimmer(VideoActivity.this, ShimmerLay.VIDEOSLAY);
                }

                if (response.isSuccessful() && response.body() != null) {
                    GetVideoModal videoResponse = response.body();

                    if (videoResponse.getStatus()) {
                        if (currentPage == 1) {
                            videoList.clear(); // Clear the list for the first page
                        }

                        videoList.addAll(videoResponse.getData()); // Append new data
                        videoList.sort((a, b) ->
                                Instant.parse(b.getUpdatedAt()).compareTo(Instant.parse(a.getUpdatedAt()))
                        );
                        // Update the adapter if it's the first time, otherwise notify data change
                        if (mAdapter == null) {
                            mAdapter = new VideoAdapter(videoList, VideoActivity.this, getSupportFragmentManager().beginTransaction());
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(VideoActivity.this, videoResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VideoActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetVideoModal> call, Throwable t) {
                isLoading = false;
                if (currentPage == 1) {
                    stopShimmer(VideoActivity.this, ShimmerLay.VIDEOSLAY);
                }
                Toast.makeText(getApplicationContext(), "Failed to load!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("backToActivity")) {
            url_notification = intent.getStringExtra("url");
            backToActivity = intent.getStringExtra("backToActivity");
        }
    }

    public void showProgressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }
}
