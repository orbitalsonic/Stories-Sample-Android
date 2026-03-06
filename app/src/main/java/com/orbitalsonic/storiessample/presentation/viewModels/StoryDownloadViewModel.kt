package com.orbitalsonic.storiessample.presentation.viewModels

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbitalsonic.storiessample.managers.StoryDownloadManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StoryDownloadViewModel(private val context: Context) : ViewModel() {

    private val downloadManager = StoryDownloadManager(context)

    /**
     * Request to download story image
     * @param url image URL
     * @param fileName Name for the downloaded file
     * @param onPermissionRequired Callback if storage permission is needed (Android < Q)
     */
    fun downloadStory(url: String, fileName: String, onPermissionRequired: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if (hasStoragePermission()) {
                downloadManager.downloadStory(url, fileName)
            } else {
                // Request permission on UI thread
                launch(Dispatchers.Main) {
                    onPermissionRequired()
                }
            }
        }
    }

    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true // Scoped storage, permission not required
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }
}