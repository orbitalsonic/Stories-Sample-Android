package com.orbitalsonic.storiessample.data.repositories

import com.orbitalsonic.storiessample.data.db.dao.CategoryDao
import com.orbitalsonic.storiessample.data.db.entities.CategoryEntity
import com.orbitalsonic.storiessample.data.local.StoryLocalDataSource
import com.orbitalsonic.storiessample.presentation.models.Category
import com.orbitalsonic.storiessample.utilities.utils.DateTime.todayDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StoryRepository(
    private val localDataSource: StoryLocalDataSource,
    private val dao: CategoryDao
) {

    private var shuffledOnceList: List<Category>? = null

    fun observeParentCategories(): Flow<List<Category>> = flow {

        clearExpired()

        dao.observeAll().collect { seenEntities ->

            val seenIds = seenEntities.map { it.id }

            val rawCategories = localDataSource.getAllCategories()

            val mapped = rawCategories.map {
                it.copy(
                    isSeen = seenIds.contains(it.id),
                    stories = emptyList()
                )
            }

            val finalList = if (shuffledOnceList == null) {

                val unseen = mapped.filter { !it.isSeen }.shuffled()
                val seen = mapped.filter { it.isSeen }

                val result = unseen + seen

                shuffledOnceList = result
                result

            } else {

                val cached = shuffledOnceList!!

                val updated = cached.map { cat ->
                    cat.copy(isSeen = seenIds.contains(cat.id))
                }

                val unseen = updated.filter { !it.isSeen }
                val seen = updated.filter { it.isSeen }

                unseen + seen
            }

            emit(finalList)
        }
    }

    suspend fun getListFromCategoryTitle(title: String): List<Category> {

        val categories = localDataSource.getAllCategories()

        val index = categories.indexOfFirst { it.title == title }

        if (index == -1) return emptyList()

        return categories.drop(index)
    }

    suspend fun markSeen(id: Int) {

        val existing = dao.getById(id)

        if (existing == null) {

            dao.insert(
                CategoryEntity(
                    id = id,
                    isSeen = true,
                    savedDate = todayDate()
                )
            )
        }
    }

    suspend fun clearExpired() {

        val today = todayDate()

        dao.clearExpired(today)
    }
}