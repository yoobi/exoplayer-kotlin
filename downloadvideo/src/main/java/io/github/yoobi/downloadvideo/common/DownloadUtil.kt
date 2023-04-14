package io.github.yoobi.downloadvideo.common


import android.content.Context
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import io.github.yoobi.downloadvideo.R
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.Executors

object DownloadUtil {
    const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel"
    private const val TAG = "DownloadUtil"
    private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"

    private lateinit var databaseProvider: DatabaseProvider
    private lateinit var downloadCache: Cache
    private lateinit var dataSourceFactory: DataSource.Factory
    private lateinit var httpDataSourceFactory: HttpDataSource.Factory
    private lateinit var downloadNotificationHelper: DownloadNotificationHelper
    private lateinit var downloadDirectory: File
    private lateinit var downloadManager: DownloadManager
    private lateinit var downloadTracker: DownloadTracker

    @Synchronized
    fun getHttpDataSourceFactory(): HttpDataSource.Factory {
        if(!DownloadUtil::httpDataSourceFactory.isInitialized) {
            httpDataSourceFactory = OkHttpDataSource.Factory(OkHttpClient.Builder().build())
//            httpDataSourceFactory = DefaultHttpDataSource.Factory()
        }
        return httpDataSourceFactory
    }

    @Synchronized
    fun getReadOnlyDataSourceFactory(context: Context): DataSource.Factory {
        if(!DownloadUtil::dataSourceFactory.isInitialized) {
            val contextApplication = context.applicationContext
            val upstreamFactory = DefaultDataSource.Factory(
                contextApplication,
                getHttpDataSourceFactory()
            )
            dataSourceFactory =
                buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache(contextApplication))
        }
        return dataSourceFactory
    }

    @Synchronized
    fun getDownloadNotificationHelper(context: Context?): DownloadNotificationHelper {
        if(!DownloadUtil::downloadNotificationHelper.isInitialized) {
            downloadNotificationHelper =
                DownloadNotificationHelper(context!!, DOWNLOAD_NOTIFICATION_CHANNEL_ID)
        }
        return downloadNotificationHelper
    }

    @Synchronized
    fun getDownloadManager(context: Context): DownloadManager {
        ensureDownloadManagerInitialized(context)
        return downloadManager
    }

    @Synchronized
    fun getDownloadTracker(context: Context): DownloadTracker {
        ensureDownloadManagerInitialized(context)
        return downloadTracker
    }

    fun getDownloadString(context: Context, @Download.State downloadState: Int): String {
        return when (downloadState) {
            Download.STATE_COMPLETED -> context.resources.getString(R.string.exo_download_completed)
            Download.STATE_DOWNLOADING -> context.resources.getString(R.string.exo_download_downloading)
            Download.STATE_FAILED -> context.resources.getString(R.string.exo_download_failed)
            Download.STATE_QUEUED -> context.resources.getString(R.string.exo_download_queued)
            Download.STATE_REMOVING -> context.resources.getString(R.string.exo_download_removing)
            Download.STATE_RESTARTING -> context.resources.getString(R.string.exo_download_restarting)
            Download.STATE_STOPPED -> context.resources.getString(R.string.exo_download_stopped)
            else -> throw IllegalArgumentException()
        }
    }

    @Synchronized
    private fun getDownloadCache(context: Context): Cache {
        if(!DownloadUtil::downloadCache.isInitialized) {
            val downloadContentDirectory =
                File(getDownloadDirectory(context), DOWNLOAD_CONTENT_DIRECTORY)
            downloadCache = SimpleCache(
                downloadContentDirectory,
                NoOpCacheEvictor(),
                getDatabaseProvider(context)
            )
        }
        return downloadCache
    }

    @Synchronized
    private fun ensureDownloadManagerInitialized(context: Context) {
        if(!DownloadUtil::downloadManager.isInitialized) {
            downloadManager = DownloadManager(
                context,
                getDatabaseProvider(context),
                getDownloadCache(context),
                getHttpDataSourceFactory(),
                Executors.newFixedThreadPool(6)
            ).apply {
                maxParallelDownloads = 2
            }
            downloadTracker =
                DownloadTracker(context, getHttpDataSourceFactory(), downloadManager)
        }
    }

    @Synchronized
    private fun getDatabaseProvider(context: Context): DatabaseProvider {
        if(!DownloadUtil::databaseProvider.isInitialized) databaseProvider =
            StandaloneDatabaseProvider(context)
        return databaseProvider
    }

    @Synchronized
    fun getDownloadDirectory(context: Context): File {
        if(!DownloadUtil::downloadDirectory.isInitialized) {
            downloadDirectory = context.getExternalFilesDir(null) ?: context.filesDir
        }
        return downloadDirectory
    }

    private fun buildReadOnlyCacheDataSource(
        upstreamFactory: DataSource.Factory,
        cache: Cache
    ): CacheDataSource.Factory {
        return CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setCacheWriteDataSinkFactory(null)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }
}
