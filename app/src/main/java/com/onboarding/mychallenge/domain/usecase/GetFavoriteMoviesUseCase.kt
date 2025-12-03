package com.onboarding.mychallenge.domain.usecase

import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase para obter filmes favoritos
 * Encapsula a lógica de negócio para obter lista de favoritos
 */
class GetFavoriteMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Obtém todos os filmes favoritos como Flow
     * @return Flow com lista de filmes favoritos
     */
    operator fun invoke(): Flow<List<Movie>> {
        return repository.getFavoriteMovies()
    }
}

