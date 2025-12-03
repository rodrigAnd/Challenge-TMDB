package com.onboarding.mychallenge.presentation.movieDetail
import app.cash.turbine.test
import com.onboarding.mychallenge.domain.model.Genre
import com.onboarding.mychallenge.domain.model.MovieDetail
import com.onboarding.mychallenge.domain.usecase.GetMovieDetailsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MovieDetailViewModelTest {
    private lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase
    private lateinit var viewModel: MovieDetailViewModel

    @Before
    fun setup() {
        getMovieDetailsUseCase = mockk()
        viewModel = MovieDetailViewModel(getMovieDetailsUseCase)
    }

    @Test
    fun `loadMovieDetails should emit Loading then Success when details are loaded`() =
        runTest {
            val movieDetail = createMovieDetail(1, "Movie 1")
            coEvery { getMovieDetailsUseCase(1) } returns Result.success(movieDetail)
            viewModel.loadMovieDetails(1)
            viewModel.uiState.test {
                val loadingState = awaitItem()
                assertTrue(loadingState is MovieDetailUiState.Loading)
                val successState = awaitItem() as MovieDetailUiState.Success
                assertEquals("Movie 1", successState.movieDetail.title)
                assertEquals(1, successState.movieDetail.id)
            }
        }

    @Test
    fun `loadMovieDetails should emit Error when useCase fails`() =
        runTest {
            val errorMessage = "Network error"
            coEvery { getMovieDetailsUseCase(1) } returns Result.failure(Exception(errorMessage))
            viewModel.loadMovieDetails(1)
            viewModel.uiState.test {
                val loadingState = awaitItem()
                assertTrue(loadingState is MovieDetailUiState.Loading)
                val errorState = awaitItem() as MovieDetailUiState.Error
                assertEquals(errorMessage, errorState.message)
            }
        }

    @Test
    fun `resetState should reset to Loading`() =
        runTest {
            val movieDetail = createMovieDetail(1, "Movie 1")
            coEvery { getMovieDetailsUseCase(1) } returns Result.success(movieDetail)
            viewModel.loadMovieDetails(1)
            viewModel.uiState.test {
                skipItems(1)
                awaitItem()
            }
            viewModel.resetState()
            viewModel.uiState.test {
                val loadingState = awaitItem()
                assertTrue(loadingState is MovieDetailUiState.Loading)
            }
        }

    private fun createMovieDetail(
        id: Int,
        title: String,
    ): MovieDetail {
        return MovieDetail(
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
            genres = listOf(Genre(1, "Action")),
            tagline = "Tagline",
            budget = 50000000L,
            revenue = 200000000L,
            status = "Released",
            homepage = "https://example.com",
        )
    }
}
