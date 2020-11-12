package io.github.yoobi.downloadvideo.offlineVideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.yoobi.downloadvideo.R

class OfflineVideoActivity: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val offlineVideoViewModel by lazy {
        ViewModelProvider(this).get(OfflineVideoViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_downloaded_video)

        recyclerView = findViewById(R.id.rv_downloaded_video)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val downloadAdapter = OfflineVideoAdapter()
        recyclerView.adapter = downloadAdapter

        offlineVideoViewModel.downloads.observe(this) {
            downloadAdapter.submitList(it.toList())
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
