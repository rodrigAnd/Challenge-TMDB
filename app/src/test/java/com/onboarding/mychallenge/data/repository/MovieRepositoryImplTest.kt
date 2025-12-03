package com.onboarding.mychallenge.data.repository

import com.onboarding.mychallenge.data.local.dao.FavoriteMovieDao
import com.onboarding.mychallenge.data.local.entity.FavoriteMovieEntity
import com.onboarding.mychallenge.data.mapper.toDomain
import com.onboarding.mychallenge.data.mapper.toEntity
import com.onboarding.mychallenge.data.mapper.toMovieDetail
import com.onboarding.mychallenge.data.remote.api.TmdbApiService
import com.onboarding.mychallenge.data.remote.dto.MovieDetailDto
import com.onboarding.mychallenge.data.remote.dto.MovieDto
import com.onboarding.mychallenge.data.remote.dto.MoviesResponseDto
import com.onboarding.mychallenge.domain.model.Genre
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.model.MovieDetail
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MovieRepositoryImplTest {

    private lateinit var apiService: TmdbApiService
    private lateinit var favoriteDao: FavoriteMovieDao
    private lateinit var repository: MovieRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        favoriteDao = mockk()
        repository = MovieRepositoryImpl(apiService, favoriteDao)
    }

    @Test
    fun `getPopularMovies should return success when API returns movies`() = runTest {
        // Given
        val movieDto = createMovieDto(1, "Movie 1")
        val response = MoviesResponseDto(
            page = 1,
            results = listOf(movieDto),
            totalPages = 10,
            totalResults = 100
        )
        coEvery { apiService.getPopularMovies(1, "pt-BR") } returns response

        // When
        val result = repository.getPopularMovies(1)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Movie 1", result.getOrNull()?.get(0)?.title)
    }

    @Test
    fun `getPopularMovies should return failure when API throws exception`() = runTest {
        // Given
        val error = Exception("Network error")
        coEvery { apiService.getPopularMovies(1, "pt-BR") } throws error

        // When
        val result = repository.getPopularMovies(1)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `addMovieDetailToFavorites should save to database`() = runTest {
        // Given
        val movieDetail = createMovieDetail(1, "Movie 1")
        coEvery { favoriteDao.insertFavorite(any()) } returns Unit

        // When
        repository.addMovieDetailToFavorites(movieDetail)

        // Then
        coVerify(exactly = 1) { favoriteDao.insertFavorite(any()) }
    }

    @Test
    fun `removeFromFavorites should delete from database`() = runTest {
        // Given
        coEvery { favoriteDao.deleteFavorite(1) } returns Unit

        // When
        repository.removeFromFavorites(1)

        // Then
        coVerify(exactly = 1) { favoriteDao.deleteFavorite(1) }
    }

    @Test
    fun `isFavorite should return true when movie is in database`() = runTest {
        // Given
        coEvery { favoriteDao.isFavorite(1) } returns true

        // When
        val result = repository.isFavorite(1)

        // Then
        assertTrue(result)
        coVerify(exactly = 1) { favoriteDao.isFavorite(1) }
    }

    @Test
    fun `getFavoriteMovies should return flow of movies`() = runTest {
        // Given
        val entity = createFavoriteEntity(1, "Movie 1")
        every { favoriteDao.getAllFavorites() } returns flowOf(listOf(entity))

        // When
        val flow = repository.getFavoriteMovies()

        // Then
        flow.collect { movies ->
            assertEquals(1, movies.size)
            assertEquals("Movie 1", movies[0].title)
        }
    }

    @Test
    fun `getMovieDetails should return success when API returns details`() = runTest {
        // Given
        val detailDto = createMovieDetailDto(1, "Movie 1")
        coEvery { apiService.getMovieDetails(1, "pt-BR") } returns detailDto

        // When
        val result = repository.getMovieDetails(1)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Movie 1", result.getOrNull()?.title)
    }

    @Test
    fun `getFavoriteMovieDetails should return success when found in database`() = runTest {
        // Given
        val entity = createFavoriteEntity(1, "Movie 1")
        coEvery { favoriteDao.getFavoriteById(1) } returns entity

        // When
        val result = repository.getFavoriteMovieDetails(1)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Movie 1", result.getOrNull()?.title)
    }

    @Test
    fun `getFavoriteMovieDetails should return failure when not found`() = runTest {
        // Given
        coEvery { favoriteDao.getFavoriteById(1) } returns null

        // When
        val result = repository.getFavoriteMovieDetails(1)

        // Then
        assertTrue(result.isFailure)
    }

    private fun createMovieDto(id: Int, title: String): MovieDto {
        return MovieDto(
            adult = false,
            backdropPath = "/backdrop.jpg",
            genreIds = listOf(1, 2),
            id = id,
            originalLanguage = "en",
            originalTitle = title,
            overview = "Overview",
            popularity = 100.0,
            posterPath = "/poster.jpg",
            releaseDate = "2024-01-01",
            title = title,
            video = false,
            voteAverage = 8.5,
            voteCount = 100
        )
    }

    private fun createMovieDetail(id: Int, title: String): MovieDetail {
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
            homepage = "https://example.com"
        )
    }

    private fun createFavoriteEntity(id: Int, title: String): FavoriteMovieEntity {
        return FavoriteMovieEntity(
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
            genresJson = """[{"id":1,"name":"Action"}]""",
            tagline = "Tagline",
            budget = 50000000L,
            revenue = 200000000L,
            status = "Released",
            homepage = "https://example.com"
        )
    }

    private fun createMovieDetailDto(id: Int, title: String): MovieDetailDto {
        return MovieDetailDto(
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
            genres = emptyList(),
            tagline = "Tagline",
            budget = 50000000L,
            revenue = 200000000L,
            status = "Released",
            homepage = "https://example.com"
        )
    }
}
