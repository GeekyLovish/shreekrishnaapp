package com.raman.kumar.shrikrishan.tmrMusic;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raman.kumar.shrikrishan.R;

/**
 * Created by rahul on 6/22/2017.
 */

public class AllSongs extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.allsongs, container, false);
        return v;
    }
}
