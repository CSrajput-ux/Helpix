package com.healthai.app.di

import com.healthai.app.data.repository.HealthRepository
import com.healthai.app.data.repository.HealthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * RepositoryModule — binds abstract repository interfaces to their implementations.
 * UserRepository and AppointmentRepository are provided directly in AppModule
 * since they require constructor injection from HelpixApi.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHealthRepository(
        impl: HealthRepositoryImpl
    ): HealthRepository
}