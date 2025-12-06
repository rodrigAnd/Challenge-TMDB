package com.onboarding.mychallenge.domain.usecase
import com.onboarding.mychallenge.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RemoveFromFavoritesUseCaseTest {
    private lateinit var repository: MovieRepository
    private lateinit var useCase: RemoveFromFavoritesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = RemoveFromFavoritesUseCase(repository)
    }

    @Test
    fun `invoke should return success when movie is removed`() =
        runTest {
            val movieId = 1
            coEvery { repository.removeFromFavorites(movieId) } returns Unit
            val result = useCase(movieId)
            assertTrue(result.isSuccess)
            coVerify(exactly = 1) { repository.removeFromFavorites(movieId) }
        }

    @Test
    fun `invoke should return failure when movieId is invalid`() =
        runTest {
            val invalidId = 0
            val result = useCase(invalidId)
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            coVerify(exactly = 0) { repository.removeFromFavorites(any()) }
        }

    @Test
    fun `invoke should return failure when repository throws exception`() =
        runTest {
            val movieId = 1
            val error = android.database.sqlite.SQLiteException("Database error")
            coEvery { repository.removeFromFavorites(movieId) } throws error
            val result = useCase(movieId)
            assertTrue(result.isFailure)
            assertEquals(error, result.exceptionOrNull())
            coVerify(exactly = 1) { repository.removeFromFavorites(movieId) }
        }

    @Test
    fun `invoke should return failure when movieId is negative`() =
        runTest {
            val negativeId = -1
            val result = useCase(negativeId)
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            coVerify(exactly = 0) { repository.removeFromFavorites(any()) }
        }
}
