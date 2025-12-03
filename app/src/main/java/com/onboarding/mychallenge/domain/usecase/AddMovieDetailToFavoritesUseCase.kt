package com.onboarding.mychallenge.domain.usecase

import com.onboarding.mychallenge.domain.model.MovieDetail
import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para adicionar filme aos favoritos com detalhes completos
 * Encapsula a lógica de negócio para favoritar filmes com MovieDetail
 */
class AddMovieDetailToFavoritesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Adiciona um filme aos favoritos com detalhes completos
     * @param movieDetail Detalhes completos do filme a ser adicionado
     * @return Result indicando sucesso ou falha
     */
    suspend operator fun invoke(movieDetail: MovieDetail): Result<Unit> {
        return try {
            repository.addMovieDetailToFavorites(movieDetail)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

