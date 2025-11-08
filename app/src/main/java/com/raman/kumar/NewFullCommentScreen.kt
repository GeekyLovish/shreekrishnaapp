package com.raman.kumar

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.raman.kumar.shrikrishan.R
import com.raman.kumar.shrikrishan.databinding.ActivityNewFullCommentScreenBinding
import com.raman.kumar.utils.Extensions.shareImageFromUrl
import com.raman.kumar.shrikrishan.fbreaction.ReactionPopup
import com.raman.kumar.shrikrishan.fbreaction.ReactionsConfig
import com.raman.kumar.shrikrishan.fbreaction.ReactionsConfigBuilder
import com.raman.kumar.shrikrishan.fbreaction.ReactionSelectedListener

class NewFullCommentScreen : AppCompatActivity() {

    private lateinit var binding: ActivityNewFullCommentScreenBinding

    private var currentReaction: ReactionType? = null
    private var reactionPopup: ReactionPopup? = null

    private enum class ReactionType(val iconRes: Int, val message: String) {
        LIKE(R.drawable.like_, "Liked"),
        LOVE(R.drawable.love, "Loved")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewFullCommentScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadAd()
        loadImage()
        setupListeners()

        onBackPressedDispatcher.addCallback { finish() }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadAd() {
        binding.adView.loadAd(AdRequest.Builder().build())
    }

    private fun loadImage() {
        val imageUrl = intent.getStringExtra("imageUrl").orEmpty()
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.krishna)
            .error(R.drawable.krishna)
            .into(binding.imageView)
    }

    private fun setupListeners() {
        val imageUrl = intent.getStringExtra("imageUrl").orEmpty()

        binding.sharePost.setOnClickListener {
            shareImageFromUrl(imageUrl)
        }

        binding.postLikeButton.setOnClickListener {
            handleLikeTap()
        }

        binding.postLikeButton.setOnLongClickListener {
            showReactionPopup()
            true
        }
    }

    private fun handleLikeTap() {
        if (currentReaction != null) {
            val removedReaction = currentReaction
            removeReaction()
            Toast.makeText(
                this,
                "${removedReaction!!.message} removed",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            applyReaction(ReactionType.LIKE)
        }
    }

    private fun removeReaction() {
        currentReaction = null
        binding.likeIcon.setImageResource(R.drawable.like_icon) // reset to default
    }

    private fun applyReaction(reaction: ReactionType) {
        currentReaction = reaction
        binding.likeIcon.setImageResource(reaction.iconRes)
        Toast.makeText(this, reaction.message, Toast.LENGTH_SHORT).show()
    }

    private fun showReactionPopup() {
        val reactions = intArrayOf(
            R.drawable.like_,
            R.drawable.love
        )

        val config: ReactionsConfig = ReactionsConfigBuilder(this)
            .withReactions(reactions)
            .withReactionSize(resources.getDimensionPixelSize(R.dimen.margin_40))
            .withPopupColor(Color.LTGRAY)
            .build()

        val popup = ReactionPopup(
            context = this,
            reactionsConfig = config,
            reactionSelectedListener = object : ReactionSelectedListener {
                override fun invoke(position: Int): Boolean {
                    when (position) {
                        0 -> {
                            applyReaction(ReactionType.LIKE)
                            Toast.makeText(this@NewFullCommentScreen, "Liked", Toast.LENGTH_SHORT).show()
                        }
                        1 -> {
                            applyReaction(ReactionType.LOVE)
                            Toast.makeText(this@NewFullCommentScreen, "Loved", Toast.LENGTH_SHORT).show()
                        }
                        else -> return false
                    }
                    // REMOVE popup listener after selection so clicks work again
                    binding.postLikeButton.setOnTouchListener(null)
                    return true
                }
            }
        )

        binding.postLikeButton.setOnTouchListener(popup)
    }

    override fun onDestroy() {
        super.onDestroy()
        reactionPopup = null // clear to avoid leaks
    }
}
