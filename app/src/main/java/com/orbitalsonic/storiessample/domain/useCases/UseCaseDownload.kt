package com.orbitalsonic.storiessample.domain.useCases

import android.util.Log
import com.orbitalsonic.storiessample.data.entities.DownloadEntity
import com.orbitalsonic.storiessample.data.entities.DownloadStatus
import com.orbitalsonic.storiessample.domain.repositories.RepositoryDownload
import com.orbitalsonic.storiessample.utilities.utils.Constants.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.net.URL

/**
 * Use case for download operations
 */
class UseCaseDownload(private val repository: RepositoryDownload) {

    /**
     * Download a story image
     * @param imageUrl The URL of the image to download
     * @param storyTitle The title of the story (used for filename)
     * @return Result with download ID
     */
    suspend fun downloadStoryImage(imageUrl: String, storyTitle: String): Result<Long> = withContext(Dispatchers.IO) {
        try {
            // Check permissions first
            if (!repository.checkStoragePermission()) {
                Log.w(TAG, "UseCaseDownload: downloadStoryImage: Storage permission not granted")
                return@withContext Result.failure(Exception("Storage permission not granted"))
            }

            // Generate filename from URL and story title
            val fileName = generateFileName(imageUrl, storyTitle)
            
            // Create download entity
            val downloadEntity = DownloadEntity(
                url = imageUrl,
                fileName = fileName,
                mimeType = getMimeTypeFromUrl(imageUrl)
            )

            // Start download
            repository.startDownload(downloadEntity)
        } catch (ex: Exception) {
            Log.e(TAG, "UseCaseDownload: downloadStoryImage: Failed to download image", ex)
            Result.failure(ex)
        }
    }

    /**
     * Cancel a download
     * @param downloadId The download ID to cancel
     */
    suspend fun cancelDownload(downloadId: Long) = withContext(Dispatchers.IO) {
        try {
            repository.cancelDownload(downloadId)
            Log.d(TAG, "UseCaseDownload: cancelDownload: Cancelled download $downloadId")
        } catch (ex: Exception) {
            Log.e(TAG, "UseCaseDownload: cancelDownload: Failed to cancel download", ex)
        }
    }

    /**
     * Get download progress
     * @param downloadId The download ID
     * @return Flow of download progress
     */
    fun getDownloadProgress(downloadId: Long): Flow<DownloadEntity> {
        return repository.getDownloadProgress(downloadId)
    }

    /**
     * Check if storage permission is granted
     */
    suspend fun checkStoragePermission(): Boolean = withContext(Dispatchers.IO) {
        repository.checkStoragePermission()
    }

    /**
     * Request storage permission
     */
    suspend fun requestStoragePermission(): Boolean = withContext(Dispatchers.IO) {
        repository.requestStoragePermission()
    }

    /**
     * Generate a filename from URL and story title
     */
    private fun generateFileName(imageUrl: String, storyTitle: String): String {
        return try {
            val url = URL(imageUrl)
            val path = url.path
            val extension = if (path.contains(".")) {
                path.substring(path.lastIndexOf("."))
            } else {
                ".jpg"
            }
            
            // Clean story title for filename
            val cleanTitle = storyTitle.replace(Regex("[^a-zA-Z0-9\\s]"), "").replace(" ", "_")
            "${cleanTitle}_${System.currentTimeMillis()}$extension"
        } catch (ex: Exception) {
            Log.w(TAG, "UseCaseDownload: generateFileName: Failed to parse URL, using default", ex)
            "${storyTitle}_${System.currentTimeMillis()}.jpg"
        }
    }

    /**
     * Get MIME type from URL
     */
    private fun getMimeTypeFromUrl(url: String): String {
        return when {
            url.contains(".png", ignoreCase = true) -> "image/png"
            url.contains(".gif", ignoreCase = true) -> "image/gif"
            url.contains(".webp", ignoreCase = true) -> "image/webp"
            else -> "image/jpeg"
        }
    }
}
