package com.onboarding.mychallenge.domain.usecase

import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para buscar filmes populares
 * Encapsula a lógica de negócio para obter filmes populares
 */
class GetPopularMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Executa a busca de filmes populares
     * @param page Número da página (padrão: 1)
     * @return Result com lista de filmes ou erro
     */
    suspend operator fun invoke(page: Int = 1): Result<List<Movie>> {
        return if (page < 1) {
            Result.failure(IllegalArgumentException("Page number must be greater than 0"))
        } else {
            repository.getPopularMovies(page)
        }
    }
}

