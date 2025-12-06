package com.onboarding.mychallenge.domain.usecase
import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para remover filme dos favoritos
 * Encapsula a lógica de negócio para desfavoritar filmes
 */
class RemoveFromFavoritesUseCase
    @Inject
    constructor(
        private val repository: MovieRepository,
    ) {
        /**
         * Remove um filme dos favoritos
         * @param movieId ID do filme a ser removido
         * @return Result indicando sucesso ou falha
         */
        suspend operator fun invoke(movieId: Int): Result<Unit> {
            return if (movieId <= 0) {
                Result.failure(IllegalArgumentException("Movie ID must be greater than 0"))
            } else {
                try {
                    repository.removeFromFavorites(movieId)
                    Result.success(Unit)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }
    }
