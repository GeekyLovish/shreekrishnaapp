package com.raman.kumar.shrikrishan.audio_player

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.raman.kumar.customClasses.Song
import com.raman.kumar.shrikrishan.R
import com.raman.kumar.shrikrishan.tmrMusic.TmrMusicNewActivity
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit


class MusicPlayerService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)


    private val binder = MusicPlayerBinder()
    private lateinit var mediaSession: MediaSessionCompat
    private val CHANNEL_ID = "media_playback_channel"
    var songs: List<Song> = listOf()
    var currentSongIndex: Int = 0
    var mediaPlayer: MediaPlayer? = null
    var isPlaying = false  // Track if the song is playing

    var isShuffle = false
    private var originalSongsList: List<Song> = listOf()

    private val handler = Handler(Looper.getMainLooper())
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayer != null && isPlaying) {
                updateNotification(songs[currentSongIndex].title, isPlaying,1F)
                handler.postDelayed(this, 1000) // Update every second
            }
        }
    }

    // Define the callback inline to avoid errors in setCallback
    private val callback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            playSong(currentSongIndex)
            // start playback
        }

        override fun onPause() {
            pauseSong()
            // pause playback
        }

        override fun onSkipToPrevious() {
            previousSong()
            // skip to previous
        }

        override fun onSkipToNext() {
            nextSong()
            // skip to next
        }

        override fun onSeekTo(pos: Long) {
            seekTo(pos.toInt())
            // jump to position in track
        }

        override fun onCustomAction(action: String, extras: Bundle?) {
            when (action) {
//                CUSTOM_ACTION_1 -> doCustomAction1(extras)
//                CUSTOM_ACTION_2 -> doCustomAction2(extras)
//                else -> {
//                    Log.w(TAG, "Unknown custom action $action")
//                }
            }
        }
    }


    override fun onCreate() {
        super.onCreate()
        println("MUSIC_PLAYER_SERVICE onCreate")
        createNotificationChannel()
        mediaSession = MediaSessionCompat(this, "AudioPlayerSession")
    }

    // Returns the current playing state and song index
    fun getPlaybackState(): Pair<Int, Boolean> {
        return Pair(currentSongIndex, isPlaying)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        println("MUSIC_PLAYER_SERVICE onStartCommand")
        val action = intent.action

        when (action) {
            "PLAY" -> playSong(currentSongIndex)
            "PAUSE" -> pauseSong()
            "NEXT" -> nextSong()
            "PREVIOUS" -> previousSong()
            "SEEK" -> {
                val seekPosition = intent.getIntExtra("seek_position", 0)
                seekTo(seekPosition)
            }
            "SHUFFLE" -> toggleShuffle()
            "CANCEL" -> stopService()
        }

//        return START_STICKY
        return START_NOT_STICKY
    }

    // Return the binder instance to allow the activity to access the service
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    // Public method to set the list of songs
    fun setSongList(songs: List<Song>) {
        this.songs = songs
        this.originalSongsList = songs
    }


    fun playSong(index: Int) {
        println("MUSIC_PLAYER_SERVICE playSong: Service is playSong")
        if (songs.isEmpty()) {
            // Handle the case where the list is empty, e.g., show an error or log it
            return
        }

        if (index < 0 || index >= songs.size) return

        currentSongIndex = index
        val song = songs[index]

        // Release any existing mediaPlayer before starting a new song
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(song.url)
            prepare()
            start()

            // Set a listener to move to the next song when the current song finishes
            setOnCompletionListener {
                nextSong() // Automatically move to the next song
            }
        }

        isPlaying = true
        updateNotification(song.title, isPlaying, 1F)
        sendRefreshStateBroadcast() // Send broadcast
        handler.post(updateProgressRunnable) // Start updating notification progress
    }



    fun pauseSong() {
        println("MUSIC_PLAYER_SERVICE pauseSong: Service is pauseSong")
        mediaPlayer?.pause()
        isPlaying = false
        sendRefreshStateBroadcast() // Send broadcast
        handler.removeCallbacks(updateProgressRunnable) // Stop updating notification progress
        updateNotification(songs[currentSongIndex].title, isPlaying = false,0F)
//        removeNotification()
    }

     fun removeNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        if (notificationManager != null) {
            serviceScope.launch {
                try {
                    repeat(3) {
                        delay(1000) // Wait for 1 second
                        notificationManager.cancelAll()
                    }
                } catch (e: Exception) {
                    e.printStackTrace() // Log the exception to prevent crashes
                }
            }
        } else {
            Log.e("NotificationManager", "NotificationManager is null")
        }
    }

    private fun stopService() {

        println("MUSIC_PLAYER_SERVICE stopService: Service is stopping")
//        removeNotification()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopForeground(STOP_FOREGROUND_REMOVE) // Dismiss the notification
        stopSelf() // Stop the service
    }

//    fun nextSong() {
//        if (currentSongIndex < songs.size - 1) {
//            currentSongIndex++
//            playSong(currentSongIndex)
//        }
//    }
//
//    fun previousSong() {
//        if (currentSongIndex > 0) {
//            currentSongIndex--
//            playSong(currentSongIndex)
//        }
//    }

    fun nextSong() {
        if (currentSongIndex < songs.size - 1) {
            currentSongIndex++
        } else {
            // If the last song is reached, go back to the first song
            currentSongIndex = 0
        }
        playSong(currentSongIndex)
        sendRefreshStateBroadcast() // Send broadcast
    }

    fun previousSong() {
        if (currentSongIndex > 0) {
            currentSongIndex--
        } else {
            // If the first song is reached, go to the last song
            currentSongIndex = songs.size - 1
        }
        playSong(currentSongIndex)
        sendRefreshStateBroadcast() // Send broadcast
    }

    // Seek to a specific position in the current song
    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        // Optionally, update the notification to reflect the change in position
        updateNotification(songs[currentSongIndex].title, isPlaying,1F)
    }

//    // Shuffle the song list
//    fun shuffleSongs() {
//        isShuffle = true
//        originalSongsList = songs // Keep a copy of the original list
//        songs = songs.shuffled() // Shuffle the songs
//        playSong(0) // Play the first song from the shuffled list
//    }
//
//    // Restore the original song list
//    fun restoreOriginalOrder() {
//        isShuffle = false
//        songs = originalSongsList // Restore original order
//        playSong(0) // Play the first song from the original list
//    }

    private fun toggleShuffle() {
        if (isShuffle) {
            restoreOriginalOrder()  // If shuffle is on, restore original order
        } else {
            shuffleSongs()  // Shuffle the list if shuffle is off
        }
    }

    fun shuffleSongs() {
        if (!isShuffle) {
            isShuffle = true
            originalSongsList = songs // Keep a copy of the original list
            songs = songs.shuffled() // Shuffle the songs
            currentSongIndex = 0 // Start from the first song in the shuffled list
            playSong(currentSongIndex) // Play the first song from the shuffled list
        }
        updateNotification(songs[currentSongIndex].title, isPlaying,0F) // Update notification with shuffled state
    }

    fun restoreOriginalOrder() {
        if (isShuffle) {
            isShuffle = false
            songs = originalSongsList // Restore original order
            currentSongIndex = 0 // Start from the first song in the original list
            playSong(currentSongIndex) // Play the first song from the original list
        }
        updateNotification(songs[currentSongIndex].title, isPlaying,0F) // Update notification with restored order
    }


    // Method to format milliseconds as mm:ss
    private fun formatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, NotificationActionReceiver::class.java).apply { this.action = action }
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }


    private fun updateNotification(title: String, isPlaying: Boolean, playBackSpeed: Float) {
        println("MUSIC_PLAYER_SERVICE updateNotification")


        val playPauseIcon = if (isPlaying) com.arges.sepan.argmusicplayer.R.drawable.arg_music_pause else com.arges.sepan.argmusicplayer.R.drawable.arg_music_play
        val playPauseText = if (isPlaying) "Pause" else "Play"
        val currentPosition = mediaPlayer?.currentPosition ?: 0
        val duration = mediaPlayer?.duration ?: 0
        val formattedCurrentPosition = formatTime(currentPosition)
        val formattedDuration = formatTime(duration)

        val songImageBitmap = BitmapFactory.decodeResource(resources, R.drawable.krishna)

        // Intent to open SongScreen
        val intent = Intent(this, TmrMusicNewActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("intentValue", "")
            putExtra("FROM", "Notification")
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )




        // Update MediaSession state with actions
        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(
                    if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                    currentPosition.toLong(),
                    playBackSpeed
                )
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                            PlaybackStateCompat.ACTION_SEEK_TO
                )
                .build()
        )

        // Set metadata (song duration)
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration.toLong())
                .build()
        )

        mediaSession.setCallback(callback)


        // Create notification with MediaStyle, using the playback actions set in mediaSession
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_account)
            .setLargeIcon(songImageBitmap)
            .setContentTitle(title)
            .setContentText("Playing audio")
            .setSubText("$formattedCurrentPosition / $formattedDuration")
            .setContentIntent(pendingIntent)
            .setStyle(
                MediaStyle()
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(createPendingIntent("CANCEL"))
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2) // Show Previous, Play/Pause, Next
            )
            .addAction(com.arges.sepan.argmusicplayer.R.drawable.arg_music_prev_large, "Previous", createPendingIntent("ACTION_PREVIOUS"))
            .addAction(playPauseIcon, playPauseText, createPendingIntent(if (isPlaying) "ACTION_PAUSE" else "ACTION_PLAY"))
            .addAction(com.arges.sepan.argmusicplayer.R.drawable.arg_music_next_large, "Next", createPendingIntent("ACTION_NEXT"))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setProgress(duration, currentPosition, false)
            .setAutoCancel(false)
            .build()

        // Notify with permission check
        notifyWithPermissionCheck(notification)
    }


    private fun notifyWithPermissionCheck(notification: Notification) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(1, notification)
        } else {
            checkNotificationPermission()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Media Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for media playback controls"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Send a broadcast to MainActivity to request permission
                val permissionIntent = Intent(this, TmrMusicNewActivity::class.java)
                permissionIntent.action = "REQUEST_NOTIFICATION_PERMISSION"
                sendBroadcast(permissionIntent)
            }
        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        songs = mutableListOf()
//        mediaPlayer?.let {
//            if (it.isPlaying) {
//                it.stop()
//            }
//            it.release()
//        }
//        mediaSession.release()
//        NotificationManagerCompat.from(this).cancel(1)
//        stopForeground(STOP_FOREGROUND_REMOVE)
//        // Ensure proper cleanup
//        stopSelf()
//    }
override fun onDestroy() {
    println("MUSIC_PLAYER_SERVICE onDestroy: Service destroyed")
    super.onDestroy()
    songs = mutableListOf()
    mediaPlayer?.let {
        if (it.isPlaying) {
            it.stop()
        }
        it.release()
    }
    mediaSession.release()

//    // Cancel the notification
//    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    notificationManager.cancelAll()
//    // Use the updated stopForeground method
//    stopForeground(STOP_FOREGROUND_REMOVE)
//
//    stopSelf() // Ensure the service stops completely
}



    override fun onTaskRemoved(rootIntent: Intent?) {
        println("MUSIC_PLAYER_SERVICE onTaskRemoved: App removed from recents")
        stopForeground(STOP_FOREGROUND_REMOVE) // Remove the notification
        stopSelf() // Stop the service
        super.onTaskRemoved(rootIntent)
    }


    override fun onUnbind(intent: Intent?): Boolean {
        println("MUSIC_PLAYER_SERVICE onUnbind: App is onUnbind")

        return super.onUnbind(intent)
    }

    override fun onTimeout(startId: Int) {
        println("MUSIC_PLAYER_SERVICE onTimeout: App is onTimeout")
        super.onTimeout(startId)
    }




    // Broadcast the playback state
    private fun sendRefreshStateBroadcast() {
        val intent = Intent("com.example.audioplayer.REFRESH_STATE").apply {
            putExtra("isRefreshing", "Refresh")
            putExtra("currentSongIndex", currentSongIndex)
            putExtra("isPlaying", isPlaying)
        }
        sendBroadcast(intent)
    }

    // Define the binder class
    inner class MusicPlayerBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }
}

