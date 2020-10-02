package io.github.yoobi.qualityselector

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.ui.TrackSelectionDialogBuilder
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*

const val HLS_STATIC_URL = "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"
const val STATE_RESUME_WINDOW = "resumeWindow"
const val STATE_RESUME_POSITION = "resumePosition"
const val STATE_PLAYER_FULLSCREEN = "playerFullscreen"
const val STATE_PLAYER_PLAYING = "playerOnPlay"
const val MAX_HEIGHT = 539
const val MAX_WIDTH = 959

class MainActivity : AppCompatActivity() {

    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var dataSourceFactory: DataSource.Factory
    private lateinit var trackSelector: DefaultTrackSelector

    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var isFullscreen = false
    private var isPlayerPlaying = true
    private var trackDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataSourceFactory = DefaultDataSourceFactory(this,
            Util.getUserAgent(this, "testapp"))

        exo_quality.setOnClickListener{
            if(trackDialog == null){
                initPopupQuality()
            }
            trackDialog?.show()
        }

        if (savedInstanceState != null) {
            currentWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW)
            playbackPosition = savedInstanceState.getLong(STATE_RESUME_POSITION)
            isFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN)
            isPlayerPlaying = savedInstanceState.getBoolean(STATE_PLAYER_PLAYING)
        }
    }

    private fun initPlayer(){

        trackSelector = DefaultTrackSelector(this)
        // When player is initialized it'll be played with a quality of MaxVideoSize to prevent loading in 1080p from the start
        trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSize(MAX_WIDTH,MAX_HEIGHT))
        exoPlayer = SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build()
        val videoSource = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(HLS_STATIC_URL))

        with(exoPlayer) {
            playWhenReady = isPlayerPlaying
            seekTo(currentWindow, playbackPosition)
            prepare(videoSource, false, false)
        }
        player_view.player = exoPlayer

        //Listener on player
        exoPlayer.addListener(object: Player.EventListener{
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if(playbackState == Player.STATE_READY){
                    exo_quality.visibility = View.VISIBLE
                }
            }
        })
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

    // QUALITY SELECTOR

    private fun initPopupQuality() {
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo
        var videoRenderer : Int? = null

        if(mappedTrackInfo == null) return else exo_quality.visibility = View.VISIBLE

        for(i in 0 until mappedTrackInfo.rendererCount){
            if(isVideoRenderer(mappedTrackInfo, i)){
                videoRenderer = i
            }
        }

        if(videoRenderer == null){
            exo_quality.visibility = View.GONE
            return
        }

        val trackSelectionDialogBuilder = TrackSelectionDialogBuilder(this, getString(R.string.qualitySelector), trackSelector, videoRenderer)
        trackSelectionDialogBuilder.setTrackNameProvider{
            // Override function getTrackName
            getString(R.string.exo_track_resolution_pixel, it.height)
        }
        trackDialog = trackSelectionDialogBuilder.build()
    }

    private fun isVideoRenderer(mappedTrackInfo: MappingTrackSelector.MappedTrackInfo, rendererIndex: Int): Boolean {
        val trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex)
        if (trackGroupArray.length == 0) {
            return false
        }
        val trackType = mappedTrackInfo.getRendererType(rendererIndex)
        return C.TRACK_TYPE_VIDEO == trackType
    }

}