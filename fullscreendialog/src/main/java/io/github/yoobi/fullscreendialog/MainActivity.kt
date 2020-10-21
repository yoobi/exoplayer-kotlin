package io.github.yoobi.fullscreendialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*

const val HLS_STATIC_URL = "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"
const val STATE_RESUME_WINDOW = "resumeWindow"
const val STATE_RESUME_POSITION = "resumePosition"
const val STATE_PLAYER_FULLSCREEN = "playerFullscreen"
const val STATE_PLAYER_PLAYING = "playerOnPlay"

class MainActivity : AppCompatActivity() {

    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var dataSourceFactory: DataSource.Factory

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

        dataSourceFactory = DefaultDataSourceFactory(this,
            Util.getUserAgent(this, "testapp"))

        initFullScreenDialog()
        initFullScreenButton()

        if (savedInstanceState != null) {
            currentWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW)
            playbackPosition = savedInstanceState.getLong(STATE_RESUME_POSITION)
            isFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN)
            isPlayerPlaying = savedInstanceState.getBoolean(STATE_PLAYER_PLAYING)
        }
    }

    private fun initPlayer(){
        exoPlayer = SimpleExoPlayer.Builder(this).build().apply{
            playWhenReady = isPlayerPlaying
            seekTo(currentWindow, playbackPosition)
            setMediaItem(mediaItem, false)
            prepare()
        }
        player_view.player = exoPlayer

        if (isFullscreen) {
            openFullscreenDialog()
        }
    }

    private fun releasePlayer(){
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
            if (player_view != null) player_view.onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23) {
            initPlayer()
            if (player_view != null) player_view.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            if (player_view != null) player_view.onPause()
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            if (player_view != null) player_view.onPause()
            releasePlayer()
        }
    }

    // FULLSCREEN PART

    private fun initFullScreenDialog() {
        fullscreenDialog = object: Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            override fun onBackPressed() {
                if(isFullscreen) closeFullscreenDialog()
                super.onBackPressed()
            }
        }
    }

    private fun initFullScreenButton(){
        exo_fullscreen_button.setOnClickListener {
            if (!isFullscreen) {
                openFullscreenDialog()
            } else {
                closeFullscreenDialog()
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun openFullscreenDialog() {
        exo_fullscreen_icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_shrink))
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        (player_view.parent as ViewGroup).removeView(player_view)
        fullscreenDialog?.addContentView(player_view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        isFullscreen = true
        fullscreenDialog?.show()
    }

    private fun closeFullscreenDialog() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        (player_view.parent as ViewGroup).removeView(player_view)
        (main_media_frame as FrameLayout).addView(player_view)
        exo_fullscreen_icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_expand))
        isFullscreen = false
        fullscreenDialog?.dismiss()
    }
}