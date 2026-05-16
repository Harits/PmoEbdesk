package com.sekota.pmoebdesk.di

import com.sekota.pmoebdesk.features.dashboard.presentation.DashboardViewModel
import com.sekota.pmoebdesk.features.projects.presentation.ProjectSearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { DashboardViewModel(get()) }
    viewModel { ProjectSearchViewModel(get()) }
}
