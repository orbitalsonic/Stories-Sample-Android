package com.orbitalsonic.storiessample.di

import com.orbitalsonic.storiessample.data.dataSources.local.DataSourceLocalStories
import com.orbitalsonic.storiessample.data.dataSources.remote.DataSourceRemoteStories
import com.orbitalsonic.storiessample.data.repositories.RepositoryStoriesImpl
import com.orbitalsonic.storiessample.domain.useCases.UseCaseStories
import com.orbitalsonic.storiessample.presentation.viewModels.ViewModelStories
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class KoinModules {

    private val dataSource = module {
        single { DataSourceLocalStories(androidContext()) }
        single { DataSourceRemoteStories() }
    }

    private val repository = module {
        single { RepositoryStoriesImpl(get(), get()) }
    }

    private val useCase = module {
        factory { UseCaseStories(get()) }
    }

    private val viewModel = module {
        viewModel { ViewModelStories(get()) }
    }

    val moduleList = listOf(dataSource, repository, useCase, viewModel)
}