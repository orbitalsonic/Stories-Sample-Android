package com.orbitalsonic.storiessample.di

import com.orbitalsonic.storiessample.data.dataSources.local.DataSourceLocalStories
import com.orbitalsonic.storiessample.data.dataSources.remote.DataSourceRemoteStories
import com.orbitalsonic.storiessample.data.database.AppDatabase
import com.orbitalsonic.storiessample.data.repositories.RepositoryDownloadImpl
import com.orbitalsonic.storiessample.data.repositories.RepositoryStoriesImpl
import com.orbitalsonic.storiessample.data.repositories.RepositoryStorySeenImpl
import com.orbitalsonic.storiessample.domain.repositories.RepositoryDownload
import com.orbitalsonic.storiessample.domain.repositories.RepositoryStories
import com.orbitalsonic.storiessample.domain.repositories.RepositoryStorySeen
import com.orbitalsonic.storiessample.domain.useCases.UseCaseDownload
import com.orbitalsonic.storiessample.domain.useCases.UseCaseStories
import com.orbitalsonic.storiessample.domain.useCases.UseCaseStorySeen
import com.orbitalsonic.storiessample.presentation.viewModels.ViewModelDownload
import com.orbitalsonic.storiessample.presentation.viewModels.ViewModelMain
import com.orbitalsonic.storiessample.presentation.viewModels.ViewModelStories
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class KoinModules {

    private val dataSource = module {
        single { DataSourceLocalStories(androidContext()) }
        single { DataSourceRemoteStories() }
        single { AppDatabase.getDatabase(androidContext()) }
        single { get<AppDatabase>().storySeenDao() }
    }

    private val repository = module {
        single<RepositoryStories> { RepositoryStoriesImpl(get(), get()) }
        single<RepositoryStorySeen> { RepositoryStorySeenImpl(get(), androidContext()) }
        single<RepositoryDownload> { RepositoryDownloadImpl(androidContext()) }
    }

    private val useCase = module {
        factory { UseCaseStories(get<RepositoryStories>(), get<RepositoryStorySeen>()) }
        factory { UseCaseStorySeen(get()) }
        factory { UseCaseDownload(get()) }
    }

    private val viewModel = module {
        viewModel { ViewModelStories(get()) }
        viewModel { ViewModelMain(get(), get()) }
        viewModel { ViewModelDownload(get()) }
    }

    val moduleList = listOf(dataSource, repository, useCase, viewModel)
}