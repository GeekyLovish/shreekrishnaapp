package com.raman.kumar.shrikrishan.tmrMusic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.Timeline;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultDataSourceFactory;
import androidx.media3.exoplayer.ExoPlaybackException;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.SimpleExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.TrackGroupArray;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.TrackSelectionArray;

//import com.google.android.exoplayer2.ExoPlaybackException;
//import com.google.android.exoplayer2.ExoPlayerFactory;
//import com.google.android.exoplayer2.PlaybackParameters;
//import com.google.android.exoplayer2.Player;
//import com.google.android.exoplayer2.SimpleExoPlayer;
//import com.google.android.exoplayer2.Timeline;
//import com.google.android.exoplayer2.source.ExtractorMediaSource;
//import com.google.android.exoplayer2.source.MediaSource;
//import com.google.android.exoplayer2.source.TrackGroupArray;
//import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
//import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
//import com.google.android.exoplayer2.util.Util;
import com.google.firebase.firestore.EventListener;
import com.raman.kumar.shrikrishan.MainActivity;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.tmrMusic.helpers.NotificationHandler;
import com.raman.kumar.shrikrishan.tmrMusic.helpers.UpdateReceiver;
import com.raman.kumar.shrikrishan.util.ResourceHandler;
import com.raman.kumar.shrikrishan.util.ValidationsClass;

import java.util.Random;

/**
 * Created by Sarangal on 12 JAN 2020
 * <p>
 * Modified Old "SongService" refer {@link //SongServiceB}
 */

@UnstableApi
public class SongService extends Service /* implements  MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener */ {

    /**
     * BroadCast IntentFilters:
     */
    public final static String ACTION_PLAY_NEW = "PLAY_NEW";
    public final static String ACTION_PLAY = "PLAY";
    public final static String ACTION_PAUSE = "PAUSE";
    public final static String ACTION_STOP = "STOP";
    public final static String ACTION_SEEK = "SEEK_TO";
    public static final String BROADCAST_ACTION = "com.kumar.raman.shrikrishan.tmrMusic.SongService";

    /**
     * ExoPlayer PLAY STATUS
     */
    public final static int PLAYER_IDLE = 1;
    public final static int PLAYER_LOADING = 2;
    public final static int PLAYER_PLAYING = 3;
    public final static int PLAYER_STOPPED = 4;
    public final static int PLAYER_PAUSE = 5;

    /**
     * Audio Manager Component
     */
    private AudioManager mAudioManager;
    private static int AUDIO_FOCUS_REQUEST = AudioManager.AUDIOFOCUS_REQUEST_FAILED;

    /**
     * Context Instance
     */
    private Context mContext;

    /**
     * Data Variables
     */
    private String mSongURL = "", mSongTitle = "", mSongArtist = "", mSongAlbum = "";


    /**
     * BroadCast Related Components
     */
    private static UpdateReceiver mBCastHeadSet;

    /**
     * Player Components
     */
    public static SimpleExoPlayer mExoPlayer;
    private static int EXO_PLAYER_STATUS = PLAYER_IDLE;

    /**
     * Notification Handler Class
     */
    private NotificationHandler mNotificationHandler;

    /**
     * TelephoneManager To Get PHONE_CALL_STATE
     */
    private TelephonyManager mTelephonyManager;

    /**
     * AUDIO MANAGER - CALLBACK LISTENER
     */
    private static AudioManager.OnAudioFocusChangeListener mFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                    mExoPlayer.setVolume(0.2f);
                    break;

                case (AudioManager.AUDIOFOCUS_LOSS):
                case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                    if (isPlaying()) {

                        /* Manually Paused */
                        PlayBackManagerForFragment.isManuallyPaused = false;

                        PlayBackManagerForFragment.playPauseEvent(false, true);
                    }
                    break;

                case (AudioManager.AUDIOFOCUS_GAIN):
                    mExoPlayer.setVolume(1f);
                    if (!isPlaying() && !PlayBackManagerForFragment.isManuallyPaused) {
                        PlayBackManagerForFragment.playPauseEvent(false, false);
                    }
                    break;
            }
        }
    };

    /**
     * CALL STATE - CALLBACK LISTENER
     */
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (mExoPlayer != null) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    if (isPlaying()) {
                        /* Manually Paused */
                        PlayBackManagerForFragment.isManuallyPaused = false;

                        PlayBackManagerForFragment.playPauseEvent(false, true);
                    }
                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    if (!isPlaying() && !PlayBackManagerForFragment.isManuallyPaused) {
                        PlayBackManagerForFragment.playPauseEvent(false, false);
                    }
                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    if (isPlaying()) {
                        /* Manually Paused */
                        PlayBackManagerForFragment.isManuallyPaused = false;

                        PlayBackManagerForFragment.playPauseEvent(false, true);
                    }
                }
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    /**
     * ExoPlayer - EVENT CALLBACK LISTENER
     */
    private Player.Listener mPlayerEventListener = new Player.Listener() {

        @Override
        public void onTimelineChanged(Timeline timeline, int reason) {
            Log.d("TimelineChange", "Timeline changed: " + reason);
        }

        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Log.d("TracksChange", "Tracks changed");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            Log.d("LoadingChange", "Loading state changed: " + isLoading);
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_IDLE:
                case Player.STATE_ENDED:
                    EXO_PLAYER_STATUS = playbackState;

                    // Update Notification - Disable Progress & Pause
                    mNotificationHandler.showNotification(mSongTitle, mSongArtist, mSongAlbum, false, false);

                    // Notify BroadCast on Loading Change
                    sendPausedBCast(true);

                    // Start Seek Handler on Fragment
                    sendStartSeekHandlerBCast(playWhenReady);

                    if (playbackState == Player.STATE_ENDED) {
                        Log.d("###STATE_ENDED", "NEXT_SONG");
                        PlayBackManagerForFragment.playPrevNext(true);
                    } else {
                        Log.d("###STATE_IDLE", "PLAYER STOP");
                        PlayBackManagerForFragment.isManuallyPaused = true;
                    }
                    break;

                case Player.STATE_READY:
                    EXO_PLAYER_STATUS = playWhenReady ? PLAYER_PLAYING : PLAYER_PAUSE;
                    Log.d("###STATE_READY", "PLAYER PLAY " + playWhenReady);

                    sendStartSeekHandlerBCast(playWhenReady);
                    sendPausedBCast(!playWhenReady);
                    mNotificationHandler.showNotification(mSongTitle, mSongArtist, mSongAlbum, playWhenReady, false);

                    if (!isPlaying()) {
                        mExoPlayer.setPlayWhenReady(false);
                    }

                    // Initialize Listener for CALL_STATE
                    if (mTelephonyManager != null) {
                        try {
                            mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                            if (mTelephonyManager != null) {
                                mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                            }
                        } catch (Exception ignored) {
                            mTelephonyManager = null;
                        }
                    }
                    break;

                case Player.STATE_BUFFERING:
                    EXO_PLAYER_STATUS = PLAYER_LOADING;
                    Log.d("###STATE_BUFFERING", "PLAYER_LOADING");

                    sendProgressBCast(true);
                    mNotificationHandler.showNotification(mSongTitle, mSongArtist, mSongAlbum, false, true);
                    sendStartSeekHandlerBCast(false);
                    break;
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            Log.d("RepeatModeChange", "Repeat mode changed: " + repeatMode);
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            Log.d("ShuffleModeChange", "Shuffle mode enabled: " + shuffleModeEnabled);
        }

        public void onPlayerError(ExoPlaybackException error) {
            String message;
            if (new ValidationsClass().isNetworkConnected()) {
                if (error != null) {
                    if (error.getSourceException().getMessage() != null && !error.getSourceException().getMessage().isEmpty()) {
                        message = error.getSourceException().getMessage();
                    } else if (error.getUnexpectedException().getMessage() != null && !error.getUnexpectedException().getMessage().isEmpty()) {
                        message = error.getUnexpectedException().getMessage();
                    } else if (error.getRendererException().getMessage() != null && !error.getRendererException().getMessage().isEmpty()) {
                        message = error.getRendererException().getMessage();
                    } else {
                        message = ResourceHandler.getString(R.string.something_went_wrong);
                    }
                } else {
                    message = ResourceHandler.getString(R.string.something_went_wrong);
                }
            } else {
                message = ResourceHandler.getString(R.string.internet_check);
            }

            Log.d("###PLAYER_ERROR", message);
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            pausePlayer();
            sendPausedBCast(true);
            mNotificationHandler.showNotification(mSongTitle, mSongArtist, mSongAlbum, false, false);
            PlayBackManagerForFragment.isManuallyPaused = true;
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            Log.d("PositionDiscontinuity", "Position discontinuity: " + reason);
        }

        public void onSeekProcessed() {
            Log.d("SeekProcessed", "Seek processed");
        }

        // Uncomment this if you want to handle playback parameters change
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        Log.d("PlaybackParameters", "Playback parameters changed: " + playbackParameters.toString());
    }
    };

    /**
     * SERVICE - CONSTRUCTOR with CONTEXT
     */
    public SongService(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * SERVICE - DEFAULT CONSTRUCTOR
     */
    public SongService() {
    }

    /**
     * When SERVICE create
     */
    @Override
    public void onCreate() {
        super.onCreate();

        /* Get App Instance */
        mContext = this;

        /* Initialize Notification Handler Class */
        mNotificationHandler = NotificationHandler.getInstance(this);

        /* Initialize Audio Manager */
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        /* Register BroadCast Receiver For HeadSet Insert/Remove Tasks */
        IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        mBCastHeadSet = new UpdateReceiver();
        registerReceiver(mBCastHeadSet, receiverFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * When SERVICE will START
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /* Perform Action Based on Incoming Intent of Service */
        performAction(intent);

        return START_STICKY;
    }

    /**
     * When removed a task that comes from the service's application
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        System.out.println("dasfaksfaskfk.   Song Service Remove");
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    /**
     * When SERVICE will DESTROY
     */
    @Override
    public void onDestroy() {
        System.out.println("dasfaksfaskfk.   Song Service Remove");

        /* PAUSE - Player */
        pausePlayer();

        /* RELEASE - Player */
        releasePlayer();

        /* Remover BroadCast Receiver CallBacks */
        if (mBCastHeadSet != null)
            unregisterReceiver(mBCastHeadSet);

        /* Remove Player Notification From StatusBar and Notification Panel */
        if (mNotificationHandler != null)
            mNotificationHandler.onServiceDestroy();

        /* Remove AudioManager CallBacks */
        if (mAudioManager != null)
            mAudioManager.abandonAudioFocus(mFocusChangeListener);

        /* Update PlayBack Manager Class */
        PlayBackManagerForFragment.onStopService();

        /* Show Paused View on Fragment */
        sendPausedBCast(true);
    }

    /**
     * Return ExoPlayer is Playing or Not
     */
    public static boolean isPlaying() {
        if (mExoPlayer != null)
            return EXO_PLAYER_STATUS == PLAYER_PLAYING;
        return false;
    }

    /**
     * Get ExoPlayer Current Position
     */
    public static int getCurrentPosition() {
        if (mExoPlayer != null) {
            return (int) mExoPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * Get Total Duration of Song of ExoPlayer
     */
    public static int getDuration() {
        if (mExoPlayer != null) {
            return (int) mExoPlayer.getDuration();
        }
        return 0;
    }

    /**
     * Get Buffered Position of Song of ExoPlayer
     */
    public static int getBufferedPosition() {
        if (mExoPlayer != null) {
            return (int) mExoPlayer.getBufferedPosition();
        }
        return 0;
    }

    /**
     * FINISH - ExoPlayer
     */
    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    /**
     * PAUSE - ExoPlayer
     */
    private void pausePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.getPlaybackState();
        }
    }

    /**
     * RESUME - ExoPlayer
     */
    private void resumePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(true);
            mExoPlayer.getPlaybackState();
        }
    }

    /**
     * Send BroadCast to Set SeekBar
     */
    private void sendSeekbarBCast() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setPackage(BROADCAST_ACTION);
        broadcastIntent.putExtra("resetSeekBar", "resetSeekBar");
        sendBroadcast(broadcastIntent);
    }

    /**
     * Send BroadCast to Start/Stop Seek Handler
     */
    private void sendStartSeekHandlerBCast(boolean start) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BROADCAST_ACTION);
        broadcastIntent.putExtra("seekHandler", String.valueOf(start));
        sendBroadcast(broadcastIntent);
    }

    /**
     * Send BroadCast to Notify Pause Song
     */
    private void sendPausedBCast(boolean paused) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BROADCAST_ACTION);
        broadcastIntent.putExtra("isProgress", String.valueOf(false));
        broadcastIntent.putExtra("isPause", String.valueOf(paused));
        sendBroadcast(broadcastIntent);
    }

    /**
     * Send BroadCast to Notify About Loading
     */
    private void sendProgressBCast(boolean isProgress) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BROADCAST_ACTION);

        /* Add Song Title If Player is not loading */
        if (!isProgress) {
            broadcastIntent.putExtra("mSongTitle", mSongTitle);
        }

        broadcastIntent.putExtra("isProgress", String.valueOf(isProgress));
        sendBroadcast(broadcastIntent);
    }

    /**
     * Perform Action Based on Incoming Intent of Service
     */
    public void performAction(final Intent intent) {
        if (intent != null && intent.getAction() != null)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    switch (intent.getAction()) {
                        case ACTION_PLAY_NEW:

                            /* Get Data from Intent */
                            mSongURL = intent.getStringExtra(TmrMusicNewActivity.SONG_PATH);
                            mSongTitle = intent.getStringExtra(TmrMusicNewActivity.SONG_TITLE);
                            mSongArtist = intent.getStringExtra(TmrMusicNewActivity.ARTIST_NAME);
                            mSongAlbum = intent.getStringExtra(TmrMusicNewActivity.ALBUM_NAME);

                            try {
                                AUDIO_FOCUS_REQUEST = mAudioManager.requestAudioFocus(mFocusChangeListener, AudioManager.STREAM_MUSIC,
                                        AudioManager.AUDIOFOCUS_GAIN);
                                if (AUDIO_FOCUS_REQUEST == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                                    /* Play ExoPlayer with URL */
                                    prepareExoPlayerFromURL(mSongURL != null ? mSongURL : "");
                                }
                            } catch (Exception ignored) {
                            }
                            break;

                        case ACTION_PLAY:

                            if (mExoPlayer != null
                                    && (mExoPlayer.getPlaybackState() == Player.STATE_IDLE
                                    || mExoPlayer.getPlaybackState() == Player.STATE_ENDED)) {
                                /* Play ExoPlayer with URL */
                                prepareExoPlayerFromURL(mSongURL != null ? mSongURL : "");
                            } else {
                                if (mExoPlayer != null) {
                                    AUDIO_FOCUS_REQUEST = mAudioManager.requestAudioFocus(mFocusChangeListener, AudioManager.STREAM_MUSIC,
                                            AudioManager.AUDIOFOCUS_GAIN);
                                    if (AUDIO_FOCUS_REQUEST == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                                        /* Resume ExoPlayer */
                                        resumePlayer();

                                        /* Show Notification */
                                        mNotificationHandler.showNotification(mSongTitle, mSongArtist, mSongAlbum, true, false);
                                    }
                                }
                            }

                            break;

                        case ACTION_PAUSE:

                            /* Pause ExoPlayer */
                            pausePlayer();

                            /* Notify BroadCast on Loading Change */
                            sendPausedBCast(true);

                            /* Show Notification */
                            mNotificationHandler.showNotification(mSongTitle, mSongArtist, mSongAlbum, false, false);
                            break;

                        case ACTION_STOP:

                            /* Stop Service */
                            stopSelf();
                            break;

                        case ACTION_SEEK:

                            /* Get Data from Intent */
                            int mSongSeekTo = intent.getIntExtra("seekTo", 0);

                            mSongSeekTo = mSongSeekTo <= getDuration() ? mSongSeekTo : 0;

                            /* Seek ExoPlayer */
                            if (mExoPlayer != null) {
                                mExoPlayer.seekTo(mSongSeekTo);
                            }
                            break;
                    }
                }
            }).start();
    }

    /**
     * Add Song to ExoPlayer
     */
    private void prepareExoPlayerFromURL(String songURL) {

        /* Show Notification */
        mNotificationHandler.showNotification(mSongTitle, mSongArtist, mSongAlbum, false, true);

        /* Notify SeekBar BroadCast Receiver */
        sendSeekbarBCast();

        if (mExoPlayer == null) {
            // Create a new instance of SimpleExoPlayer
            mExoPlayer = new SimpleExoPlayer.Builder(mContext).build();
            mExoPlayer.addListener(mPlayerEventListener);
        }

        mExoPlayer.setPlayWhenReady(true);

        // Create a DataSource.Factory
        DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(mContext);

        // Parse the song URL
        Uri uri = Uri.parse(songURL);

        // Create a MediaItem
        MediaItem mediaItem = MediaItem.fromUri(uri);

        // Prepare the player with the media item
        mExoPlayer.setMediaItem(mediaItem);
        mExoPlayer.prepare();
    }

    private String CHANNEL_ID;

    private void createNotificationChannel() {
        CharSequence channelName = CHANNEL_ID;
        String channelDesc = "channelDesc";
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDesc);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            NotificationChannel currChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (currChannel == null)
                notificationManager.createNotificationChannel(channel);
        }
    }




    public void createNotification(String title, String message) {

        CHANNEL_ID = String.valueOf(new Random().nextInt());
        if (message != null ) {
            createNotificationChannel();

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.random)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            int notificationId = (int) (System.currentTimeMillis()/4);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            notificationManager.notify(notificationId, mBuilder.build());
        }
    }
}