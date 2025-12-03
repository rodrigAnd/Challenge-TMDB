package com.onboarding.mychallenge.data.remote.api

import com.onboarding.mychallenge.data.remote.dto.MovieDetailDto
import com.onboarding.mychallenge.data.remote.dto.MoviesResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface do Retrofit para comunicação com a API do TMDb
 */
interface TmdbApiService {
    
    /**
     * Busca filmes populares com paginação
     * @param page Número da página (padrão: 1)
     * @param language Idioma da resposta (padrão: pt-BR)
     */
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "pt-BR"
    ): MoviesResponseDto
    
    /**
     * Busca filmes por termo de pesquisa com paginação
     * @param query Termo de busca
     * @param page Número da página (padrão: 1)
     * @param language Idioma da resposta (padrão: pt-BR)
     */
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "pt-BR"
    ): MoviesResponseDto
    
    /**
     * Busca detalhes de um filme específico
     * @param movieId ID do filme
     * @param language Idioma da resposta (padrão: pt-BR)
     */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "pt-BR"
    ): MovieDetailDto
}

