package io.github.yoobi.downloadvideo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.MediaItem
import io.github.yoobi.downloadvideo.common.MediaItemTag
import io.github.yoobi.downloadvideo.player.PlayerActivity

const val BUNDLE_TITLE = "MEDIA_ITEM_TITLE"
const val BUNDLE_URL = "MEDIA_ITEM_URL"
const val BUNDLE_MIME_TYPES = "MEDIA_MIME_TYPES"
const val BUNDLE_TAG = "MEDIA_ITEM_TAG"

class OnlineAdapter : ListAdapter<MediaItem, OnlineAdapter.OnlineViewHolder>(DiffUtilOnlineAdapter) {

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
                    .putExtra(BUNDLE_URL, mediaItem.playbackProperties?.uri.toString())
                    .putExtra(BUNDLE_MIME_TYPES, mediaItem.playbackProperties?.mimeType)
//                    .putExtra(BUNDLE_TAG, mediaItem.playbackProperties?.tag as MediaItemTag)
            )
        }
    }

    class OnlineViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val title: TextView = view.findViewById(R.id.item_online_title)
        private val url: TextView = view.findViewById(R.id.item_online_url)

        fun bind(mediaItem: MediaItem) {
            title.text = mediaItem.mediaMetadata.title ?: "No title set"
            url.text = mediaItem.playbackProperties?.uri.toString()
        }
    }

    object DiffUtilOnlineAdapter: DiffUtil.ItemCallback<MediaItem>() {
        override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
            return oldItem == newItem
        }
    }

}
