package com.onboarding.mychallenge.domain.usecase
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para buscar filmes populares.
 *
 * Encapsula a lógica de negócio para obter filmes populares do repositório.
 * Garante que o número da página seja válido antes de chamar o repositório.
 *
 * @param repository O repositório de filmes para acessar os dados.
 */
class GetPopularMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Executa a busca de filmes populares.
     *
     * @param page O número da página a ser carregada. Padrão é 1.
     * @return Um [Result] contendo uma lista de [Movie] em caso de sucesso,
     *         ou um [IllegalArgumentException] se o número da página for inválido.
     * @throws IllegalArgumentException se o número da página for menor que 1.
     */
    suspend operator fun invoke(page: Int = 1): Result<List<Movie>> {
        return if (page < 1) {
            Result.failure(IllegalArgumentException("Page number must be greater than 0"))
        } else {
            repository.getPopularMovies(page)
        }
    }
}
