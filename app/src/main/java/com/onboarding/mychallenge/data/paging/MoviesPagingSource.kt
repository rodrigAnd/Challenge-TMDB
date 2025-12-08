package com.onboarding.mychallenge.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.onboarding.mychallenge.data.mapper.toDomain
import com.onboarding.mychallenge.data.remote.api.TmdbApiService
import com.onboarding.mychallenge.domain.model.Movie

/**
 * PagingSource para carregar filmes populares da API do TMDb.
 *
 * Implementa paginação usando a biblioteca Paging 3, carregando páginas sob demanda
 * conforme o usuário faz scroll na lista.
 */
class PopularMoviesPagingSource(
    private val apiService: TmdbApiService,
) : PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val page = params.key ?: 1
            val response = apiService.getPopularMovies(page)

            val movies =
                response.results.mapNotNull { dto ->
                    try {
                        dto.toDomain()
                    } catch (e: Exception) {
                        null
                    }
                }

            LoadResult.Page(
                data = movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

/**
 * PagingSource para buscar filmes na API do TMDb.
 *
 * Implementa paginação usando a biblioteca Paging 3 para resultados de busca.
 */
class SearchMoviesPagingSource(
    private val apiService: TmdbApiService,
    private val query: String,
) : PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            if (query.isBlank()) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null,
                )
            }

            val page = params.key ?: 1
            val response = apiService.searchMovies(query.trim(), page)

            val movies =
                response.results.mapNotNull { dto ->
                    try {
                        dto.toDomain()
                    } catch (e: Exception) {
                        null
                    }
                }

            LoadResult.Page(
                data = movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
