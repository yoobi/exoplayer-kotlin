package io.github.yoobi.fullscreendialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : AppCompatActivity() {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var dataSourceFactory: DataSource.Factory
    private lateinit var playerView: PlayerView
    private lateinit var exoFullScreenIcon: ImageView
    private lateinit var exoFullScreenBtn: FrameLayout
    private lateinit var mainFrameLayout: FrameLayout

    private var fullscreenDialog: Dialog? = null
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
        mainFrameLayout = findViewById(R.id.main_media_frame)
        exoFullScreenBtn = playerView.findViewById(R.id.exo_fullscreen_button)
        exoFullScreenIcon = playerView.findViewById(R.id.exo_fullscreen_icon)

        dataSourceFactory = DefaultDataSource.Factory(this)

        initFullScreenDialog()
        initFullScreenButton()

        if(savedInstanceState != null) {
            currentWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW)
            playbackPosition = savedInstanceState.getLong(STATE_RESUME_POSITION)
            isFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN)
            isPlayerPlaying = savedInstanceState.getBoolean(STATE_PLAYER_PLAYING)
        }
    }

    private fun initPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            playWhenReady = isPlayerPlaying
            seekTo(currentWindow, playbackPosition)
            setMediaItem(mediaItem, false)
            prepare()
        }
        playerView.player = exoPlayer

        if(isFullscreen) {
            openFullscreenDialog()
        }
    }

    private fun releasePlayer() {
        isPlayerPlaying = exoPlayer.playWhenReady
        playbackPosition = exoPlayer.currentPosition
        currentWindow = exoPlayer.currentMediaItemIndex
        exoPlayer.release()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_RESUME_WINDOW, exoPlayer.currentMediaItemIndex)
        outState.putLong(STATE_RESUME_POSITION, exoPlayer.currentPosition)
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, isFullscreen)
        outState.putBoolean(STATE_PLAYER_PLAYING, isPlayerPlaying)
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        if(Build.VERSION.SDK_INT > 23) {
            initPlayer()
            playerView.onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        if(Build.VERSION.SDK_INT <= 23) {
            initPlayer()
            playerView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if(Build.VERSION.SDK_INT <= 23) {
            playerView.onPause()
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if(Build.VERSION.SDK_INT > 23) {
            playerView.onPause()
            releasePlayer()
        }
    }

    // FULLSCREEN PART

    private fun initFullScreenDialog() {
        fullscreenDialog =
            object : Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
                @Deprecated("Deprecated in Java")
                override fun onBackPressed() {
                    if(isFullscreen) closeFullscreenDialog()
                    super.onBackPressed()
                }
            }
    }

    private fun initFullScreenButton() {
        exoFullScreenBtn.setOnClickListener {
            if(!isFullscreen) {
                openFullscreenDialog()
            } else {
                closeFullscreenDialog()
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun openFullscreenDialog() {
        exoFullScreenIcon.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_shrink)
        )
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        (playerView.parent as ViewGroup).removeView(playerView)
        fullscreenDialog?.addContentView(
            playerView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        isFullscreen = true
        fullscreenDialog?.show()
    }

    private fun closeFullscreenDialog() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        (playerView.parent as ViewGroup).removeView(playerView)
        mainFrameLayout.addView(playerView)
        exoFullScreenIcon.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_expand)
        )
        isFullscreen = false
        fullscreenDialog?.dismiss()
    }

    companion object {
        const val HLS_STATIC_URL =
            "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"
        const val STATE_RESUME_WINDOW = "resumeWindow"
        const val STATE_RESUME_POSITION = "resumePosition"
        const val STATE_PLAYER_FULLSCREEN = "playerFullscreen"
        const val STATE_PLAYER_PLAYING = "playerOnPlay"
    }
}