package com.onboarding.mychallenge.domain.usecase
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para adicionar filme aos favoritos
 * Encapsula a lógica de negócio para favoritar filmes
 */
class AddToFavoritesUseCase
    @Inject
    constructor(
        private val repository: MovieRepository,
    ) {
        /**
         * Adiciona um filme aos favoritos
         * @param movie Filme a ser adicionado
         * @return Result indicando sucesso ou falha
         */
        suspend operator fun invoke(movie: Movie): Result<Unit> {
            return try {
                repository.addToFavorites(movie)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
