package io.github.yoobi.downloadvideo.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MediaItemTag(val duration: Long, val title: String, val image: String? = null): Parcelable