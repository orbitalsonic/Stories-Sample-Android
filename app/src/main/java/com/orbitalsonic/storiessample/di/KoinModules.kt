package com.orbitalsonic.storiessample.di

import org.koin.dsl.module

class KoinModules {

    private val dataSource = module {
        /*single { DataSourceLocalReel(androidContext()) }
        single { DataSourceRemoteReel() }
        single { DataSourceDownloader(androidContext()) }*/
    }

    private val repository = module {
        /*single { RepositoryReelImpl(get(), get()) }
        single { RepositoryDownloaderImpl(get()) }*/
    }

    private val useCase = module {
        /*factory { UseCaseReelCategory(get()) }
        factory { UseCaseReels(get()) }
        factory { UseCaseVideoPlayer(get()) }
        factory { UseCaseDownloader(get()) }*/
    }

    private val viewModel = module {
        /*viewModel { ViewModelReelCategory(get()) }
        viewModel { ViewModelReels(get(), get()) }
        viewModel { ViewModelVideo(get(), get()) }*/
    }

    val moduleList = listOf(dataSource, repository, useCase, viewModel)
}