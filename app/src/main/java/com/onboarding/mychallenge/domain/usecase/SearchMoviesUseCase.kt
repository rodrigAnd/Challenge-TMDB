package com.onboarding.mychallenge.domain.usecase

import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para buscar filmes por termo de pesquisa.
 * 
 * Encapsula a lógica de negócio para pesquisar filmes na API do TMDb usando
 * um termo de busca. Valida os parâmetros de entrada antes de realizar a busca.
 * 
 * @property repository Repositório de filmes para acesso aos dados.
 * 
 * @constructor Cria uma nova instância do [SearchMoviesUseCase] com o repositório injetado.
 */
class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Executa a busca de filmes por termo de pesquisa.
     * 
     * Valida se o termo de busca não está vazio e se o número da página é válido
     * antes de realizar a busca no repositório. Remove espaços em branco do termo
     * de busca automaticamente.
     * 
     * @param query Termo de busca para pesquisar filmes.
     * @param page Número da página a ser buscada (padrão: 1).
     * @return [Result] contendo a lista de [Movie] em caso de sucesso,
     *         ou uma exceção em caso de erro ou parâmetros inválidos.
     */
    suspend operator fun invoke(query: String, page: Int = 1): Result<List<Movie>> {
        return when {
            query.isBlank() -> {
                Result.failure(IllegalArgumentException("Search query cannot be empty"))
            }
            page < 1 -> {
                Result.failure(IllegalArgumentException("Page number must be greater than 0"))
            }
            else -> {
                repository.searchMovies(query.trim(), page)
            }
        }
    }
}

