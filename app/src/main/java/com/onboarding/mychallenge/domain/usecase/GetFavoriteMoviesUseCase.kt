package com.onboarding.mychallenge.domain.usecase
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase para obter filmes favoritos.
 *
 * Encapsula a lógica de negócio para obter a lista de filmes favoritos do repositório
 * como um [Flow], permitindo observação em tempo real.
 *
 * @param repository O repositório de filmes para acessar os dados.
 */
class GetFavoriteMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Obtém todos os filmes favoritos como um [Flow].
     *
     * @return Um [Flow] que emite uma lista de [Movie] favoritos sempre que houver uma mudança.
     */
    operator fun invoke(): Flow<List<Movie>> {
        return repository.getFavoriteMovies()
    }
}
