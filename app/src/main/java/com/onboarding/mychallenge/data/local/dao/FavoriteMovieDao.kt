package com.onboarding.mychallenge.data.local.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.onboarding.mychallenge.data.local.entity.FavoriteMovieEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para operações com filmes favoritos no Room.
 *
 * Define os métodos para interagir com a tabela `favorite_movies`.
 */
@Dao
interface FavoriteMovieDao {
    /**
     * Obtém todos os filmes favoritos, ordenados pela data de adição (mais recentes primeiro).
     *
     * @return Um [Flow] que emite uma lista de [FavoriteMovieEntity] sempre que a tabela é alterada.
     */
    @Query("SELECT * FROM favorite_movies ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteMovieEntity>>

    /**
     * Obtém um filme favorito específico pelo seu ID.
     *
     * @param movieId O ID do filme a ser buscado.
     * @return O [FavoriteMovieEntity] correspondente, ou `null` se não for encontrado.
     */
    @Query("SELECT * FROM favorite_movies WHERE id = :movieId LIMIT 1")
    suspend fun getFavoriteById(movieId: Int): FavoriteMovieEntity?

    /**
     * Obtém todos os IDs dos filmes favoritos.
     *
     * @return Um [Flow] que emite uma lista de IDs de filmes favoritos.
     */
    @Query("SELECT id FROM favorite_movies")
    fun getAllFavoriteIds(): Flow<List<Int>>

    /**
     * Verifica se um filme com o dado ID está marcado como favorito.
     *
     * @param movieId O ID do filme a ser verificado.
     * @return `true` se o filme é favorito, `false` caso contrário.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :movieId LIMIT 1)")
    suspend fun isFavorite(movieId: Int): Boolean

    /**
     * Insere um novo filme favorito no banco de dados.
     * Se o filme já existir (baseado no ID), ele será substituído.
     *
     * @param movie O [FavoriteMovieEntity] a ser inserido.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(movie: FavoriteMovieEntity)

    /**
     * Remove um filme favorito do banco de dados pelo seu ID.
     *
     * @param movieId O ID do filme a ser removido.
     */
    @Query("DELETE FROM favorite_movies WHERE id = :movieId")
    suspend fun deleteFavorite(movieId: Int)
}
