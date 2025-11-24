package dev.gerits.speed.data.statistics

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.MultiProcessDataStoreFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StatisticsModule {

    companion object {

        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Statistics> =
            MultiProcessDataStoreFactory.create(
                produceFile = {
                    File("${context.cacheDir.path}/statistics.json")
                },
                serializer = StatisticsSerializer,
                corruptionHandler = null
            )
    }

    @Singleton
    @Binds
    abstract fun bindStatisticsRepository(
        defaultStatisticsRepository: DefaultStatisticsRepository
    ): StatisticsRepository

}