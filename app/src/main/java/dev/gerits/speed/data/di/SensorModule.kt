package dev.gerits.speed.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.gerits.speed.data.DefaultSensorRepository
import dev.gerits.speed.data.SensorRepository
import dev.gerits.speed.data.source.DefaultSensorDataSource
import dev.gerits.speed.data.source.SensorDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SensorModule {

    @Singleton
    @Binds
    abstract fun bindSensorRepository(
        defaultSensorRepository: DefaultSensorRepository
    ): SensorRepository

    @Singleton
    @Binds
    abstract fun bindSensorDataSource(
        defaultSensorDataSource: DefaultSensorDataSource
    ): SensorDataSource
}