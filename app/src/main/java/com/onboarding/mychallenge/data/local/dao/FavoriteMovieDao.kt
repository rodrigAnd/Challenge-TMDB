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
 * Define métodos para acessar e manipular dados de filmes favoritos no banco de dados local.
 * Utiliza Flow para observar mudanças nos dados em tempo real.
 */
@Dao
interface FavoriteMovieDao {
    
    /**
     * Busca todos os filmes favoritos ordenados por data de adição.
     * 
     * Retorna um [Flow] que emite a lista atualizada de filmes favoritos sempre que
     * houver mudanças no banco de dados, ordenados do mais recente para o mais antigo.
     * 
     * @return [Flow] que emite uma lista de [FavoriteMovieEntity] ordenada por data de adição (DESC).
     */
    @Query("SELECT * FROM favorite_movies ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteMovieEntity>>
    
    /**
     * Busca um filme favorito específico pelo ID.
     * 
     * @param movieId ID do filme a ser buscado.
     * @return [FavoriteMovieEntity] se encontrado, `null` caso contrário.
     */
    @Query("SELECT * FROM favorite_movies WHERE id = :movieId LIMIT 1")
    suspend fun getFavoriteById(movieId: Int): FavoriteMovieEntity?
    
    /**
     * Busca todos os IDs dos filmes favoritos.
     * 
     * Retorna um [Flow] que emite a lista atualizada de IDs sempre que houver
     * mudanças no banco de dados.
     * 
     * @return [Flow] que emite uma lista de IDs dos filmes favoritos.
     */
    @Query("SELECT id FROM favorite_movies")
    fun getAllFavoriteIds(): Flow<List<Int>>
    
    /**
     * Verifica se um filme está nos favoritos.
     * 
     * @param movieId ID do filme a ser verificado.
     * @return `true` se o filme está nos favoritos, `false` caso contrário.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :movieId LIMIT 1)")
    suspend fun isFavorite(movieId: Int): Boolean
    
    /**
     * Insere ou atualiza um filme favorito.
     * 
     * Se o filme já existir (mesmo ID), ele será substituído devido à estratégia
     * [OnConflictStrategy.REPLACE].
     * 
     * @param movie Entidade do filme a ser inserida ou atualizada.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(movie: FavoriteMovieEntity)
    
    /**
     * Remove um filme dos favoritos.
     * 
     * @param movieId ID do filme a ser removido dos favoritos.
     */
    @Query("DELETE FROM favorite_movies WHERE id = :movieId")
    suspend fun deleteFavorite(movieId: Int)
}

