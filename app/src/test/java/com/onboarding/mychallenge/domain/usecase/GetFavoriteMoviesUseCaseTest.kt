package com.onboarding.mychallenge.domain.usecase
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetFavoriteMoviesUseCaseTest {
    private lateinit var repository: MovieRepository
    private lateinit var useCase: GetFavoriteMoviesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetFavoriteMoviesUseCase(repository)
    }

    @Test
    fun `invoke should return flow of favorite movies`() =
        runTest {
            val favorites =
                listOf(
                    createMockMovie(1, "Movie 1"),
                    createMockMovie(2, "Movie 2"),
                )
            every { repository.getFavoriteMovies() } returns flowOf(favorites)
            val flow = useCase()
            flow.collect { movies ->
                assertEquals(2, movies.size)
                assertEquals("Movie 1", movies[0].title)
                assertEquals("Movie 2", movies[1].title)
            }
        }

    @Test
    fun `invoke should return empty flow when no favorites`() =
        runTest {
            every { repository.getFavoriteMovies() } returns flowOf(emptyList())
            val flow = useCase()
            flow.collect { movies ->
                assertTrue(movies.isEmpty())
            }
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
}

