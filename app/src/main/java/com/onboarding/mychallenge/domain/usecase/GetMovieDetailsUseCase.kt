package com.onboarding.mychallenge.domain.usecase
import com.onboarding.mychallenge.domain.model.MovieDetail
import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para buscar detalhes completos de um filme.
 *
 * Encapsula a lógica de negócio para obter detalhes de um filme específico do repositório.
 * Garante que o ID do filme seja válido.
 *
 * @param repository O repositório de filmes para acessar os dados.
 */
class GetMovieDetailsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Executa a busca de detalhes do filme.
     *
     * @param movieId O ID do filme para o qual buscar os detalhes.
     * @return Um [Result] contendo os [MovieDetail] em caso de sucesso,
     *         ou um [IllegalArgumentException] se o ID do filme for inválido.
     * @throws IllegalArgumentException se o ID do filme for menor ou igual a 0.
     */
    suspend operator fun invoke(movieId: Int): Result<MovieDetail> {
        return if (movieId <= 0) {
            Result.failure(IllegalArgumentException("Movie ID must be greater than 0"))
        } else {
            repository.getMovieDetails(movieId)
        }
    }
}
