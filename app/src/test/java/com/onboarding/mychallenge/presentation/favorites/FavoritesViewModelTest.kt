package com.onboarding.mychallenge.presentation.favorites

import app.cash.turbine.test
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FavoritesViewModelTest {

    private lateinit var repository: MovieRepository
    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setup() {
        repository = mockk()
        viewModel = FavoritesViewModel(repository)
    }

    @Test
    fun `loadFavorites should emit Loading then Success when favorites exist`() = runTest {
        // Given
        val favorites = listOf(
            createMovie(1, "Movie 1"),
            createMovie(2, "Movie 2")
        )
        every { repository.getFavoriteMovies() } returns flowOf(favorites)

        // When
        viewModel.loadFavorites()

        // Then
        viewModel.uiState.test {
            val loadingState = awaitItem()
            assertTrue(loadingState is FavoritesUiState.Loading)
            
            val successState = awaitItem() as FavoritesUiState.Success
            assertEquals(2, successState.movies.size)
            assertEquals("Movie 1", successState.movies[0].title)
            assertEquals("Movie 2", successState.movies[1].title)
        }
    }

    @Test
    fun `loadFavorites should emit Empty when no favorites exist`() = runTest {
        // Given
        every { repository.getFavoriteMovies() } returns flowOf(emptyList())

        // When
        viewModel.loadFavorites()

        // Then
        viewModel.uiState.test {
            val loadingState = awaitItem()
            assertTrue(loadingState is FavoritesUiState.Loading)
            
            val emptyState = awaitItem()
            assertTrue(emptyState is FavoritesUiState.Empty)
        }
    }

    @Test
    fun `updateSearchQuery should filter favorites`() = runTest {
        // Given
        val favorites = listOf(
            createMovie(1, "Action Movie"),
            createMovie(2, "Comedy Movie")
        )
        every { repository.getFavoriteMovies() } returns flowOf(favorites)

        // When
        viewModel.loadFavorites()
        viewModel.updateSearchQuery("Action")
        
        // Wait for debounce (500ms)
        delay(600)

        // Then
        viewModel.uiState.test {
            skipItems(1) // Skip Loading
            val successState = awaitItem() as FavoritesUiState.Success
            assertEquals(1, successState.movies.size)
            assertEquals("Action Movie", successState.movies[0].title)
        }
    }

    @Test
    fun `removeFromFavorites should remove movie`() = runTest {
        // Given
        val favorites = listOf(createMovie(1, "Movie 1"))
        every { repository.getFavoriteMovies() } returns flowOf(favorites)
        coEvery { repository.removeFromFavorites(1) } returns Unit

        // When
        viewModel.removeFromFavorites(1)

        // Then
        coVerify(exactly = 1) { repository.removeFromFavorites(1) }
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
}

