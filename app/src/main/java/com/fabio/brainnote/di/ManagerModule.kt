package com.fabio.brainnote.di

import android.content.Context
import com.fabio.brainnote.data.alarm.AlarmSchedulerImpl
import com.fabio.brainnote.data.manager.AudioRecorderManager
import com.fabio.brainnote.data.manager.ImageStorageManager
import com.fabio.brainnote.domain.repo.AlarmScheduler
import com.fabio.brainnote.domain.repo.AudioRecorderRepository
import com.fabio.brainnote.domain.repo.ImageStorageRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ManagerModule {

    @Binds
    @Singleton
    abstract fun bindAudioRecorderRepository(impl: AudioRecorderManager): AudioRecorderRepository

    @Binds
    @Singleton
    abstract fun bindImageStorageRepository(impl: ImageStorageManager): ImageStorageRepository

    companion object {
        @Provides
        @Singleton
        fun provideAlarmScheduler(
            @ApplicationContext context: Context
        ): AlarmScheduler = AlarmSchedulerImpl(context)
    }
}