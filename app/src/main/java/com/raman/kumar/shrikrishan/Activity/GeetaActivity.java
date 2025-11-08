package com.raman.kumar.shrikrishan.Activity;

import static com.raman.kumar.shrikrishan.util.ShimmerHelper.startShimmer;
import static com.raman.kumar.shrikrishan.util.ShimmerHelper.stopShimmer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.raman.kumar.modals.getaModal.getGeetaModal.Datum;
import com.raman.kumar.modals.getaModal.getGeetaModal.GetGeetaModal;
import com.raman.kumar.shrikrishan.Adapter.GeetaAdapter;
import com.raman.kumar.shrikrishan.Pojo.AratiData;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.TextActivity;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.GeetaModel;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.util.ShimmerLay;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mann on 20/2/18.
 */

public class GeetaActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private GeetaAdapter mAdapter;
    private List<AratiData> arraylist;
    ProgressDialog progress;

    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseStorage mStorage;
    GeetaModel geetaModel;
    List<Datum> geetaModelList = new ArrayList<>();

    private int currentPage = 1;
    private final int perPageLimit = 30;
    private boolean isLoading = false;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geeta_activit_lay);
        mStorage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        recyclerView = findViewById(R.id.recycler_view);
        setTitle("Geeta");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        progress = new ProgressDialog(this);
        if (isNetworkConnected()) {
//            getGeetaText();
            getGeetaList(currentPage);
        } else {
            Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();
        }

//        Toast.makeText(this, "Enter to Geta", Toast.LENGTH_SHORT).show();



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
                        getGeetaList(currentPage);
                    }
                }
            }
        });
    }

//    private void getGeetaList() {
//        showProgressDialog();
//
//        Call<GetGeetaModal> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .getGeeta();
//
//        call.enqueue(new Callback<GetGeetaModal>() {
//            @Override
//            public void onResponse(Call<GetGeetaModal> call, Response<GetGeetaModal> response) {
//                GetGeetaModal geetaResponse = response.body();
//                if (response.isSuccessful()) {
//                    progress.dismiss();
//                    if (geetaResponse.getStatus()) {
//                        geetaModelList.addAll(geetaResponse.getData());
//                        mAdapter = new GeetaAdapter(geetaModelList, getApplicationContext(), getSupportFragmentManager().beginTransaction());
//                        recyclerView.setAdapter(mAdapter);
//                        progress.dismiss();
//                    } else {
//                        progress.dismiss();
//                        Toast.makeText(GeetaActivity.this, geetaResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    progress.dismiss();
//                    Toast.makeText(GeetaActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GetGeetaModal> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
//                progress.dismiss();
//            }
//        });
//    }




    private void getGeetaList(int page) {
        if (isLoading) return;

        isLoading = true;
        // Show loading dialog only for the first page
        if (page == 1) {
            startShimmer(GeetaActivity.this, ShimmerLay.AARTILAY);
        }

        Call<GetGeetaModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .getGeeta(page, perPageLimit);

        call.enqueue(new Callback<GetGeetaModal>() {
            @Override
            public void onResponse(Call<GetGeetaModal> call, Response<GetGeetaModal> response) {
                isLoading = false;
                stopShimmer(GeetaActivity.this, ShimmerLay.AARTILAY);

                GetGeetaModal geetaResponse = response.body();
                if (response.isSuccessful() && geetaResponse.getStatus()) {
                    geetaModelList.addAll(geetaResponse.getData());
                    if (mAdapter == null) {
                        mAdapter = new GeetaAdapter(geetaModelList, getApplicationContext(), getSupportFragmentManager().beginTransaction());
                        recyclerView.setAdapter(mAdapter);
                    } else {
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(GeetaActivity.this, geetaResponse != null ? geetaResponse.getMessage() : "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetGeetaModal> call, Throwable t) {
                isLoading = false;
                stopShimmer(GeetaActivity.this, ShimmerLay.AARTILAY);
                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void showProgressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

//    public void getGeetaText() {
//        JSONObject json = new JSONObject();
//
//        showProgressDialog();
//        RequestHandler.getGeetaDetails(this, json, new NetworkingCallbackInterface() {
//            @Override
//            public void onSuccess(NetworkResponse response, boolean fromCache) {
//                System.out.print("response........" + response);
//                try {
//                    JSONArray jsonArray = new JSONArray(response);
//                    System.out.print("jsonArrayresponse........" + jsonArray);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onSuccess(String response, boolean fromCache) {
//                progress.dismiss();
//                System.out.print("response........" + response);
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    JSONArray jsonArray = jsonObject.getJSONArray("posts");
//                    AratiData content = new AratiData();
//                    arraylist = new ArrayList<>();
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        content = new AratiData();
//                        JSONObject json = jsonArray.getJSONObject(i);
//                        content.setTitle(json.getString("title"));
//                        content.setContent(json.getString("content"));
//                        ;
//                        arraylist.add(content);
//                    }
//                    Collections.reverse(arraylist);
//                    mAdapter = new EditGitaAdapter(arraylist, getApplicationContext(), getSupportFragmentManager().beginTransaction());
//
//                    recyclerView.setAdapter(mAdapter);
//                    System.out.print("jsonObjectArrayresponse........" + jsonArray);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(VolleyError error) {
//                progress.dismiss();
//                Toast.makeText(getApplicationContext(), "Connection not available", Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onNetworkFailure(String error) {
//                progress.dismiss();
//                Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }
}
