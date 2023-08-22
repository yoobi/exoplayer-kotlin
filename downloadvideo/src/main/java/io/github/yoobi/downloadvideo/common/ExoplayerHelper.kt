package io.github.yoobi.downloadvideo.common

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource

object ExoplayerHelper {
    fun createMediaSource(context: Context, mediaItem: MediaItem): MediaSource {
        val dataSource = DownloadUtil.getHttpDataSourceFactory(context)
        return when(mediaItem.localConfiguration?.mimeType) {
            MimeTypes.APPLICATION_M3U8 -> HlsMediaSource.Factory(dataSource)
            MimeTypes.APPLICATION_MP4 -> ProgressiveMediaSource.Factory(dataSource)
            else -> throw IllegalArgumentException("Unknown mimeType ${mediaItem.localConfiguration?.mimeType}")
        }.createMediaSource(mediaItem)
    }
}