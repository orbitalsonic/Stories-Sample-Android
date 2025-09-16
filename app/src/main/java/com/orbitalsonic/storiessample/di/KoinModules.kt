package com.orbitalsonic.storiessample.di

import com.orbitalsonic.storiessample.data.dataSources.local.DataSourceLocalStories
import com.orbitalsonic.storiessample.data.repositories.RepositoryStoriesImpl
import com.orbitalsonic.storiessample.domain.useCases.UseCaseStories
import com.orbitalsonic.storiessample.presentation.viewModels.ViewModelStories
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class KoinModules {

    private val dataSource = module {
        single { DataSourceLocalStories() }
    }

    private val repository = module {
        single { RepositoryStoriesImpl(get()) }
    }

    private val useCase = module {
        factory { UseCaseStories(get()) }
    }

    private val viewModel = module {
        viewModel { ViewModelStories(get()) }
    }

    val moduleList = listOf(dataSource, repository, useCase, viewModel)
}