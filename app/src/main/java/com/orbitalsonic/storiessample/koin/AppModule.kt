package com.orbitalsonic.storiessample.koin

import androidx.room.Room
import com.orbitalsonic.storiessample.data.db.AppDatabase
import com.orbitalsonic.storiessample.data.local.StoryLocalDataSource
import com.orbitalsonic.storiessample.data.repositories.StoryRepository
import com.orbitalsonic.storiessample.presentation.viewModels.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { StoryLocalDataSource(get()) }

    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "story_db"
        ).build()
    }

    single { get<AppDatabase>().categoryDao() }

    single { StoryRepository(get(), get()) }

    viewModel { MainViewModel(get()) }
}