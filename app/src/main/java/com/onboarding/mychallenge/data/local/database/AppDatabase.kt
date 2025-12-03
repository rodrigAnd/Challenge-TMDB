package com.onboarding.mychallenge.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.onboarding.mychallenge.data.local.dao.FavoriteMovieDao
import com.onboarding.mychallenge.data.local.entity.FavoriteMovieEntity

/**
 * Configuração do banco de dados Room para o aplicativo.
 * 
 * Define o banco de dados principal do aplicativo, incluindo todas as entidades
 * e seus respectivos DAOs. Utiliza conversores de tipo para serializar/deserializar
 * tipos complexos como listas de gêneros.
 * 
 * @property version Versão atual do schema do banco de dados (2).
 * @property exportSchema Indica se o schema deve ser exportado (false).
 */
@Database(
    entities = [FavoriteMovieEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(com.onboarding.mychallenge.data.local.converter.GenreListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Obtém o DAO para operações com filmes favoritos.
     * 
     * @return Instância do [FavoriteMovieDao].
     */
    abstract fun favoriteMovieDao(): FavoriteMovieDao
    
    companion object {
        /**
         * Nome do arquivo do banco de dados.
         */
        const val DATABASE_NAME = "movie_database"
    }
}

