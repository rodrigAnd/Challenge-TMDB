package com.onboarding.mychallenge.domain.repository

import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.model.MovieDetail
import kotlinx.coroutines.flow.Flow

/**
 * Interface do repositório de filmes.
 * 
 * Define os contratos para acesso aos dados de filmes, incluindo operações
 * de busca na API remota e gerenciamento de filmes favoritos no armazenamento local.
 * 
 * Esta interface abstrai a fonte de dados, permitindo que a camada de domínio
 * não dependa de implementações específicas de acesso a dados.
 */
interface MovieRepository {
    
    /**
     * Busca filmes populares com paginação.
     * 
     * Realiza uma requisição à API do TMDb para obter a lista de filmes populares
     * da página especificada.
     * 
     * @param page Número da página a ser buscada (padrão: 1).
     * @return [Result] contendo a lista de [Movie] em caso de sucesso,
     *         ou uma exceção em caso de erro.
     */
    suspend fun getPopularMovies(page: Int = 1): Result<List<Movie>>
    
    /**
     * Busca filmes por termo de pesquisa com paginação.
     * 
     * Realiza uma busca na API do TMDb usando o termo fornecido e retorna
     * os resultados da página especificada.
     * 
     * @param query Termo de busca para pesquisar filmes.
     * @param page Número da página a ser buscada (padrão: 1).
     * @return [Result] contendo a lista de [Movie] em caso de sucesso,
     *         ou uma exceção em caso de erro.
     */
    suspend fun searchMovies(query: String, page: Int = 1): Result<List<Movie>>
    
    /**
     * Adiciona um filme aos favoritos.
     * 
     * Salva o filme no banco de dados local (Room) para acesso offline.
     * 
     * @param movie Filme a ser adicionado aos favoritos.
     * @throws Exception Se houver erro ao salvar no banco de dados.
     */
    suspend fun addToFavorites(movie: Movie)
    
    /**
     * Adiciona um filme aos favoritos com detalhes completos.
     * 
     * Salva o filme com todas as informações detalhadas no banco de dados local.
     * Útil quando já se possui os detalhes completos do filme.
     * 
     * @param movieDetail Detalhes completos do filme a ser adicionado aos favoritos.
     * @throws Exception Se houver erro ao salvar no banco de dados.
     */
    suspend fun addMovieDetailToFavorites(movieDetail: MovieDetail)
    
    /**
     * Remove um filme dos favoritos.
     * 
     * Remove o filme do banco de dados local usando seu ID.
     * 
     * @param movieId ID do filme a ser removido dos favoritos.
     * @throws Exception Se houver erro ao remover do banco de dados.
     */
    suspend fun removeFromFavorites(movieId: Int)
    
    /**
     * Verifica se um filme está nos favoritos.
     * 
     * Consulta o banco de dados local para verificar se o filme com o ID
     * especificado está salvo como favorito.
     * 
     * @param movieId ID do filme a ser verificado.
     * @return `true` se o filme está nos favoritos, `false` caso contrário.
     * @throws Exception Se houver erro ao consultar o banco de dados.
     */
    suspend fun isFavorite(movieId: Int): Boolean
    
    /**
     * Busca todos os filmes favoritos.
     * 
     * Retorna um [Flow] que emite a lista atualizada de filmes favoritos
     * sempre que houver mudanças no banco de dados local.
     * 
     * @return [Flow] que emite uma lista de [Movie] favoritos.
     */
    fun getFavoriteMovies(): Flow<List<Movie>>
    
    /**
     * Busca detalhes completos de um filme.
     * 
     * Realiza uma requisição à API do TMDb para obter informações detalhadas
     * do filme com o ID especificado.
     * 
     * @param movieId ID do filme cujos detalhes devem ser buscados.
     * @return [Result] contendo [MovieDetail] em caso de sucesso,
     *         ou uma exceção em caso de erro.
     */
    suspend fun getMovieDetails(movieId: Int): Result<MovieDetail>
    
    /**
     * Busca detalhes completos de um filme favorito do banco de dados local.
     * 
     * Consulta o banco de dados Room para obter os detalhes do filme favorito
     * com o ID especificado. Útil para acesso offline aos detalhes.
     * 
     * @param movieId ID do filme favorito cujos detalhes devem ser buscados.
     * @return [Result] contendo [MovieDetail] se encontrado,
     *         ou uma exceção se o filme não estiver nos favoritos.
     */
    suspend fun getFavoriteMovieDetails(movieId: Int): Result<MovieDetail>
}

