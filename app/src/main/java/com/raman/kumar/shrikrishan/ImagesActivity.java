package com.raman.kumar.shrikrishan;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mann on 12/2/18.
 */

public class ImagesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private ImagesAdapter mAdapter;
    private List<Integer> imagesList = new ArrayList<>();
    Spinner selctOptions;
    ImageView uploadImage,uploadAudio,uploadVideo;
    ImageView imagePreview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        uploadImage=(ImageView) findViewById(R.id.uploadImage);
//        uploadAudio=(ImageView) findViewById(R.id.uploadAudio);
//        uploadVideo=(ImageView) findViewById(R.id.uploadVideo);
//
//        imagesList.add(R.drawable.srikrishna);
//        imagesList.add(R.drawable.image1);
//        imagesList.add(R.drawable.image2);
//        imagesList.add(R.drawable.srikrishna);
//        imagesList.add(R.drawable.image1);
//        imagesList.add(R.drawable.image2);
//        mAdapter = new ImagesAdapter(imagesList,getApplicationContext(),getSupportFragmentManager()
//                .beginTransaction());
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
//        recyclerView.setAdapter(mAdapter);
//
//
//        uploadImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              //  showCustomDialog();
//
//            }
//        });

    }
}
