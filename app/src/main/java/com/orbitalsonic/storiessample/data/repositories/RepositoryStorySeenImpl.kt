package com.orbitalsonic.storiessample.data.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.orbitalsonic.storiessample.data.database.dao.StorySeenDao
import com.orbitalsonic.storiessample.data.database.entities.StorySeenEntity
import com.orbitalsonic.storiessample.domain.repositories.RepositoryStorySeen
import com.orbitalsonic.storiessample.utilities.utils.Constants.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

class RepositoryStorySeenImpl(private val storySeenDao: StorySeenDao, private val context: Context) : RepositoryStorySeen {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("story_seen_prefs", Context.MODE_PRIVATE)
    }

    private val LAST_RESET_DATE_KEY = "last_reset_date"

    override suspend fun markStoryAsSeen(storyId: Int, categoryId: Int): Unit = withContext(Dispatchers.IO) {
        try {
            val storySeen = StorySeenEntity(
                storyId = storyId,
                categoryId = categoryId,
                isSeen = true,
                seenDate = Date()
            )
            storySeenDao.insertOrUpdateStorySeen(storySeen)
            Log.d(TAG, "RepositoryStorySeenImpl: markStoryAsSeen: Story $storyId marked as seen")
        } catch (ex: Exception) {
            Log.e(TAG, "RepositoryStorySeenImpl: markStoryAsSeen: Failed to mark story as seen", ex)
        }
    }

    override suspend fun isStorySeen(storyId: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val storySeen = storySeenDao.getStorySeenStatus(storyId)
            storySeen?.isSeen ?: false
        } catch (ex: Exception) {
            Log.e(TAG, "RepositoryStorySeenImpl: isStorySeen: Failed to check story seen status", ex)
            false
        }
    }

    override suspend fun isCategorySeen(categoryId: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val seenCount = storySeenDao.getSeenCountForCategory(categoryId)
            val totalCount = storySeenDao.getTotalCountForCategory(categoryId)
            seenCount > 0 && seenCount == totalCount
        } catch (ex: Exception) {
            Log.e(TAG, "RepositoryStorySeenImpl: isCategorySeen: Failed to check category seen status", ex)
            false
        }
    }

    override suspend fun resetAllSeenStories(): Unit = withContext(Dispatchers.IO) {
        try {
            val today = Date()
            // Reset all stories to unseen (don't delete, just mark as unseen)
            storySeenDao.resetAllSeenStatus(today)
            Log.d(TAG, "RepositoryStorySeenImpl: resetAllSeenStories: All stories reset to unseen")
        } catch (ex: Exception) {
            Log.e(TAG, "RepositoryStorySeenImpl: resetAllSeenStories: Failed to reset stories", ex)
        }
    }

    override suspend fun cleanupOldEntries(): Unit = withContext(Dispatchers.IO) {
        try {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, -1) // One month ago
            val cutoffDate = calendar.time

            storySeenDao.deleteOldEntries(cutoffDate)
            Log.d(TAG, "RepositoryStorySeenImpl: cleanupOldEntries: Cleaned up entries older than 1 month")
        } catch (ex: Exception) {
            Log.e(TAG, "RepositoryStorySeenImpl: cleanupOldEntries: Failed to cleanup old entries", ex)
        }
    }

    override suspend fun checkAndResetIfDateChanged(): Unit = withContext(Dispatchers.IO) {
        try {
            val today = Calendar.getInstance()
            val lastResetDate = getLastResetDate()

            Log.d(TAG, "RepositoryStorySeenImpl: checkAndResetIfDateChanged: Today: ${today.time}")
            Log.d(TAG, "RepositoryStorySeenImpl: checkAndResetIfDateChanged: Last reset: ${lastResetDate?.time ?: "Never"}")

            if (lastResetDate == null || !isSameDay(today, lastResetDate)) {
                Log.d(TAG, "RepositoryStorySeenImpl: checkAndResetIfDateChanged: Date changed, resetting stories")
                resetAllSeenStories()
                saveLastResetDate(today)
                Log.d(TAG, "RepositoryStorySeenImpl: checkAndResetIfDateChanged: Stories reset successfully")
            } else {
                Log.d(TAG, "RepositoryStorySeenImpl: checkAndResetIfDateChanged: Same day, no reset needed")
            }
        } catch (ex: Exception) {
            Log.e(TAG, "RepositoryStorySeenImpl: checkAndResetIfDateChanged: Failed to check date change", ex)
        }
    }

    private fun getLastResetDate(): Calendar? {
        val lastResetTimestamp = sharedPreferences.getLong(LAST_RESET_DATE_KEY, -1)
        return if (lastResetTimestamp != -1L) {
            Calendar.getInstance().apply {
                timeInMillis = lastResetTimestamp
            }
        } else {
            null
        }
    }

    private fun saveLastResetDate(calendar: Calendar) {
        sharedPreferences.edit {
            putLong(LAST_RESET_DATE_KEY, calendar.timeInMillis)
        }
        Log.d(TAG, "RepositoryStorySeenImpl: saveLastResetDate: Saved reset date: ${calendar.time}")
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
