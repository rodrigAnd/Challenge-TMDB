package com.onboarding.mychallenge.domain.repository
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.model.MovieDetail
import com.onboarding.mychallenge.domain.model.PaginatedResult
import kotlinx.coroutines.flow.Flow

/**
 * Interface do repositório de filmes.
 *
 * Define os contratos para acesso aos dados de filmes, abstraindo a origem dos dados
 * (API remota, banco de dados local, etc.).
 */
interface MovieRepository {
    /**
     * Busca filmes populares com paginação.
     *
     * @param page O número da página a ser carregada. Padrão é 1.
     * @return Um [Result] contendo um [PaginatedResult] com os filmes e informações de paginação em caso de sucesso,
     *         ou um [Throwable] em caso de falha.
     */
    suspend fun getPopularMovies(page: Int = 1): Result<PaginatedResult<Movie>>

    /**
     * Busca filmes por um termo de pesquisa com paginação.
     *
     * @param query O termo de busca para encontrar filmes.
     * @param page O número da página a ser carregada. Padrão é 1.
     * @return Um [Result] contendo um [PaginatedResult] com os filmes e informações de paginação em caso de sucesso,
     *         ou um [Throwable] em caso de falha.
     */
    suspend fun searchMovies(
        query: String,
        page: Int = 1,
    ): Result<PaginatedResult<Movie>>

    /**
     * Adiciona um filme aos favoritos.
     *
     * @param movie O [Movie] a ser adicionado aos favoritos.
     */
    suspend fun addToFavorites(movie: Movie)

    /**
     * Adiciona um filme aos favoritos com detalhes completos.
     *
     * @param movieDetail Os [MovieDetail] completos do filme a ser adicionado.
     */
    suspend fun addMovieDetailToFavorites(movieDetail: MovieDetail)

    /**
     * Remove um filme dos favoritos.
     *
     * @param movieId O ID do filme a ser removido dos favoritos.
     */
    suspend fun removeFromFavorites(movieId: Int)

    /**
     * Verifica se um filme está nos favoritos.
     *
     * @param movieId O ID do filme a ser verificado.
     * @return `true` se o filme está nos favoritos, `false` caso contrário.
     */
    suspend fun isFavorite(movieId: Int): Boolean

    /**
     * Busca todos os filmes favoritos.
     *
     * @return Um [Flow] que emite uma lista de [Movie] favoritos sempre que houver uma mudança.
     */
    fun getFavoriteMovies(): Flow<List<Movie>>

    /**
     * Busca detalhes completos de um filme.
     *
     * @param movieId O ID do filme para o qual buscar os detalhes.
     * @return Um [Result] contendo os [MovieDetail] em caso de sucesso, ou um [Throwable] em caso de falha.
     */
    suspend fun getMovieDetails(movieId: Int): Result<MovieDetail>

    /**
     * Busca detalhes completos de um filme favorito armazenado localmente no Room.
     *
     * @param movieId O ID do filme favorito para o qual buscar os detalhes.
     * @return Um [Result] contendo os [MovieDetail] em caso de sucesso, ou um [Throwable] em caso de falha
     * (por exemplo, se o filme não for encontrado nos favoritos).
     */
    suspend fun getFavoriteMovieDetails(movieId: Int): Result<MovieDetail>
}
