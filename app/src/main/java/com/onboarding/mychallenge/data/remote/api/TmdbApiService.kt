package com.onboarding.mychallenge.data.remote.api

import com.onboarding.mychallenge.data.remote.dto.MovieDetailDto
import com.onboarding.mychallenge.data.remote.dto.MoviesResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface do Retrofit para comunicação com a API do TMDb (The Movie Database).
 * 
 * Define os endpoints da API do TMDb usando anotações Retrofit para realizar
 * requisições HTTP. Todas as requisições são autenticadas através do
 * [AuthInterceptor] que adiciona o Bearer token automaticamente.
 */
interface TmdbApiService {
    
    /**
     * Busca filmes populares com paginação.
     * 
     * Realiza uma requisição GET para o endpoint `/movie/popular` da API do TMDb
     * para obter a lista de filmes populares.
     * 
     * @param page Número da página a ser buscada (padrão: 1).
     * @param language Código do idioma para a resposta (padrão: "pt-BR").
     * @return [MoviesResponseDto] contendo a lista de filmes e metadados de paginação.
     */
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "pt-BR"
    ): MoviesResponseDto
    
    /**
     * Busca filmes por termo de pesquisa com paginação.
     * 
     * Realiza uma requisição GET para o endpoint `/search/movie` da API do TMDb
     * para buscar filmes que correspondam ao termo fornecido.
     * 
     * @param query Termo de busca para pesquisar filmes.
     * @param page Número da página a ser buscada (padrão: 1).
     * @param language Código do idioma para a resposta (padrão: "pt-BR").
     * @return [MoviesResponseDto] contendo a lista de filmes encontrados e metadados de paginação.
     */
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "pt-BR"
    ): MoviesResponseDto
    
    /**
     * Busca detalhes completos de um filme específico.
     * 
     * Realiza uma requisição GET para o endpoint `/movie/{movie_id}` da API do TMDb
     * para obter informações detalhadas de um filme específico.
     * 
     * @param movieId ID único do filme cujos detalhes devem ser buscados.
     * @param language Código do idioma para a resposta (padrão: "pt-BR").
     * @return [MovieDetailDto] contendo todos os detalhes do filme.
     */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "pt-BR"
    ): MovieDetailDto
}

