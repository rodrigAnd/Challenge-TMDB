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
class IsFavoriteUseCaseTest {
    private lateinit var repository: MovieRepository
    private lateinit var useCase: IsFavoriteUseCase
    @Before
    fun setup() {
        repository = mockk()
        useCase = IsFavoriteUseCase(repository)
    }
    @Test
    fun `invoke should return true when movie is favorite`() = runTest {
        val movieId = 1
        coEvery { repository.isFavorite(movieId) } returns true
        val result = useCase(movieId)
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
        coVerify(exactly = 1) { repository.isFavorite(movieId) }
    }
    @Test
    fun `invoke should return false when movie is not favorite`() = runTest {
        val movieId = 1
        coEvery { repository.isFavorite(movieId) } returns false
        val result = useCase(movieId)
        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrNull())
        coVerify(exactly = 1) { repository.isFavorite(movieId) }
    }
    @Test
    fun `invoke should return failure when movieId is invalid`() = runTest {
        val invalidId = 0
        val result = useCase(invalidId)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { repository.isFavorite(any()) }
    }
    @Test
    fun `invoke should return failure when repository throws exception`() = runTest {
        val movieId = 1
        val error = Exception("Database error")
        coEvery { repository.isFavorite(movieId) } throws error
        val result = useCase(movieId)
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
        coVerify(exactly = 1) { repository.isFavorite(movieId) }
    }
}
