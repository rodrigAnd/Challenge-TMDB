package com.onboarding.mychallenge.presentation.favorites

import app.cash.turbine.test
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertIs

@ExperimentalCoroutinesApi
class FavoritesViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var movieRepository: MovieRepository

    private lateinit var viewModel: FavoritesViewModel

    private lateinit var favoritesFlow: MutableSharedFlow<List<Movie>>

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        movieRepository = mockk(relaxUnitFun = true)
        favoritesFlow = MutableSharedFlow(replay = 1)
        every { movieRepository.getFavoriteMovies() } returns favoritesFlow
        coEvery { movieRepository.removeFromFavorites(any()) } returns Unit

        viewModel = FavoritesViewModel(movieRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should emit Loading and then Success state when favorites are loaded`() =
        runTest {
            // Arrange
            val movies = listOf(createMockMovie(1, "Inception"))
            favoritesFlow.emit(movies) // Simula o banco de dados emitindo a lista

            // Assert
            viewModel.uiState.test {
                // O primeiro estado é Loading
                assertEquals(FavoritesUiState.Loading, awaitItem())

                val successState = awaitItem()
                assertTrue(successState is FavoritesUiState.Success)
                assertEquals(1, (successState as FavoritesUiState.Success).movies.size)
                assertEquals("Inception", successState.movies[0].title)
            }
        }

    @Test
    fun `init should emit Empty state when favorites list is empty`() =
        runTest {
            // Arrange
            favoritesFlow.emit(emptyList()) // Simula o banco de dados emitindo uma lista vazia

            // Assert
            viewModel.uiState.test {
                assertEquals(FavoritesUiState.Loading, awaitItem())
                assertEquals(FavoritesUiState.Empty, awaitItem())
            }
        }

    @Test
    fun `updateSearchQuery should filter favorites list after debounce`() =
        runTest {
            // Arrange
            val movies =
                listOf(
                    createMockMovie(1, "Inception"),
                    createMockMovie(2, "Interstellar"),
                )
            favoritesFlow.emit(movies)

            // Act & Assert
            viewModel.uiState.test {
                assertEquals(FavoritesUiState.Loading, awaitItem())
                assertEquals(2, (awaitItem() as FavoritesUiState.Success).movies.size)

                viewModel.updateSearchQuery("Incep")
                advanceTimeBy(501)

                val filteredState = awaitItem()
                assertTrue(filteredState is FavoritesUiState.Success)
                assertEquals(1, (filteredState as FavoritesUiState.Success).movies.size)
                assertEquals("Inception", filteredState.movies[0].title)
            }
        }

    @Test
    fun `updateSearchQuery with blank query should show all favorites`() =
        runTest {
            // Arrange
            val movies =
                listOf(
                    createMockMovie(1, "Inception"),
                    createMockMovie(2, "Interstellar"),
                )
            favoritesFlow.emit(movies)

            // Act & Assert
            viewModel.uiState.test {
                awaitItem() // Loading
                awaitItem() // Success com 2 filmes

                viewModel.updateSearchQuery("Incep")
                advanceTimeBy(501)
                awaitItem() // Success com 1 filme (filtrado)

                viewModel.updateSearchQuery("")
                advanceTimeBy(501)

                val fullListState = awaitItem()
                assertTrue(fullListState is FavoritesUiState.Success)
                assertEquals(2, (fullListState as FavoritesUiState.Success).movies.size)
            }
        }

    private fun createMockMovie(
        id: Int,
        title: String,
    ): Movie {
        return Movie(
            id = id,
            title = title,
            overview = "Overview of $title",
            posterPath = "/path.jpg",
            backdropPath = "/backdrop.jpg",
            releaseDate = "2024-01-01",
            voteAverage = 8.0,
            voteCount = 100,
            popularity = 100.0,
        )
    }

    @Test
    fun `init should emit Loading and then Success when favorites are loaded`() =
        runTest {
            // Arrange
            val movies = listOf(createMockMovie(1, "Inception"))

            viewModel.uiState.test {
                assertEquals(FavoritesUiState.Loading, awaitItem())

                favoritesFlow.emit(movies)
                advanceUntilIdle() // Garante que a coleta e o processamento aconteçam

                val successState = awaitItem()
                assertIs<FavoritesUiState.Success>(successState)
                assertEquals(1, successState.movies.size)
                assertEquals("Inception", successState.movies[0].title)
            }
        }

    @Test
    fun `init should emit Error when repository throws exception`() =
        runTest {
            // Arrange
            val error = RuntimeException("Database error")
            every {
                movieRepository.getFavoriteMovies()
            } returns kotlinx.coroutines.flow.flow { throw error }
            // Assert
            viewModel.uiState.test {
                assertEquals(FavoritesUiState.Loading, awaitItem())

                val errorState = awaitItem()
                assertIs<FavoritesUiState.Error>(errorState)
                assertEquals("Database error", errorState.message)
            }
        }

    @Test
    fun `search should return Empty when no movies match query`() =
        runTest {
            val movies = listOf(createMockMovie(1, "Inception"))

            viewModel.uiState.test {
                assertEquals(FavoritesUiState.Loading, awaitItem())
                favoritesFlow.emit(movies)
                advanceUntilIdle()
                awaitItem()

                viewModel.updateSearchQuery("Matrix")
                advanceTimeBy(501)
                advanceUntilIdle()

                assertEquals(FavoritesUiState.Empty, awaitItem())
            }
        }
}
