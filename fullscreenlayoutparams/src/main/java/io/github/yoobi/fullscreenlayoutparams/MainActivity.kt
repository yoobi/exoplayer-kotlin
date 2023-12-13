package io.github.yoobi.fullscreenlayoutparams

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : AppCompatActivity() {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: PlayerView
    private val windowInsetsController by lazy {
        WindowInsetsControllerCompat(window, window.decorView)
    }

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
        playerView.setFullscreenButtonClickListener {
            Log.e("MainActivity", "isFullscreen: $it")
            if(it) {
                openFullscreen()
            } else {
                closeFullscreen()
            }
        }
        playerView.player = exoPlayer

        if(isFullscreen) openFullscreen()
    }

    private fun releasePlayer() {
        playerView.setFullscreenButtonClickListener(null)
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

    override fun onBackPressed() {
        if(isFullscreen) {
            closeFullscreen()
            return
        }
        super.onBackPressed()
    }

    // FULLSCREEN PART

    @SuppressLint("SourceLockedOrientationActivity")
    private fun openFullscreen() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val params: LinearLayout.LayoutParams = playerView.layoutParams as LinearLayout.LayoutParams
        params.width = LinearLayout.LayoutParams.MATCH_PARENT
        params.height = LinearLayout.LayoutParams.MATCH_PARENT
        playerView.layoutParams = params
        hideSystemUi()
        isFullscreen = true
    }

    private fun closeFullscreen() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val params: LinearLayout.LayoutParams = playerView.layoutParams as LinearLayout.LayoutParams
        params.width = LinearLayout.LayoutParams.MATCH_PARENT
        params.height = 0
        playerView.layoutParams = params
        showSystemUi()
        isFullscreen = false
    }

    private fun hideSystemUi() {
        supportActionBar?.hide()
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun showSystemUi() {
        supportActionBar?.show()
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
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
