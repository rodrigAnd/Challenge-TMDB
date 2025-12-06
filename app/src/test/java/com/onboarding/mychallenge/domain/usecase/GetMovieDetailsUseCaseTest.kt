package com.onboarding.mychallenge.domain.usecase
import com.onboarding.mychallenge.domain.model.Genre
import com.onboarding.mychallenge.domain.model.MovieDetail
import com.onboarding.mychallenge.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetMovieDetailsUseCaseTest {
    private lateinit var repository: MovieRepository
    private lateinit var useCase: GetMovieDetailsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetMovieDetailsUseCase(repository)
    }

    @Test
    fun `invoke should return success when movie details are found`() =
        runTest {
            val movieDetail = createMockMovieDetail(1, "Movie 1")
            coEvery { repository.getMovieDetails(1) } returns Result.success(movieDetail)
            val result = useCase(1)
            assertTrue(result.isSuccess)
            assertEquals("Movie 1", result.getOrNull()?.title)
            assertEquals(1, result.getOrNull()?.id)
            coVerify(exactly = 1) { repository.getMovieDetails(1) }
        }

    @Test
    fun `invoke should return failure when repository fails`() =
        runTest {
            val errorMessage = "Network error"
            coEvery { repository.getMovieDetails(1) } returns Result.failure(Exception(errorMessage))
            val result = useCase(1)
            assertTrue(result.isFailure)
            assertEquals(errorMessage, result.exceptionOrNull()?.message)
            coVerify(exactly = 1) { repository.getMovieDetails(1) }
        }

    @Test
    fun `invoke should return failure when movieId is invalid`() =
        runTest {
            val invalidId = 0
            val result = useCase(invalidId)
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            assertEquals(
                "Movie ID must be greater than 0",
                result.exceptionOrNull()?.message,
            )
            coVerify(exactly = 0) { repository.getMovieDetails(any()) }
        }

    @Test
    fun `invoke should return failure when movieId is negative`() =
        runTest {
            val negativeId = -1
            val result = useCase(negativeId)
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            coVerify(exactly = 0) { repository.getMovieDetails(any()) }
        }

    private fun createMockMovieDetail(
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

