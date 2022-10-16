package io.github.yoobi.nativepreview

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.TimeBar
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util

class MainActivity : AppCompatActivity() {

    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var dataSourceFactory: DataSource.Factory
    private lateinit var playerView: PlayerView
    private lateinit var exoProgress: DefaultTimeBar
    private lateinit var previewFrameLayout: FrameLayout
    private lateinit var previewImage: ImageView

    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var isFullscreen = false
    private var isPlayerPlaying = true
    private val mediaItem = MediaItem.Builder()
        .setUri(HLS_STATIC_URL)
        .setMimeType(MimeTypes.APPLICATION_M3U8)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.player_view)
        exoProgress = playerView.findViewById(R.id.exo_progress)
        previewFrameLayout = playerView.findViewById(R.id.preview_frame_layout)
        previewImage = playerView.findViewById(R.id.preview_image)

        dataSourceFactory = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, "testapp")
        )

        if (savedInstanceState != null) {
            currentWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW)
            playbackPosition = savedInstanceState.getLong(STATE_RESUME_POSITION)
            isFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN)
            isPlayerPlaying = savedInstanceState.getBoolean(STATE_PLAYER_PLAYING)
        }
    }

    private fun initPlayer() {
        exoPlayer = SimpleExoPlayer.Builder(this).build().apply {
            playWhenReady = isPlayerPlaying
            seekTo(currentWindow, playbackPosition)
            setMediaItem(mediaItem, false)
            prepare()
        }
        playerView.player = exoPlayer

        exoProgress.addListener(object : TimeBar.OnScrubListener {
            override fun onScrubMove(timeBar: TimeBar, position: Long) {
                previewFrameLayout.visibility = View.VISIBLE
                previewFrameLayout.x =
                    updatePreviewX(position.toInt(), exoPlayer.duration.toInt()).toFloat()

                val drawable = previewImage.drawable
                var glideOptions = RequestOptions().dontAnimate().skipMemoryCache(false)
                if (drawable != null) {
                    glideOptions = glideOptions.placeholder(drawable)
                }

                Glide.with(previewImage).asBitmap()
                    .apply(glideOptions)
                    .load(THUMBNAIL_MOSAIQUE)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .transform(GlideThumbnailTransformation(position))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(previewImage)
            }

            override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                previewFrameLayout.visibility = View.INVISIBLE
            }

            override fun onScrubStart(timeBar: TimeBar, position: Long) {}
        })

    }

    private fun updatePreviewX(progress: Int, max: Int): Int {
        if (max == 0) {
            return 0
        }

        val parent = previewFrameLayout.parent as ViewGroup
        val layoutParams = previewFrameLayout.layoutParams as MarginLayoutParams
        val offset = progress.toFloat() / max
        val minimumX: Int = previewFrameLayout.left
        val maximumX = (parent.width - parent.paddingRight - layoutParams.rightMargin)

        val previewPaddingRadius: Int =
            resources.getDimensionPixelSize(R.dimen.scrubber_dragged_size).div(2)
        val previewLeftX = (exoProgress as View).left.toFloat()
        val previewRightX = (exoProgress as View).right.toFloat()
        val previewSeekBarStartX: Float = previewLeftX + previewPaddingRadius
        val previewSeekBarEndX: Float = previewRightX - previewPaddingRadius
        val currentX = (previewSeekBarStartX + (previewSeekBarEndX - previewSeekBarStartX) * offset)
        val startX: Float = currentX - previewFrameLayout.width / 2f
        val endX: Float = startX + previewFrameLayout.width

        // Clamp the moves
        return if (startX >= minimumX && endX <= maximumX) {
            startX.toInt()
        } else if (startX < minimumX) {
            minimumX
        } else {
            maximumX - previewFrameLayout.width
        }
    }

    private fun releasePlayer() {
        isPlayerPlaying = exoPlayer.playWhenReady
        playbackPosition = exoPlayer.currentPosition
        currentWindow = exoPlayer.currentWindowIndex
        exoPlayer.release()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_RESUME_WINDOW, exoPlayer.currentWindowIndex)
        outState.putLong(STATE_RESUME_POSITION, exoPlayer.currentPosition)
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, isFullscreen)
        outState.putBoolean(STATE_PLAYER_PLAYING, isPlayerPlaying)
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initPlayer()
            playerView.onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23) {
            initPlayer()
            playerView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            playerView.onPause()
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            playerView.onPause()
            releasePlayer()
        }
    }

    companion object {
        const val HLS_STATIC_URL =
            "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"
        const val THUMBNAIL_MOSAIQUE =
            "https://bitdash-a.akamaihd.net/content/MI201109210084_1/thumbnails/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.jpg"
        const val STATE_RESUME_WINDOW = "resumeWindow"
        const val STATE_RESUME_POSITION = "resumePosition"
        const val STATE_PLAYER_FULLSCREEN = "playerFullscreen"
        const val STATE_PLAYER_PLAYING = "playerOnPlay"
    }
}