package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raman.kumar.AudiosModal.AudiosModal;
import com.raman.kumar.AudiosModal.Datum;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.model.AudioResponse;
import com.raman.kumar.shrikrishan.model.GetAartGitaResponse;
import com.raman.kumar.shrikrishan.model.GetAllAudioResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAudioActivity extends AppCompatActivity {
    RecyclerView editAudioRecyclerView;
    RecyclerView toolbar;
    EditAudioAdapter editAudioAdapter;
    private ArrayList<Datum> audioList = new ArrayList<>();
    ImageView backButton;

    private int currentPage = 1;
    private final int pageSize = 20;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_audio);
        initView();
        setAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllRingtones();
    }

    private void setAdapter() {
        editAudioRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        editAudioAdapter = new EditAudioAdapter(EditAudioActivity.this, audioList);
        editAudioRecyclerView.setItemAnimator(new DefaultItemAnimator());
        editAudioRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        editAudioRecyclerView.setAdapter(editAudioAdapter);
    }

    private void initView() {


        editAudioRecyclerView = findViewById(R.id.editAudioRecyclerView);
        toolbar = findViewById(R.id.toolbar);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(toolbar, new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
                v.setPadding(0, statusBarHeight, 0, 0);
                return insets;
            }
        });

        editAudioRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                            firstVisibleItemPosition >= 0) {
                        currentPage++;
                        getAllRingtones();
                    }
                }
            }
        });


    }

//    private void getAllRingtones() {
//
//        Call<AudiosModal> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .getAllAudios("application/json","",1,20);
//        call.enqueue(new Callback<AudiosModal>() {
//            @Override
//            public void onResponse(Call<AudiosModal> call, Response<AudiosModal> response) {
//                AudiosModal getAudios = response.body();
//                if (response.isSuccessful()) {
//
//                    audioList.clear();
//                    audioList.addAll(getAudios.getData());
//                    editAudioAdapter.notifyDataSetChanged();
//                    Toast.makeText(getApplicationContext(), getAudios.getMessage(), Toast.LENGTH_SHORT).show();
//
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<AudiosModal> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Failed to load !", Toast.LENGTH_LONG).show();
////                progress.dismiss();
//            }
//        });
//
//    }


    private void getAllRingtones() {
        if (isLoading) return;
        isLoading = true;

        Call<AudiosModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .getAllAudios("application/json", "", currentPage, pageSize);

        call.enqueue(new Callback<AudiosModal>() {
            @Override
            public void onResponse(Call<AudiosModal> call, Response<AudiosModal> response) {
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    AudiosModal getAudios = response.body();

                    if (currentPage == 1) {
                        audioList.clear();
                    }

                    audioList.addAll(getAudios.getData());
                    editAudioAdapter.notifyDataSetChanged();

                    if (getAudios.getData().size() < pageSize) {
                        isLastPage = true;
                    }

                    Toast.makeText(getApplicationContext(), getAudios.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AudiosModal> call, Throwable t) {
                isLoading = false;
                Toast.makeText(getApplicationContext(), "Failed to load!", Toast.LENGTH_LONG).show();
            }
        });
    }




}

