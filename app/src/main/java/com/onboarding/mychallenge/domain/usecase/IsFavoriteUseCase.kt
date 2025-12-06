package com.onboarding.mychallenge.domain.usecase
import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para verificar se um filme está nos favoritos
 * Encapsula a lógica de negócio para verificar status de favorito
 */
class IsFavoriteUseCase
    @Inject
    constructor(
        private val repository: MovieRepository,
    ) {
        /**
         * Verifica se um filme está nos favoritos
         * @param movieId ID do filme
         * @return Result com true se favorito, false caso contrário, ou erro
         */
        suspend operator fun invoke(movieId: Int): Result<Boolean> {
            return if (movieId <= 0) {
                Result.failure(IllegalArgumentException("Movie ID must be greater than 0"))
            } else {
                try {
                    val isFavorite = repository.isFavorite(movieId)
                    Result.success(isFavorite)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }
    }
