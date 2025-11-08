package com.raman.kumar.shrikrishan;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.arges.sepan.argmusicplayer.ArgMusicService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell- on 7/9/2018.
 * This Activity is responsible for displaying two tabs: "RingTone" and "Bhajan",
 * each containing a corresponding fragment.
 */
public class NewAudioActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_lay);

        // Set up the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Audio");

        // Set up the ViewPager and TabLayout
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Optionally, you can set the offscreen page limit if required
        viewPager.setOffscreenPageLimit(2);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        // Adding fragments to the ViewPagerAdapter
        adapter.addFrag(new AudioFragment(), "RingTone");
        adapter.addFrag(new BhajanFragment(), "Bhajan");

        // Setting the adapter to the ViewPager
        viewPager.setAdapter(adapter);
    }

    // ViewPagerAdapter manages the fragments
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Stop music services when the back button is pressed
        stopService(new Intent(getApplicationContext(), Music.class));
        stopService(new Intent(getApplicationContext(), ArgMusicService.class));

        // Set the flag for the NewBhajanFragment
        NewBhajanFragment.flag = true;
    }
}
