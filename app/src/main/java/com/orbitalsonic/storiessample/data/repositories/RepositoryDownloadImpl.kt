package com.orbitalsonic.storiessample.data.repositories

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import com.orbitalsonic.storiessample.data.entities.DownloadEntity
import com.orbitalsonic.storiessample.data.entities.DownloadStatus
import com.orbitalsonic.storiessample.domain.repositories.RepositoryDownload
import com.orbitalsonic.storiessample.utilities.utils.Constants.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class RepositoryDownloadImpl(private val context: Context) : RepositoryDownload {

    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    override suspend fun startDownload(downloadEntity: DownloadEntity): Result<Long> = withContext(Dispatchers.IO) {
        try {
            // Check permissions first
            if (!checkStoragePermission()) {
                return@withContext Result.failure(Exception("Storage permission not granted"))
            }

            // Create download directory
            val downloadDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "StoriesSample")
            if (!downloadDir.exists()) {
                downloadDir.mkdirs()
            }

            // Create unique filename
            val fileName = "${System.currentTimeMillis()}_${downloadEntity.fileName}"
            val file = File(downloadDir, fileName)

            // Create download request
            val request = DownloadManager.Request(Uri.parse(downloadEntity.url)).apply {
                setTitle("Downloading ${downloadEntity.fileName}")
                setDescription("Downloading story image")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationUri(Uri.fromFile(file))
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                setAllowedOverRoaming(false)
                setMimeType(downloadEntity.mimeType)
            }

            // Start download
            val downloadId = downloadManager.enqueue(request)
            Log.d(TAG, "RepositoryDownloadImpl: startDownload: Started download with ID: $downloadId")
            
            Result.success(downloadId)
        } catch (ex: Exception) {
            Log.e(TAG, "RepositoryDownloadImpl: startDownload: Failed to start download", ex)
            Result.failure(ex)
        }
    }

    override suspend fun cancelDownload(downloadId: Long): Unit = withContext(Dispatchers.IO) {
        try {
            downloadManager.remove(downloadId)
            Log.d(TAG, "RepositoryDownloadImpl: cancelDownload: Cancelled download with ID: $downloadId")
        } catch (ex: Exception) {
            Log.e(TAG, "RepositoryDownloadImpl: cancelDownload: Failed to cancel download", ex)
        }
    }

    override fun getDownloadProgress(downloadId: Long): Flow<DownloadEntity> = callbackFlow {
        val query = DownloadManager.Query().setFilterById(downloadId)
        
        try {
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val status = cursor.getInt(statusIndex)
                
                val downloadStatus = when (status) {
                    DownloadManager.STATUS_PENDING -> DownloadStatus.PENDING
                    DownloadManager.STATUS_RUNNING -> DownloadStatus.DOWNLOADING
                    DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.COMPLETED
                    DownloadManager.STATUS_FAILED -> DownloadStatus.FAILED
                    else -> DownloadStatus.PENDING
                }
                
                val urlIndex = cursor.getColumnIndex(DownloadManager.COLUMN_URI)
                val url = cursor.getString(urlIndex)
                
                val fileNameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)
                val fileName = cursor.getString(fileNameIndex)?.let { File(it).name } ?: "unknown"
                
                val downloadEntity = DownloadEntity(
                    url = url,
                    fileName = fileName,
                    downloadId = downloadId,
                    status = downloadStatus
                )
                
                trySend(downloadEntity)
            }
            cursor.close()
        } catch (ex: Exception) {
            Log.e(TAG, "RepositoryDownloadImpl: getDownloadProgress: Failed to get download progress", ex)
        }
        
        awaitClose()
    }

    override suspend fun checkStoragePermission(): Boolean = withContext(Dispatchers.IO) {
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+ (API 33+), we don't need storage permissions for downloads
            true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11+ (API 30+), we don't need storage permissions for downloads
            true
        } else {
            // For older versions, check storage permission
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
        
        Log.d(TAG, "RepositoryDownloadImpl: checkStoragePermission: $hasPermission")
        hasPermission
    }

    override suspend fun requestStoragePermission(): Boolean = withContext(Dispatchers.IO) {
        // For modern Android versions, we don't need to request storage permissions
        // Downloads go to the Downloads folder which doesn't require permission
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            true
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
        
        Log.d(TAG, "RepositoryDownloadImpl: requestStoragePermission: $hasPermission")
        hasPermission
    }
}
