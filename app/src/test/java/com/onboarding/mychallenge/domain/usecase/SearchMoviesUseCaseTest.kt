package com.onboarding.mychallenge.domain.usecase

import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchMoviesUseCaseTest {

    private lateinit var repository: MovieRepository
    private lateinit var useCase: SearchMoviesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = SearchMoviesUseCase(repository)
    }

    @Test
    fun `invoke should return success when repository returns movies`() = runTest {
        // Given
        val query = "batman"
        val page = 1
        val movies = listOf(createMockMovie(1, "Batman Begins"))
        coEvery { repository.searchMovies(query.trim(), page) } returns Result.success(movies)

        // When
        val result = useCase(query, page)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(movies, result.getOrNull())
        coVerify(exactly = 1) { repository.searchMovies(query.trim(), page) }
    }

    @Test
    fun `invoke should return failure when query is blank`() = runTest {
        // Given
        val blankQuery = "   "

        // When
        val result = useCase(blankQuery)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { repository.searchMovies(any(), any()) }
    }

    @Test
    fun `invoke should return failure when page is less than 1`() = runTest {
        // Given
        val query = "batman"
        val invalidPage = 0

        // When
        val result = useCase(query, invalidPage)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { repository.searchMovies(any(), any()) }
    }

    @Test
    fun `invoke should trim query before searching`() = runTest {
        // Given
        val queryWithSpaces = "  batman  "
        val movies = listOf(createMockMovie(1, "Batman"))
        coEvery { repository.searchMovies("batman", 1) } returns Result.success(movies)

        // When
        val result = useCase(queryWithSpaces)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.searchMovies("batman", 1) }
    }

    @Test
    fun `invoke should return failure when repository returns error`() = runTest {
        // Given
        val query = "batman"
        val error = Exception("Network error")
        coEvery { repository.searchMovies(query, 1) } returns Result.failure(error)

        // When
        val result = useCase(query)

        // Then
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    private fun createMockMovie(id: Int, title: String): Movie {
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

