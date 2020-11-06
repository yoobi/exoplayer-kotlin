package io.github.yoobi.downloadvideo.common

import java.text.DecimalFormat

fun Long.formatFileSize(): String? {
    var hrSize: String? = null
    val b = this.toDouble()
    val k = this / 1024.0
    val m = this / 1024.0 / 1024.0
    val g = this / 1024.0 / 1024.0 / 1024.0
    val t = this / 1024.0 / 1024.0 / 1024.0 / 1024.0
    val dec = DecimalFormat("0.00")
    hrSize = when {
        t > 1 -> { dec.format(t) + " TB" }
        g > 1 -> { dec.format(g) + " GB" }
        m > 1 -> { dec.format(m) + " MB" }
        k > 1 -> { dec.format(k) + " KB" }
        else -> { dec.format(b) + " Bytes" }
    }
    return hrSize
}
