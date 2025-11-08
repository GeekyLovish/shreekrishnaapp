package com.raman.kumar.shrikrishan.tmrMusic;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
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
import com.raman.kumar.shrikrishan.adminPanel.addPosts.AudioUploadInfo;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.application.MyApp;
import com.raman.kumar.shrikrishan.model.GetAllAudioResponse;
import com.raman.kumar.shrikrishan.tmrMusic.helpers.BitmapPalette;
import com.raman.kumar.shrikrishan.tmrMusic.helpers.Listeners;
import com.raman.kumar.shrikrishan.util.SharedPrefClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sachin Rathore on 7/27/2019.
 * <p>
 * <p>
 * Code Modification and Fixing by Aman on 23 AUG 2020
 */

public class PlayBackManagerForFragment {

    /**
     * Data Variables
     */
    private List<Datum> audioList1 = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> songsList;
    public static boolean isManuallyPaused = false;

    public static final String songPref = "songPref10";
    private static Context mContext;
    private static SharedPreferences sharedPref;
    private static PlayBackManagerForFragment mPlaybackManager;
    private static String fragType;

    String Database_Path_audio = "All_Audio_Uploads_Database";
    DatabaseReference databaseReference;

    private PlayBackManagerForFragment(Context context, String type) {
        mContext = context;
        sharedPref = mContext.getSharedPreferences(songPref, Context.MODE_PRIVATE);
        fragType = type;
        createPlayList(type);
    }


    public static PlayBackManagerForFragment getInstance(Context mContext, String type) {
        songsList = new ArrayList<>();
        mPlaybackManager = new PlayBackManagerForFragment(mContext, type);
        fragType = type;
        return mPlaybackManager;
    }

    private Listeners.LoadSongListener loadSongListener = new Listeners.LoadSongListener() {

        @Override
        public void onSongLoaded(ArrayList<HashMap<String, String>> list) {
            ((TmrMusicNewActivity) mContext).setAllSongs(fragType, list);
        }
    };

    private void createPlayList(final String fragType) {
//        new PlayBackManagerForFragment.LoadSongsAsync(fragType).execute();
        getAudioFiles();
    }

    private void getAudioFiles() {


        Call<AudiosModal> call = RetrofitClient.getInstance().getApi().getAllAudios("application/json","bhajan",1,10);
        call.enqueue(new Callback<AudiosModal>() {
            @Override
            public void onResponse(Call<AudiosModal> call, Response<AudiosModal> response) {
                AudiosModal getAllAudioResponse = response.body();
                if (response.isSuccessful())
                {
                    if (getAllAudioResponse.getStatus())
                    {
                        audioList1.addAll(getAllAudioResponse.getData());
                        for (int i = 0; i <= audioList1.size()-1; i++)
                        {
                            if (audioList1.get(i).getType().equals("bhajan")) {
                                HashMap<String, String> song = new HashMap<String, String>();
                                String song_title = audioList1.get(i).getTitle();
                                String display_name = audioList1.get(i).getTitle();
                                String song_id = audioList1.get(i).getId().toString();
                                String song_path = audioList1.get(i).getPath();
                                String album_name = audioList1.get(i).getTitle();
                                String artist_name = audioList1.get(i).getTitle();
                                String song_duration = audioList1.get(i).getDuration().equals("") ? "00.00"/*"00.00f"*/ : audioList1.get(i).getDuration() + /*"f"*/"";
                                song_duration = song_duration.replace(":", ".");
                                song.put(TmrMusicNewActivity.SONG_TITLE, song_title);
                                song.put(TmrMusicNewActivity.DISPLAY_NAME, display_name);
                                song.put(TmrMusicNewActivity.SONG_ID, "" + song_id);
                                song.put(TmrMusicNewActivity.SONG_PATH, song_path);
                                song.put(TmrMusicNewActivity.ALBUM_NAME, album_name);
                                song.put(TmrMusicNewActivity.ARTIST_NAME, artist_name);
                                song.put(TmrMusicNewActivity.SONG_DURATION, song_duration);
                                song.put(TmrMusicNewActivity.SONG_POS, "" + songsList.size());
                                songsList.add(song);
                            }
                        }
                        if (!songsList.isEmpty() && songsList.get(0) != null) {
                            HashMap hashMap = songsList.get(0);
//                    hashMap.put(TmrMusicNewActivity.SONG_POS, "" + (songsList.size()));
//                    songsList.remove(0);
//                    songsList.add(hashMap);
                            HashMap hashMap1 = getPlayingSongPref();
                            Object val = hashMap1.get("songTitle");
                            if (val.equals("")) {
                                setPlayingSongPref(hashMap);
                            } else {
                                for (HashMap hashMap2 : songsList) {
                                    try {
                                        if (hashMap1.get(TmrMusicNewActivity.SONG_ID).equals(hashMap2.get(TmrMusicNewActivity.SONG_ID))) {
                                            setPlayingSongPref(hashMap2);
                                            break;
                                        }
                                    } catch (Exception ignored) {
                                    }

                                }
                            }

                        }
                        loadSongListener.onSongLoaded(songsList);
                    }
                }
            }

            @Override
            public void onFailure(Call<AudiosModal> call, Throwable t) {
                Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

//        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path_audio);
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                    AudioUploadInfo audioUploadInfo = postSnapshot.getValue(AudioUploadInfo.class);
//
//                    if (audioUploadInfo.getAudioType().equals("bhajan")) {
//                        HashMap<String, String> song = new HashMap<String, String>();
//                        String song_title = audioUploadInfo.getSongTitle();
//                        String display_name = audioUploadInfo.getSongTitle();
//                        int song_id = Integer.parseInt(audioUploadInfo.getSongID());
//                        String song_path = audioUploadInfo.getSongPath();
//                        String album_name = audioUploadInfo.getSongTitle();
//                        String artist_name = audioUploadInfo.getSongTitle();
//                        String song_duration = audioUploadInfo.getSongDuration().equals("") ? "00.00"/*"00.00f"*/ : audioUploadInfo.getSongDuration() + /*"f"*/"";
//                        song_duration = song_duration.replace(":", ".");
//                        song.put(TmrMusicNewActivity.SONG_TITLE, song_title);
//                        song.put(TmrMusicNewActivity.DISPLAY_NAME, display_name);
//                        song.put(TmrMusicNewActivity.SONG_ID, "" + song_id);
//                        song.put(TmrMusicNewActivity.SONG_PATH, song_path);
//                        song.put(TmrMusicNewActivity.ALBUM_NAME, album_name);
//                        song.put(TmrMusicNewActivity.ARTIST_NAME, artist_name);
//                        song.put(TmrMusicNewActivity.SONG_DURATION, song_duration);
//                        song.put(TmrMusicNewActivity.SONG_POS, "" + songsList.size());
//                        songsList.add(song);
//                    }
//                }
//
//                if (!songsList.isEmpty() && songsList.get(0) != null) {
//                    HashMap hashMap = songsList.get(0);
////                    hashMap.put(TmrMusicNewActivity.SONG_POS, "" + (songsList.size()));
////                    songsList.remove(0);
////                    songsList.add(hashMap);
//                    HashMap hashMap1 = getPlayingSongPref();
//                    Object val = hashMap1.get("songTitle");
//                    if (val.equals("")) {
//                        setPlayingSongPref(hashMap);
//                    } else {
//                        for (HashMap hashMap2 : songsList) {
//                            try {
//                                if (hashMap1.get(TmrMusicNewActivity.SONG_ID).equals(hashMap2.get(TmrMusicNewActivity.SONG_ID))) {
//                                    setPlayingSongPref(hashMap2);
//                                    break;
//                                }
//                            } catch (Exception ignored) {
//                            }
//
//                        }
//                    }
//
//                }
//                loadSongListener.onSongLoaded(songsList);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadSongsAsync extends AsyncTask<String, Void, String> {
        String musicType;

        public LoadSongsAsync(String musicType) {
            this.musicType = musicType;
        }

        static final String REQUEST_METHOD = "GET";
        static final int READ_TIMEOUT = 15000;
        static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(String... params) {

            String result;
            String inputLine;
            String m_URL = "https://ramankumarynr.com/api/?json=get_post&id=1294";

            try {
                //Create a URL object holding our url
                URL myUrl = new URL(m_URL);
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
                Log.d("TAG", "" + result);
            } catch (IOException e) {
                e.printStackTrace();
                result = null;
            }
            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject json = jsonObject.getJSONObject("post");
                JSONArray arrayAttachment = json.getJSONArray("attachments");
                for (int j = 0; j < arrayAttachment.length(); j++) {
                    JSONObject attachJson = arrayAttachment.getJSONObject(j);


                    HashMap<String, String> song = new HashMap<String, String>();
                    String song_title = attachJson.getString("title");
                    String display_name = attachJson.getString("title");
                    int song_id = attachJson.getInt("id");

                    String song_path = attachJson.getString("url");

                    String album_name = attachJson.getString("title");

                    String artist_name = attachJson.getString("slug");

                    String song_duration = attachJson.getString("caption").equals("") ? "00.00"/*"00.00f"*/ : attachJson.getString("caption") + /*"f"*/"";
                    song_duration = song_duration.replace(":", ".");
                    song.put(TmrMusicNewActivity.SONG_TITLE, song_title);
                    song.put(TmrMusicNewActivity.DISPLAY_NAME, display_name);
                    song.put(TmrMusicNewActivity.SONG_ID, "" + song_id);
                    song.put(TmrMusicNewActivity.SONG_PATH, song_path);
                    song.put(TmrMusicNewActivity.ALBUM_NAME, album_name);
                    song.put(TmrMusicNewActivity.ARTIST_NAME, artist_name);
                    song.put(TmrMusicNewActivity.SONG_DURATION, song_duration);
                    song.put(TmrMusicNewActivity.SONG_POS, "" + j);
                    songsList.add(song);

                }

                if (!songsList.isEmpty() && songsList.get(0) != null) {
                    HashMap hashMap = songsList.get(0);
//                    hashMap.put(TmrMusicNewActivity.SONG_POS, "" + (songsList.size()));
//                    songsList.remove(0);
//                    songsList.add(hashMap);
                    HashMap hashMap1 = getPlayingSongPref();
                    Object val = hashMap1.get("songTitle");
                    if (val.equals("")) {
                        setPlayingSongPref(hashMap);
                    } else {
                        for (HashMap hashMap2 : songsList) {
                            try {
                                if (hashMap1.get(TmrMusicNewActivity.SONG_ID).equals(hashMap2.get(TmrMusicNewActivity.SONG_ID))) {
                                    setPlayingSongPref(hashMap2);
                                    break;
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadSongListener.onSongLoaded(songsList);
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    public static void stopService() {
        mContext.startService(new Intent(mContext, SongService.class).setAction(SongService.ACTION_STOP).putExtra("message", "sensitive_content"));
    }

    public static void onStopService() {
        if (BitmapPalette.smallBitmap != null) {
            BitmapPalette.smallBitmap.recycle();
            BitmapPalette.mediumBitmap.recycle();
        }
    }

    /**
     * PLAY/PAUSE SONG through SERVICE
     */
    @OptIn(markerClass = UnstableApi.class)
    public static boolean playPauseEvent(boolean headphone, boolean isPlaying) {
        ((TmrMusicNewActivity) mContext).setPlayPauseView(!isPlaying);
        HashMap<String, String> hashMap = getPlayingSongPref();

        if (isPlayerServiceRunning() && SongService.mExoPlayer.getPlaybackState() != Player.STATE_IDLE) {
            if (headphone || isPlaying) {
                /*
                 * PAUSE if Song is running
                 * */

                /* PAUSE Song in Service */
                mContext.startService(new Intent(mContext, SongService.class).setAction(SongService.ACTION_PAUSE));

                /* Update Song PROGRESS in SharedPref */
                hashMap.put(TmrMusicNewActivity.SONG_PROGRESS, "" + SongService.getCurrentPosition());
                setPlayingSongPref(hashMap);
                return false;
            } else {
                /*
                 * PLAY if Song is PAUSE
                 * */

                /* PLAY Song in Service */
                mContext.startService(new Intent(mContext, SongService.class).setAction(SongService.ACTION_PLAY));
                return true;
            }
        } else {
            playSong(hashMap);
            return true;
        }
    }


    /**
     * Seek Song to Selected Position
     */
    public static void seekTo(final int progress) {
        if (isPlayerServiceRunning())
            new Thread(new Runnable() {
                @OptIn(markerClass = UnstableApi.class)
                @Override
                public void run() {
                    Intent i = new Intent(mContext, SongService.class);
                    i.setAction(SongService.ACTION_SEEK);
                    i.putExtra("seekTo", progress);
                    mContext.startService(i);
                }
            }).start();
    }

    /**
     * Play Previous OR Next Song
     */
    public static void playPrevNext(boolean isNext /* TRUE = NEXT, FALSE = PREVIOUS */) {
        int position=0;
        String lastPos = getPlayingSongPref().get(TmrMusicNewActivity.SONG_POS);
        int pos = Integer.parseInt((lastPos == null) ? "0" : lastPos);
        if (new SharedPrefClass().isShuffleEnable() && !songsList.isEmpty()) {
            pos = new Random().nextInt(songsList.size());
        } else {
            if (isNext) {
                pos++;
            } else {
                pos--;
            }
        }
        if (songsList != null && !songsList.isEmpty()) {

            /*pos = pos <= 0 || pos > songsList.size() ? 1 : pos;
            HashMap<String, String> hashMap = songsList.get(pos - 1);
            Log.d(isNext ? "###NEXT_PLAY: " : "###PREV_PLAY: ", String.valueOf(pos - 1));*/
            HashMap<String, String> hashMap = new HashMap<>();
            if (new SharedPrefClass().isShuffleEnable() && !songsList.isEmpty()) {
                pos = pos < 0 || pos >= songsList.size() ? 0 : pos;
                hashMap = songsList.get(pos);
            } else {
                pos = pos < 0 || pos >= songsList.size() ? 0 : pos;
                hashMap = songsList.get(pos);
            }
            Log.d(isNext ? "###NEXT_PLAY: " : "###PREV_PLAY: ", String.valueOf(pos));
            playSong(hashMap);
        }
    }

    /**
     * Play New Song
     */
    public static void playSong(final HashMap<String, String> hashMap) {
        Log.d("#PLAY_SONG: ", Objects.requireNonNull(String.valueOf(hashMap.get(TmrMusicNewActivity.SONG_POS))));

        /* Save Current Song Data to SharedPref */
        setPlayingSongPref(hashMap);

        new Thread(new Runnable() {
            @OptIn(markerClass = UnstableApi.class)
            @Override
            public void run() {
                Intent i = new Intent(mContext, SongService.class);
                i.setAction(SongService.ACTION_PLAY_NEW);
                i.putExtra(TmrMusicNewActivity.SONG_PATH, hashMap.get(TmrMusicNewActivity.SONG_PATH));
                i.putExtra(TmrMusicNewActivity.SONG_TITLE, hashMap.get(TmrMusicNewActivity.SONG_TITLE));
                i.putExtra(TmrMusicNewActivity.ARTIST_NAME, hashMap.get(TmrMusicNewActivity.ARTIST_NAME));
                i.putExtra(TmrMusicNewActivity.ALBUM_NAME, hashMap.get(TmrMusicNewActivity.ALBUM_NAME));
                mContext.startService(i);
            }
        }).start();
    }

    /**
     * Save Song Data To SharedPref
     */
    public static void setPlayingSongPref(final HashMap<String, String> songDetail) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                prefEditor.putString(TmrMusicNewActivity.SONG_TITLE, songDetail.get(TmrMusicNewActivity.SONG_TITLE))
                        .putString(TmrMusicNewActivity.SONG_ID, songDetail.get(TmrMusicNewActivity.SONG_ID))
                        .putString(TmrMusicNewActivity.ARTIST_NAME, songDetail.get(TmrMusicNewActivity.ARTIST_NAME))
                        .putString(TmrMusicNewActivity.ALBUM_NAME, songDetail.get(TmrMusicNewActivity.ALBUM_NAME))
                        .putString(TmrMusicNewActivity.SONG_DURATION, songDetail.get(TmrMusicNewActivity.SONG_DURATION))
                        .putString(TmrMusicNewActivity.SONG_PATH, songDetail.get(TmrMusicNewActivity.SONG_PATH))
                        .putString(TmrMusicNewActivity.SONG_POS, songDetail.get(TmrMusicNewActivity.SONG_POS))
                        .putString(TmrMusicNewActivity.SONG_PROGRESS, songDetail.get(TmrMusicNewActivity.SONG_PROGRESS));

                prefEditor.apply();
            }
        }).start();
    }

    /**
     * Get Song Data From SharedPref
     */
    public static HashMap<String, String> getPlayingSongPref() {
        HashMap<String, String> hashMap = new HashMap<>();
        if (sharedPref != null) {
            hashMap.put(TmrMusicNewActivity.SONG_TITLE, sharedPref.getString(TmrMusicNewActivity.SONG_TITLE, ""));
            hashMap.put(TmrMusicNewActivity.SONG_ID, sharedPref.getString(TmrMusicNewActivity.SONG_ID, ""));
            hashMap.put(TmrMusicNewActivity.ARTIST_NAME, sharedPref.getString(TmrMusicNewActivity.ARTIST_NAME, ""));
            hashMap.put(TmrMusicNewActivity.ALBUM_NAME, sharedPref.getString(TmrMusicNewActivity.ALBUM_NAME, ""));
            hashMap.put(TmrMusicNewActivity.SONG_DURATION, sharedPref.getString(TmrMusicNewActivity.SONG_DURATION, "" + 0));
            hashMap.put(TmrMusicNewActivity.SONG_PATH, sharedPref.getString(TmrMusicNewActivity.SONG_PATH, ""));
            hashMap.put(TmrMusicNewActivity.SONG_POS, sharedPref.getString(TmrMusicNewActivity.SONG_POS, -1 + ""));
            hashMap.put(TmrMusicNewActivity.SONG_PROGRESS, sharedPref.getString(TmrMusicNewActivity.SONG_PROGRESS, 0 + ""));
        }
        return hashMap;
    }

    /**
     * Check "SongService" Service is Running or Not
     */
    @OptIn(markerClass = UnstableApi.class)
    public static boolean isPlayerServiceRunning() {
        SongService locationService = new SongService(MyApp.getApplication());
        return isMyServiceRunning(locationService.getClass());
    }

    /**
     * Check Service Is Running Or Not
     */
    private static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) MyApp.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null)
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        return false;
    }

    /**
     * START/STOP SEEK HANDLER
     */
    public static void startSeekHandler(boolean startSeekHandler) {
        ((TmrMusicNewActivity) mContext).startSeekHandler(startSeekHandler);
    }
}
