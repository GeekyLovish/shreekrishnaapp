package com.raman.kumar.shrikrishan.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.raman.kumar.shrikrishan.application.MyApp;

import static com.raman.kumar.shrikrishan.tmrMusic.PlayBackManagerForFragment.songPref;

/**
 *
 * Written By Sarangal
 *
 * */

public class SharedPrefClass {

    public SharedPrefClass() {
    }

    /* get Shuffle Status */
    public boolean isShuffleEnable() {
        SharedPreferences preferences = MyApp.getApplication().getSharedPreferences(songPref, Context.MODE_PRIVATE);
        return preferences.getBoolean("isShuffleActive", false);
    }

    public void setShuffleEnable(boolean isActive){
        SharedPreferences.Editor prefEditor = MyApp.getApplication().getSharedPreferences(songPref, Context.MODE_PRIVATE).edit();
        prefEditor.putBoolean("isShuffleActive", isActive);
        prefEditor.apply();
    }
}
