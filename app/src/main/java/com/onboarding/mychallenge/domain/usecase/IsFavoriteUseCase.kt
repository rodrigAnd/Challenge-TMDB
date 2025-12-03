package com.onboarding.mychallenge.domain.usecase

import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para verificar se um filme está nos favoritos.
 * 
 * Encapsula a lógica de negócio para verificar se um filme específico está
 * salvo na lista de favoritos do banco de dados local.
 * 
 * @property repository Repositório de filmes para acesso aos dados.
 * 
 * @constructor Cria uma nova instância do [IsFavoriteUseCase] com o repositório injetado.
 */
class IsFavoriteUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Verifica se um filme está nos favoritos.
     * 
     * Valida se o ID do filme é válido (deve ser maior que 0) antes de realizar
     * a verificação no repositório. Captura exceções e retorna um [Result] com
     * o resultado da verificação.
     * 
     * @param movieId ID do filme a ser verificado.
     * @return [Result] contendo `true` se o filme está nos favoritos, `false` caso contrário,
     *         ou uma exceção em caso de erro ou ID inválido.
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

