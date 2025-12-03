package com.onboarding.mychallenge.presentation.movieList1

import app.cash.turbine.test
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.usecase.AddMovieDetailToFavoritesUseCase
import com.onboarding.mychallenge.domain.usecase.AddToFavoritesUseCase
import com.onboarding.mychallenge.domain.usecase.GetFavoriteMoviesUseCase
import com.onboarding.mychallenge.domain.usecase.GetMovieDetailsUseCase
import com.onboarding.mychallenge.domain.usecase.GetPopularMoviesUseCase
import com.onboarding.mychallenge.domain.usecase.IsFavoriteUseCase
import com.onboarding.mychallenge.domain.usecase.RemoveFromFavoritesUseCase
import com.onboarding.mychallenge.domain.usecase.SearchMoviesUseCase
import com.onboarding.mychallenge.presentation.movieList.MovieListUiState
import com.onboarding.mychallenge.presentation.movieList.MovieListViewModel
import com.onboarding.mychallenge.presentation.movieList.MovieViewObject
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MovieListViewModelTest {
    private lateinit var getPopularMoviesUseCase: GetPopularMoviesUseCase
    private lateinit var searchMoviesUseCase: SearchMoviesUseCase
    private lateinit var addToFavoritesUseCase: AddToFavoritesUseCase
    private lateinit var removeFromFavoritesUseCase: RemoveFromFavoritesUseCase
    private lateinit var isFavoriteUseCase: IsFavoriteUseCase
    private lateinit var getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase
    private lateinit var addMovieDetailToFavoritesUseCase: AddMovieDetailToFavoritesUseCase
    private lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase
    private lateinit var viewModel: MovieListViewModel

    @Before
    fun setup() {
        getPopularMoviesUseCase = mockk()
        searchMoviesUseCase = mockk()
        addToFavoritesUseCase = mockk()
        removeFromFavoritesUseCase = mockk()
        isFavoriteUseCase = mockk()
        getFavoriteMoviesUseCase = mockk()
        every { getFavoriteMoviesUseCase() } returns flowOf(emptyList())
    }

    @Test
    fun `initial state should be Loading`() =
        runTest {
            val movies = listOf(createMockMovie(1, "Movie 1"))
            coEvery { getPopularMoviesUseCase(1) } returns Result.success(movies)
            viewModel =
                MovieListViewModel(
                    getPopularMoviesUseCase,
                    searchMoviesUseCase,
                    addToFavoritesUseCase,
                    addMovieDetailToFavoritesUseCase,
                    removeFromFavoritesUseCase,
                    isFavoriteUseCase,
                    getFavoriteMoviesUseCase,
                    getMovieDetailsUseCase,
                )
            viewModel.uiState.test {
                val initialState = awaitItem()
                assertTrue(initialState is MovieListUiState.Loading)
            }
        }

    @Test
    fun `loadPopularMovies should update state to Success when movies are loaded`() =
        runTest {
            val movies = listOf(createMockMovie(1, "Movie 1"))
            coEvery { getPopularMoviesUseCase(1) } returns Result.success(movies)
            viewModel = createViewModel()
            viewModel.loadPopularMovies()
            viewModel.uiState.test {
                skipItems(1)
                val successState = awaitItem() as MovieListUiState.Success
                assertEquals(1, successState.movies.size)
                assertEquals("Movie 1", successState.movies[0].title)
                assertFalse(successState.isSearch)
                assertEquals(1, successState.currentPage)
            }
            coVerify(exactly = 1) { getPopularMoviesUseCase(1) }
        }

    @Test
    fun `loadPopularMovies should update state to Empty when no movies are returned`() =
        runTest {
            coEvery { getPopularMoviesUseCase(1) } returns Result.success(emptyList())
            viewModel = createViewModel()
            viewModel.loadPopularMovies()
            viewModel.uiState.test {
                skipItems(1)
                val emptyState = awaitItem()
                assertTrue(emptyState is MovieListUiState.Empty)
            }
        }

    @Test
    fun `loadPopularMovies should update state to Error when useCase fails`() =
        runTest {
            val error = Exception("Network error")
            coEvery { getPopularMoviesUseCase(1) } returns Result.failure(error)
            viewModel = createViewModel()
            viewModel.loadPopularMovies()
            viewModel.uiState.test {
                skipItems(1)
                val errorState = awaitItem() as MovieListUiState.Error
                assertTrue(errorState.message.contains("Network error"))
            }
        }

    @Test
    fun `updateSearchQuery should update search query`() =
        runTest {
            val query = "batman"
            viewModel = createViewModel()
            viewModel.updateSearchQuery(query)
            assertEquals(query, viewModel.searchQuery.value)
        }

    @Test
    fun `loadNextPage should append new movies to existing list`() =
        runTest {
            val page1Movies = listOf(createMockMovie(1, "Movie 1"))
            val page2Movies = listOf(createMockMovie(2, "Movie 2"))
            coEvery { getPopularMoviesUseCase(1) } returns Result.success(page1Movies)
            coEvery { getPopularMoviesUseCase(2) } returns Result.success(page2Movies)
            viewModel = createViewModel()
            viewModel.loadPopularMovies()
            viewModel.loadNextPage()
            viewModel.uiState.test {
                skipItems(2)
                val successState = awaitItem() as MovieListUiState.Success
                assertEquals(2, successState.movies.size)
                assertEquals(2, successState.currentPage)
            }
        }

    @Test
    fun `loadNextPage should not load when already loading`() =
        runTest {
            val movies = listOf(createMockMovie(1, "Movie 1"))
            coEvery { getPopularMoviesUseCase(1) } returns Result.success(movies)
            viewModel = createViewModel()
            viewModel.loadPopularMovies()
            // Aguardar o estado inicial carregar
            viewModel.uiState.test {
                skipItems(1)
                awaitItem()
            }
            // Tentar carregar próxima página imediatamente (já está carregando)
            viewModel.loadNextPage()
            // Verificar que não foi chamado novamente
            coVerify(exactly = 1) { getPopularMoviesUseCase(1) }
        }

    @Test
    fun `toggleFavorite should add to favorites when not favorite`() =
        runTest {
            val movie = createMockMovieViewObject(1, "Movie 1", isFavorite = false)
            coEvery { isFavoriteUseCase(1) } returns Result.success(false)
            coEvery { addToFavoritesUseCase(any()) } returns Result.success(Unit)
            viewModel = createViewModel()
            viewModel.toggleFavorite(movie)
            coVerify(exactly = 1) { isFavoriteUseCase(1) }
            coVerify(exactly = 1) { addToFavoritesUseCase(any()) }
            coVerify(exactly = 0) { removeFromFavoritesUseCase(any()) }
        }

    @Test
    fun `toggleFavorite should remove from favorites when favorite`() =
        runTest {
            val movie = createMockMovieViewObject(1, "Movie 1", isFavorite = true)
            coEvery { isFavoriteUseCase(1) } returns Result.success(true)
            coEvery { removeFromFavoritesUseCase(1) } returns Result.success(Unit)
            viewModel = createViewModel()
            viewModel.toggleFavorite(movie)
            coVerify(exactly = 1) { isFavoriteUseCase(1) }
            coVerify(exactly = 1) { removeFromFavoritesUseCase(1) }
            coVerify(exactly = 0) { addToFavoritesUseCase(any()) }
        }

    @Test
    fun `retry should reload popular movies when query is empty`() =
        runTest {
            val movies = listOf(createMockMovie(1, "Movie 1"))
            coEvery { getPopularMoviesUseCase(1) } returns Result.success(movies)
            viewModel = createViewModel()
            viewModel.retry()
            coVerify(atLeast = 1) { getPopularMoviesUseCase(1) }
        }

    private fun createViewModel(): MovieListViewModel {
        return MovieListViewModel(
            getPopularMoviesUseCase,
            searchMoviesUseCase,
            addToFavoritesUseCase,
            addMovieDetailToFavoritesUseCase,
            removeFromFavoritesUseCase,
            isFavoriteUseCase,
            getFavoriteMoviesUseCase,
            getMovieDetailsUseCase,
        )
    }

    private fun createMockMovie(
        id: Int,
        title: String,
    ): Movie {
        return Movie(
            id = id,
            title = title,
            overview = "Overview",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            releaseDate = "2024-01-01",
            voteAverage = 8.5,
            voteCount = 100,
            popularity = 100.0,
        )
    }

    private fun createMockMovieViewObject(
        id: Int,
        title: String,
        isFavorite: Boolean = false,
    ): MovieViewObject {
        return MovieViewObject(
            id = id,
            title = title,
            overview = "Overview",
            posterUrl = "https://example.com/poster.jpg",
            backdropUrl = "https://example.com/backdrop.jpg",
            releaseDate = "2024-01-01",
            formattedReleaseDate = "01/01/2024",
            rating = 8.5,
            formattedRating = "8.5",
            voteCount = 100,
            popularity = 100.0,
            isFavorite = isFavorite,
        )
    }
}
