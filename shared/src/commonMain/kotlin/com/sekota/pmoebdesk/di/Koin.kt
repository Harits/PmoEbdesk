package com.sekota.pmoebdesk.di

import org.koin.core.context.startKoin
import org.koin.dsl.module
import com.sekota.pmoebdesk.features.dashboard.domain.OpenProjectRepository
import com.sekota.pmoebdesk.features.dashboard.data.ProductionOpenProjectRepositoryImpl
import com.sekota.pmoebdesk.features.dashboard.data.MockOpenProjectRepositoryImpl
import com.sekota.pmoebdesk.features.dashboard.domain.GetBodDashboardDataUseCase
import com.sekota.pmoebdesk.features.projects.domain.ProjectSearchRepository
import com.sekota.pmoebdesk.features.projects.data.ProductionProjectSearchRepositoryImpl
import com.sekota.pmoebdesk.features.projects.data.MockProjectSearchRepositoryImpl
import com.sekota.pmoebdesk.features.projects.domain.SearchProjectsUseCase

fun initKoin(useMockData: Boolean = true, additionalModules: List<org.koin.core.module.Module> = emptyList()) {
    startKoin {
        modules(sharedModule(useMockData) + additionalModules)
    }
}

fun sharedModule(useMockData: Boolean) = module {
    if (useMockData) {
        single<OpenProjectRepository> { MockOpenProjectRepositoryImpl() }
        single<ProjectSearchRepository> { MockProjectSearchRepositoryImpl() }
    } else {
        single<OpenProjectRepository> { ProductionOpenProjectRepositoryImpl() }
        single<ProjectSearchRepository> { ProductionProjectSearchRepositoryImpl() }
    }

    factory { GetBodDashboardDataUseCase(get()) }
    factory { SearchProjectsUseCase(get()) }
}
