package com.onboarding.mychallenge.data.remote.api
import com.onboarding.mychallenge.data.remote.dto.MovieDetailDto
import com.onboarding.mychallenge.data.remote.dto.MoviesResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface do Retrofit para comunicação com a API do TMDb.
 *
 * Define os endpoints para buscar filmes populares, pesquisar filmes e obter detalhes de filmes.
 */
interface TmdbApiService {
    /**
     * Busca filmes populares com paginação.
     *
     * @param page O número da página a ser carregada. Padrão é 1.
     * @param language O idioma da resposta. Padrão é "pt-BR".
     * @return Um [MoviesResponseDto] contendo a lista de filmes populares.
     */
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "pt-BR",
    ): MoviesResponseDto

    /**
     * Busca filmes por um termo de pesquisa com paginação.
     *
     * @param query O termo de busca.
     * @param page O número da página a ser carregada. Padrão é 1.
     * @param language O idioma da resposta. Padrão é "pt-BR".
     * @return Um [MoviesResponseDto] contendo a lista de filmes que correspondem à pesquisa.
     */
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "pt-BR",
    ): MoviesResponseDto

    /**
     * Busca detalhes de um filme específico.
     *
     * @param movieId O ID do filme.
     * @param language O idioma da resposta. Padrão é "pt-BR".
     * @return Um [MovieDetailDto] contendo os detalhes completos do filme.
     */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "pt-BR",
    ): MovieDetailDto
}
