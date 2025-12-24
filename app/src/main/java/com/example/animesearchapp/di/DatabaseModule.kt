package com.example.animesearchapp.di

import android.content.Context
import androidx.room.Room
import com.example.animesearchapp.data.local.AnimeDatabase
import com.example.animesearchapp.data.local.dao.AnimeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AnimeDatabase =
        Room.databaseBuilder(context, AnimeDatabase::class.java, "anime_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideDao(db: AnimeDatabase): AnimeDao = db.animeDao()
}
