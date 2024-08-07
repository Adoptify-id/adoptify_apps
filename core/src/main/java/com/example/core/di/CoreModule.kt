package com.example.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.core.data.repository.AuthRepository
import com.example.core.data.repository.MainRepository
import com.example.core.data.source.local.LocalDataSource
import com.example.core.data.source.local.datastore.UserPreferences
import com.example.core.data.source.local.room.PetsDao
import com.example.core.data.source.local.room.PetsDatabase
import com.example.core.data.source.remote.RemoteDataSource
import com.example.core.data.source.remote.network.ApiService
import com.example.core.domain.repository.IAuthRepository
import com.example.core.domain.repository.IMainRepository
import com.example.core.utils.AuthInterceptor
import com.example.core.utils.NetworkInterceptor
import com.example.core.utils.SessionManager
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
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(get<NetworkInterceptor>())
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

fun provideRoomDatabase(context: Context) : PetsDatabase {
    return Room.databaseBuilder(
        context,
        PetsDatabase::class.java,
        "Pets.db"
    ).build()
}

fun providePetsDao(petsDatabase: PetsDatabase) : PetsDao {
    return petsDatabase.petsDao()
}

val databaseModule = module {
    single { provideRoomDatabase(androidContext()) }
    single { providePetsDao(get()) }
}

val repositoryModule = module {
    single { provideDataStore(androidContext())}
    single { UserPreferences(get()) }
    single { LocalDataSource(get(), get()) }
    single { RemoteDataSource(get(), get()) }
    single { SessionManager(get(), get()) }
    single<IMainRepository> { MainRepository(get(), get()) }
    single<IAuthRepository> { AuthRepository(get(), get()) }
    single { AuthInterceptor(get()) }
    single { NetworkInterceptor(androidContext()) }
}



