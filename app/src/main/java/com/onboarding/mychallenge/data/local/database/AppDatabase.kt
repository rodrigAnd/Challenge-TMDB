package com.onboarding.mychallenge.data.local.database
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.onboarding.mychallenge.data.local.dao.FavoriteMovieDao
import com.onboarding.mychallenge.data.local.entity.FavoriteMovieEntity

/**
 * Configuração do banco de dados Room para o aplicativo.
 *
 * Define as entidades ([FavoriteMovieEntity]), a versão do banco de dados e
 * os [TypeConverters] necessários para tipos complexos.
 *
 * @property entities A lista de classes de entidade que pertencem a este banco de dados.
 * @property version A versão do banco de dados. Deve ser incrementada a cada mudança de esquema.
 * @property exportSchema Define se o esquema deve ser exportado para um arquivo JSON.
 */
@Database(
    entities = [FavoriteMovieEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(com.onboarding.mychallenge.data.local.converter.GenreListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Retorna o Data Access Object (DAO) para operações com filmes favoritos.
     *
     * @return Uma instância de [FavoriteMovieDao].
     */
    abstract fun favoriteMovieDao(): FavoriteMovieDao

    companion object {
        /**
         * O nome do arquivo do banco de dados.
         */
        const val DATABASE_NAME = "movie_database"
    }
}
