package com.onboarding.mychallenge.domain.usecase

import com.onboarding.mychallenge.domain.model.MovieDetail
import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para buscar detalhes completos de um filme
 * Encapsula a lógica de negócio para obter detalhes do filme
 */
class GetMovieDetailsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Executa a busca de detalhes do filme
     * @param movieId ID do filme
     * @return Result com detalhes do filme ou erro
     */
    suspend operator fun invoke(movieId: Int): Result<MovieDetail> {
        return if (movieId <= 0) {
            Result.failure(IllegalArgumentException("Movie ID must be greater than 0"))
        } else {
            repository.getMovieDetails(movieId)
        }
    }
}

