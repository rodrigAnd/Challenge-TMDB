package com.onboarding.mychallenge.presentation.movieList
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
import com.onboarding.mychallenge.presentation.movieList.MovieViewObject
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
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
    private lateinit var addMovieDetailToFavoritesUseCase: AddMovieDetailToFavoritesUseCase
    private lateinit var removeFromFavoritesUseCase: RemoveFromFavoritesUseCase
    private lateinit var isFavoriteUseCase: IsFavoriteUseCase
    private lateinit var getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase
    private lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase
    private lateinit var viewModel: MovieListViewModel
    @Before
    fun setup() {
        getPopularMoviesUseCase = mockk()
        searchMoviesUseCase = mockk()
        addToFavoritesUseCase = mockk()
        addMovieDetailToFavoritesUseCase = mockk()
        removeFromFavoritesUseCase = mockk()
        isFavoriteUseCase = mockk()
        getFavoriteMoviesUseCase = mockk()
        getMovieDetailsUseCase = mockk()
        every { getFavoriteMoviesUseCase() } returns flowOf(emptyList())
        viewModel = MovieListViewModel(
            getPopularMoviesUseCase = getPopularMoviesUseCase,
            searchMoviesUseCase = searchMoviesUseCase,
            addToFavoritesUseCase = addToFavoritesUseCase,
            addMovieDetailToFavoritesUseCase = addMovieDetailToFavoritesUseCase,
            removeFromFavoritesUseCase = removeFromFavoritesUseCase,
            isFavoriteUseCase = isFavoriteUseCase,
            getFavoriteMoviesUseCase = getFavoriteMoviesUseCase,
            getMovieDetailsUseCase = getMovieDetailsUseCase
        )
    }
    @Test
    fun `loadPopularMovies should emit Loading then Success when API returns movies`() = runTest {
        val movies = listOf(
            createMovie(1, "Movie 1"),
            createMovie(2, "Movie 2")
        )
        coEvery { getPopularMoviesUseCase(1) } returns Result.success(movies)
        every { getFavoriteMoviesUseCase() } returns flowOf(emptyList())
        viewModel.uiState.test {
            viewModel.loadPopularMovies()
            val loadingState = awaitItem()
            assertTrue(loadingState is MovieListUiState.Loading)
            val successState = awaitItem() as MovieListUiState.Success
            assertEquals(2, successState.movies.size)
            assertEquals(1, successState.currentPage)
            assertTrue(successState.canLoadMore)
            assertFalse(successState.isLoadingMore)
        }
        coVerify(exactly = 1) { getPopularMoviesUseCase(1) }
    }
    @Test
    fun `loadPopularMovies should emit Error when API fails`() = runTest {
        val errorMessage = "Network error"
        coEvery { getPopularMoviesUseCase(1) } returns Result.failure(Exception(errorMessage))
        every { getFavoriteMoviesUseCase() } returns flowOf(emptyList())
        viewModel.uiState.test {
            viewModel.loadPopularMovies()
            val loadingState = awaitItem()
            assertTrue(loadingState is MovieListUiState.Loading)
            val errorState = awaitItem() as MovieListUiState.Error
            assertEquals(errorMessage, errorState.message)
        }
    }
    @Test
    fun `loadPopularMovies should emit Empty when API returns empty list`() = runTest {
        coEvery { getPopularMoviesUseCase(1) } returns Result.success(emptyList())
        every { getFavoriteMoviesUseCase() } returns flowOf(emptyList())
        viewModel.uiState.test {
            viewModel.loadPopularMovies()
            val loadingState = awaitItem()
            assertTrue(loadingState is MovieListUiState.Loading)
            val emptyState = awaitItem()
            assertTrue(emptyState is MovieListUiState.Empty)
        }
    }
    @Test
    fun `loadNextPage should append movies to existing list`() = runTest {
        val initialMovies = listOf(createMovie(1, "Movie 1"))
        val newMovies = listOf(createMovie(2, "Movie 2"))
        coEvery { getPopularMoviesUseCase(1) } returns Result.success(initialMovies)
        coEvery { getPopularMoviesUseCase(2) } returns Result.success(newMovies)
        every { getFavoriteMoviesUseCase() } returns flowOf(emptyList())
        viewModel.loadPopularMovies()
        viewModel.uiState.test {
            skipItems(1)
            awaitItem()
        }
        viewModel.loadNextPage()
        viewModel.uiState.test {
            skipItems(1)
            val successState = awaitItem() as MovieListUiState.Success
            assertEquals(2, successState.movies.size)
            assertEquals(2, successState.currentPage)
        }
        coVerify { getPopularMoviesUseCase(1) }
        coVerify { getPopularMoviesUseCase(2) }
    }
    @Test
    fun `updateSearchQuery should trigger search after debounce`() = runTest {
        val query = "test"
        val movies = listOf(createMovie(1, "Test Movie"))
        coEvery { searchMoviesUseCase(query, 1) } returns Result.success(movies)
        every { getFavoriteMoviesUseCase() } returns flowOf(emptyList())
        viewModel.updateSearchQuery(query)
        kotlinx.coroutines.delay(600)
        viewModel.uiState.test {
            skipItems(1)
            val successState = awaitItem() as MovieListUiState.Success
            assertEquals(1, successState.movies.size)
        }
        coVerify(exactly = 1) { searchMoviesUseCase(query, 1) }
    }
    @Test
    fun `toggleFavorite should add to favorites when not favorite`() = runTest {
        val movie = createMovie(1, "Movie 1")
        val movieViewObject = createMovieViewObject(1, "Movie 1")
        val movies = listOf(movie)
        val movieDetail = createMovieDetail(1, "Movie 1")
        coEvery { getPopularMoviesUseCase(1) } returns Result.success(movies)
        coEvery { isFavoriteUseCase(1) } returns Result.success(false)
        coEvery { getMovieDetailsUseCase(1) } returns Result.success(movieDetail)
        coEvery { addMovieDetailToFavoritesUseCase(any()) } returns Result.success(Unit)
        every { getFavoriteMoviesUseCase() } returns flowOf(emptyList())
        viewModel.loadPopularMovies()
        viewModel.uiState.test {
            skipItems(1)
            awaitItem()
        }
        viewModel.toggleFavorite(movieViewObject)
        coVerify(exactly = 1) { addMovieDetailToFavoritesUseCase(any()) }
        coVerify(exactly = 0) { removeFromFavoritesUseCase(any()) }
    }
    @Test
    fun `toggleFavorite should remove from favorites when favorite`() = runTest {
        val movie = createMovie(1, "Movie 1")
        val movieViewObject = createMovieViewObject(1, "Movie 1", isFavorite = true)
        val movies = listOf(movie)
        coEvery { getPopularMoviesUseCase(1) } returns Result.success(movies)
        coEvery { isFavoriteUseCase(1) } returns Result.success(true)
        coEvery { removeFromFavoritesUseCase(1) } returns Result.success(Unit)
        every { getFavoriteMoviesUseCase() } returns flowOf(emptyList())
        viewModel.loadPopularMovies()
        viewModel.uiState.test {
            skipItems(1)
            awaitItem()
        }
        viewModel.toggleFavorite(movieViewObject)
        coVerify(exactly = 1) { removeFromFavoritesUseCase(1) }
        coVerify(exactly = 0) { addMovieDetailToFavoritesUseCase(any()) }
    }
    @Test
    fun `retry should reload popular movies`() = runTest {
        val movies = listOf(createMovie(1, "Movie 1"))
        coEvery { getPopularMoviesUseCase(1) } returns Result.success(movies)
        every { getFavoriteMoviesUseCase() } returns flowOf(emptyList())
        viewModel.retry()
        viewModel.uiState.test {
            skipItems(1)
            val successState = awaitItem() as MovieListUiState.Success
            assertEquals(1, successState.movies.size)
        }
        coVerify(exactly = 1) { getPopularMoviesUseCase(1) }
    }
    @Test
    fun `addToFavorites should fetch details and add to favorites`() = runTest {
        val movie = createMovie(1, "Movie 1")
        val movieDetail = createMovieDetail(1, "Movie 1")
        val movies = listOf(movie)
        coEvery { getPopularMoviesUseCase(1) } returns Result.success(movies)
        coEvery { getMovieDetailsUseCase(1) } returns Result.success(movieDetail)
        coEvery { addMovieDetailToFavoritesUseCase(any()) } returns Result.success(Unit)
        every { getFavoriteMoviesUseCase() } returns flowOf(emptyList())
        viewModel.loadPopularMovies()
        viewModel.uiState.test {
            skipItems(1)
            awaitItem()
        }
        viewModel.addToFavorites(1)
        coVerify(exactly = 1) { getMovieDetailsUseCase(1) }
        coVerify(exactly = 1) { addMovieDetailToFavoritesUseCase(any()) }
    }
    @Test
    fun `addToFavorites should fallback to basic movie when details fail`() = runTest {
        val movie = createMovie(1, "Movie 1")
        val movies = listOf(movie)
        coEvery { getPopularMoviesUseCase(1) } returns Result.success(movies)
        coEvery { getMovieDetailsUseCase(1) } returns Result.failure(Exception("Network error"))
        coEvery { addToFavoritesUseCase(any()) } returns Result.success(Unit)
        every { getFavoriteMoviesUseCase() } returns flowOf(emptyList())
        viewModel.loadPopularMovies()
        viewModel.uiState.test {
            skipItems(1)
            awaitItem()
        }
        viewModel.addToFavorites(1)
        coVerify(exactly = 1) { getMovieDetailsUseCase(1) }
        coVerify(exactly = 1) { addToFavoritesUseCase(any()) }
    }
    private fun createMovie(id: Int, title: String): Movie {
        return Movie(
            id = id,
            title = title,
            overview = "Overview",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            releaseDate = "2024-01-01",
            voteAverage = 8.5,
            voteCount = 100,
            popularity = 100.0
        )
    }
    private fun createMovieViewObject(id: Int, title: String, isFavorite: Boolean = false): MovieViewObject {
        return MovieViewObject(
            id = id,
            title = title,
            overview = "Overview",
            posterUrl = "https:
            backdropUrl = "https:
            releaseDate = "2024-01-01",
            formattedReleaseDate = "01/01/2024",
            rating = 8.5,
            formattedRating = "8.5",
            voteCount = 100,
            popularity = 100.0,
            isFavorite = isFavorite
        )
    }
    private fun createMovieDetail(id: Int, title: String): com.onboarding.mychallenge.domain.model.MovieDetail {
        return com.onboarding.mychallenge.domain.model.MovieDetail(
            id = id,
            title = title,
            overview = "Overview",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            releaseDate = "2024-01-01",
            voteAverage = 8.5,
            voteCount = 100,
            popularity = 100.0,
            runtime = 120,
            genres = emptyList(),
            tagline = null,
            budget = 50000000L,
            revenue = 200000000L,
            status = "Released",
            homepage = null
        )
    }
}
