package com.raman.kumar.shrikrishan.tmrMusic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import com.raman.kumar.shrikrishan.AudioFragment;
import com.raman.kumar.shrikrishan.BhajanFragment;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.tmrMusic.fragments.TmrBhajanFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Sachin Rathore on 7/27/2019.
 */

public class TmrMusicNewActivity extends AppCompatActivity {

    public static final String SONG_TITLE = "songTitle";
    public static final String DISPLAY_NAME = "displayName";
    public static final String SONG_ID = "songID";
    public static final String SONG_PATH = "songPath";
    public static final String ALBUM_NAME = "albumName";
    public static final String ARTIST_NAME = "artistName";
    public static final String SONG_DURATION = "songDuration";
    public static final String SONG_POS = "songPosInList";
    public static final String SONG_PROGRESS = "songProgress";

    public static TmrMusicNewActivity instance;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabAdapter tabAdapter;

    TmrBhajanFragment tmrBhajanFragment;
    AudioFragment audioFragment;
    BhajanFragment bhajanFragment;
    int fragIndex = 0;
    Intent intent;
    private boolean bound = false;
    String intentValue;
    String from = "";
    AdView adView;

    public static TmrMusicNewActivity getInstance() {
        return instance;
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tmr_new_music_activity);
        instance = this;
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        intent = getIntent();
        if (intent.hasExtra("intentValue")) {
            intentValue = intent.getStringExtra("intentValue");
            from = intent.getStringExtra("FROM");

        }

       AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        initViewPager();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, 1);
        }
    }




    private void initViewPager() {
        // Add tabs to the TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("RingTone"));
        tabLayout.addTab(tabLayout.newTab().setText("Bhajan"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Link TabLayout with ViewPager
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // Handle Tab selection events
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                fragIndex = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No action needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No action needed
            }
        });

        // Set up the adapter
        setAdapter();

        // Check for the intent value to navigate to the Bhajan fragment
        if ("Notification".equals(from)) {
            viewPager.setCurrentItem(1); // Navigate to Bhajan fragment
            fragIndex = 1;
        } else if ("bhajan".equalsIgnoreCase(intentValue)) {
            viewPager.setCurrentItem(1); // Also navigate to Bhajan for "bhajan" intentValue
            fragIndex = 1;
        } else {
            viewPager.setCurrentItem(0); // Default to RingTone fragment
            fragIndex = 0;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            audioFragment.setPermission();
        }
    }

    private void setAdapter() {
        tabAdapter = new TabAdapter(getSupportFragmentManager());
        audioFragment = new AudioFragment();
        tmrBhajanFragment = new TmrBhajanFragment();
        bhajanFragment = new BhajanFragment();

        tabAdapter.addFragment(audioFragment);
        tabAdapter.addFragment(bhajanFragment);

        viewPager.setAdapter(tabAdapter);
        if (intentValue.equals("bhajan")) {
            viewPager.setCurrentItem(1);
        }

    }


    public void setAllSongs(String type, ArrayList<HashMap<String, String>> list) {
        if (type.equalsIgnoreCase("ringtone"))
            getInstance().tmrBhajanFragment.setAllSongs(list);
    }

    public void setPlayPauseView(boolean isPlaying) {
        getInstance().tmrBhajanFragment.setPlayPauseView(isPlaying);
    }

    public String calculateDuration(String type, int songDuration) {
        getInstance().tmrBhajanFragment.calculateDuration(songDuration);
        return "";
    }

    public void startSeekHandler(boolean start) {
        getInstance().tmrBhajanFragment.startSeekHandler(start);
    }
}
