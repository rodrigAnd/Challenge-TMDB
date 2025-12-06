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

class GetPopularMoviesUseCaseTest {
    private lateinit var repository: MovieRepository
    private lateinit var useCase: GetPopularMoviesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetPopularMoviesUseCase(repository)
    }

    @Test
    fun `invoke should return success when repository returns movies`() =
        runTest {
            val page = 1
            val movies =
                listOf(
                    createMockMovie(1, "Movie 1"),
                    createMockMovie(2, "Movie 2"),
                )
            coEvery { repository.getPopularMovies(page) } returns Result.success(movies)
            val result = useCase(page)
            assertTrue(result.isSuccess)
            assertEquals(movies, result.getOrNull())
            coVerify(exactly = 1) { repository.getPopularMovies(page) }
        }

    @Test
    fun `invoke should return failure when repository returns error`() =
        runTest {
            val page = 1
            val error = Exception("Network error")
            coEvery { repository.getPopularMovies(page) } returns Result.failure(error)
            val result = useCase(page)
            assertTrue(result.isFailure)
            assertEquals(error, result.exceptionOrNull())
            coVerify(exactly = 1) { repository.getPopularMovies(page) }
        }

    @Test
    fun `invoke should return failure when page is less than 1`() =
        runTest {
            val invalidPage = 0
            val result = useCase(invalidPage)
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            coVerify(exactly = 0) { repository.getPopularMovies(any()) }
        }

    @Test
    fun `invoke should use default page 1 when not specified`() =
        runTest {
            val movies = listOf(createMockMovie(1, "Movie 1"))
            coEvery { repository.getPopularMovies(1) } returns Result.success(movies)
            val result = useCase()
            assertTrue(result.isSuccess)
            coVerify(exactly = 1) { repository.getPopularMovies(1) }
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
