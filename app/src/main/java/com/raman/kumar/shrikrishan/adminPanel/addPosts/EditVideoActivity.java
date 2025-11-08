package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.raman.kumar.modals.video.GetVideoModal.Datum;
import com.raman.kumar.modals.video.GetVideoModal.GetVideoModal;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditVideoActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditVideoAdapter editVideoAdapter;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    ProgressDialog progress;
    private List<Datum> videoList = new ArrayList<>();

    private int currentPage = 1;
    private final int perPageLimit = 20; // Adjust the number of items per page as needed
    private boolean isLoading = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_video);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        initView();
        setAdapter();


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

    @Override
    protected void onResume() {
        super.onResume();
        getVideoLinks();
    }

    public void showProgressDialog() {
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

//    private void getVideoLinks() {
//        showProgressDialog();
//
//        Call<GetVideoModal> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .getVideos();
//        call.enqueue(new Callback<GetVideoModal>() {
//            @Override
//            public void onResponse(Call<GetVideoModal> call, Response<GetVideoModal> response) {
//                GetVideoModal getVideos = response.body();
//                if (response.isSuccessful()) {
//                    if (getVideos.getStatus()) {
//                        videoList.clear();
//                        videoList.addAll(getVideos.getData());
//                        Collections.reverse(videoList);
//                        editVideoAdapter.notifyDataSetChanged();
//                        progress.dismiss();
////                        Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_LONG).show();
//                    } else {
//                        progress.dismiss();
//                        Toast.makeText(EditVideoActivity.this, getVideos.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GetVideoModal> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Failed to load !", Toast.LENGTH_LONG).show();
//                progress.dismiss();
//            }
//        });
//
//    }


    private void getVideoLinks() {
        if (isLoading) return; // Prevent multiple simultaneous requests

        isLoading = true;

        // Show progress dialog only for the first page
        if (currentPage == 1) {
            showProgressDialog();
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
                    progress.dismiss();
                }

                if (response.isSuccessful() && response.body() != null) {
                    GetVideoModal videoResponse = response.body();

                    if (videoResponse.getStatus()) {
                        if (currentPage == 1) {
                            videoList.clear(); // Clear the list for the first page
                        }

                        videoList.addAll(videoResponse.getData()); // Append new data

                        // Update the adapter if it's the first time, otherwise notify data change
                        editVideoAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(EditVideoActivity.this, videoResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditVideoActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetVideoModal> call, Throwable t) {
                isLoading = false;
                if (currentPage == 1) {
                    progress.dismiss();
                }
                Toast.makeText(getApplicationContext(), "Failed to load!", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void setAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        editVideoAdapter = new EditVideoAdapter(EditVideoActivity.this, videoList);
        recyclerView.setAdapter(editVideoAdapter);
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerView);
    }
}