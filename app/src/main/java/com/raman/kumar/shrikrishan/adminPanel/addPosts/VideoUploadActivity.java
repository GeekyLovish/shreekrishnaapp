package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.modals.video.EditVideo.EditVideoModal;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.model.AartiGeetaResponse;
import com.raman.kumar.shrikrishan.model.VideoResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoUploadActivity extends AppCompatActivity {
    EditText titleEditText, contentEditText, urlEditText;
    Button uploadButton;
    ProgressDialog progress;
    VideoModel videoModel;
    List<VideoModel> videoModels;
    Intent intent;
    String title = "", content = "", url = "", id = "";
    private List<VideoModel> videoList = new ArrayList<>();

    public void showProgressDialog() {
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);
        intent = getIntent();
        if (intent.hasExtra("title")) {
            title = intent.getStringExtra("title");
            content = intent.getStringExtra("content");
            url = intent.getStringExtra("url");
            id = intent.getStringExtra("id");
        }
        uploadButton = findViewById(R.id.uploadButton);
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        urlEditText = findViewById(R.id.urlEditText);
//        getVideoLinks();
        if (!url.equals("")) {
            titleEditText.setText(title);
            contentEditText.setText(content);
            urlEditText.setText(url);
        }
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                videoModels = new ArrayList<>();
                if (url.isEmpty()) {

//                    String title = titleEditText.getText().toString().trim();
                    String title = contentEditText.getText().toString().trim();
                    String url = urlEditText.getText().toString().trim();

                    if (title.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Please enter title", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (url.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Please enter url", Toast.LENGTH_LONG).show();
                        return;
                    }



                    Call<EditVideoModal> call = RetrofitClient
                            .getInstance()
                            .getApi()
                            .postVideo("application/json", Extensions.getBearerToken(),title,url);
                    call.enqueue(new Callback<EditVideoModal>() {
                        @Override
                        public void onResponse(Call<EditVideoModal> call, Response<EditVideoModal> response) {
                            EditVideoModal postVideo = response.body();
                            if (response.isSuccessful()) {
                                if (postVideo.getStatus()) {
                                    progress.dismiss();
                                    Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_LONG).show();
                                    onBackPressed();
                                } else {
                                    progress.dismiss();
                                    Toast.makeText(VideoUploadActivity.this, postVideo.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<EditVideoModal> call, Throwable t) {
                            Log.d("loading",t.getMessage());
                            Toast.makeText(getApplicationContext(), "Failed to upload !", Toast.LENGTH_LONG).show();
                            progress.dismiss();
                        }
                    });

                } else {

//                    String title = titleEditText.getText().toString().trim();
                    String title = contentEditText.getText().toString().trim();
                    String url = urlEditText.getText().toString().trim();

                    if (title.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Please enter title", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Call<EditVideoModal> call = RetrofitClient
                            .getInstance()
                            .getApi()
                            .updateVideo("application/json", Extensions.getBearerToken(),id,title,url);
                    call.enqueue(new Callback<EditVideoModal>() {
                        @Override
                        public void onResponse(Call<EditVideoModal> call, Response<EditVideoModal> response) {
                            EditVideoModal updateVideo = response.body();
                            if (response.isSuccessful()) {
                                if (updateVideo.getStatus()) {
                                    progress.dismiss();
                                    Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_LONG).show();
                                    onBackPressed();
                                } else {
                                    progress.dismiss();
                                    Toast.makeText(VideoUploadActivity.this, updateVideo.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<EditVideoModal> call, Throwable t) {
                            Log.d("loading",t.getMessage());
                            Toast.makeText(getApplicationContext(), "Failed to update !", Toast.LENGTH_LONG).show();
                            progress.dismiss();
                        }
                    });

                }
//                progress.dismiss();
            }
        });
    }

}
