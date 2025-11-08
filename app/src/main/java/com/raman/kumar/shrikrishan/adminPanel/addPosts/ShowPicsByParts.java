package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.raman.kumar.modals.pictureByParts.pictureByPart.Datum;
import com.raman.kumar.modals.pictureByParts.pictureByPart.PictureByPartModal;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.model.GetImagesByPartsResponse;
import com.raman.kumar.shrikrishan.model.GetImagesResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowPicsByParts extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;

    // Creating URI.
    Uri FilePathUri;

    // Creating StorageReference and DatabaseReference object.
    StorageReference storageReference;
    DatabaseReference databaseReference;
    ByPartsImagesAdapter allImagesAdapter;
    // Image request code for onActivityResult() .
    int Image_Request_Code = 7;

    ProgressDialog progressDialog;
    List<Datum> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pics_by_parts);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllImages();
    }

    private void getAllImages() {

        progressDialog = new ProgressDialog(ShowPicsByParts.this);
        progressDialog.setMessage("Loading Images...");
        progressDialog.show();

        Call<PictureByPartModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .getAllImagesByParts();

        call.enqueue(new Callback<PictureByPartModal>() {
            @Override
            public void onResponse(Call<PictureByPartModal> call, Response<PictureByPartModal> response) {
                PictureByPartModal imagesResponse = response.body();
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    if (imagesResponse.getStatus()) {
                        list.clear();
                        list.addAll(imagesResponse.getData());
                        Collections.reverse(list);
//                        Log.e("list",list.get(0).getCreatedAt());
                        allImagesAdapter = new ByPartsImagesAdapter(ShowPicsByParts.this, list);
                        recyclerView.setAdapter(allImagesAdapter);
                        progressDialog.dismiss();

                    } else {
                        Toast.makeText(ShowPicsByParts.this, imagesResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(ShowPicsByParts.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PictureByPartModal> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error","Message:"+t.getMessage());
                progressDialog.dismiss();
            }
        });

    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

// Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(new LinearLayoutManager(ShowPicsByParts.this));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
}