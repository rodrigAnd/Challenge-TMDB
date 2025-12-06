package com.onboarding.mychallenge.domain.usecase
import com.onboarding.mychallenge.domain.model.Genre
import com.onboarding.mychallenge.domain.model.MovieDetail
import com.onboarding.mychallenge.domain.repository.MovieRepository
import com.onboarding.mychallenge.utils.createMockMovieDetail
import com.onboarding.mychallenge.utils.createMovieDetailDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
class AddMovieDetailToFavoritesUseCaseTest {
    private lateinit var repository: MovieRepository
    private lateinit var useCase: AddMovieDetailToFavoritesUseCase
    @Before
    fun setup() {
        repository = mockk()
        useCase = AddMovieDetailToFavoritesUseCase(repository)
    }
    @Test
    fun `invoke should return success when movieDetail is added`() = runTest {
        val movieDetail = createMockMovieDetail(1, "Movie 1")
        coEvery { repository.addMovieDetailToFavorites(movieDetail) } returns Unit
        val result = useCase(movieDetail)
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.addMovieDetailToFavorites(movieDetail) }
    }
    @Test
    fun `invoke should return failure when repository throws exception`() = runTest {
        val movieDetail = createMockMovieDetail(1, "Movie 1")
        val error = Exception("Database error")
        coEvery { repository.addMovieDetailToFavorites(movieDetail) } throws error
        val result = useCase(movieDetail)
        assertTrue(result.isFailure)
        coVerify(exactly = 1) { repository.addMovieDetailToFavorites(movieDetail) }
    }
}
