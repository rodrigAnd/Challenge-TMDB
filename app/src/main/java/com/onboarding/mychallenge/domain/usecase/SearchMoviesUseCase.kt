package com.onboarding.mychallenge.domain.usecase

import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para buscar filmes por termo de pesquisa
 * Encapsula a lógica de negócio para pesquisar filmes
 */
class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Executa a busca de filmes por termo
     * @param query Termo de busca
     * @param page Número da página (padrão: 1)
     * @return Result com lista de filmes ou erro
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

