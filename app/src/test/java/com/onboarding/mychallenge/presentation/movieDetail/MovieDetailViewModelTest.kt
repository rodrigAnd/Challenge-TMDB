package com.onboarding.mychallenge.presentation.movieDetail

import app.cash.turbine.test
import com.onboarding.mychallenge.domain.model.Genre
import com.onboarding.mychallenge.domain.model.MovieDetail
import com.onboarding.mychallenge.domain.usecase.GetMovieDetailsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MovieDetailViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase

    private lateinit var viewModel: MovieDetailViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getMovieDetailsUseCase = mockk()

        viewModel = MovieDetailViewModel(getMovieDetailsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading`() =
        runTest {
            // Assert
            assertEquals(MovieDetailUiState.Loading, viewModel.uiState.value)
        }

    @Test
    fun `loadMovieDetails should emit Success state when use case returns success`() =
        runTest {
            // Arrange
            val movieId = 123
            val mockMovieDetail = createMockMovieDetail(movieId, "Filme de Teste")

            coEvery { getMovieDetailsUseCase(movieId) } returns Result.success(mockMovieDetail)

            // Act & Assert
            viewModel.uiState.test {
                assertEquals(MovieDetailUiState.Loading, awaitItem())

                viewModel.loadMovieDetails(movieId)

                val successState = awaitItem()
                assertTrue("O estado deveria ser Success", successState is MovieDetailUiState.Success)
                assertEquals(mockMovieDetail, (successState as MovieDetailUiState.Success).movieDetail)

                ensureAllEventsConsumed()
            }

            coVerify(exactly = 1) { getMovieDetailsUseCase(movieId) }
        }

    @Test
    fun `loadMovieDetails should emit Error state when use case returns failure`() =
        runTest {
            // Arrange
            val movieId = 456
            val errorMessage = "Erro de rede simulado"

            coEvery { getMovieDetailsUseCase(movieId) } returns Result.failure(Exception(errorMessage))

            // Act & Assert
            viewModel.uiState.test {
                assertEquals(MovieDetailUiState.Loading, awaitItem())

                viewModel.loadMovieDetails(movieId)

                val errorState = awaitItem()
                assertTrue("O estado deveria ser Error", errorState is MovieDetailUiState.Error)
                assertEquals(errorMessage, (errorState as MovieDetailUiState.Error).message)

                ensureAllEventsConsumed()
            }

            // Verifica se o use case foi chamado
            coVerify(exactly = 1) { getMovieDetailsUseCase(movieId) }
        }

    @Test
    fun `loadMovieDetails should emit Error state with default message on null exception message`() =
        runTest {
            // Arrange
            val movieId = 789

            coEvery { getMovieDetailsUseCase(movieId) } returns Result.failure(Exception(null as String?))

            // Act & Assert
            viewModel.uiState.test {
                assertEquals(MovieDetailUiState.Loading, awaitItem())

                viewModel.loadMovieDetails(movieId)

                val errorState = awaitItem()
                assertTrue(errorState is MovieDetailUiState.Error)
                assertEquals("Erro ao carregar detalhes do filme", (errorState as MovieDetailUiState.Error).message)

                ensureAllEventsConsumed()
            }

            coVerify(exactly = 1) { getMovieDetailsUseCase(movieId) }
        }

    @Test
    fun `resetState should emit Loading state`() =
        runTest {
            val mockMovieDetail = createMockMovieDetail(1, "Filme Qualquer")
            coEvery { getMovieDetailsUseCase(1) } returns Result.success(mockMovieDetail)
            viewModel.loadMovieDetails(1)

            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.uiState.test {
                val initialState = awaitItem()
                assertTrue("O estado inicial deveria ser Success", initialState is MovieDetailUiState.Success)

                viewModel.resetState()

                assertEquals(MovieDetailUiState.Loading, awaitItem())

                ensureAllEventsConsumed()
            }
        }
}

private fun createMockMovieDetail(
    id: Int,
    title: String,
): MovieDetail {
    return MovieDetail(
        id = id,
        title = title,
        overview = "Esta é a sinopse de um filme de teste.",
        posterPath = "/poster.jpg",
        backdropPath = "/backdrop.jpg",
        releaseDate = "2024-01-01",
        voteAverage = 8.5,
        voteCount = 1000,
        popularity = 120.0,
        runtime = 150,
        genres = listOf(Genre(1, "Ação")),
        tagline = "Uma frase de efeito para o teste.",
        budget = 100000000L,
        revenue = 500000000L,
        status = "Released",
        homepage = "http://example.com",
    )
}
