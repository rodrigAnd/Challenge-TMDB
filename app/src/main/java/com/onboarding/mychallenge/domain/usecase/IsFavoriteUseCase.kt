package com.onboarding.mychallenge.domain.usecase
import android.database.sqlite.SQLiteException
import com.onboarding.mychallenge.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * UseCase para verificar se um filme está nos favoritos.
 *
 * Encapsula a lógica de negócio para verificar o status de favorito de um filme,
 * delegando a operação ao repositório. Garante que o ID do filme seja válido.
 *
 * @param repository O repositório de filmes para acessar os dados.
 */
class IsFavoriteUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Verifica se um filme está nos favoritos.
     *
     * @param movieId O ID do filme a ser verificado.
     * @return Um [Result] contendo `true` se o filme é favorito, `false` caso contrário,
     *         ou um [Throwable] em caso de erro.
     * @throws IllegalArgumentException se o ID do filme for menor ou igual a 0.
     */
    suspend operator fun invoke(movieId: Int): Result<Boolean> {
        return if (movieId <= 0) {
            Result.failure(IllegalArgumentException("Movie ID must be greater than 0"))
        } else {
            try {
                val isFavorite = repository.isFavorite(movieId)
                Result.success(isFavorite)
            } catch (e: SQLiteException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
