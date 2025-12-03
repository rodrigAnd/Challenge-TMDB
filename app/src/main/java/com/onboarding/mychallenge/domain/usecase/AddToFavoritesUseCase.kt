package com.onboarding.mychallenge.domain.usecase
import android.database.sqlite.SQLiteException
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para adicionar filme aos favoritos.
 *
 * Encapsula a lógica de negócio para favoritar filmes, delegando a operação ao repositório.
 *
 * @param repository O repositório de filmes para acessar os dados.
 */
class AddToFavoritesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Adiciona um filme aos favoritos.
     *
     * @param movie O [Movie] a ser adicionado aos favoritos.
     * @return Um [Result] indicando sucesso ([Unit]) ou falha ([Throwable]).
     */
    suspend operator fun invoke(movie: Movie): Result<Unit> {
        return try {
            repository.addToFavorites(movie)
            Result.success(Unit)
        } catch (e: SQLiteException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
