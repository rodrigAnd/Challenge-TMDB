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
    fun `invoke should return success when movie is removed`() = runTest {
        // Given
        val movieId = 1
        coEvery { repository.removeFromFavorites(movieId) } returns Unit

        // When
        val result = useCase(movieId)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.removeFromFavorites(movieId) }
    }

    @Test
    fun `invoke should return failure when movieId is invalid`() = runTest {
        // Given
        val invalidId = 0

        // When
        val result = useCase(invalidId)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { repository.removeFromFavorites(any()) }
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runTest {
        // Given
        val movieId = 1
        val error = Exception("Database error")
        coEvery { repository.removeFromFavorites(movieId) } throws error

        // When
        val result = useCase(movieId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
        coVerify(exactly = 1) { repository.removeFromFavorites(movieId) }
    }

    @Test
    fun `invoke should return failure when movieId is negative`() = runTest {
        // Given
        val negativeId = -1

        // When
        val result = useCase(negativeId)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { repository.removeFromFavorites(any()) }
    }
}

