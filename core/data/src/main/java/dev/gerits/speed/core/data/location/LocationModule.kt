package dev.gerits.speed.core.data.location

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Singleton
    @Binds
    abstract fun bindLocationRepository(
        defaultLocationDataSource: DefaultLocationRepository
    ): LocationRepository

    @Singleton
    @Binds
    abstract fun bindLocationDataSource(
        defaultLocationDataSource: DefaultLocationDataSource
    ): LocationDataSource
}