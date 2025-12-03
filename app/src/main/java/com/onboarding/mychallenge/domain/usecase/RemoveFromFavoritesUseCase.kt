package com.onboarding.mychallenge.domain.usecase

import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para remover filme dos favoritos.
 * 
 * Encapsula a lógica de negócio para remover um filme da lista de favoritos,
 * deletando-o do banco de dados local.
 * 
 * @property repository Repositório de filmes para acesso aos dados.
 * 
 * @constructor Cria uma nova instância do [RemoveFromFavoritesUseCase] com o repositório injetado.
 */
class RemoveFromFavoritesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Remove um filme dos favoritos.
     * 
     * Valida se o ID do filme é válido (deve ser maior que 0) antes de realizar
     * a remoção no repositório. Captura exceções e retorna um [Result] indicando
     * sucesso ou falha.
     * 
     * @param movieId ID do filme a ser removido dos favoritos.
     * @return [Result] contendo [Unit] em caso de sucesso,
     *         ou uma exceção em caso de erro ou ID inválido.
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

