package io.github.yoobi.downloadvideo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.yoobi.downloadvideo.player.PlayerActivity

class OnlineAdapter :
    ListAdapter<MediaItem, OnlineAdapter.OnlineViewHolder>(DiffUtilOnlineAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return OnlineViewHolder(inflater.inflate(R.layout.item_online_row, parent, false))
    }

    override fun onBindViewHolder(holder: OnlineViewHolder, position: Int) {
        val mediaItem: MediaItem = getItem(position)
        holder.bind(mediaItem)
        holder.itemView.setOnClickListener {
            it.context.startActivity(
                Intent(it.context, PlayerActivity::class.java)
                    .putExtra(BUNDLE_TITLE, mediaItem.mediaMetadata.title ?: "No title")
                    .putExtra(BUNDLE_URL, mediaItem.localConfiguration?.uri.toString())
                    .putExtra(BUNDLE_MIME_TYPES, mediaItem.localConfiguration?.mimeType)
            )
        }
    }

    class OnlineViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val title: TextView = view.findViewById(R.id.item_online_title)
        private val url: TextView = view.findViewById(R.id.item_online_url)

        fun bind(mediaItem: MediaItem) {
            title.text = mediaItem.mediaMetadata.title ?: "No title set"
            url.text = mediaItem.localConfiguration?.uri.toString()
        }
    }

    object DiffUtilOnlineAdapter : DiffUtil.ItemCallback<MediaItem>() {
        override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        const val BUNDLE_TITLE = "MEDIA_ITEM_TITLE"
        const val BUNDLE_URL = "MEDIA_ITEM_URL"
        const val BUNDLE_MIME_TYPES = "MEDIA_MIME_TYPES"
    }
}
