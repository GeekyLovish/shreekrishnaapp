package com.raman.kumar.shrikrishan.tmrMusic.helpers;

import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Rahul Kumar on 6/29/2017.
 */

public class Listeners {

    public interface LoadSongListener {
        void onSongLoaded(ArrayList<HashMap<String, String>> list);
    }

    public interface onSongClickListener{
        void onSongClick();
    }


    public interface LoadImageListener {
        void onImageLoaded();
    }

    public interface MediaPlayerListener {
        void onMediaPlayerStarted(MediaPlayer mp);
    }

    public interface SongUpdateListener{
        void onUpdateSong(String type);
    }
}
