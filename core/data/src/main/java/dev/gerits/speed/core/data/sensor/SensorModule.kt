package dev.gerits.speed.core.data.sensor

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
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