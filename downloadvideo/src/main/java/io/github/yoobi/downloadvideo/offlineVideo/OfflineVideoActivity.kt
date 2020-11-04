package io.github.yoobi.downloadvideo.offlineVideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import io.github.yoobi.downloadvideo.R
import io.github.yoobi.downloadvideo.common.PieProgressDrawable

class OfflineVideoActivity: AppCompatActivity() {

    private lateinit var pieProgressDrawable: PieProgressDrawable
    private lateinit var recyclerView: RecyclerView
    private val downloadAdapter = OfflineVideoAdapter()
    private val offlineVideoViewModel by lazy {
        ViewModelProvider(this).get(OfflineVideoViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_downloaded_video)

        pieProgressDrawable = PieProgressDrawable().apply {
            setColor(ContextCompat.getColor(this@OfflineVideoActivity, R.color.colorAccent))
        }

        recyclerView = findViewById(R.id.rv_downloaded_video)
        recyclerView.adapter = downloadAdapter
        downloadAdapter.submitList(offlineVideoViewModel.downloads.value)

        offlineVideoViewModel.downloads.observe(this) { possibleList ->
            possibleList?.let { listDownload ->
                //TODO: Remove notifyDataSetChanged when this issue https://issuetracker.google.com/issues/149274000 is resolved
                downloadAdapter.submitList(listDownload.toList())
                downloadAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        offlineVideoViewModel.startFlow(this)
    }

    override fun onPause() {
        super.onPause()
        offlineVideoViewModel.stopFlow()
    }
}
