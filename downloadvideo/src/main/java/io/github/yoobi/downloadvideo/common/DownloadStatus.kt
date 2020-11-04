package io.github.yoobi.downloadvideo.common

// can use context so we can have localization on strings
enum class DownloadStatus(val value: String) {
    DOWNLOAD_START("Start Download"),
    DOWNLOAD_PAUSE("Pause Download"),
    DOWNLOAD_RESUME("Resume Download"),
    DOWNLOAD_COMPLETED("Downloaded"),
    DOWNLOAD_RETRY("Retry Download"),
    DOWNLOAD_QUEUE("Download In Queue")
}