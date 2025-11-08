package com.raman.kumar.shrikrishan.CommentFiles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.AddGitaActivity;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.AddPostActivity;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.AudioPostActivity;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.DisplayAllImagesActivity;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.EditAudioActivity;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.EditGitaActivity;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.EditVideoActivity;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.ShowPicsByParts;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.UploadByPartsActivity;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.VideoUploadActivity;

public class AdminPanel extends AppCompatActivity {

    CardView uploadAmritGalleryLayout, showAmritGalleryPics, uploadByPartsLayout, showByPartsLayout, uploadRingtoneLayout, showRingtoneLayout,
            uploadArtiLayout, showArtiLayout, uploadGitaLayout, showGitaLayout, uploadVideoLayout, showVideosLayout;
    ImageView backButton;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        initViews();
    }


    private void initViews() {
        uploadAmritGalleryLayout = findViewById(R.id.uploadAmritGalleryLayout);
        showAmritGalleryPics = findViewById(R.id.showAmritGalleryPics);
        uploadByPartsLayout = findViewById(R.id.uploadByPartsLayout);
        showByPartsLayout = findViewById(R.id.showByPartsLayout);
        uploadRingtoneLayout = findViewById(R.id.uploadRingtoneLayout);
        showRingtoneLayout = findViewById(R.id.showRingtoneLayout);
        uploadArtiLayout = findViewById(R.id.uploadArtiLayout);
        showArtiLayout = findViewById(R.id.showArtiLayout);
        uploadGitaLayout = findViewById(R.id.uploadGitaLayout);
        showGitaLayout = findViewById(R.id.showGitaLayout);
        uploadVideoLayout = findViewById(R.id.uploadVideoLayout);
        showVideosLayout = findViewById(R.id.showVideosLayout);
        backButton = findViewById(R.id.backButton);


//        uploadAmritGalleryLayout.setOnClickListener((View.OnClickListener) this);
//        showAmritGalleryPics.setOnClickListener((View.OnClickListener) this);
//        uploadByPartsLayout.setOnClickListener((View.OnClickListener) this);
//        uploadRingtoneLayout.setOnClickListener((View.OnClickListener) this);
//        showRingtoneLayout.setOnClickListener((View.OnClickListener) this);
//        uploadArtiLayout.setOnClickListener((View.OnClickListener) this);
//        showArtiLayout.setOnClickListener((View.OnClickListener) this);
//        uploadGitaLayout.setOnClickListener((View.OnClickListener) this);
//        showGitaLayout.setOnClickListener((View.OnClickListener) this);
//        uploadVideoLayout.setOnClickListener((View.OnClickListener) this);
//        showVideosLayout.setOnClickListener((View.OnClickListener) this);
//        backButton.setOnClickListener((View.OnClickListener) this);
//        showByPartsLayout.setOnClickListener((View.OnClickListener) this);


        uploadAmritGalleryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanel.this, AddPostActivity.class);
                intent.putExtra("id", "");
                intent.putExtra("title", "");
                intent.putExtra("content", "");
                intent.putExtra("url", "");
                intent.putExtra("gallery", "");
                intent.putExtra("amrit", "");
                intent.putExtra("byParts", "");
                intent.putExtra("createdAt", "");
                intent.putExtra("position", "");
                startActivity(intent);
            }
        });

        showAmritGalleryPics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showPics = new Intent(AdminPanel.this, DisplayAllImagesActivity.class);
                startActivity(showPics);
            }
        });

        uploadByPartsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent byParts = new Intent(AdminPanel.this, UploadByPartsActivity.class);
                byParts.putExtra("id", "");
                byParts.putExtra("url", "");
                byParts.putExtra("section", "");
                startActivity(byParts);
            }
        });

        uploadRingtoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ringtoneUpload = new Intent(AdminPanel.this, AudioPostActivity.class);
                ringtoneUpload.putExtra("id", "");
                ringtoneUpload.putExtra("audio_type", "");
                ringtoneUpload.putExtra("created_at", "");
                ringtoneUpload.putExtra("display_name", "");
                ringtoneUpload.putExtra("song_duration", "");
                ringtoneUpload.putExtra("song_path", "");
                ringtoneUpload.putExtra("title", "");
                startActivity(ringtoneUpload);
            }
        });

        showRingtoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ringtoneShow = new Intent(AdminPanel.this, EditAudioActivity.class);
                startActivity(ringtoneShow);
            }
        });

        uploadArtiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ArtiUpload = new Intent(AdminPanel.this, AddGitaActivity.class);
                ArtiUpload.putExtra("title", "");
                ArtiUpload.putExtra("content", "");
                ArtiUpload.putExtra("position", "");
                ArtiUpload.putExtra("from", "arti");
                startActivity(ArtiUpload);
            }
        });

        showArtiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent artiShow = new Intent(AdminPanel.this, EditGitaActivity.class);
                artiShow.putExtra("from", "arti");
                startActivity(artiShow);
            }
        });

        uploadGitaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gitaUpload = new Intent(AdminPanel.this, AddGitaActivity.class);
                gitaUpload.putExtra("title", "");
                gitaUpload.putExtra("content", "");
                gitaUpload.putExtra("position", "");
                gitaUpload.putExtra("from", "gita");
                startActivity(gitaUpload);
            }
        });

        showGitaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent geetaShow = new Intent(AdminPanel.this, EditGitaActivity.class);
                geetaShow.putExtra("from", "gita");
                startActivity(geetaShow);
            }
        });
        uploadVideoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent videoUpload = new Intent(AdminPanel.this, VideoUploadActivity.class);
                startActivity(videoUpload);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
        showVideosLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent videoShow = new Intent(AdminPanel.this, EditVideoActivity.class);
                videoShow.putExtra("title", "");
                videoShow.putExtra("content", "");
                videoShow.putExtra("url", "");
                startActivity(videoShow);
            }
        });
        showByPartsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent parts = new Intent(AdminPanel.this, ShowPicsByParts.class);
                startActivity(parts);
            }
        });
    }


}
