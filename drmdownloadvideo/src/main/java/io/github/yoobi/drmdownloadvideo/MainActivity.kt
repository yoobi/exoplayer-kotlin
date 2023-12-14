package io.github.yoobi.drmdownloadvideo

import android.content.Intent
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.DrmConfiguration
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.yoobi.drmdownloadvideo.common.MyDownloadService
import io.github.yoobi.drmdownloadvideo.offlineVideo.OfflineVideoActivity

@OptIn(UnstableApi::class)
class MainActivity : AppCompatActivity() {
    private val listMediaItem: List<MediaItem> = listOf(
        MediaItem.Builder()
            .setUri("https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd")
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .setMediaMetadata(MediaMetadata.Builder().setTitle("Clear - HD (MP4, H264)").build())
            .build(),
        MediaItem.Builder()
            .setUri("https://storage.googleapis.com/wvmedia/clear/hevc/tears/tears.mpd")
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .setMediaMetadata(MediaMetadata.Builder().setTitle("Clear - HD (MP4, H265)").build())
            .build(),
        MediaItem.Builder()
            .setUri("https://storage.googleapis.com/wvmedia/clear/vp9/tears/tears.mpd")
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .setMediaMetadata(MediaMetadata.Builder().setTitle("Clear - HD (WebM, VP9)").build())
            .build(),


        MediaItem.Builder()
            .setUri("https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd")
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .setDrmConfiguration(
                DrmConfiguration.Builder(C.WIDEVINE_UUID)
                    .setLicenseUri("https://proxy.uat.widevine.com/proxy?video_id=2015_tears&provider=widevine_test")
                    .build()
            )
            .setMediaMetadata(MediaMetadata.Builder().setTitle("Licensed - HD H264 (cenc)").build())
            .build(),
        MediaItem.Builder()
            .setUri("https://storage.googleapis.com/wvmedia/cenc/hevc/tears/tears.mpd")
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .setDrmConfiguration(
                DrmConfiguration.Builder(C.WIDEVINE_UUID)
                    .setLicenseUri("https://proxy.uat.widevine.com/proxy?video_id=2015_tears&provider=widevine_test")
                    .build()
            )
            .setMediaMetadata(MediaMetadata.Builder().setTitle("Licensed - HD H265 (cenc)").build())
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
