package io.github.yoobi.downloadvideo.offlineVideo

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.offline.Download
import io.github.yoobi.downloadvideo.*
import io.github.yoobi.downloadvideo.common.DownloadUtil
import io.github.yoobi.downloadvideo.common.PieProgressDrawable
import io.github.yoobi.downloadvideo.common.formatFileSize
import io.github.yoobi.downloadvideo.player.PlayerActivity
import kotlin.math.roundToInt

private const val BUNDLE_PERCENTAGE = "bundle_percentage"
private const val BUNDLE_STATE = "bundle_state"
private const val BUNDLE_BYTES_DOWNLOADED = "bundle_bytes_downloaded"

class OfflineVideoAdapter: ListAdapter<Download, OfflineVideoAdapter.DownloadedVideoViewHolder>(DownloadDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadedVideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_downloaded, parent, false)
        return DownloadedVideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: DownloadedVideoViewHolder, position: Int) {
        val download = getItem(position)
        holder.bind(download)

        if(download.state == Download.STATE_COMPLETED && download.percentDownloaded > 99f) {
            holder.itemView.setOnClickListener {
                it.context.startActivity(
                    Intent(it.context, PlayerActivity::class.java)
                        .putExtra(BUNDLE_TITLE, download.request.id)
                        .putExtra(BUNDLE_URL, download.request.uri.toString())
                        .putExtra(BUNDLE_MIME_TYPES, download.request.mimeType)
                )
            }
        }

        holder.imageMenu.setOnClickListener {
            DownloadUtil.getDownloadTracker(it.context).toggleDownloadPopupMenu(it.context, it, download.request.uri)
        }
    }

    class DownloadedVideoViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val title: TextView = view.findViewById(R.id.item_download_title)
        private val percentage: TextView = view.findViewById(R.id.item_download_percentage)
        private val status: TextView = view.findViewById(R.id.item_download_status)
        val imageMenu: ImageView = view.findViewById(R.id.item_download_overflow)

        fun bind(download: Download) {
//            Log.e("DownloadAdapter",
//                "status: ${download.state} - ${DownloadUtil.getDownloadString(status.context, download.state)} " +
//                        "progress: ${download.percentDownloaded}"
//            )

            imageMenu.apply ImageView@ {
                when(download.state) {
                    Download.STATE_DOWNLOADING -> {
                        if(drawable !is PieProgressDrawable) setImageDrawable(PieProgressDrawable().apply {
                            setColor(ContextCompat.getColor(this@ImageView.context, R.color.colorAccent))
                        })

                    }
                    Download.STATE_QUEUED, Download.STATE_STOPPED -> {
                        setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_pause))
                    }
                    Download.STATE_COMPLETED -> {
                        setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_download_done))
                    }
                    else -> { }
                }
            }

            if(imageMenu.drawable is PieProgressDrawable) {
                imageMenu.drawable.level = download.percentDownloaded.roundToInt()
                imageMenu.invalidate()
            }

            title.text = download.request.id

            status.text = DownloadUtil.getDownloadString(status.context, download.state)

            if (download.state == Download.STATE_DOWNLOADING || download.state == Download.STATE_COMPLETED) {
                percentage.visibility = View.VISIBLE
                percentage.text = percentage.context.resources.getString(
                    R.string.item_download_percentage,
                    download.bytesDownloaded.formatFileSize(), download.percentDownloaded.roundToInt()
                )
            } else {
                percentage.visibility = View.INVISIBLE
            }
        }
    }

    object DownloadDiffCallback: DiffUtil.ItemCallback<Download>() {
        override fun areItemsTheSame(oldItem: Download, newItem: Download): Boolean {
            return oldItem.request.id == newItem.request.id
        }

        override fun areContentsTheSame(oldItem: Download, newItem: Download): Boolean {
            return (oldItem.request == newItem.request
                    && oldItem.state == newItem.state
                    && oldItem.updateTimeMs == newItem.updateTimeMs
                    && oldItem.bytesDownloaded == newItem.bytesDownloaded
                    && oldItem.percentDownloaded == newItem.percentDownloaded)
        }
    }
}
