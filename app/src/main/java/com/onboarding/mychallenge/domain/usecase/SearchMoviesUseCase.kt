package com.onboarding.mychallenge.domain.usecase
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para buscar filmes por termo de pesquisa.
 *
 * Encapsula a lógica de negócio para pesquisar filmes no repositório.
 * Garante que a query não seja vazia e que o número da página seja válido.
 *
 * @param repository O repositório de filmes para acessar os dados.
 */
class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Executa a busca de filmes por termo.
     *
     * @param query O termo de busca. Não pode ser vazio.
     * @param page O número da página a ser carregada. Padrão é 1.
     * @return Um [Result] contendo uma lista de [Movie] em caso de sucesso,
     *         ou um [IllegalArgumentException] se a query for vazia ou a página inválida.
     * @throws IllegalArgumentException se a query for vazia ou o número da página for menor que 1.
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
