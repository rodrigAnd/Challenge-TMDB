package com.onboarding.mychallenge.domain.usecase

import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase para obter filmes favoritos.
 * 
 * Encapsula a lógica de negócio para obter a lista de filmes favoritos do banco
 * de dados local. Retorna um [Flow] que emite atualizações sempre que a lista
 * de favoritos mudar.
 * 
 * @property repository Repositório de filmes para acesso aos dados.
 * 
 * @constructor Cria uma nova instância do [GetFavoriteMoviesUseCase] com o repositório injetado.
 */
class GetFavoriteMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Obtém todos os filmes favoritos como Flow.
     * 
     * Retorna um [Flow] que emite a lista atualizada de filmes favoritos sempre
     * que houver mudanças no banco de dados local.
     * 
     * @return [Flow] que emite uma lista de [Movie] favoritos.
     */
    operator fun invoke(): Flow<List<Movie>> {
        return repository.getFavoriteMovies()
    }
}

