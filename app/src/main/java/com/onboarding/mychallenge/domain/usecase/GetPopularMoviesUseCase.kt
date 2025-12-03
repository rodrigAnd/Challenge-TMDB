package com.onboarding.mychallenge.domain.usecase

import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para buscar filmes populares.
 * 
 * Encapsula a lógica de negócio para obter a lista de filmes populares da API do TMDb.
 * Valida os parâmetros de entrada antes de realizar a busca.
 * 
 * @property repository Repositório de filmes para acesso aos dados.
 * 
 * @constructor Cria uma nova instância do [GetPopularMoviesUseCase] com o repositório injetado.
 */
class GetPopularMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Executa a busca de filmes populares.
     * 
     * Valida se o número da página é válido (deve ser maior que 0) antes de realizar
     * a busca no repositório.
     * 
     * @param page Número da página a ser buscada (padrão: 1).
     * @return [Result] contendo a lista de [Movie] em caso de sucesso,
     *         ou uma exceção em caso de erro ou parâmetro inválido.
     */
    suspend operator fun invoke(page: Int = 1): Result<List<Movie>> {
        return if (page < 1) {
            Result.failure(IllegalArgumentException("Page number must be greater than 0"))
        } else {
            repository.getPopularMovies(page)
        }
    }
}

