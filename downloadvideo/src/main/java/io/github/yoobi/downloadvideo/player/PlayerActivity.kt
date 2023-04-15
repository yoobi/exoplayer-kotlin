package io.github.yoobi.downloadvideo.player

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.util.Util
import com.google.android.material.snackbar.Snackbar
import io.github.yoobi.downloadvideo.OnlineAdapter.Companion.BUNDLE_MIME_TYPES
import io.github.yoobi.downloadvideo.OnlineAdapter.Companion.BUNDLE_TITLE
import io.github.yoobi.downloadvideo.OnlineAdapter.Companion.BUNDLE_URL
import io.github.yoobi.downloadvideo.R
import io.github.yoobi.downloadvideo.common.*
import kotlin.math.roundToInt

class PlayerActivity : AppCompatActivity(), DownloadTracker.Listener {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var progressDrawable: ImageView
    private lateinit var playerView: StyledPlayerView

    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var isFullscreen = false
    private var isPlayerPlaying = true
    private val mediaItem: MediaItem by lazy {
        MediaItem.Builder()
            .setUri(intent.getStringExtra(BUNDLE_URL))
            .setMimeType(intent.getStringExtra(BUNDLE_MIME_TYPES))
            .setMediaMetadata(
                MediaMetadata.Builder().setTitle(intent.getStringExtra(BUNDLE_TITLE)).build()
            )
            .setTag(MediaItemTag(-1, intent.getStringExtra(BUNDLE_TITLE)!!))
            .build()
    }
    private val playerViewModel by lazy {
        ViewModelProvider(this).get(PlayerViewModel::class.java)
    }
    private val pieProgressDrawable: PieProgressDrawable by lazy {
        PieProgressDrawable().apply {
            setColor(ContextCompat.getColor(this@PlayerActivity, R.color.colorAccent))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_video_player)
        playerView = findViewById(R.id.player_view)
        if(savedInstanceState != null) {
            currentWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW)
            playbackPosition = savedInstanceState.getLong(STATE_RESUME_POSITION)
            isFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN)
            isPlayerPlaying = savedInstanceState.getBoolean(STATE_PLAYER_PLAYING)
        }

        DownloadUtil.getDownloadTracker(this).addListener(this)

        progressDrawable = findViewById<ImageView>(R.id.download_state).apply {
            setOnClickListener {
                if(DownloadUtil.getDownloadTracker(context).isDownloaded(mediaItem)) {
                    Snackbar.make(
                        this.rootView,
                        "You've already downloaded the video",
                        Snackbar.LENGTH_SHORT
                    ).setAction("Delete") {
                        DownloadUtil.getDownloadTracker(this@PlayerActivity)
                            .removeDownload(mediaItem.localConfiguration?.uri)
                    }.show()
                } else {
                    val item = mediaItem.buildUpon()
                        .setTag((mediaItem.localConfiguration?.tag as MediaItemTag).copy(duration = exoPlayer.duration))
                        .build()
                    if(!DownloadUtil.getDownloadTracker(this@PlayerActivity)
                            .hasDownload(item.localConfiguration?.uri)
                    ) {
                        DownloadUtil.getDownloadTracker(this@PlayerActivity)
                            .toggleDownloadDialogHelper(this@PlayerActivity, item)
                    } else {
                        DownloadUtil.getDownloadTracker(this@PlayerActivity)
                            .toggleDownloadPopupMenu(
                                this@PlayerActivity,
                                this,
                                item.localConfiguration?.uri
                            )
                    }
                }
            }
        }

        playerViewModel.downloadPercent.observe(this) {
            it?.let {
                pieProgressDrawable.level = it.roundToInt()
                progressDrawable.invalidate()
            }
        }
    }

    private fun initPlayer() {
        val downloadRequest: DownloadRequest? =
            DownloadUtil.getDownloadTracker(this)
                .getDownloadRequest(mediaItem.localConfiguration?.uri)

        val mediaSourceFactory = if(downloadRequest == null) {
            ExoplayerHelper.createMediaSourceFactory(mediaItem)
        } else {
            // Offline content
            ProgressiveMediaSource.Factory(DownloadUtil.getReadOnlyDataSourceFactory(this))
        }

        exoPlayer = ExoPlayer
            .Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
            .apply {
                playWhenReady = isPlayerPlaying
                seekTo(currentWindow, playbackPosition)
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
        DownloadUtil.getDownloadTracker(this).downloads[mediaItem.localConfiguration?.uri!!]?.let {
            // Not so clean, used to set the right drawable on the ImageView
            // And start the Flow if the download is in progress
            onDownloadsChanged(it)
        }
        if(Util.SDK_INT > 23) {
            initPlayer()
            playerView.onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        if(Util.SDK_INT <= 23) {
            initPlayer()
            playerView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if(Util.SDK_INT <= 23) {
            playerView.onPause()
            releasePlayer()
        }
    }

    override fun onStop() {
        playerViewModel.stopFlow()
        super.onStop()
        if(Util.SDK_INT > 23) {
            playerView.onPause()
            releasePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        DownloadUtil.getDownloadTracker(this).removeListener(this)
    }

    override fun onDownloadsChanged(download: Download) {
        when (download.state) {
            Download.STATE_DOWNLOADING -> {
                if(progressDrawable.drawable !is PieProgressDrawable) {
                    progressDrawable.setImageDrawable(pieProgressDrawable)
                }
                playerViewModel.startFlow(this, download.request.uri)
            }
            Download.STATE_QUEUED, Download.STATE_STOPPED -> {
                progressDrawable.setImageDrawable(
                    AppCompatResources.getDrawable(this, R.drawable.ic_pause)
                )
            }
            Download.STATE_COMPLETED -> {
                progressDrawable.setImageDrawable(
                    AppCompatResources.getDrawable(this, R.drawable.ic_download_done)
                )
            }
            Download.STATE_REMOVING -> {
                progressDrawable.setImageDrawable(
                    AppCompatResources.getDrawable(this, R.drawable.ic_download)
                )
            }
            Download.STATE_FAILED, Download.STATE_RESTARTING -> {}
        }
    }

    companion object {
        const val STATE_RESUME_WINDOW = "resumeWindow"
        const val STATE_RESUME_POSITION = "resumePosition"
        const val STATE_PLAYER_FULLSCREEN = "playerFullscreen"
        const val STATE_PLAYER_PLAYING = "playerOnPlay"
    }
}
