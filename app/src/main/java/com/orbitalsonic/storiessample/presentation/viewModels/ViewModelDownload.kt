package com.orbitalsonic.storiessample.presentation.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbitalsonic.storiessample.data.entities.DownloadEntity
import com.orbitalsonic.storiessample.data.entities.DownloadStatus
import com.orbitalsonic.storiessample.domain.useCases.UseCaseDownload
import com.orbitalsonic.storiessample.utilities.utils.Constants.TAG
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel for download operations
 */
class ViewModelDownload(private val useCase: UseCaseDownload) : ViewModel() {

    // UI State
    private val _downloadState = MutableLiveData<DownloadState>()
    val downloadState: LiveData<DownloadState> = _downloadState

    private val _isDownloading = MutableLiveData<Boolean>()
    val isDownloading: LiveData<Boolean> = _isDownloading

    private val _downloadProgress = MutableLiveData<DownloadEntity>()
    val downloadProgress: LiveData<DownloadEntity> = _downloadProgress

    private var currentDownloadJob: Job? = null
    private var currentDownloadId: Long = -1L

    /**
     * Download a story image
     * @param imageUrl The URL of the image to download
     * @param storyTitle The title of the story
     */
    fun downloadStoryImage(imageUrl: String, storyTitle: String) {
        // Cancel any existing download
        cancelCurrentDownload()

        _isDownloading.value = true
        _downloadState.value = DownloadState.Loading

        currentDownloadJob = viewModelScope.launch {
            try {
                // Check permissions first
                if (!useCase.checkStoragePermission()) {
                    _downloadState.value = DownloadState.Error("Storage permission not granted")
                    _isDownloading.value = false
                    return@launch
                }

                // Start download
                useCase.downloadStoryImage(imageUrl, storyTitle)
                    .onSuccess { downloadId ->
                        currentDownloadId = downloadId
                        _downloadState.value = DownloadState.Downloading(downloadId)
                        Log.d(TAG, "ViewModelDownload: downloadStoryImage: Started download with ID: $downloadId")
                        
                        // Monitor download progress
                        monitorDownloadProgress(downloadId)
                    }
                    .onFailure { exception ->
                        _downloadState.value = DownloadState.Error(exception.message ?: "Download failed")
                        _isDownloading.value = false
                        Log.e(TAG, "ViewModelDownload: downloadStoryImage: Download failed", exception)
                    }
            } catch (ex: Exception) {
                _downloadState.value = DownloadState.Error(ex.message ?: "Download failed")
                _isDownloading.value = false
                Log.e(TAG, "ViewModelDownload: downloadStoryImage: Exception occurred", ex)
            }
        }
    }

    /**
     * Cancel current download
     */
    fun cancelCurrentDownload() {
        currentDownloadJob?.cancel()
        if (currentDownloadId != -1L) {
            viewModelScope.launch {
                useCase.cancelDownload(currentDownloadId)
                currentDownloadId = -1L
            }
        }
        _isDownloading.value = false
        _downloadState.value = DownloadState.Cancelled
    }

    /**
     * Monitor download progress
     */
    private fun monitorDownloadProgress(downloadId: Long) {
        viewModelScope.launch {
            useCase.getDownloadProgress(downloadId)
                .catch { exception ->
                    Log.e(TAG, "ViewModelDownload: monitorDownloadProgress: Error monitoring progress", exception)
                    _downloadState.value = DownloadState.Error("Failed to monitor download progress")
                    _isDownloading.value = false
                }
                .collect { downloadEntity ->
                    _downloadProgress.value = downloadEntity
                    
                    when (downloadEntity.status) {
                        DownloadStatus.COMPLETED -> {
                            _downloadState.value = DownloadState.Success("Download completed successfully")
                            _isDownloading.value = false
                            currentDownloadId = -1L
                            Log.d(TAG, "ViewModelDownload: monitorDownloadProgress: Download completed")
                        }
                        DownloadStatus.FAILED -> {
                            _downloadState.value = DownloadState.Error("Download failed")
                            _isDownloading.value = false
                            currentDownloadId = -1L
                            Log.e(TAG, "ViewModelDownload: monitorDownloadProgress: Download failed")
                        }
                        DownloadStatus.CANCELLED -> {
                            _downloadState.value = DownloadState.Cancelled
                            _isDownloading.value = false
                            currentDownloadId = -1L
                            Log.d(TAG, "ViewModelDownload: monitorDownloadProgress: Download cancelled")
                        }
                        else -> {
                            // Still downloading or pending
                            Log.d(TAG, "ViewModelDownload: monitorDownloadProgress: Download status: ${downloadEntity.status}")
                        }
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cancelCurrentDownload()
    }
}

/**
 * Download state sealed class
 */
sealed class DownloadState {
    object Loading : DownloadState()
    data class Downloading(val downloadId: Long) : DownloadState()
    data class Success(val message: String) : DownloadState()
    data class Error(val message: String) : DownloadState()
    object Cancelled : DownloadState()
}
