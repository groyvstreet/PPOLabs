package com.example.timer.di

import android.app.Application
import androidx.room.Room
import com.example.timer.data.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideElementDatabase(app: Application): TimerDatabase {
        return Room.databaseBuilder(
            app,
            TimerDatabase::class.java,
            "timer_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSequenceRepository(db: TimerDatabase): SequenceRepository {
        return SequenceRepositoryImpl(db.sequenceDao)
    }

    @Provides
    @Singleton
    fun provideElementRepository(db: TimerDatabase): ElementRepository {
        return ElementRepositoryImpl(db.elementDao)
    }
}
