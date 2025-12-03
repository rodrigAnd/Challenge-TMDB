package com.onboarding.mychallenge.domain.usecase

import com.onboarding.mychallenge.domain.model.MovieDetail
import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para buscar detalhes completos de um filme.
 * 
 * Encapsula a lógica de negócio para obter informações detalhadas de um filme
 * específico da API do TMDb. Valida o ID do filme antes de realizar a busca.
 * 
 * @property repository Repositório de filmes para acesso aos dados.
 * 
 * @constructor Cria uma nova instância do [GetMovieDetailsUseCase] com o repositório injetado.
 */
class GetMovieDetailsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Executa a busca de detalhes completos do filme.
     * 
     * Valida se o ID do filme é válido (deve ser maior que 0) antes de realizar
     * a busca no repositório.
     * 
     * @param movieId ID do filme cujos detalhes devem ser buscados.
     * @return [Result] contendo [MovieDetail] em caso de sucesso,
     *         ou uma exceção em caso de erro ou ID inválido.
     */
    suspend operator fun invoke(movieId: Int): Result<MovieDetail> {
        return if (movieId <= 0) {
            Result.failure(IllegalArgumentException("Movie ID must be greater than 0"))
        } else {
            repository.getMovieDetails(movieId)
        }
    }
}

