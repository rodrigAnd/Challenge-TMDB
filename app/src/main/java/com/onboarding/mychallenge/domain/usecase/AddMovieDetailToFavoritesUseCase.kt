package com.onboarding.mychallenge.domain.usecase
import android.database.sqlite.SQLiteException
import com.onboarding.mychallenge.domain.model.MovieDetail
import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para adicionar filme aos favoritos com detalhes completos.
 *
 * Encapsula a lógica de negócio para favoritar filmes usando o modelo [MovieDetail],
 * delegando a operação ao repositório.
 *
 * @param repository O repositório de filmes para acessar os dados.
 */
class AddMovieDetailToFavoritesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Adiciona um filme aos favoritos com detalhes completos.
     *
     * @param movieDetail Os [MovieDetail] completos do filme a ser adicionado.
     * @return Um [Result] indicando sucesso ([Unit]) ou falha ([Throwable]).
     */
    suspend operator fun invoke(movieDetail: MovieDetail): Result<Unit> {
        return try {
            repository.addMovieDetailToFavorites(movieDetail)
            Result.success(Unit)
        } catch (e: SQLiteException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
