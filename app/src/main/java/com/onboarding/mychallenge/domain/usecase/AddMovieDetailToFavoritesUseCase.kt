package com.onboarding.mychallenge.domain.usecase

import com.onboarding.mychallenge.domain.model.MovieDetail
import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para adicionar filme aos favoritos com detalhes completos.
 * 
 * Encapsula a lógica de negócio para adicionar um filme à lista de favoritos
 * com todas as informações detalhadas, salvando-o no banco de dados local.
 * Útil quando já se possui os detalhes completos do filme.
 * 
 * @property repository Repositório de filmes para acesso aos dados.
 * 
 * @constructor Cria uma nova instância do [AddMovieDetailToFavoritesUseCase] com o repositório injetado.
 */
class AddMovieDetailToFavoritesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Adiciona um filme aos favoritos com detalhes completos.
     * 
     * Salva o filme com todas as informações detalhadas no banco de dados local
     * através do repositório. Captura exceções e retorna um [Result] indicando
     * sucesso ou falha.
     * 
     * @param movieDetail Detalhes completos do filme a ser adicionado aos favoritos.
     * @return [Result] contendo [Unit] em caso de sucesso,
     *         ou uma exceção em caso de erro ao salvar.
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

