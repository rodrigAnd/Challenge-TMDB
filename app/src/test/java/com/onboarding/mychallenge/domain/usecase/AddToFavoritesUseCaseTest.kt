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
class AddToFavoritesUseCaseTest {
    private lateinit var repository: MovieRepository
    private lateinit var useCase: AddToFavoritesUseCase
    @Before
    fun setup() {
        repository = mockk()
        useCase = AddToFavoritesUseCase(repository)
    }
    @Test
    fun `invoke should return success when movie is added`() = runTest {
        val movie = createMockMovie(1, "Movie 1")
        coEvery { repository.addToFavorites(movie) } returns Unit
        val result = useCase(movie)
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.addToFavorites(movie) }
    }
    @Test
    fun `invoke should return failure when repository throws exception`() = runTest {
        val movie = createMockMovie(1, "Movie 1")
        val error = Exception("Database error")
        coEvery { repository.addToFavorites(movie) } throws error
        val result = useCase(movie)
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
        coVerify(exactly = 1) { repository.addToFavorites(movie) }
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
