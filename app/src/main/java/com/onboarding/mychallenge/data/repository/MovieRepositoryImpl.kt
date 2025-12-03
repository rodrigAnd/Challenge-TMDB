package com.onboarding.mychallenge.data.repository

import android.util.Log
import com.onboarding.mychallenge.data.local.dao.FavoriteMovieDao
import com.onboarding.mychallenge.data.mapper.toDomain
import com.onboarding.mychallenge.data.mapper.toEntity
import com.onboarding.mychallenge.data.mapper.toMovieDetail
import com.onboarding.mychallenge.data.remote.api.TmdbApiService
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.model.MovieDetail
import com.onboarding.mychallenge.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementação do repositório de filmes.
 * 
 * Combina dados remotos (API do TMDb via Retrofit) e locais (Room) para fornecer
 * uma única fonte de dados para a camada de domínio. Gerencia operações de busca
 * na API, armazenamento local de favoritos e tratamento de erros de rede.
 * 
 * @property apiService Serviço da API do TMDb para requisições remotas.
 * @property favoriteDao DAO para operações com filmes favoritos no banco de dados local.
 * 
 * @constructor Cria uma nova instância do [MovieRepositoryImpl] com as dependências injetadas.
 */
class MovieRepositoryImpl @Inject constructor(
    private val apiService: TmdbApiService,
    private val favoriteDao: FavoriteMovieDao
) : MovieRepository {
    
    companion object {
        private const val TAG = "MovieRepositoryImpl"
    }
    
    override suspend fun getPopularMovies(page: Int): Result<List<Movie>> {
        Log.d(TAG, "getPopularMovies: Chamando API para filmes populares, página $page")
        return try {
            if (page < 1) {
                return Result.failure(IllegalArgumentException("Page number must be greater than 0"))
            }
            
            val response = apiService.getPopularMovies(page)
            
            // Validação da resposta
            if (response.results.isEmpty() && page == 1) {
                Log.w(TAG, "getPopularMovies: Nenhum filme encontrado na primeira página")
            }
            
            val movies = response.results.mapNotNull { dto ->
                try {
                    dto.toDomain()
                } catch (e: Exception) {
                    Log.e(TAG, "getPopularMovies: Erro ao converter DTO para Domain", e)
                    null
                }
            }
            
            Log.d(TAG, "getPopularMovies: Sucesso, ${movies.size} filmes recebidos.")
            Result.success(movies)
        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "getPopularMovies: Sem conexão com a internet", e)
            Result.failure(Exception("Sem conexão com a internet. Verifique sua conexão e tente novamente.", e))
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "getPopularMovies: Timeout na requisição", e)
            Result.failure(Exception("Tempo de espera esgotado. Tente novamente.", e))
        } catch (e: retrofit2.HttpException) {
            val errorMessage = when (e.code()) {
                401 -> "Não autorizado. Verifique suas credenciais."
                404 -> "Recurso não encontrado."
                429 -> "Muitas requisições. Aguarde um momento e tente novamente."
                500 -> "Erro no servidor. Tente novamente mais tarde."
                else -> "Erro ao buscar filmes: ${e.message()}"
            }
            Log.e(TAG, "getPopularMovies: Erro HTTP ${e.code()}", e)
            Result.failure(Exception(errorMessage, e))
        } catch (e: Exception) {
            Log.e(TAG, "getPopularMovies: Erro ao buscar filmes populares", e)
            Result.failure(Exception("Erro ao carregar filmes. Tente novamente.", e))
        }
    }
    
    override suspend fun searchMovies(query: String, page: Int): Result<List<Movie>> {
        Log.d(TAG, "searchMovies: Chamando API para pesquisa '$query', página $page")
        return try {
            if (query.isBlank()) {
                return Result.failure(IllegalArgumentException("Search query cannot be empty"))
            }
            
            if (page < 1) {
                return Result.failure(IllegalArgumentException("Page number must be greater than 0"))
            }
            
            val response = apiService.searchMovies(query.trim(), page)
            
            val movies = response.results.mapNotNull { dto ->
                try {
                    dto.toDomain()
                } catch (e: Exception) {
                    Log.e(TAG, "searchMovies: Erro ao converter DTO para Domain", e)
                    null
                }
            }
            
            Log.d(TAG, "searchMovies: Sucesso, ${movies.size} filmes encontrados para '$query'.")
            Result.success(movies)
        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "searchMovies: Sem conexão com a internet", e)
            Result.failure(Exception("Sem conexão com a internet. Verifique sua conexão e tente novamente.", e))
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "searchMovies: Timeout na requisição", e)
            Result.failure(Exception("Tempo de espera esgotado. Tente novamente.", e))
        } catch (e: retrofit2.HttpException) {
            val errorMessage = when (e.code()) {
                401 -> "Não autorizado. Verifique suas credenciais."
                404 -> "Nenhum resultado encontrado para '$query'."
                429 -> "Muitas requisições. Aguarde um momento e tente novamente."
                500 -> "Erro no servidor. Tente novamente mais tarde."
                else -> "Erro ao buscar filmes: ${e.message()}"
            }
            Log.e(TAG, "searchMovies: Erro HTTP ${e.code()}", e)
            Result.failure(Exception(errorMessage, e))
        } catch (e: Exception) {
            Log.e(TAG, "searchMovies: Erro ao buscar filmes para '$query'", e)
            Result.failure(Exception("Erro ao buscar filmes. Tente novamente.", e))
        }
    }
    
    override suspend fun addToFavorites(movie: Movie) {
        Log.d(TAG, "addToFavorites: Adicionando filme ${movie.id} aos favoritos")
        try {
            favoriteDao.insertFavorite(movie.toEntity())
        } catch (e: Exception) {
            Log.e(TAG, "addToFavorites: Erro ao adicionar filme ${movie.id} aos favoritos", e)
            throw e
        }
    }
    
    override suspend fun addMovieDetailToFavorites(movieDetail: MovieDetail) {
        Log.d(TAG, "addMovieDetailToFavorites: Adicionando filme ${movieDetail.id} aos favoritos com detalhes completos")
        try {
            favoriteDao.insertFavorite(movieDetail.toEntity())
        } catch (e: Exception) {
            Log.e(TAG, "addMovieDetailToFavorites: Erro ao adicionar filme ${movieDetail.id} aos favoritos", e)
            throw e
        }
    }
    
    override suspend fun removeFromFavorites(movieId: Int) {
        Log.d(TAG, "removeFromFavorites: Iniciando remoção do filme $movieId dos favoritos")
        try {
            favoriteDao.deleteFavorite(movieId)
            Log.d(TAG, "removeFromFavorites: Filme $movieId removido do banco de dados com sucesso")
        } catch (e: Exception) {
            Log.e(TAG, "removeFromFavorites: Erro ao remover filme $movieId dos favoritos", e)
            throw e
        }
    }
    
    override suspend fun isFavorite(movieId: Int): Boolean {
        return try {
            val isFav = favoriteDao.isFavorite(movieId)
            Log.d(TAG, "isFavorite: Filme $movieId é favorito? $isFav")
            isFav
        } catch (e: Exception) {
            Log.e(TAG, "isFavorite: Erro ao verificar se filme $movieId é favorito", e)
            throw e
        }
    }
    
    override fun getFavoriteMovies(): Flow<List<Movie>> {
        Log.d(TAG, "getFavoriteMovies: Criando Flow para observar filmes favoritos")
        return try {
            favoriteDao.getAllFavorites()
                .map { entities -> 
                    Log.d(TAG, "getFavoriteMovies: Recebidas ${entities.size} entidades do DAO")
                    val movies = entities.map { it.toDomain() }
                    Log.d(TAG, "getFavoriteMovies: Convertidas ${movies.size} entidades para Movie, IDs: ${movies.map { it.id }}")
                    movies
                }
        } catch (e: Exception) {
            Log.e(TAG, "getFavoriteMovies: Erro ao obter filmes favoritos", e)
            throw e
        }
    }
    
    override suspend fun getMovieDetails(movieId: Int): Result<MovieDetail> {
        Log.d(TAG, "getMovieDetails: Buscando detalhes do filme $movieId")
        return try {
            if (movieId <= 0) {
                return Result.failure(IllegalArgumentException("Movie ID must be greater than 0"))
            }
            
            val response = apiService.getMovieDetails(movieId, "pt-BR")
            val movieDetail = response.toDomain()
            
            Log.d(TAG, "getMovieDetails: Sucesso ao buscar detalhes do filme $movieId")
            Result.success(movieDetail)
        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "getMovieDetails: Sem conexão com a internet", e)
            Result.failure(Exception("Sem conexão com a internet. Verifique sua conexão e tente novamente.", e))
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "getMovieDetails: Timeout na requisição", e)
            Result.failure(Exception("Tempo de espera esgotado. Tente novamente.", e))
        } catch (e: retrofit2.HttpException) {
            val errorMessage = when (e.code()) {
                401 -> "Não autorizado. Verifique suas credenciais."
                404 -> "Filme não encontrado."
                429 -> "Muitas requisições. Aguarde um momento e tente novamente."
                500 -> "Erro no servidor. Tente novamente mais tarde."
                else -> "Erro ao buscar detalhes do filme: ${e.message()}"
            }
            Log.e(TAG, "getMovieDetails: Erro HTTP ${e.code()}", e)
            Result.failure(Exception(errorMessage, e))
        } catch (e: Exception) {
            Log.e(TAG, "getMovieDetails: Erro ao buscar detalhes do filme $movieId", e)
            Result.failure(Exception("Erro ao carregar detalhes do filme. Tente novamente.", e))
        }
    }
    
    override suspend fun getFavoriteMovieDetails(movieId: Int): Result<MovieDetail> {
        Log.d(TAG, "getFavoriteMovieDetails: Buscando detalhes do filme favorito $movieId")
        return try {
            val entity = favoriteDao.getFavoriteById(movieId)
            if (entity != null) {
                val movieDetail = entity.toMovieDetail()
                Log.d(TAG, "getFavoriteMovieDetails: Sucesso ao buscar detalhes do filme favorito $movieId")
                Result.success(movieDetail)
            } else {
                Log.w(TAG, "getFavoriteMovieDetails: Filme favorito $movieId não encontrado")
                Result.failure(Exception("Filme favorito não encontrado"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getFavoriteMovieDetails: Erro ao buscar detalhes do filme favorito $movieId", e)
            Result.failure(Exception("Erro ao carregar detalhes do filme favorito. Tente novamente.", e))
        }
    }
}

