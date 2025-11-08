package com.raman.kumar.shrikrishan.Activity;

import static com.raman.kumar.shrikrishan.util.ShimmerHelper.startShimmer;
import static com.raman.kumar.shrikrishan.util.ShimmerHelper.stopShimmer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.raman.kumar.modals.pictureByParts.getPictureByPart.Datum;
import com.raman.kumar.modals.pictureByParts.getPictureByPart.GetPictureByPartModal;
import com.raman.kumar.shrikrishan.Adapter.NewGalleryAdapter;
import com.raman.kumar.shrikrishan.ImageActivity;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.util.ShimmerLay;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewGalleryActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressDialog progress;
    private NewGalleryAdapter mAdapter;
    private List<Datum> gallerySection = new ArrayList<>();


    private int currentPage = 1;
    private final int perPageLimit = 30;
    private boolean isLoading = false;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Pictures By Parts");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));

        progress = new ProgressDialog(this);

        getPictureByPartData();





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
                        getPictureByPartData();
                    }
                }
            }
        });

    }

//    private void getPictureByPartData() {
//
//
//        showProgressDialog();
//        Call<GetPictureByPartModal> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .getPictureByPart();
//
//        call.enqueue(new Callback<GetPictureByPartModal>() {
//            @Override
//            public void onResponse(Call<GetPictureByPartModal> call, Response<GetPictureByPartModal> response) {
//                GetPictureByPartModal imagesResponse = response.body();
//                if (response.isSuccessful()) {
//                    if (imagesResponse.getStatus()) {
//                        gallerySection.clear();
//                        gallerySection.addAll(imagesResponse.getData());
//                        mAdapter = new NewGalleryAdapter(gallerySection,NewGalleryActivity.this,getSupportFragmentManager()
//                                .beginTransaction());
//                        recyclerView.setAdapter(mAdapter);
//                        progress.dismiss();
//                    } else {
//                        Toast.makeText(NewGalleryActivity.this, imagesResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                        progress.dismiss();
//                    }
//                }
//                else
//                {
//                    Toast.makeText(NewGalleryActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                    progress.dismiss();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GetPictureByPartModal> call, Throwable t) {
//                Toast.makeText(NewGalleryActivity.this, "Failed to load!", Toast.LENGTH_LONG).show();
//                Log.d("error","Message:"+t.getMessage());
//                progress.dismiss();
//            }
//        });
//    }



    private void getPictureByPartData() {
        if (isLoading) return;

        isLoading = true;

        // Show progress dialog only for the first page
        if (currentPage == 1) {
            startShimmer(NewGalleryActivity.this, ShimmerLay.WALLPAPERLAY);
        }

        Call<GetPictureByPartModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .getPictureByPart(currentPage, perPageLimit);

        call.enqueue(new Callback<GetPictureByPartModal>() {
            @Override
            public void onResponse(Call<GetPictureByPartModal> call, Response<GetPictureByPartModal> response) {
                isLoading = false;

                if (currentPage == 1) {
                    stopShimmer(NewGalleryActivity.this, ShimmerLay.WALLPAPERLAY);
                }

                if (response.isSuccessful() && response.body() != null) {
                    GetPictureByPartModal imagesResponse = response.body();
                    if (imagesResponse.getStatus()) {
                        if (currentPage == 1) {
                            gallerySection.clear();
                        }

                        gallerySection.addAll(imagesResponse.getData());

                        if (mAdapter == null) {
                            mAdapter = new NewGalleryAdapter(gallerySection, NewGalleryActivity.this,
                                    getSupportFragmentManager().beginTransaction());
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }

                    } else {
                        Toast.makeText(NewGalleryActivity.this, imagesResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NewGalleryActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetPictureByPartModal> call, Throwable t) {
                isLoading = false;
                if (currentPage == 1) {
                    stopShimmer(NewGalleryActivity.this, ShimmerLay.WALLPAPERLAY);                }
                Toast.makeText(NewGalleryActivity.this, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error", "Message:" + t.getMessage());
            }
        });
    }


    public void showProgressDialog(){
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

}
