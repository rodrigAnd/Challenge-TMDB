package com.onboarding.mychallenge.domain.usecase

import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para adicionar filme aos favoritos.
 * 
 * Encapsula a lógica de negócio para adicionar um filme à lista de favoritos,
 * salvando-o no banco de dados local para acesso offline.
 * 
 * @property repository Repositório de filmes para acesso aos dados.
 * 
 * @constructor Cria uma nova instância do [AddToFavoritesUseCase] com o repositório injetado.
 */
class AddToFavoritesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Adiciona um filme aos favoritos.
     * 
     * Salva o filme no banco de dados local através do repositório.
     * Captura exceções e retorna um [Result] indicando sucesso ou falha.
     * 
     * @param movie Filme a ser adicionado aos favoritos.
     * @return [Result] contendo [Unit] em caso de sucesso,
     *         ou uma exceção em caso de erro ao salvar.
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

