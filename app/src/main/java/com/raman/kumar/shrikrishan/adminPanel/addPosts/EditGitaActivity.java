package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.raman.kumar.modals.getaModal.getGeetaModal.Datum;
import com.raman.kumar.modals.getaModal.getGeetaModal.GetGeetaModal;
import com.raman.kumar.shrikrishan.Pojo.AratiData;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditGitaActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private EditGitaAdapter mAdapter;
    private List<AratiData> arraylist;
    ProgressDialog progress;
    GeetaModel geetaModel;
    List<Datum> geetaModelList = new ArrayList<>();
    List<Datum> ArtiList = new ArrayList<>();
    String from="";
    Intent intent;

    private int currentPage = 1;
    private final int perPageLimit = 20;
    private boolean isLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gita);
        intent=getIntent();
        if (intent.hasExtra("from")){
            from=intent.getStringExtra("from");
        }
        recyclerView = findViewById(R.id.recycler_view);
        setTitle("Geeta");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        progress = new ProgressDialog(this);
        setUpPaginationScrollListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNetworkConnected()) {
            if (from.equals("gita")){
                getGeetaList(currentPage);

            }else {
                getArtiList(currentPage);
            }

        } else {
            Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpPaginationScrollListener() {
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
                        if (from.equals("gita")){
                            getGeetaList(currentPage);

                        }else {
                            getArtiList(currentPage);
                        }

                    }
                }
            }
        });
    }

//    public void getArtiList() {
//        showProgressDialog();
//        Call<GetGeetaModal> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .getAarti(1,200);
//        call.enqueue(new Callback<GetGeetaModal>() {
//            @Override
//            public void onResponse(Call<GetGeetaModal> call, Response<GetGeetaModal> response) {
//                GetGeetaModal getAarti = response.body();
//                if (response.isSuccessful()) {
//                    if (getAarti.getStatus()) {
//                        ArtiList.clear();
//                        ArtiList.addAll(getAarti.getData());
////                        Collections.reverse(ArtiList);
//                        mAdapter = new EditGitaAdapter(ArtiList, EditGitaActivity.this,
//                                getSupportFragmentManager().beginTransaction(), from);
//                        recyclerView.setAdapter(mAdapter);
//                        progress.dismiss();
////                        Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_LONG).show();
//
//                    } else {
//                        progress.dismiss();
//                        Toast.makeText(getApplicationContext(), getAarti.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GetGeetaModal> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Failed to load !", Toast.LENGTH_LONG).show();
//                progress.dismiss();
//            }
//        });
//
//    }




    public void getArtiList(int page) {
        if (isLoading) return;

        isLoading = true;
        // Show loading dialog only for the first page
        if (page == 1) {
            showProgressDialog();
        }

        Call<GetGeetaModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .getAarti(page, perPageLimit);

        call.enqueue(new Callback<GetGeetaModal>() {
            @Override
            public void onResponse(Call<GetGeetaModal> call, Response<GetGeetaModal> response) {
                isLoading = false;
                progress.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    GetGeetaModal getAarti = response.body();
                    if (getAarti.getStatus()) {
                        if (page == 1) {
                            ArtiList.clear();  // Clear only for the first page
                        }

                        ArtiList.addAll(getAarti.getData());
                        if (mAdapter == null) {
                            mAdapter = new EditGitaAdapter(ArtiList, EditGitaActivity.this,
                                    getSupportFragmentManager().beginTransaction(), from);
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), getAarti.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetGeetaModal> call, Throwable t) {
                isLoading = false;
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Failed to load!", Toast.LENGTH_LONG).show();
            }
        });
    }


//    public void getGeetaList() {
//        showProgressDialog();
//        Call<GetGeetaModal> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .getGeeta(1,200);
//        call.enqueue(new Callback<GetGeetaModal>() {
//            @Override
//            public void onResponse(Call<GetGeetaModal> call, Response<GetGeetaModal> response) {
//                GetGeetaModal getGeeta = response.body();
//                if (response.isSuccessful()) {
//                    if (getGeeta.getStatus()) {
//
//                        geetaModelList.clear();
//                        geetaModelList.addAll(getGeeta.getData());
////                        Collections.reverse(geetaModelList);
//                        mAdapter = new EditGitaAdapter(geetaModelList, EditGitaActivity.this,
//                                getSupportFragmentManager().beginTransaction(), from);
//                        recyclerView.setAdapter(mAdapter);
//                        progress.dismiss();
////                        Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_LONG).show();
//
//                    } else {
//                        progress.dismiss();
//                        Toast.makeText(getApplicationContext(), getGeeta.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GetGeetaModal> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Something went wrong !", Toast.LENGTH_LONG).show();
//                progress.dismiss();
//            }
//        });
//
//    }


    public void getGeetaList(int page) {
        if (isLoading) return;

        isLoading = true;
        // Show loading dialog only for the first page
        if (page == 1) {
            showProgressDialog();
        }

        Call<GetGeetaModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .getGeeta(page, perPageLimit);

        call.enqueue(new Callback<GetGeetaModal>() {
            @Override
            public void onResponse(Call<GetGeetaModal> call, Response<GetGeetaModal> response) {
                isLoading = false;
                progress.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    GetGeetaModal getGeeta = response.body();
                    if (getGeeta.getStatus()) {
                        if (page == 1) {
                            geetaModelList.clear();  // Clear only on the first page
                        }
                        geetaModelList.addAll(getGeeta.getData());

                        if (mAdapter == null) {
                            mAdapter = new EditGitaAdapter(geetaModelList, EditGitaActivity.this,
                                    getSupportFragmentManager().beginTransaction(), from);
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getGeeta.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetGeetaModal> call, Throwable t) {
                isLoading = false;
                progress.dismiss();
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
}
