package io.github.yoobi.downloadvideo.common

import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.util.MimeTypes

object ExoplayerHelper {
    fun createMediaSource(mediaItem: MediaItem): MediaSource {
        val dataSource = DownloadUtil.getHttpDataSourceFactory()
        return when(mediaItem.localConfiguration?.mimeType) {
            MimeTypes.APPLICATION_M3U8 -> HlsMediaSource.Factory(dataSource)
            MimeTypes.APPLICATION_MP4 -> ProgressiveMediaSource.Factory(dataSource)
            else -> throw IllegalArgumentException("Unknown mimeType ${mediaItem.localConfiguration?.mimeType}")
        }.createMediaSource(mediaItem)
    }
}