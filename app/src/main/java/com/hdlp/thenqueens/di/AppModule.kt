package com.hdlp.thenqueens.di

import android.content.Context
import android.os.SystemClock
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.hdlp.thenqueens.data.BestTimeRepository
import com.hdlp.thenqueens.data.DataStoreBestTimeRepository
import com.hdlp.thenqueens.data.GameClock
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun gameClock(): GameClock = GameClock { SystemClock.elapsedRealtime() }

    @Provides
    @Singleton
    fun preferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("best_times")
        }
}

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun bestTimeRepository(impl: DataStoreBestTimeRepository): BestTimeRepository
}
