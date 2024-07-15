    package com.example.adoptify_core.di

import com.example.adoptify_core.ui.adopt.AdoptViewModel
import com.example.adoptify_core.ui.auth.login.LoginViewModel
import com.example.adoptify_core.ui.auth.register.RegisterViewModel
import com.example.adoptify_core.ui.bookmark.BookmarkViewModel
import com.example.adoptify_core.ui.foster.FosterViewModel
import com.example.adoptify_core.ui.main.MainViewModel
import com.example.adoptify_core.ui.medical.MedicalRecordViewModel
import com.example.adoptify_core.ui.profile.ProfileViewModel
import com.example.adoptify_core.ui.virtual.VirtualPetViewModel
import com.example.core.domain.usecase.AuthInteractor
import com.example.core.domain.usecase.AuthUseCase
import com.example.core.domain.usecase.MainInteractor
import com.example.core.domain.usecase.MainUseCase
import com.example.core.utils.SessionViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<AuthUseCase> { AuthInteractor(get()) }
    factory<MainUseCase> { MainInteractor(get()) }
}

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { VirtualPetViewModel(get()) }
    viewModel { MedicalRecordViewModel(get()) }
    viewModel { FosterViewModel(get()) }
    viewModel { AdoptViewModel(get()) }
    viewModel { SessionViewModel(get()) }
    viewModel { BookmarkViewModel(get()) }
}