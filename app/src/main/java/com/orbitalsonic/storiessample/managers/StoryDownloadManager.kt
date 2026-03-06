package com.orbitalsonic.storiessample.managers

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

class StoryDownloadManager(private val context: Context) {

    fun downloadStory(url: String, fileName: String) {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        dm.enqueue(request)
    }
}