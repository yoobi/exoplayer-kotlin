package io.github.yoobi.downloadvideo.player

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.yoobi.downloadvideo.common.DownloadUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val _downloadPercent = MutableLiveData<Float>()
    val downloadPercent: LiveData<Float>
        get() = _downloadPercent

    private var coroutineScope: CoroutineScope? = null

    fun startFlow(context: Context, uri: Uri) {
        coroutineScope?.cancel()
        val job = SupervisorJob()
        coroutineScope = CoroutineScope(Dispatchers.Main + job).apply {
            launch {
                DownloadUtil.getDownloadTracker(context).getCurrentProgressDownload(uri).collect {
                    _downloadPercent.postValue(it)
                }
            }
        }
    }

    fun stopFlow() {
        coroutineScope?.cancel()
    }

}