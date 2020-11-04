package io.github.yoobi.downloadvideo.offlineVideo

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.offline.Download
import io.github.yoobi.downloadvideo.common.DownloadUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class OfflineVideoViewModel(application: Application): AndroidViewModel(application) {

    private val _downloads: MutableLiveData<List<Download>> =
        MutableLiveData(DownloadUtil.getDownloadTracker(application).downloads.values.toMutableList())
    val downloads: LiveData<List<Download>>
        get() = _downloads

    private var job: CompletableJob? = null
    private var coroutineScope: CoroutineScope? = null

    fun startFlow(context: Context) {
        job = SupervisorJob()
        coroutineScope = CoroutineScope(Dispatchers.Main + job!!).apply {
            launch {
//                DownloadUtil.getAllCurrentDownload(context).collect {
//                    _downloads.postValue(it)
//                }
                DownloadUtil.getDownloadTracker(context).getAllDownloadProgressFlow().collect {
                    _downloads.postValue(it.values.toMutableList())
                }
            }
        }
    }

    fun stopFlow() {
        coroutineScope?.cancel()
    }

}
