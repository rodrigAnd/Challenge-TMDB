package com.onboarding.mychallenge.data.repository
import com.onboarding.mychallenge.data.local.dao.FavoriteMovieDao
import com.onboarding.mychallenge.data.local.entity.FavoriteMovieEntity
import com.onboarding.mychallenge.data.remote.api.TmdbApiService
import com.onboarding.mychallenge.data.remote.dto.MovieDetailDto
import com.onboarding.mychallenge.data.remote.dto.MovieDto
import com.onboarding.mychallenge.data.remote.dto.MoviesResponseDto
import com.onboarding.mychallenge.domain.model.Genre
import com.onboarding.mychallenge.domain.model.MovieDetail
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
    fun `getPopularMovies should return success when API returns movies`() =
        runTest {
            val movieDto = createMovieDto(1, "Movie 1")
            val response =
                MoviesResponseDto(
                    page = 1,
                    results = listOf(movieDto),
                    totalPages = 10,
                    totalResults = 100,
                )
            coEvery { apiService.getPopularMovies(1) } returns response
            val result = repository.getPopularMovies(1)
            assertTrue(result.isSuccess)
            assertEquals(1, result.getOrNull()?.size)
            assertEquals("Movie 1", result.getOrNull()?.get(0)?.title)
        }

    @Test
    fun `getPopularMovies should return failure when API throws exception`() =
        runTest {
            val error = Exception("Network error")
            coEvery { apiService.getPopularMovies(1) } throws error
            val result = repository.getPopularMovies(1)
            assertTrue(result.isFailure)
        }

    @Test
    fun `addMovieDetailToFavorites should save to database`() =
        runTest {
            val movieDetail = createMovieDetail(1, "Movie 1")
            coEvery { favoriteDao.insertFavorite(any()) } returns Unit
            repository.addMovieDetailToFavorites(movieDetail)
            coVerify(exactly = 1) { favoriteDao.insertFavorite(any()) }
        }

    @Test
    fun `removeFromFavorites should delete from database`() =
        runTest {
            coEvery { favoriteDao.deleteFavorite(1) } returns Unit
            repository.removeFromFavorites(1)
            coVerify(exactly = 1) { favoriteDao.deleteFavorite(1) }
        }

    @Test
    fun `isFavorite should return true when movie is in database`() =
        runTest {
            coEvery { favoriteDao.isFavorite(1) } returns true
            val result = repository.isFavorite(1)
            assertTrue(result)
            coVerify(exactly = 1) { favoriteDao.isFavorite(1) }
        }

    @Test
    fun `getFavoriteMovies should return flow of movies`() =
        runTest {
            val entity = createFavoriteEntity(1, "Movie 1")
            every { favoriteDao.getAllFavorites() } returns flowOf(listOf(entity))
            val flow = repository.getFavoriteMovies()
            flow.collect { movies ->
                assertEquals(1, movies.size)
                assertEquals("Movie 1", movies[0].title)
            }
        }

    @Test
    fun `getMovieDetails should return success when API returns details`() =
        runTest {
            val detailDto = createMovieDetailDto(1, "Movie 1")
            coEvery { apiService.getMovieDetails(1, any()) } returns detailDto
            val result = repository.getMovieDetails(1)
            assertTrue(result.isSuccess)
            assertEquals("Movie 1", result.getOrNull()?.title)
        }

    @Test
    fun `getFavoriteMovieDetails should return success when found in database`() =
        runTest {
            val entity = createFavoriteEntity(1, "Movie 1")
            coEvery { favoriteDao.getFavoriteById(1) } returns entity
            val result = repository.getFavoriteMovieDetails(1)
            assertTrue(result.isSuccess)
            assertEquals("Movie 1", result.getOrNull()?.title)
        }

    @Test
    fun `getFavoriteMovieDetails should return failure when not found`() =
        runTest {
            coEvery { favoriteDao.getFavoriteById(1) } returns null
            val result = repository.getFavoriteMovieDetails(1)
            assertTrue(result.isFailure)
        }

    private fun createMovieDto(
        id: Int,
        title: String,
    ): MovieDto {
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
            voteCount = 100,
        )
    }

    private fun createMovieDetail(
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

    private fun createFavoriteEntity(
        id: Int,
        title: String,
    ): FavoriteMovieEntity {
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
            homepage = "https://example.com",
        )
    }

    private fun createMovieDetailDto(
        id: Int,
        title: String,
    ): MovieDetailDto {
        return MovieDetailDto(
            adult = false,
            backdropPath = "/backdrop.jpg",
            budget = 50000000L,
            genres = emptyList(),
            homepage = "https://example.com",
            id = id,
            imdbId = null,
            originalLanguage = "en",
            originalTitle = title,
            overview = "Overview",
            popularity = 100.0,
            posterPath = "/poster.jpg",
            productionCompanies = emptyList(),
            productionCountries = emptyList(),
            releaseDate = "2024-01-01",
            revenue = 200000000L,
            runtime = 120,
            spokenLanguages = emptyList(),
            status = "Released",
            tagline = "Tagline",
            title = title,
            video = false,
            voteAverage = 8.5,
            voteCount = 100,
        )
    }
}
