package com.example.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.core.data.repository.AuthRepository
import com.example.core.data.repository.MainRepository
import com.example.core.data.source.local.LocalDataSource
import com.example.core.data.source.local.datastore.UserPreferences
import com.example.core.data.source.remote.RemoteDataSource
import com.example.core.data.source.remote.network.ApiService
import com.example.core.domain.repository.IAuthRepository
import com.example.core.domain.repository.IMainRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://adoptify-api-ypm2dd7h7q-et.a.run.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(ApiService::class.java)
    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

fun provideDataStore(context: Context): DataStore<Preferences> {
    return context.dataStore
}

val repositoryModule = module {
    single { provideDataStore(androidContext())}
    single { UserPreferences(get()) }
    single { LocalDataSource(get()) }
    single { RemoteDataSource(get()) }
    single<IMainRepository> { MainRepository(get()) }
    single<IAuthRepository> { AuthRepository(get(), get()) }
}



