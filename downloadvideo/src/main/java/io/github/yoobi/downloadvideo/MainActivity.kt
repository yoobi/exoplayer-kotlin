package io.github.yoobi.downloadvideo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.yoobi.downloadvideo.common.MediaItemTag
import io.github.yoobi.downloadvideo.common.MyDownloadService
import io.github.yoobi.downloadvideo.offlineVideo.OfflineVideoActivity

class MainActivity : AppCompatActivity() {

    private val listMediaItem: List<MediaItem> = listOf(
        MediaItem.Builder()
            .setUri("https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8")
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .setMediaMetadata(MediaMetadata.Builder().setTitle("Google Example").build())
            .build(),
        MediaItem.Builder()
            .setUri("http://demo.unified-streaming.com/video/tears-of-steel/tears-of-steel.ism/.m3u8")
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .setMediaMetadata(MediaMetadata.Builder().setTitle("Tears of Steel").build())
            .build(),
        MediaItem.Builder()
            .setUri("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8")
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .setMediaMetadata(MediaMetadata.Builder().setTitle("Animation Movie Example").build())
            .build(),
        MediaItem.Builder()
            .setUri("http://playertest.longtailvideo.com/adaptive/oceans_aes/oceans_aes.m3u8")
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .setMediaMetadata(MediaMetadata.Builder().setTitle("Ocean Example").build())
            .build(),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Can be moved to Application class
        try {
            DownloadService.start(this, MyDownloadService::class.java)
        } catch (e: IllegalStateException) {
            DownloadService.startForeground(this, MyDownloadService::class.java)
        }

        val onlineAdapter = OnlineAdapter()
        findViewById<RecyclerView>(R.id.rv_online_video).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = onlineAdapter
        }

        onlineAdapter.submitList(listMediaItem)

        findViewById<FloatingActionButton>(R.id.fab_my_downloads).setOnClickListener {
            startActivity(Intent(this, OfflineVideoActivity::class.java))
        }
    }

}
