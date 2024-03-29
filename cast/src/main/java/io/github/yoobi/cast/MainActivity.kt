package io.github.yoobi.cast

import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.cast.CastPlayer
import androidx.media3.cast.SessionAvailabilityListener
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.mediarouter.app.MediaRouteButton
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext

@OptIn(UnstableApi::class)
class MainActivity : AppCompatActivity(), SessionAvailabilityListener {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var castPlayer: CastPlayer
    private lateinit var castContext: CastContext
    private lateinit var dataSourceFactory: DataSource.Factory
    private lateinit var playerView: PlayerView

    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var isFullscreen = false
    private var isPlayerPlaying = true
    private val mediaItem = MediaItem.Builder()
        .setUri(HLS_STATIC_URL)
        .setMimeType(MimeTypes.APPLICATION_M3U8)
        .build()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu);
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        castContext = CastContext.getSharedInstance(this)
        castPlayer = CastPlayer(castContext)
        castPlayer.setPlaybackSpeed(2f)
        castPlayer.setSessionAvailabilityListener(this)

        findViewById<MediaRouteButton>(R.id.exo_cast)?.apply {
            CastButtonFactory.setUpMediaRouteButton(context, this)
            dialogFactory = CustomCastThemeFactory()
        }

        playerView = findViewById(R.id.player_view)
        dataSourceFactory = DefaultDataSource.Factory(this)

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
            setPlaybackSpeed(2f)
            setMediaItem(mediaItem, false)
            prepare()
        }
        playerView.player = exoPlayer
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

    private fun startCastPlayer() {
        castPlayer.setMediaItem(mediaItem)
        castPlayer.prepare()
        playerView.player = castPlayer
        exoPlayer.stop()
    }

    private fun startExoPlayer() {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        playerView.player = exoPlayer
        castPlayer.stop()
    }

    override fun onCastSessionAvailable() {
        startCastPlayer()
    }

    override fun onCastSessionUnavailable() {
        startExoPlayer()
    }

    companion object {
        const val HLS_STATIC_URL = "https://cph-p2p-msl.akamaized.net/hls/live/2000341/test/master.m3u8"
        const val STATE_RESUME_WINDOW = "resumeWindow"
        const val STATE_RESUME_POSITION = "resumePosition"
        const val STATE_PLAYER_FULLSCREEN = "playerFullscreen"
        const val STATE_PLAYER_PLAYING = "playerOnPlay"
    }


}
