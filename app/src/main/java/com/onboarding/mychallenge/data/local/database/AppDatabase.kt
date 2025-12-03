package com.onboarding.mychallenge.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.onboarding.mychallenge.data.local.dao.FavoriteMovieDao
import com.onboarding.mychallenge.data.local.entity.FavoriteMovieEntity

/**
 * Configuração do banco de dados Room para o aplicativo
 */
@Database(
    entities = [FavoriteMovieEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(com.onboarding.mychallenge.data.local.converter.GenreListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun favoriteMovieDao(): FavoriteMovieDao
    
    companion object {
        const val DATABASE_NAME = "movie_database"
    }
}

