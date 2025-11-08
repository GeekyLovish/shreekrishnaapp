package com.raman.kumar.shrikrishan

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.raman.kumar.AudiosModal.AudiosModal
import com.raman.kumar.customClasses.Song
import com.raman.kumar.shrikrishan.Adapter.BhajanAdapter
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient
import com.raman.kumar.shrikrishan.application.MyApp
import com.raman.kumar.shrikrishan.audio_player.MusicPlayerService
import com.raman.kumar.shrikrishan.databinding.FragmentBhajanBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BhajanFragment : Fragment() {

    private lateinit var binding: FragmentBhajanBinding
    private lateinit var bhajanAdapter: BhajanAdapter
    private var musicPlayerService: MusicPlayerService? = null
    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private val handler = Handler(Looper.getMainLooper())


    var progress: ProgressDialog? = null

    private val songs = mutableListOf<Song>()

    private var currentPage = 1
    private val perPageLimit = 30
    private var isLoading = false


//private val songs = listOf(
//    Song("Song 1", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"),
//    Song("Song 2", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"),
//    Song("Song 3", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3")
//)


    private val playbackReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == "com.example.audioplayer.REFRESH_STATE") {
                initUi()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBhajanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet as LinearLayout).apply {
            peekHeight = resources.getDimensionPixelSize(R.dimen.peek_height)
            state = BottomSheetBehavior.STATE_COLLAPSED
            isHideable = false
        }
        initUi()
    }

    override fun onResume() {
        super.onResume()
        updateBottomSheetUI()
        // Check if bhajanAdapter is initialized before using it
        if (::bhajanAdapter.isInitialized) {
            bhajanAdapter.updatePlayPauseIconForActivity(musicPlayerService?.currentSongIndex ?: 0)
        }
        val filter = IntentFilter("com.example.audioplayer.REFRESH_STATE")
        requireContext().registerReceiver(playbackReceiver, filter, Context.RECEIVER_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(playbackReceiver)
    }

    private fun initUi() {

        val application = requireContext().applicationContext as MyApp
        musicPlayerService = application.getMusicPlayerService()

        progress = ProgressDialog(activity);

        getAllRingtones()

        setupRecyclerView()
    }

    private fun setupBottomSheet() {
//        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet as LinearLayout).apply {
//            peekHeight = resources.getDimensionPixelSize(R.dimen.peek_height)
//            state = BottomSheetBehavior.STATE_COLLAPSED
//            isHideable = false
//        }

        bottomSheetBehavior!!.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Respond to state changes (collapsed, expanded, etc.)
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        // Handle the collapsed state
                        binding.cancelBtn.visibility = View.GONE
                        binding.playPause.visibility = View.VISIBLE
                   }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // Handle the expanded state
                        binding.cancelBtn.visibility = View.VISIBLE
                        binding.playPause.visibility = View.GONE
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {
                        // Handle dragging
                    }

                    BottomSheetBehavior.STATE_SETTLING -> {
                        // Handle settling
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        // Handle hidden state (if allowed)
                    }

                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        TODO()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Respond to sliding events
            }
        })



        binding.bottomSheet.setOnClickListener {
            bottomSheetBehavior?.state =
                if (bottomSheetBehavior?.state == BottomSheetBehavior.STATE_COLLAPSED)
                    BottomSheetBehavior.STATE_EXPANDED
                else
                    BottomSheetBehavior.STATE_COLLAPSED
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.playBtn2.setOnClickListener {
            if (musicPlayerService?.isPlaying == true) {
                musicPlayerService?.pauseSong()
            } else {
                musicPlayerService?.playSong(musicPlayerService?.currentSongIndex ?: 0)
            }
            bhajanAdapter.updatePlayPauseIconForActivity(musicPlayerService?.currentSongIndex ?: 0)
            updateBottomSheetUI()
        }

        binding.playPause.setOnClickListener {
            if (musicPlayerService?.isPlaying == true) {
                musicPlayerService?.pauseSong()
            } else {
                musicPlayerService?.playSong(musicPlayerService?.currentSongIndex ?: 0)
            }
            bhajanAdapter.updatePlayPauseIconForActivity(musicPlayerService?.currentSongIndex ?: 0)
            updateBottomSheetUI()
        }

        binding.btnNext.setOnClickListener {
            playSong((musicPlayerService?.currentSongIndex ?: 0) + 1)
        }

        binding.btnPrevious.setOnClickListener {
            playSong((musicPlayerService?.currentSongIndex ?: 0) - 1)
        }
        binding.cancelBtn.setOnClickListener {
            // Stop the song
            musicPlayerService?.pauseSong()

            // Collapse the bottom sheet
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicPlayerService?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun playSong(position: Int) {
        if (position in songs.indices) {
            musicPlayerService?.playSong(position)
            updateBottomSheetUI()
            bhajanAdapter.notifyDataSetChanged()
        }
    }

    private fun updateBottomSheetUI() {
        val currentIndex = musicPlayerService?.currentSongIndex ?: -1
        val currentSong = songs.getOrNull(currentIndex)

        currentSong?.let {
            binding.songTitle.text = it.title
        }

        binding.playPause.setImageResource(
            if (musicPlayerService?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
        )

        binding.playBtn2.setImageResource(
            if (musicPlayerService?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
        )

        binding.seekBar.max = musicPlayerService?.mediaPlayer?.duration ?: 0
        binding.seekBar.progress = musicPlayerService?.mediaPlayer?.currentPosition ?: 0

        startSeekBarUpdate()
    }


    private fun startSeekBarUpdate() {
        handler.post(object : Runnable {
            override fun run() {
                val mediaPlayer = musicPlayerService?.mediaPlayer
                if (mediaPlayer != null && mediaPlayer.isPlaying) {
                    val currentPosition = mediaPlayer.currentPosition
                    val duration = mediaPlayer.duration

                    // Update the seek bar progress
                    binding.seekBar.progress = currentPosition

                    // Update the current time and total duration text
                    binding.tvCurrentTime.text = formatTime(currentPosition)
                    binding.tvTotalDuration.text = formatTime(duration)
                }
                handler.postDelayed(this, 1000) // Update every second
            }
        })
    }

    // Helper function to format time in mm:ss
    private fun formatTime(milliseconds: Int): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%d:%02d", minutes, seconds)
    }
    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        bhajanAdapter = BhajanAdapter(requireContext(), songs) { position ->
            updateBottomSheetUI()
        }
        binding.recyclerView.adapter = bhajanAdapter

        // Add scroll listener for pagination
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading ) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount &&
                        firstVisibleItemPosition >= 0
                    ) {
                        getAllRingtones() // Load the next page
                    }
                }
            }
        })
    }



//    private fun getAllRingtones() {
//        showProgressDialog()
//
//        val call: Call<AudiosModal> =
//            RetrofitClient.getInstance().api.getAllAudios("application/json", "bhajan",1,10)
//
//        call.enqueue(object : Callback<AudiosModal?> {
//            override fun onResponse(call: Call<AudiosModal?>, response: Response<AudiosModal?>) {
//                progress?.dismiss()
//
//                if (response.isSuccessful && response.body() != null) {
//                    val getAllAudioResponse: AudiosModal = response.body()!!
//
//                    val apiSongList = getAllAudioResponse.data
//                    if (!apiSongList.isNullOrEmpty()) {
//                        // Map API response to Song list
//                        val fetchedSongs = apiSongList.map { datum ->
//                            Song(
//                                title = datum.title ?: "Unknown Title",
//                                url = datum.path ?: ""
//                            )
//                        }
//
//                        // Update songs list
//                        songs.clear()
//                        songs.addAll(fetchedSongs)
//
//                        musicPlayerService?.setSongList(songs)
//
//                        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
//                        bhajanAdapter = BhajanAdapter(requireContext(), songs) { position ->
////                            playSong(position)
//                            updateBottomSheetUI()
//                        }
//                        binding.recyclerView.adapter = bhajanAdapter
//                        setupBottomSheet()
//                        updateBottomSheetUI()
//                        Log.d("API Response", "Audio data updated in songs list.")
//                    } else {
//                        Log.e("API Response", "No audio data available.")
//                        Toast.makeText(activity, "No audio data available.", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    Log.e("API Response", "Failed to fetch audio data.")
//                    Toast.makeText(activity, "Failed to fetch audio data.", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<AudiosModal?>, t: Throwable) {
//                progress?.dismiss()
//                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
//                Log.e("API Error", "Failed to fetch audios", t)
//            }
//        })
//    }


    private fun getAllRingtones() {
        if (isLoading) return
        isLoading = true
        showProgressDialog()

        val call: Call<AudiosModal> = RetrofitClient.getInstance().api.getAllAudios(
            "application/json", "bhajan", currentPage, perPageLimit
        )

        call.enqueue(object : Callback<AudiosModal?> {
            override fun onResponse(call: Call<AudiosModal?>, response: Response<AudiosModal?>) {
                progress?.dismiss()
                isLoading = false

                if (response.isSuccessful && response.body() != null) {
                    val getAllAudioResponse: AudiosModal = response.body()!!
                    val apiSongList = getAllAudioResponse.data

                    if (!apiSongList.isNullOrEmpty()) {
                        val fetchedSongs = apiSongList.map { datum ->
                            Song(
                                title = datum.title ?: "Unknown Title",
                                url = datum.path ?: ""
                            )
                        }

                        // Append to existing song list
                        songs.addAll(fetchedSongs)

                        musicPlayerService?.setSongList(songs)

                        if (bhajanAdapter == null) {
                            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                            bhajanAdapter = BhajanAdapter(requireContext(), songs) { position ->
                                updateBottomSheetUI()
                            }
                            binding.recyclerView.adapter = bhajanAdapter
                        } else {
                            bhajanAdapter?.notifyDataSetChanged()
                        }

                        setupBottomSheet()
                        updateBottomSheetUI()


                    }
                } else {
                    Toast.makeText(activity, "Failed to fetch audio data.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AudiosModal?>, t: Throwable) {
                progress?.dismiss()
                isLoading = false
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun showProgressDialog() {
//        progress?.apply {
//            setTitle("Loading")
//            setMessage("Wait while loading...")
//            setCancelable(false) // disable dismiss by tapping outside of the dialog
//            show()
//        }
    }





}



