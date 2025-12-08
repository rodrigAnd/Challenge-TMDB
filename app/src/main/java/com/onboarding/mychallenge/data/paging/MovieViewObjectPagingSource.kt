package com.onboarding.mychallenge.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.presentation.mapper.toViewObject
import com.onboarding.mychallenge.presentation.movieList.MovieViewObject

/**
 * PagingSource wrapper que converte Movie para MovieViewObject.
 * Usa os conjuntos de favoritos fornecidos para enriquecer os dados.
 */
class MovieViewObjectPagingSource(
    private val basePagingSource: PagingSource<Int, Movie>,
    private val favoriteIds: Set<Int>,
    private val loadingFavoriteIds: Set<Int>,
) : PagingSource<Int, MovieViewObject>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieViewObject> {
        return when (val result = basePagingSource.load(params)) {
            is LoadResult.Page -> {
                LoadResult.Page(
                    data =
                        result.data.map { movie ->
                            movie.toViewObject(
                                isFavorite = favoriteIds.contains(movie.id),
                                isLoadingFavorite = loadingFavoriteIds.contains(movie.id),
                            )
                        },
                    prevKey = result.prevKey,
                    nextKey = result.nextKey,
                )
            }
            is LoadResult.Error -> LoadResult.Error(result.throwable)
            else -> LoadResult.Error(Exception("Invalid load result"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieViewObject>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
