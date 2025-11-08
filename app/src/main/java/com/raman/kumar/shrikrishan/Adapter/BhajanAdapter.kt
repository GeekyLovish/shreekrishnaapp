package com.raman.kumar.shrikrishan.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raman.kumar.customClasses.Song
import com.raman.kumar.shrikrishan.MainActivity
import com.raman.kumar.shrikrishan.R
import com.raman.kumar.shrikrishan.application.MyApp
import com.raman.kumar.shrikrishan.audio_player.MusicPlayerService
import com.raman.kumar.shrikrishan.databinding.ItemSongBinding

class BhajanAdapter(
    private val context: Context,
    private val songs: List<Song>,
    private val onSongClicked: (Int) -> Unit
) : RecyclerView.Adapter<BhajanAdapter.SongViewHolder>() {

    private var musicPlayerService: MusicPlayerService? = null

    init {
        val application = context.applicationContext as MyApp
        musicPlayerService = application.getMusicPlayerService()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]

        // Bind song title
        holder.binding.songTitle.text = song.title

        // Set the initial play/pause button state
        updatePlayPauseIcon(holder.binding, position)

        // Handle play button click
        holder.binding.playButton.setOnClickListener {
            musicPlayerService?.let { service ->
                val (currentSongIndex, isPlaying) = service.getPlaybackState()

                if (currentSongIndex == position && isPlaying) {
                    // If the current song is playing, pause it
                    service.pauseSong()
                } else {
                    // Otherwise, play the selected song
                    service.playSong(position)

//                     Notify previous song to update its icon to "play"
                    if (currentSongIndex != -1) {
                        notifyItemChanged(currentSongIndex)
                    }


                }

                // Update the clicked song's play/pause button
                updatePlayPauseIcon(holder.binding, position)
                notifyItemChanged(position) // Notify RecyclerView of the change
                onSongClicked(position) // Notify the activity/fragment
            }
        }
    }

    override fun getItemCount(): Int = songs.size

    /**
     * Updates the play/pause icon for a given position based on the playback state.
     */
    private fun updatePlayPauseIcon(binding: ItemSongBinding, position: Int) {
        val (currentSongIndex, isPlaying) = musicPlayerService?.getPlaybackState() ?: Pair(-1, false)
        binding.playButton.setImageResource(
            if (isPlaying && currentSongIndex == position) R.drawable.ic_pause else R.drawable.ic_play
        )
    }

    class SongViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root)


    fun updatePlayPauseIconForActivity(position: Int) {
        // Get the current song's index and playback state
        val (currentSongIndex, _) = musicPlayerService?.getPlaybackState() ?: Pair(-1, false)

        // Loop through the list of songs and update their play/pause icon
        for (i in songs.indices) {
            // Notify all items to update their icon
            notifyItemChanged(i)
        }

        // Now update the icon for the clicked song, it will be "pause" if it's playing
        if (currentSongIndex != -1) {
            // Ensure the previous song gets the "play" icon (since it's no longer playing)
            notifyItemChanged(currentSongIndex)
        }
        // Set the new song as "pause" if it’s the one being played
        notifyItemChanged(position)
    }

}
