package com.raman.kumar.shrikrishan;

import static com.raman.kumar.shrikrishan.util.ShimmerHelper.startShimmer;
import static com.raman.kumar.shrikrishan.util.ShimmerHelper.stopShimmer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.raman.kumar.modals.getaModal.getGeetaModal.Datum;
import com.raman.kumar.modals.getaModal.getGeetaModal.GetGeetaModal;
import com.raman.kumar.shrikrishan.Pojo.AratiData;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.util.ShimmerLay;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TextActivity extends AppCompatActivity {

    private int currentPage = 1;
    private final int perPageLimit = 30;
    private boolean isLoading = false;
    RecyclerView recyclerView;
    private TextAdapter mAdapter;
    private List<String> text = new ArrayList<>();
    ArrayList<AratiData> arrayList = new ArrayList<>();
    String url = "http://ramankumarynr.com/api/get_category_posts/?id=4";
    ProgressDialog progress;
    TextView titleText;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    List<Datum> ArtiList = new ArrayList<>();
    AdView adView;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        titleText = (TextView) findViewById(R.id.titleText);
        setTitle("Arati");

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        progress = new ProgressDialog(this);
        if (isNetworkConnected()) {
//            getGeetaText();
            getArtiList(currentPage);
        } else {
            Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();
        }

//        Toast.makeText(this, "Enter to Arti", Toast.LENGTH_SHORT).show();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        currentPage++;
                        getArtiList(currentPage);
                    }
                }
            }
        });

    }

//    private void getArtiList() {
//        showProgressDialog();
//
//        Call<GetGeetaModal> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .getAarti();
//
//        call.enqueue(new Callback<GetGeetaModal>() {
//            @Override
//            public void onResponse(Call<GetGeetaModal> call, Response<GetGeetaModal> response) {
//                GetGeetaModal aartiResponse = response.body();
//                if (response.isSuccessful()) {
//                    progress.dismiss();
//                    if (aartiResponse.getStatus()) {
//                       ArtiList.addAll(aartiResponse.getData());
//                       mAdapter = new TextAdapter(ArtiList, getApplicationContext(), getSupportFragmentManager().beginTransaction());
//                       recyclerView.setAdapter(mAdapter);
//                       progress.dismiss();
//                    } else {
//                        progress.dismiss();
//                        Toast.makeText(TextActivity.this, aartiResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    progress.dismiss();
//                    Toast.makeText(TextActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GetGeetaModal> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
//                progress.dismiss();
//            }
//        });
//
//    }

    private void getArtiList(int page) {
        if (isLoading) return;

        isLoading = true;
        // Show loading dialog only for the first page
        if (page == 1) {
            startShimmer(TextActivity.this, ShimmerLay.AARTILAY);
        }

        Call<GetGeetaModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .getAarti(page, perPageLimit);

        call.enqueue(new Callback<GetGeetaModal>() {
            @Override
            public void onResponse(Call<GetGeetaModal> call, Response<GetGeetaModal> response) {
                isLoading = false;
                stopShimmer(TextActivity.this, ShimmerLay.AARTILAY);

                if (response.isSuccessful() && response.body() != null && response.body().getStatus()) {
                    ArtiList.addAll(response.body().getData());

                    if (mAdapter == null) {
                        mAdapter = new TextAdapter(ArtiList, getApplicationContext(), getSupportFragmentManager().beginTransaction());
                        recyclerView.setAdapter(mAdapter);
                    } else {
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(TextActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetGeetaModal> call, Throwable t) {
                isLoading = false;
                stopShimmer(TextActivity.this, ShimmerLay.AARTILAY);
                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void showProgressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");

        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }
}
