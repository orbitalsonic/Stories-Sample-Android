package com.orbitalsonic.storiessample.domain.repositories

import com.orbitalsonic.storiessample.data.entities.DownloadEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for download operations
 */
interface RepositoryDownload {
    
    /**
     * Start a download
     * @param downloadEntity The download request
     * @return Download ID
     */
    suspend fun startDownload(downloadEntity: DownloadEntity): Result<Long>
    
    /**
     * Cancel a download
     * @param downloadId The download ID to cancel
     */
    suspend fun cancelDownload(downloadId: Long)
    
    /**
     * Get download progress
     * @param downloadId The download ID
     * @return Flow of download progress
     */
    fun getDownloadProgress(downloadId: Long): Flow<DownloadEntity>
    
    /**
     * Check if storage permission is granted
     */
    suspend fun checkStoragePermission(): Boolean
    
    /**
     * Request storage permission
     */
    suspend fun requestStoragePermission(): Boolean
}
