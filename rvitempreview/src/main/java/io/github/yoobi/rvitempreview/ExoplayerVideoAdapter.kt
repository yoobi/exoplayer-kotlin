package io.github.yoobi.rvitempreview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem

class ExoplayerVideoAdapter(private val listener: ExoplayerOnClickListener) :
    RecyclerView.Adapter<ExoplayerVideoAdapter.VideoViewHolder>() {

    var data = listOf<VideoCard>()

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val element = data[position]

        holder.bind(element)
        holder.itemView.setOnClickListener {
            listener.onClick(element)
        }
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val parent: View = itemView
        private val title: TextView = itemView.findViewById(R.id.video_title)
        val videoContainer: FrameLayout = itemView.findViewById(R.id.video_container)
        val imageView: ImageView = itemView.findViewById(R.id.video_thumbnail)
        val videoProgressbar: ProgressBar = itemView.findViewById(R.id.video_progressbar)
        lateinit var videoPreview: MediaItem

        fun bind(data: VideoCard) {
            // Initialize for ExoplayerRecyclerView
            parent.tag = this
            videoPreview = MediaItem.fromUri(data.videoPreview)

            title.text = data.title
            Glide.with(itemView).load(data.videoThumbnail).into(imageView)
        }

        companion object {
            fun from(parent: ViewGroup): VideoViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return VideoViewHolder(inflater.inflate(R.layout.item_video, parent, false))
            }
        }
    }

    class ExoplayerOnClickListener(val clickListener: (video: VideoCard) -> Unit) {
        fun onClick(video: VideoCard) = clickListener(video)
    }
}