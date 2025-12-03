package com.onboarding.mychallenge.domain.repository

import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.model.MovieDetail
import kotlinx.coroutines.flow.Flow

/**
 * Interface do repositório de filmes
 * Define os contratos para acesso aos dados de filmes
 */
interface MovieRepository {
    
    /**
     * Busca filmes populares com paginação
     * @param page Número da página
     * @return Resultado com lista de filmes ou erro
     */
    suspend fun getPopularMovies(page: Int = 1): Result<List<Movie>>
    
    /**
     * Busca filmes por termo de pesquisa com paginação
     * @param query Termo de busca
     * @param page Número da página
     * @return Resultado com lista de filmes ou erro
     */
    suspend fun searchMovies(query: String, page: Int = 1): Result<List<Movie>>
    
    /**
     * Adiciona um filme aos favoritos
     * @param movie Filme a ser adicionado
     */
    suspend fun addToFavorites(movie: Movie)
    
    /**
     * Adiciona um filme aos favoritos com detalhes completos
     * @param movieDetail Detalhes completos do filme a ser adicionado
     */
    suspend fun addMovieDetailToFavorites(movieDetail: MovieDetail)
    
    /**
     * Remove um filme dos favoritos
     * @param movieId ID do filme a ser removido
     */
    suspend fun removeFromFavorites(movieId: Int)
    
    /**
     * Verifica se um filme está nos favoritos
     * @param movieId ID do filme
     * @return true se o filme está nos favoritos, false caso contrário
     */
    suspend fun isFavorite(movieId: Int): Boolean
    
    /**
     * Busca todos os filmes favoritos
     * @return Flow com lista de filmes favoritos
     */
    fun getFavoriteMovies(): Flow<List<Movie>>
    
    /**
     * Busca detalhes completos de um filme
     * @param movieId ID do filme
     * @return Resultado com detalhes do filme ou erro
     */
    suspend fun getMovieDetails(movieId: Int): Result<MovieDetail>
    
    /**
     * Busca detalhes completos de um filme favorito do Room
     * @param movieId ID do filme
     * @return Resultado com detalhes do filme ou erro se não encontrado
     */
    suspend fun getFavoriteMovieDetails(movieId: Int): Result<MovieDetail>
}

