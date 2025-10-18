package com.orbitalsonic.storiessample.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Entity representing a download request
 */
@Parcelize
data class DownloadEntity(
    val url: String,
    val fileName: String,
    val mimeType: String = "image/jpeg",
    val downloadId: Long = -1L,
    val status: DownloadStatus = DownloadStatus.PENDING
) : Parcelable

/**
 * Download status enum
 */
enum class DownloadStatus {
    PENDING,
    DOWNLOADING,
    COMPLETED,
    FAILED,
    CANCELLED
}
