package dev.gerits.speed.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.gerits.speed.data.DefaultLocationRepository
import dev.gerits.speed.data.LocationRepository
import dev.gerits.speed.data.source.DefaultLocationDataSource
import dev.gerits.speed.data.source.LocationDataSource
import javax.inject.Singleton

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