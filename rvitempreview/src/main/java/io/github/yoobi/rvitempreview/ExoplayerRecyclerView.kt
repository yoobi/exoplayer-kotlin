package io.github.yoobi.rvitempreview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import androidx.media3.common.Player
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

class ExoplayerRecyclerView : RecyclerView {

    // ui
    private var thumbnail: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var viewHolderParent: View? = null
    private var frameLayout: FrameLayout? = null
    private lateinit var videoSurfaceView: PlayerView
    private var videoPlayer: ExoPlayer? = null

    // vars
    private var isVideoViewAdded = false

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        videoSurfaceView = PlayerView(context)
        videoSurfaceView.videoSurfaceView
        videoSurfaceView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

        // 2. Create the player
        videoPlayer = ExoPlayer.Builder(context).build()
        // Bind the player to the view.
        videoSurfaceView.useController = false
        videoSurfaceView.player = videoPlayer
        videoPlayer?.volume = 0f
        videoPlayer?.repeatMode = Player.REPEAT_MODE_ONE

        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == SCROLL_STATE_IDLE) {
                    if(thumbnail != null) { // show the old thumbnail
                        thumbnail?.visibility = View.VISIBLE
                    }
                }
            }

        })
        addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                view.setOnLongClickListener { v ->
                    playVideo(v)
                    true
                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                if(viewHolderParent != null && viewHolderParent == view) {
                    resetVideoView()
                }
            }
        })
        videoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        progressBar?.visibility = View.VISIBLE
                    }
                    Player.STATE_READY -> {
                        progressBar?.visibility = View.GONE
                        if(!isVideoViewAdded) addVideoView()
                    }
                    else -> super.onPlaybackStateChanged(playbackState)
                }
            }
        })
    }

    fun playVideo(view: View) {
        // video is already playing so return
        if(viewHolderParent != null && viewHolderParent == view) {
            return
        } else {
            // remove any old surface views from previously playing videos
            progressBar?.visibility = View.GONE
            resetVideoView()
        }

        // set the position of the list-item that is to be played
        if(!::videoSurfaceView.isInitialized) return

        val holder = view.tag as ExoplayerVideoAdapter.VideoViewHolder? ?: return
        thumbnail = holder.imageView
        progressBar = holder.videoProgressbar
        viewHolderParent = holder.itemView
        frameLayout = holder.videoContainer
        videoSurfaceView.player = videoPlayer
        val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(context)

        holder.videoPreview.let {
            val videoSource: MediaSource =
                ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(it)
            videoPlayer?.setMediaSource(videoSource)
            videoPlayer?.prepare()
            videoPlayer?.playWhenReady = true
        }
    }

    // Remove the old player
    private fun removeVideoView(videoView: PlayerView?) {
        val parent = videoView?.parent as ViewGroup?
        val index = parent?.indexOfChild(videoView)
        if(index != null && index >= 0) {
            parent.removeViewAt(index)
            isVideoViewAdded = false
        }
    }

    private fun addVideoView() {
        frameLayout!!.addView(videoSurfaceView)
        isVideoViewAdded = true
        videoSurfaceView.requestFocus()
        videoSurfaceView.visibility = View.VISIBLE
        videoSurfaceView.alpha = 1f
        thumbnail?.visibility = View.GONE
    }

    private fun resetVideoView() {
        if(isVideoViewAdded) {
            removeVideoView(videoSurfaceView)
            progressBar?.visibility = View.INVISIBLE
            videoSurfaceView.visibility = View.INVISIBLE
            thumbnail?.visibility = View.VISIBLE
        }
    }

    fun releasePlayer() {
        if(videoPlayer != null) {
            videoPlayer?.release()
            videoPlayer = null
        }
        resetVideoView()
        viewHolderParent = null
    }

    fun createPlayer() {
        if(videoPlayer == null) init(context)
    }
}