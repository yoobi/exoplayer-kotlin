package io.github.yoobi.downloadvideo.offlineVideo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.offline.Download
import io.github.yoobi.downloadvideo.BUNDLE_MIME_TYPES
import io.github.yoobi.downloadvideo.BUNDLE_TITLE
import io.github.yoobi.downloadvideo.BUNDLE_URL
import io.github.yoobi.downloadvideo.R
import io.github.yoobi.downloadvideo.common.DownloadUtil
import io.github.yoobi.downloadvideo.common.PieProgressDrawable
import io.github.yoobi.downloadvideo.common.formatFileSize
import io.github.yoobi.downloadvideo.player.PlayerActivity
import kotlin.math.roundToInt

class OfflineVideoAdapter :
    ListAdapter<Download, OfflineVideoAdapter.DownloadedVideoViewHolder>(DownloadDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadedVideoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_downloaded, parent, false)
        return DownloadedVideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: DownloadedVideoViewHolder, position: Int) {
        val download = getItem(position)
        holder.bind(download)

        if (download.state == Download.STATE_COMPLETED && download.percentDownloaded > 99f) {
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
            DownloadUtil.getDownloadTracker(it.context)
                .toggleDownloadPopupMenu(it.context, it, download.request.uri)
        }
    }

    override fun onBindViewHolder(
        holder: DownloadedVideoViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            return
        }

        if (payloads[0] is Bundle) {
            val diffBundle: Bundle = payloads[0] as Bundle
            Log.e("OfflineAdapter", diffBundle.toString())
            diffBundle.getInt(BUNDLE_STATE, -1).let {
                if (it != -1) holder.status.text =
                    DownloadUtil.getDownloadString(holder.status.context, it)
                holder.imageMenu.apply ImageView@{
                    when (it) {
                        Download.STATE_DOWNLOADING -> {
                            holder.percentage.visibility = View.VISIBLE
                            if (drawable !is PieProgressDrawable) setImageDrawable(
                                PieProgressDrawable().apply {
                                    setColor(
                                        ContextCompat.getColor(
                                            this@ImageView.context,
                                            R.color.colorAccent
                                        )
                                    )
                                })
                        }
                        Download.STATE_QUEUED, Download.STATE_STOPPED -> {
                            holder.percentage.visibility = View.INVISIBLE
                            setImageDrawable(
                                ContextCompat.getDrawable(
                                    this.context,
                                    R.drawable.ic_pause
                                )
                            )
                        }
                        Download.STATE_COMPLETED -> {
                            holder.percentage.visibility = View.VISIBLE
                            setImageDrawable(
                                ContextCompat.getDrawable(
                                    this.context,
                                    R.drawable.ic_download_done
                                )
                            )
                        }
                        else -> {}
                    }
                }
            }

            val bytesDownload = diffBundle.getLong(BUNDLE_BYTES_DOWNLOADED, -1)
            diffBundle.getFloat(BUNDLE_PERCENTAGE).let {
                if (holder.imageMenu.drawable is PieProgressDrawable) {
                    holder.imageMenu.drawable.level = it.roundToInt()
                    holder.imageMenu.invalidate()
                }
                if (it != -1f && bytesDownload != -1L) {
                    holder.percentage.apply {
                        text = context.resources.getString(
                            R.string.item_download_percentage,
                            bytesDownload.formatFileSize(),
                            it.roundToInt()
                        )
                    }
                }
            }
        }

    }

    class DownloadedVideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val title: TextView = view.findViewById(R.id.item_download_title)
        val percentage: TextView = view.findViewById(R.id.item_download_percentage)
        val status: TextView = view.findViewById(R.id.item_download_status)
        val imageMenu: ImageView = view.findViewById(R.id.item_download_overflow)

        fun bind(download: Download) {
            Log.e(
                "OfflineAdapter",
                "status: ${download.state} - ${
                    DownloadUtil.getDownloadString(
                        status.context,
                        download.state
                    )
                } " +
                        "progress: ${download.percentDownloaded}"
            )
            imageMenu.apply ImageView@{
                when (download.state) {
                    Download.STATE_DOWNLOADING -> {
                        if (drawable !is PieProgressDrawable) setImageDrawable(PieProgressDrawable().apply {
                            setColor(
                                ContextCompat.getColor(
                                    this@ImageView.context,
                                    R.color.colorAccent
                                )
                            )
                        })
                    }
                    Download.STATE_QUEUED, Download.STATE_STOPPED -> {
                        setImageDrawable(
                            ContextCompat.getDrawable(
                                this.context,
                                R.drawable.ic_pause
                            )
                        )
                    }
                    Download.STATE_COMPLETED -> {
                        setImageDrawable(
                            ContextCompat.getDrawable(
                                this.context,
                                R.drawable.ic_download_done
                            )
                        )
                    }
                    else -> {}
                }
            }

            if (imageMenu.drawable is PieProgressDrawable) {
                imageMenu.drawable.level = download.percentDownloaded.roundToInt()
                imageMenu.invalidate()
            }

            title.text = download.request.id

            status.text = DownloadUtil.getDownloadString(status.context, download.state)

            if (download.state == Download.STATE_DOWNLOADING || download.state == Download.STATE_COMPLETED) {
                percentage.visibility = View.VISIBLE
                percentage.text = percentage.context.resources.getString(
                    R.string.item_download_percentage,
                    download.bytesDownloaded.formatFileSize(),
                    download.percentDownloaded.roundToInt()
                )
            } else {
                percentage.visibility = View.INVISIBLE
            }
        }
    }

    object DownloadDiffCallback : DiffUtil.ItemCallback<Download>() {
        override fun areItemsTheSame(oldItem: Download, newItem: Download): Boolean {
            return oldItem.request == newItem.request
        }

        override fun areContentsTheSame(oldItem: Download, newItem: Download): Boolean {
            return false
        }

        override fun getChangePayload(oldItem: Download, newItem: Download): Any {
            val diffBundle = Bundle()

            diffBundle.putInt(BUNDLE_STATE, newItem.state)
            diffBundle.putFloat(BUNDLE_PERCENTAGE, newItem.percentDownloaded)
            diffBundle.putLong(BUNDLE_BYTES_DOWNLOADED, newItem.bytesDownloaded)

            return diffBundle
        }
    }

    companion object {
        private const val BUNDLE_PERCENTAGE = "bundle_percentage"
        private const val BUNDLE_STATE = "bundle_state"
        private const val BUNDLE_BYTES_DOWNLOADED = "bundle_bytes_downloaded"
    }
}
