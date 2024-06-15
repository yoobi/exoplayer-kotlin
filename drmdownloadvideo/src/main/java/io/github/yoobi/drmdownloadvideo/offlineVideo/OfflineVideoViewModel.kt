package io.github.yoobi.drmdownloadvideo.offlineVideo

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import io.github.yoobi.drmdownloadvideo.common.DownloadUtil
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@UnstableApi
class OfflineVideoViewModel(application: Application) : AndroidViewModel(application) {

    private val _downloads: MutableLiveData<List<Download>> = MutableLiveData()
    val downloads: LiveData<List<Download>>
        get() = _downloads

    private var job: CompletableJob = SupervisorJob()
    private var coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + job)

    fun startFlow(context: Context) {
        coroutineScope.launch {
            DownloadUtil.getDownloadTracker(context).getAllDownloadProgressFlow().collect {
                _downloads.postValue(it)
            }
        }
    }

    fun stopFlow() {
        coroutineScope.cancel()
    }
}
