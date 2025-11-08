package com.raman.kumar.shrikrishan.audio_player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationActionReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, MusicPlayerService::class.java)

        println("MUSIC_PLAYER_SERVICE NotificationActionReceiver ${intent.action}")
        when (intent.action) {


            "ACTION_PLAY" -> {
                Log.d("NotificationReceiver", "Play action clicked")
                serviceIntent.action = "PLAY"
            }
            "ACTION_PAUSE" -> {
                Log.d("NotificationReceiver", "Pause action clicked")
                serviceIntent.action = "PAUSE"
            }
            "ACTION_NEXT" -> {
                Log.d("NotificationReceiver", "Next action clicked")
                serviceIntent.action = "NEXT"
            }
            "ACTION_PREVIOUS" -> {
                Log.d("NotificationReceiver", "Previous action clicked")
                serviceIntent.action = "PREVIOUS"
            }
            "ACTION_SEEK" -> {
                // Get the current seek position
                val seekPosition = intent.getIntExtra("seek_position", 0)
                Log.d("NotificationReceiver", "Seek action clicked at position: $seekPosition")
                serviceIntent.action = "SEEK"
                serviceIntent.putExtra("seek_position", seekPosition)
            }
            "ACTION_SHUFFLE" -> {
                // Handle shuffle action by passing it to the service
                Log.d("NotificationReceiver", "Shuffle action clicked")
                // Toggle shuffle state
                serviceIntent.action = "SHUFFLE"
            }
            "ACTION_CANCEL" -> {
                Log.d("NotificationReceiver", "Cancel action clicked")
                serviceIntent.action = "CANCEL"
            }
        }
        context.startService(serviceIntent) // Start the service to handle the action
    }


}
