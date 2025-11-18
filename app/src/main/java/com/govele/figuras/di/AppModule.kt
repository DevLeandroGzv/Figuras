// di/Modules.kt
package com.govele.figuras.di

import android.content.Context
import com.govele.figuras.data.datasource.local.LocalDataSource
import com.govele.figuras.data.local.database.AppDatabase
import com.govele.figuras.data.remote.api.FiguraApiService
//import com.govele.figuras.data.datasource.local.LocalDataSource
//import com.govele.figuras.data.datasource.remote.RemoteDataSource
//import com.govele.figuras.data.remote.api.FiguraApiService
//import com.govele.figuras.data.repository.impl.FiguraRepositoryImpl
//import com.govele.figuras.domain.repository.FiguraRepository
//import com.govele.figuras.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideFiguraApiService(): FiguraApiService {
        return Retrofit.Builder()
            .baseUrl("https://gca.traces.com.co/pruebamovil/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FiguraApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(appDatabase: AppDatabase): LocalDataSource {
        return LocalDataSource(appDatabase.figuraDao())
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(apiService: FiguraApiService): RemoteDataSource {
        return RemoteDataSource(apiService)
    }

    @Provides
    @Singleton
    fun provideFiguraRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource
    ): FiguraRepository {
        return FiguraRepositoryImpl(localDataSource, remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideGetFigurasPredisenadasUseCase(repository: FiguraRepository): GetFigurasPredisenadasUseCase {
        return GetFigurasPredisenadasUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveFiguraUseCase(repository: FiguraRepository): SaveFiguraUseCase {
        return SaveFiguraUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRefreshFigurasUseCase(repository: FiguraRepository): RefreshFigurasUseCase {
        return RefreshFigurasUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetUltimaFiguraUseCase(repository: FiguraRepository): GetUltimaFiguraUseCase {
        return GetUltimaFiguraUseCase(repository)
    }
}