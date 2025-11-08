package com.raman.kumar.shrikrishan.tmrMusic;//package com.kumar.raman.shrikrishan.tmrMusic;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.os.IBinder;
//import android.telephony.PhoneStateListener;
//import android.telephony.TelephonyManager;
//import android.util.Log;
//
//import androidx.annotation.Nullable;
//
//import com.google.android.exoplayer2.SimpleExoPlayer;
//import com.kumar.raman.shrikrishan.tmrMusic.helpers.Listeners;
//import com.kumar.raman.shrikrishan.tmrMusic.helpers.NotificationHandler;
//import com.kumar.raman.shrikrishan.tmrMusic.helpers.UpdateReceiver;
//import com.kumar.raman.shrikrishan.util.SharedPrefClass;
//
//import java.io.IOException;
//
///**
// * Created by rahul on 6/9/2017.
// */
//
//public class SongServiceB extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener {
//    public final static String ACTION_PLAY = "PLAY";
//    public final static String ACTION_PAUSE = "PAUSE";
//    public final static String ACTION_RESUME = "RESUME";
//    public final static String ACTION_STOP = "STOP";
//    public final static String ACTION_SEEK = "SEEK_TO";
//    public final static String UPDATE_NOTIF = "updateNotification";
//    public static final String BROADCAST_ACTION = "com.kumar.raman.shrikrishan.tmrMusic.SongService";
//    private AudioManager audioManager;
//    public static MediaPlayer player;
//    private static Context mContext;
//    String data = "", title = "", artist = "", album = "";
//    int seekTo = -1;
//    private static UpdateReceiver receiver;
//    private NotificationHandler notificationHandler;
//    private static int result = 11;
//    private boolean isMediaPlayerReset = false;
//
//    private SimpleExoPlayer mSimpleExoPlayer;
//
//    Intent intent;
//
//    private Listeners.MediaPlayerListener mediaPlayerListener = new Listeners.MediaPlayerListener() {
//        @Override
//        public void onMediaPlayerStarted(MediaPlayer mp) {
//            PlayBackManagerForFragment.mediaPlayerStarted(mp);
//        }
//    };
//
//    private static AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
//        @Override
//        public void onAudioFocusChange(int focusChange) {
//            switch (focusChange) {
//
//                case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
//                    player.setVolume(0.2f, 0.2f);
//                    break;
//                case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
//                    PlayBackManagerForFragment.playPauseEvent(false, player.isPlaying(), false, player.getCurrentPosition());
//                    break;
//                case (AudioManager.AUDIOFOCUS_LOSS):
//                    if (PlayBackManagerForFragment.goAhead)
//                        PlayBackManagerForFragment.playPauseEvent(false, true, false, player.getCurrentPosition());
//                    break;
//                case (AudioManager.AUDIOFOCUS_GAIN):
//                    player.setVolume(1f, 1f);
//                    if (!player.isPlaying() && !PlayBackManagerForFragment.isManuallyPaused)
//                        PlayBackManagerForFragment.playPauseEvent(false, false, false, player.getCurrentPosition());
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
//        @Override
//        public void onCallStateChanged(int state, String incomingNumber) {
//            if (state == TelephonyManager.CALL_STATE_RINGING) {
//                if (player.isPlaying()) {
//                    PlayBackManagerForFragment.playPauseEvent(false, true, false, -1);
//                }
//            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
//                if (player != null && !player.isPlaying()) {
//                    PlayBackManagerForFragment.playPauseEvent(false, false, false, player.getCurrentPosition());
//                }
//            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
//                if (player.isPlaying()) {
//                    PlayBackManagerForFragment.playPauseEvent(false, true, false, -1);
//                }
//            }
//            super.onCallStateChanged(state, incomingNumber);
//        }
//    };
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        mContext = getApplicationContext();
//        intent = new Intent(BROADCAST_ACTION);
//        PlayBackManagerForFragment.isServiceRunning = true;
//        notificationHandler = NotificationHandler.getInstance(this);
//        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
//        player = new MediaPlayer();
////        player.setOnCompletionListener(this);
//        player.setOnPreparedListener(this);
//        player.setOnSeekCompleteListener(this);
//        player.setOnErrorListener(this);
//
//
//
//        try {
//            TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//            if (mgr != null) {
//                mContext.getSystemService(Context.TELEPHONY_SERVICE);
//                mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
//            }
//            IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
//            receiver = new UpdateReceiver();
//            registerReceiver(receiver, receiverFilter);
//        } catch (Exception e) {
//            Log.e("tmessages", e.toString());
//        }
//
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        performAction(intent);
////        return Service.START_NOT_STICKY;
//        return START_STICKY;
//    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        return super.onUnbind(intent);
//    }
//
////    @Override
////    public void onTaskRemoved(Intent rootIntent) {
////        super.onTaskRemoved(rootIntent);
////        stopSelf();
////    }
//
//    @Override
//    public void onDestroy() {
//        PlayBackManagerForFragment.isServiceRunning = false;
//        PlayBackManagerForFragment.isFirstLoad = true;
//        PlayBackManagerForFragment.goAhead = true;
//        if(player!=null){
//            player.stop();
//            player.release();
//            player = null;
//        }
//        if(receiver!=null)
//            unregisterReceiver(receiver);
//        if(notificationHandler!=null)
//            notificationHandler.onServiceDestroy();
//        if(audioManager!=null)
//            audioManager.abandonAudioFocus(focusChangeListener);
//        PlayBackManagerForFragment.onStopService();
//    }
//
//    @Override
//    public void onLowMemory() {
//
//    }
//
//    public void performAction(final Intent intent) {
//        final String action = intent.getAction();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                switch (action) {
//                    case ACTION_PLAY:
//                        Intent broadcastIntent = new Intent();
//                        broadcastIntent.setAction(BROADCAST_ACTION);
//                        broadcastIntent.putExtra("isProgress","true");
//                        sendBroadcast(broadcastIntent);
//
////                        notificationHandler.updateNotification(title, artist, album, true,true);
//
//
//                        data = intent.getStringExtra(TmrMusicNewActivity.SONG_PATH);
//                        title = intent.getStringExtra(TmrMusicNewActivity.SONG_TITLE);
//                        artist = intent.getStringExtra(TmrMusicNewActivity.ARTIST_NAME);
//                        album = intent.getStringExtra(TmrMusicNewActivity.ALBUM_NAME);
//
//                        notificationHandler.showNotification(title, artist, album, true);
//                        try {
//                            isMediaPlayerReset = true;
//                            player.reset();
//                            player.setDataSource(data);
//                            result = audioManager.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC,
//                                    AudioManager.AUDIOFOCUS_GAIN);
//                            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//                                player.prepareAsync();
//                            }
//                           // player.start();
//                        } catch (IOException | IllegalStateException e) {
//                            try {
//                                player.start();
//                            } catch (Exception ignore){
//                                PlayBackManagerForFragment.goAhead = true;
//                            }
//                            e.printStackTrace();
//                        } catch (RuntimeException e) {
//                            e.printStackTrace();
//                            PlayBackManagerForFragment.goAhead = true;
//                        }
//                        PlayBackManagerForFragment.goAhead = true;
//                      //  mediaPlayerListener.onMediaPlayerStarted(player);
//                        break;
//                    case ACTION_PAUSE:
//                        if (player.isPlaying()) {
//                            player.pause();
//                        }
//                        notificationHandler.showNotification(title, artist, album, false);
//                        PlayBackManagerForFragment.goAhead = true;
//                        break;
//                    case ACTION_RESUME:
//                        if (player != null) {
//                            result = audioManager.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC,
//                                    AudioManager.AUDIOFOCUS_GAIN);
//                            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//                                player.start();
//                                notificationHandler.showNotification(title, artist, album, true);
//                                mediaPlayerListener.onMediaPlayerStarted(player);
//                            }
//                        }
//                        PlayBackManagerForFragment.goAhead = true;
//                        break;
//                    case ACTION_STOP:
//                        Log.d("AudioFocus", "State: " + result);
//                        if (player != null) {
//                            stopSelf();
//                        }
//                        PlayBackManagerForFragment.goAhead = true;
//                        break;
//                    case ACTION_SEEK:
//                        notificationHandler.updateNotification(title, artist, album, true,true);
//                        Intent broadcastIntents = new Intent();
//                        broadcastIntents.setAction(BROADCAST_ACTION);
//                        broadcastIntents.putExtra("isProgress","true");
//                        sendBroadcast(broadcastIntents);
//
//                        seekTo = intent.getIntExtra("seekTo", -1);
//                        data = intent.getStringExtra(TmrMusicNewActivity.SONG_PATH);
//                        title = intent.getStringExtra(TmrMusicNewActivity.SONG_TITLE);
//                        artist = intent.getStringExtra(TmrMusicNewActivity.ARTIST_NAME);
//                        album = intent.getStringExtra(TmrMusicNewActivity.ALBUM_NAME);
//                        boolean resume = intent.getBooleanExtra("resume", false);
//                        if (player != null) {
//                            try {
//                                result = audioManager.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC,
//                                        AudioManager.AUDIOFOCUS_GAIN);
//                                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//                                    if (!android.text.TextUtils.isEmpty(data) && resume) {
//                                        isMediaPlayerReset = true;
//                                        player.reset();
//                                        player.setDataSource(data);
//                                        player.prepareAsync();
//                                    } else if(seekTo!=-1){
//                                        player.start();
//                                        player.seekTo(seekTo);
//                                        seekTo = -1;
////                                        notificationHandler.showNotification(title, artist, album, true);
//                                        mediaPlayerListener.onMediaPlayerStarted(player);
//                                    } else {
//                                        player.start();
//                                        notificationHandler.showNotification(title, artist, album, true);
//                                        mediaPlayerListener.onMediaPlayerStarted(player);
//                                    }
////                                    notificationHandler.showNotification(title, artist, album, true);
//                                }
//                            } catch (IOException | RuntimeException e) {
//                                e.printStackTrace();
//                                PlayBackManagerForFragment.goAhead = true;
//                            }
//                        }
//                        //PlayBackManagerForFragment.goAhead = true;
//                        break;
//                    case UPDATE_NOTIF:
//                        notificationHandler.updateNotification(title, artist, album, true,false);
//                        break;
//                }
//            }
//        }).start();
//    }
//
//    public static boolean isPlaying() {
//        if (player != null)
//            return player.isPlaying();
//        return false;
//    }
//
//    public static int getCurrentPosition() {
//        if (player != null) {
//            return player.getCurrentPosition();
//        }
//        return 0;
//    }
//
//    public static int getDuration() {
//        if (player != null) {
//            return player.getDuration();
//        }
//        return 0;
//    }
//
//    @Override
//    public void onCompletion(MediaPlayer mp) {
//        if (isMediaPlayerReset) {
//            isMediaPlayerReset = false;
//        } else if (mp != null && !mp.isPlaying() && PlayBackManagerForFragment.goAhead) {
//            Log.d("###PLAYER_COMPLETED: ", "PLAY_NEXT");
//            PlayBackManagerForFragment.playNext(new SharedPrefClass().isShuffleEnable());
//        }
//        else if(mp != null) {
//            Log.d("###PLAYER_COMPLETED: ", "PLAY_PAUSE_EVENT");
//            PlayBackManagerForFragment.playPauseEvent(false, false, false, mp.getCurrentPosition());
//        }
//    }
//
//    @Override
//    public void onPrepared(MediaPlayer mp) {
//        isMediaPlayerReset = false;
//        player.setOnCompletionListener(this);
//      //  PlayBackManagerForFragment.goAhead = true;
//        player.start();
//        if(seekTo!=-1)
//            player.seekTo(seekTo);
//        seekTo = -1;
//        notificationHandler.showNotification(title, artist, album, true);
//        mediaPlayerListener.onMediaPlayerStarted(player);
//        intent.putExtra("title",title);
//        intent.putExtra("isProgress","false");
//        sendBroadcast(intent);
//    }
//
//    @Override
//    public void onSeekComplete(MediaPlayer mp) {
//        intent.putExtra("title",title);
//        intent.putExtra("isProgress","false");
//        sendBroadcast(intent);
//    }
//
//    @Override
//    public boolean onError(MediaPlayer mp, int what, int extra) {
//        return true;
//    }
//}
