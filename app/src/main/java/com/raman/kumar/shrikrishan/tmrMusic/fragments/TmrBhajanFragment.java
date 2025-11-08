package com.raman.kumar.shrikrishan.tmrMusic.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.media3.common.util.UnstableApi;

import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.tmrMusic.PlayBackManagerForFragment;
import com.raman.kumar.shrikrishan.tmrMusic.SongService;
import com.raman.kumar.shrikrishan.tmrMusic.TmrBhajanAdapter;
import com.raman.kumar.shrikrishan.tmrMusic.helpers.PlayPauseView;
import com.raman.kumar.shrikrishan.tmrMusic.slidinguppanelhelper.SlidingUpPanelLayout;
import com.raman.kumar.shrikrishan.util.ResourceHandler;
import com.raman.kumar.shrikrishan.util.SharedPrefClass;
import com.raman.kumar.shrikrishan.util.ValidationsClass;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sachin Rathore on 7/27/2019.
 * <p>
 * Code Modification and Fixing by Sarangal on 12 JAN 2020
 */
public class TmrBhajanFragment extends Fragment implements View.OnClickListener {

    /**
     * Context Component
     */
    private Context mContext;

    /**
     * Progress Component
     */
    private ProgressDialog mProgressDialog;

    /**
     * PlayBackManager Instance
     */
    private PlayBackManagerForFragment mPlayBackManagerForFragment;

    public static final String SONG_TITLE = "songTitle";
    public static final String DISPLAY_NAME = "displayName";
    public static final String SONG_ID = "songID";
    public static final String SONG_PATH = "songPath";
    public static final String ALBUM_NAME = "albumName";
    public static final String ARTIST_NAME = "artistName";
    public static final String SONG_DURATION = "songDuration";
    public static final String SONG_POS = "songPosInList";
    public static final String SONG_PROGRESS = "songProgress";

    private ListView mLVSongList;

    private SlidingUpPanelLayout mLayout;
    private ImageView songAlbumbg, img_bottom_slideone, img_bottom_slidetwo,
            imgbtn_backward, imgbtn_forward, ivListBG;

    private PlayPauseView btn_playpause, btn_playpausePanel;

    private TextView txt_timeprogress, txt_timetotal, txt_playesongname,
            txt_songartistname, txt_playesongname_slidetoptwo, txt_songartistname_slidetoptwo,txt_playesongname_onPlayer;

    private RelativeLayout slidepanelchildtwo_topviewone, slidepanelchildtwo_topviewtwo;
    Space space;
    private LinearLayout llBottomLayout;
    private boolean isExpand = false;

    private SeekBar mSeekBar;

    String type = "ringtone";

    private LinearLayout llProgress;
    private ProgressBar progressBar;
    ImageView btn_shuffle;
    LinearLayout llShuffle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tmr_ringtone_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* Initialize Components */
        initialize(view);

        /* Set Listeners */
        initListeners();

        /* Initialize PlayBackManager */
        initPlayBackManagerForFragment();
    }

    /* Initialize Components */
    private void initialize(View view) {
        mLVSongList = view.findViewById(R.id.recycler_allSongs);
        mLayout = view.findViewById(R.id.sliding_layout);
        songAlbumbg = view.findViewById(R.id.image_songAlbumbg_mid);
        ivListBG = view.findViewById(R.id.iv_lvBG);
        img_bottom_slideone = view.findViewById(R.id.img_bottom_slideone);
        img_bottom_slidetwo = view.findViewById(R.id.img_bottom_slidetwo);

        llBottomLayout = view.findViewById(R.id.ll_bottom);
        txt_timeprogress = view.findViewById(R.id.slidepanel_time_progress);
        txt_timetotal = view.findViewById(R.id.slidepanel_time_total);
        imgbtn_backward = view.findViewById(R.id.btn_backward);
        imgbtn_forward = view.findViewById(R.id.btn_forward);

        btn_playpause = view.findViewById(R.id.btn_play);
        progressBar = view.findViewById(R.id.progressBar);

        btn_playpausePanel = view.findViewById(R.id.bottombar_play);
        llProgress = view.findViewById(R.id.llProgress);

        btn_shuffle = view.findViewById(R.id.btn_shuffle);
        llShuffle = view.findViewById(R.id.llShuffle);

        mSeekBar = view.findViewById(R.id.seekbar);

        /* Update Shuffle Button */
        updateShuffleButton();

        mSeekBar.setEnabled(false);
        btn_playpausePanel.Pause();
        btn_playpause.Pause();

        TypedValue typedvaluecoloraccent = new TypedValue();
        requireActivity().getTheme().resolveAttribute(R.color.colorAccent, typedvaluecoloraccent, true);

        txt_playesongname = view.findViewById(R.id.txt_playesongname);
        txt_songartistname = view.findViewById(R.id.txt_songartistname);
        txt_playesongname_slidetoptwo = view.findViewById(R.id.txt_playesongname_slidetoptwo);
        txt_playesongname_onPlayer = view.findViewById(R.id.txt_playesongname_onPlayer);
        txt_songartistname_slidetoptwo = view.findViewById(R.id.txt_songartistname_slidetoptwo);

        slidepanelchildtwo_topviewone = view.findViewById(R.id.slidepanelchildtwo_topviewone);
        slidepanelchildtwo_topviewtwo = view.findViewById(R.id.slidepanelchildtwo_topviewtwo);
        space = view.findViewById(R.id.space);

        slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
        slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);
        space.setVisibility(View.GONE);
    }

    /* Set Listeners */
    private void initListeners() {
        btn_playpausePanel.setOnClickListener(this);
        btn_playpause.setOnClickListener(this);
        imgbtn_backward.setOnClickListener(this);
        imgbtn_forward.setOnClickListener(this);
        mSeekBar.setMax(2712000);


        slidepanelchildtwo_topviewone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        slidepanelchildtwo_topviewtwo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset == 0.0f) {
                    isExpand = false;
                    slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
                    slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);
                    space.setVisibility(View.GONE);
                } else if (slideOffset > 0.0f && slideOffset < 1.0f) {
                    if (isExpand) {
                        slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
                        slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);
                        space.setVisibility(View.GONE);
                    } else {
                        slidepanelchildtwo_topviewone.setVisibility(View.INVISIBLE);
                        slidepanelchildtwo_topviewtwo.setVisibility(View.VISIBLE);
                        space.setVisibility(View.VISIBLE);

                    }
                } else {
                    isExpand = true;
                    slidepanelchildtwo_topviewone.setVisibility(View.INVISIBLE);
                    slidepanelchildtwo_topviewtwo.setVisibility(View.VISIBLE);
                    space.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPanelExpanded(View panel) {
                isExpand = true;
            }

            @Override
            public void onPanelCollapsed(View panel) {
                isExpand = false;
            }

            @Override
            public void onPanelAnchored(View panel) {
            }

            @Override
            public void onPanelHidden(View panel) {
            }
        });

        mLVSongList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (new ValidationsClass().isNetworkConnected()) {
                    HashMap<String, String> hashMap = PlayBackManagerForFragment.songsList.get(position);

                    /* Set Data To View */
                    loadSongInfo(hashMap, true);

                    Log.e("TImee##TION_CLICK: ", String.valueOf(position));
                    Log.e("Timeee#POSITION_CLICK: ", PlayBackManagerForFragment.songsList.get(position).get("songDuration"));

                    /* Play Song */
                    PlayBackManagerForFragment.playSong(hashMap);
                } else {
//                    Toast.makeText(mContext, ResourceHandler.getString(R.string.internet_check), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {

                    /* Will Auto Start When Song Play */
                    if (mSeekHandler != null) {
                        mSeekHandler.removeCallbacks(mSeekRunnable);
                    }

                    /* Will Auto Update When Song Play */
                    if (txt_timeprogress != null) {
                        txt_timeprogress.setText(calculateDuration(progress));
                    }

                    Log.e("Timeeeeee",(calculateDuration(progress))+"=RuniCal===");

                    /* Seek ExoPlayer in Service */
                    PlayBackManagerForFragment.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Update Shuffle Button */
                new SharedPrefClass().setShuffleEnable(!new SharedPrefClass().isShuffleEnable());
                updateShuffleButton();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /* Show Progress Dialog */
    public void showProgressDialog() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(ResourceHandler.getString(R.string.loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    /* Initialize PlayBackManager */
    private void initPlayBackManagerForFragment() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                return;
            }
        }

        /* Show Progress Dialog */
        showProgressDialog();

        /* It Will Hit Song API */
        mPlayBackManagerForFragment = PlayBackManagerForFragment.getInstance(mContext, type);
    }

    /**
     * Update Shuffle Button
     */
    private void updateShuffleButton() {
        boolean isShuffleActive = new SharedPrefClass().isShuffleEnable();
        if (llShuffle != null)
            llShuffle.setBackgroundColor(ResourceHandler.getColor(!isShuffleActive ? R.color.colorTransparent : R.color.white));
        if (btn_shuffle != null)
            btn_shuffle.setImageDrawable(ResourceHandler.getDrawable(!isShuffleActive ? R.drawable.ic_shuffle_white_36dp : R.drawable.ic_shuffle_black_36dp));
    }

    /**
     * Handler - To Update SeekBar Status
     */

    private Handler mSeekHandler = new Handler();
    private Runnable mSeekRunnable = new Runnable() {
        @OptIn(markerClass = UnstableApi.class)
        @Override
        public void run() {
            if (mSeekBar != null) {
                int currSeekPos = SongService.getCurrentPosition();
                int fullDuration = SongService.getDuration();

                //  getVideoDurationSeconds(fullDuration);

              /*  StringBuilder mFormatBuilder = new StringBuilder();
                Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
                int totalSeconds = fullDuration / 1000;
                //  videoDurationInSeconds = totalSeconds % 60;
                int seconds = totalSeconds % 60;
                int minutes = (totalSeconds / 60) % 60;
                int hours = totalSeconds / 3600;

                mFormatBuilder.setLength(0);
                if (hours > 0) {
                    txt_timeprogress.setText(mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString());

                   // return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
                } else {
                    txt_timeprogress.setText(mFormatter.format("%02d:%02d", minutes, seconds).toString());
                    //return mFormatter.format("%02d:%02d", minutes, seconds).toString();
                }



                int bufferedPos = SongService.getBufferedPosition();


              */
                int bufferedPos = SongService.getBufferedPosition();
                int max = mSeekBar.getMax();
                long newTime = (long) ((max/100.0) * fullDuration);
                Log.e("Timeeeeee",max+"===maxFirst=");
                Log.e("Timeeeeee",currSeekPos+"===CurrSeekFirst=");

                currSeekPos = (int) (((float) currSeekPos / fullDuration) * max);
                if (currSeekPos > max) {
                    currSeekPos = 0;
                }

                Log.e("Timeeeeee",currSeekPos+"===CurrSeek=");
                // if (txt_timeprogress != null)
                txt_timeprogress.setText(calculateDuration(currSeekPos));
                //   calculateDuration(currSeekPos);
                Log.e("Timeeeeee",currSeekPos+"===CurrSeekSecondd=");


//                Log.e("Timeeeeee",calculateDurationA(currSeekPos)+"=Calcualte===");
                Log.e("Timeeeeee",fullDuration+"=FulllfullDuration===");
                Log.e("Timeeeeee",bufferedPos+"=FulllbufferedPos===");
                mSeekBar.setSecondaryProgress(bufferedPos);
                mSeekBar.setProgress(currSeekPos);

                if (currSeekPos < fullDuration && mSeekHandler != null) {
                    Log.e("Timeeeeee",fullDuration+"=SeekFulll===");

                    mSeekHandler.postDelayed(mSeekRunnable, 1000);
                }
            }
        }
    };


    private int getVideoDurationSeconds(int player)
    {
        //  int timeMs=(int) player.getDuration();
        int totalSeconds = player / 1000;
        return totalSeconds;
    }
    /**
     * Set List to Adapter
     */
    public void setAllSongs(ArrayList<HashMap<String, String>> list) {
        if (mLVSongList != null && PlayBackManagerForFragment.songsList != null && !PlayBackManagerForFragment.songsList.isEmpty()) {
            TmrBhajanAdapter mAllSongsListAdapter = new TmrBhajanAdapter(PlayBackManagerForFragment.songsList);
            mLVSongList.setAdapter(mAllSongsListAdapter);
        }
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    /**
     * Set Data to Views
     */
    public void loadSongInfo(HashMap<String, String> songDetail, boolean seeking) {
        String title = songDetail.get(SONG_TITLE);
        String artist = songDetail.get(ARTIST_NAME);
        String path = songDetail.get(SONG_PATH);

        if (txt_playesongname != null) {

            txt_playesongname.setText(title);
            txt_playesongname_slidetoptwo.setText(title);
            txt_playesongname_onPlayer.setText(title);
            txt_songartistname.setText(artist);
            txt_songartistname_slidetoptwo.setText(artist);

            if (mSeekBar != null)
                mSeekBar.setEnabled(true);

            if (songDetail.get(SONG_DURATION) != null) {

                Log.e("Poss",songDetail.get(SONG_DURATION)+"===");
                String dur = Objects.requireNonNull(songDetail.get(SONG_DURATION)).replace("f", "").replace(".", " ");
                Log.e("Poss",dur+"===");
                String[] time = Objects.requireNonNull(dur).split(" ");

                // int totalSeconds = dur / 1000;

                Log.e("Poss",time[0]+"=0==");
                Log.e("Poss",time[1]+"==1=");
                Log.e("Poss",time.length+"=t==");
                float milliSecDuration;
                if (time.length==3) {
                    Log.e("Poss",time[2]+"==3=");
                    int value= Integer.parseInt(time[2]);
                    int value2= Integer.parseInt(time[1]);

                    int secondsToMs = value * 1000;
                    int minutesToMs =value2 * 60000;
                    int hoursToMs = Integer.parseInt(time[0]) * 3600000;
                    long total = secondsToMs + minutesToMs + hoursToMs;
                    Log.e("Poss",total+"==total=");

                    milliSecDuration = total;
                } else {
                    milliSecDuration = ((Float.parseFloat(time[0])) * 60 + (Float.parseFloat(time[1]))) * 1000;
                }
                Log.e("Poss",milliSecDuration+"==MM=");

                if (txt_timetotal != null)
                    txt_timetotal.setText(dur.replace(" ",":"));

//                txt_timetotal.setText(dur);
                if (mSeekBar != null)
                    mSeekBar.setMax((int) milliSecDuration);
            }


            /* if (txt_timetotal != null)
                    txt_timetotal.setText(calculateDuration(milliSecDuration));
                if (mSeekBar != null)
                    mSeekBar.setMax((int) milliSecDuration);
            }*/
        }
    }

    /**
     * Calculate Time From MiliSecond
     */
    public String calculateDuration(float millisec) {
        float sec = millisec / 1000;
        return sec != 0 ? String.format(Locale.US, "%02d:%02d", ((int) sec / 60), ((int) sec % 60)) : "00:00";
    }

    public void calculateDurationA(long millis) {
        Log.e("TimeeeeeMilllll",millis+"===========");
        float sec = millis / 1000;

        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = millis / (1000 * 60 * 60);

        StringBuilder b = new StringBuilder();
        b.append(hours == 0 ? "00" : hours < 10 ? String.valueOf("0" + hours) :
                String.valueOf(hours));
        Log.e("Timeeee", b.toString() + "Houraaaa");

        b.append(":");

        b.append(minutes == 0 ? "00" : minutes < 10 ? String.valueOf("0" + minutes) :
                String.valueOf(minutes));

        Log.e("Timeeee",b.toString()+"Houraaaa");

        b.append(":");
        b.append(seconds == 0 ? "00" : seconds < 10 ? String.valueOf("0" + seconds) :
                String.valueOf(seconds));
        b.toString();

        // formula for conversion for
        // milliseconds to seconds
        long secondss = (millis / 1000) % 60;

        // Print the output
        System.out.println(millis + " Milliseconds = "
                + minutes + " minutes and "
                + seconds + " seconds.");



        txt_timeprogress.setText(b.toString());

        Log.e("Timeeee",b.toString()+"aaaa");
        //return sec != 0 ? String.format(Locale.US, "%02d:%02d:%02d", ((int) sec / 60), ((int) sec % 60),((int) sec % 60)) : "00:00";
    }


    /**
     * On Screen Permission Result
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                initPlayBackManagerForFragment();
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onResume() {
        super.onResume();

        if (mPlayBackManagerForFragment != null) {
            HashMap hashMap = PlayBackManagerForFragment.getPlayingSongPref();
            Object value = hashMap.get("songTitle");
            if (value != null && !value.equals("")) {
                loadSongInfo(hashMap, SongService.isPlaying());
                startSeekHandler(SongService.isPlaying());
                setPlayPauseView(SongService.isPlaying());
            }
        } else initPlayBackManagerForFragment();

        /* Register BroadCast Receiver */
        if (mContext != null && mBroadcastReceiver != null)
            mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(SongService.BROADCAST_ACTION), Context.RECEIVER_NOT_EXPORTED);
    }

//    @Override
//    public void onBackPressed() {
//        if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
//            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//        } else if (SongService.isPlaying()) {
//            super.onBackPressed();
//        } else {
//            super.onBackPressed();
//            finish();
//        }
//    }

    @Override
    public void onDestroy() {

        /* Unregister BroadCast Receiver */
        if (mContext != null && mBroadcastReceiver != null)
            mContext.unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    /**
     * Update Play/Pause Button View
     */
    public void setPlayPauseView(boolean isPlaying) {
        if (btn_playpause != null && btn_playpausePanel != null)
            if (isPlaying) {
                btn_playpause.Play();
                btn_playpausePanel.Play();
            } else {
                btn_playpause.Pause();
                btn_playpausePanel.Pause();
            }
    }

    /**
     * BroadCast Receiver - To Get Intents From Service
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @OptIn(markerClass = UnstableApi.class)
        @Override
        public void onReceive(Context context, Intent intent) {
            loadSongInfo(PlayBackManagerForFragment.getPlayingSongPref(), SongService.isPlaying());
            if (intent != null) {
                if (intent.hasExtra("isProgress")) {
                    String isProgress = intent.getStringExtra("isProgress");
                    if (isProgress != null && llProgress != null && progressBar != null
                            && btn_playpause != null && btn_playpausePanel != null)
                        if (isProgress.equalsIgnoreCase("true")) {
                            llProgress.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            btn_playpause.setVisibility(View.GONE);
                            btn_playpausePanel.setVisibility(View.GONE);
                        } else {
                            llProgress.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            btn_playpause.setVisibility(View.VISIBLE);
                            btn_playpausePanel.setVisibility(View.VISIBLE);
                        }
                }
                if (intent.hasExtra("isPause")) {
                    String isPause = intent.getStringExtra("isPause");
                    if (isPause != null)
                        setPlayPauseView(!isPause.equalsIgnoreCase("true"));

                }
                if (intent.hasExtra("seekHandler")) {
                    String start = intent.getStringExtra("seekHandler");
                    if (start != null)
                        startSeekHandler(start.equalsIgnoreCase("true"));
                }
                if (intent.hasExtra("resetSeekBar") && intent.getStringExtra("resetSeekBar") != null) {
                    String resetTime = "00:00";
                    if (txt_timeprogress != null) {
                        //      txt_timeprogress.setText(resetTime);
                    }
                    if (txt_timetotal != null) {
                        txt_timetotal.setText(resetTime);
                    }
                    if (mSeekBar != null) {
                        mSeekBar.setSecondaryProgress(0);
                        mSeekBar.setProgress(0);
                    }
                }
            }
        }
    };

    /**
     * View Click Hanlding
     */
    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onClick(View v) {
        // Check for play buttons
        if (v.getId() == R.id.btn_play || v.getId() == R.id.bottombar_play) {
            if (!PlayBackManagerForFragment.isPlayerServiceRunning() && !new ValidationsClass().isNetworkConnected()) {
//                Toast.makeText(mContext, ResourceHandler.getString(R.string.internet_check), Toast.LENGTH_SHORT).show();
                return;
            }

            // Manually Paused
            PlayBackManagerForFragment.isManuallyPaused = SongService.isPlaying();

            if (PlayBackManagerForFragment.playPauseEvent(false, SongService.isPlaying())) {
                if (v.getId() == R.id.bottombar_play && mSeekRunnable != null) {
                    mSeekRunnable.run();
                }
            }
        }
        // Check for backward and forward buttons
        else if (v.getId() == R.id.btn_backward || v.getId() == R.id.btn_forward) {
            if (new ValidationsClass().isNetworkConnected()) {
                boolean isForwardClick = v.getId() == R.id.btn_forward;
                Log.d(isForwardClick ? "###FRAG_NEXT_BTN: " : "###FRAG_PREV_BTN: ", "CLICKED");
                PlayBackManagerForFragment.playPrevNext(isForwardClick);
            } else {
//                Toast.makeText(mContext, ResourceHandler.getString(R.string.internet_check), Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * START/STOP SEEK HANDLER
     */
    public void startSeekHandler(boolean start) {
        if (mSeekHandler != null && mSeekRunnable != null)
            if (start) {
                mSeekRunnable.run();
                Log.e("Timeeeeee","=RunSeekFulll===");

            } else {
                Log.e("Timeeeeee","=RemoveSeekFulll===");

                mSeekHandler.removeCallbacks(mSeekRunnable);
            }
    }
}