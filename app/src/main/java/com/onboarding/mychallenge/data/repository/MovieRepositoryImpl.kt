package com.onboarding.mychallenge.data.repository
import com.onboarding.mychallenge.data.local.dao.FavoriteMovieDao
import com.onboarding.mychallenge.data.mapper.toDomain
import com.onboarding.mychallenge.data.mapper.toEntity
import com.onboarding.mychallenge.data.mapper.toMovieDetail
import com.onboarding.mychallenge.data.paging.PopularMoviesPagingSource
import com.onboarding.mychallenge.data.paging.SearchMoviesPagingSource
import com.onboarding.mychallenge.data.remote.api.TmdbApiService
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.model.MovieDetail
import com.onboarding.mychallenge.domain.model.PaginatedResult
import com.onboarding.mychallenge.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementação do repositório de filmes
 * Combina dados remotos (API via Retrofit) e locais (Room)
 */
class MovieRepositoryImpl
    @Inject
    constructor(
        private val apiService: TmdbApiService,
        private val favoriteDao: FavoriteMovieDao,
    ) : MovieRepository {
        override suspend fun getPopularMovies(page: Int): Result<PaginatedResult<Movie>> {
            return try {
                if (page < 1) {
                    return Result.failure(IllegalArgumentException("Page number must be greater than 0"))
                }
                val response = apiService.getPopularMovies(page)
                val movies =
                    response.results.mapNotNull { dto ->
                        try {
                            dto.toDomain()
                        } catch (e: Exception) {
                            null
                        }
                    }
                val paginatedResult =
                    PaginatedResult(
                        data = movies,
                        currentPage = response.page,
                        totalPages = response.totalPages,
                        totalResults = response.totalResults,
                    )
                Result.success(paginatedResult)
            } catch (e: java.net.UnknownHostException) {
                Result.failure(Exception("Sem conexão com a internet. Verifique sua conexão e tente novamente.", e))
            } catch (e: java.net.SocketTimeoutException) {
                Result.failure(Exception("Tempo de espera esgotado. Tente novamente.", e))
            } catch (e: retrofit2.HttpException) {
                val errorMessage =
                    when (e.code()) {
                        401 -> "Não autorizado. Verifique suas credenciais."
                        404 -> "Recurso não encontrado."
                        429 -> "Muitas requisições. Aguarde um momento e tente novamente."
                        500 -> "Erro no servidor. Tente novamente mais tarde."
                        else -> "Erro ao buscar filmes: ${e.message()}"
                    }
                Result.failure(Exception(errorMessage, e))
            } catch (e: Exception) {
                Result.failure(Exception("Erro ao carregar filmes. Tente novamente.", e))
            }
        }

        override suspend fun searchMovies(
            query: String,
            page: Int,
        ): Result<PaginatedResult<Movie>> {
            return try {
                if (query.isBlank()) {
                    return Result.failure(IllegalArgumentException("Search query cannot be empty"))
                }
                if (page < 1) {
                    return Result.failure(IllegalArgumentException("Page number must be greater than 0"))
                }
                val response = apiService.searchMovies(query.trim(), page)
                val movies =
                    response.results.mapNotNull { dto ->
                        try {
                            dto.toDomain()
                        } catch (e: Exception) {
                            null
                        }
                    }
                val paginatedResult =
                    PaginatedResult(
                        data = movies,
                        currentPage = response.page,
                        totalPages = response.totalPages,
                        totalResults = response.totalResults,
                    )
                Result.success(paginatedResult)
            } catch (e: java.net.UnknownHostException) {
                Result.failure(Exception("Sem conexão com a internet. Verifique sua conexão e tente novamente.", e))
            } catch (e: java.net.SocketTimeoutException) {
                Result.failure(Exception("Tempo de espera esgotado. Tente novamente.", e))
            } catch (e: retrofit2.HttpException) {
                val errorMessage =
                    when (e.code()) {
                        401 -> "Não autorizado. Verifique suas credenciais."
                        404 -> "Nenhum resultado encontrado para '$query'."
                        429 -> "Muitas requisições. Aguarde um momento e tente novamente."
                        500 -> "Erro no servidor. Tente novamente mais tarde."
                        else -> "Erro ao buscar filmes: ${e.message()}"
                    }
                Result.failure(Exception(errorMessage, e))
            } catch (e: Exception) {
                Result.failure(Exception("Erro ao buscar filmes. Tente novamente.", e))
            }
        }

        override suspend fun addToFavorites(movie: Movie) {
            try {
                favoriteDao.insertFavorite(movie.toEntity())
            } catch (e: Exception) {
                throw e
            }
        }

        override suspend fun addMovieDetailToFavorites(movieDetail: MovieDetail) {
            try {
                favoriteDao.insertFavorite(movieDetail.toEntity())
            } catch (e: Exception) {
                throw e
            }
        }

        override suspend fun removeFromFavorites(movieId: Int) {
            try {
                favoriteDao.deleteFavorite(movieId)
            } catch (e: Exception) {
                throw e
            }
        }

        override suspend fun isFavorite(movieId: Int): Boolean {
            return try {
                favoriteDao.isFavorite(movieId)
            } catch (e: Exception) {
                throw e
            }
        }

        override fun getFavoriteMovies(): Flow<List<Movie>> {
            return try {
                favoriteDao.getAllFavorites()
                    .map { entities ->
                        entities.map { it.toDomain() }
                    }
            } catch (e: Exception) {
                throw e
            }
        }

        override suspend fun getMovieDetails(movieId: Int): Result<MovieDetail> {
            return try {
                if (movieId <= 0) {
                    return Result.failure(IllegalArgumentException("Movie ID must be greater than 0"))
                }
                val response = apiService.getMovieDetails(movieId, "pt-BR")
                val movieDetail = response.toDomain()
                Result.success(movieDetail)
            } catch (e: java.net.UnknownHostException) {
                Result.failure(Exception("Sem conexão com a internet. Verifique sua conexão e tente novamente.", e))
            } catch (e: java.net.SocketTimeoutException) {
                Result.failure(Exception("Tempo de espera esgotado. Tente novamente.", e))
            } catch (e: retrofit2.HttpException) {
                val errorMessage =
                    when (e.code()) {
                        401 -> "Não autorizado. Verifique suas credenciais."
                        404 -> "Filme não encontrado."
                        429 -> "Muitas requisições. Aguarde um momento e tente novamente."
                        500 -> "Erro no servidor. Tente novamente mais tarde."
                        else -> "Erro ao buscar detalhes do filme: ${e.message()}"
                    }
                Result.failure(Exception(errorMessage, e))
            } catch (e: Exception) {
                Result.failure(Exception("Erro ao carregar detalhes do filme. Tente novamente.", e))
            }
        }

        override suspend fun getFavoriteMovieDetails(movieId: Int): Result<MovieDetail> {
            return try {
                val entity = favoriteDao.getFavoriteById(movieId)
                if (entity != null) {
                    val movieDetail = entity.toMovieDetail()
                    Result.success(movieDetail)
                } else {
                    Result.failure(Exception("Filme favorito não encontrado"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Erro ao carregar detalhes do filme favorito. Tente novamente.", e))
            }
        }

        /**
         * Retorna um PagingSource para filmes populares usando Paging 3.
         *
         * @return Um [PopularMoviesPagingSource] configurado para carregar filmes populares.
         */
        fun getPopularMoviesPagingSource(): PopularMoviesPagingSource {
            return PopularMoviesPagingSource(apiService)
        }

        /**
         * Retorna um PagingSource para busca de filmes usando Paging 3.
         *
         * @param query O termo de busca.
         * @return Um [SearchMoviesPagingSource] configurado para buscar filmes.
         */
        fun getSearchMoviesPagingSource(query: String): SearchMoviesPagingSource {
            return SearchMoviesPagingSource(apiService, query)
        }
    }
