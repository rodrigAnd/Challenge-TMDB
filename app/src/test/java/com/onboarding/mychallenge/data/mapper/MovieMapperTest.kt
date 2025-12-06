package com.onboarding.mychallenge.data.mapper

import com.onboarding.mychallenge.data.local.entity.FavoriteMovieEntity
import com.onboarding.mychallenge.data.remote.dto.GenreDto
import com.onboarding.mychallenge.data.remote.dto.MovieDetailDto
import com.onboarding.mychallenge.data.remote.dto.MovieDto
import com.onboarding.mychallenge.domain.model.Genre
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.model.MovieDetail
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class MovieMapperTest {
    @Test
    fun `MovieDto toDomain should convert correctly`() {
        // Arrange
        val movieDto =
            MovieDto(
                adult = false,
                backdropPath = "/backdrop.jpg",
                genreIds = listOf(28, 12),
                id = 1,
                originalLanguage = "en",
                originalTitle = "Test Movie",
                overview = "Test overview",
                popularity = 100.0,
                posterPath = "/poster.jpg",
                releaseDate = "2024-01-01",
                title = "Test Movie",
                video = false,
                voteAverage = 8.5,
                voteCount = 1000,
            )

        // Act
        val result = movieDto.toDomain()

        // Assert
        assertEquals(1, result.id)
        assertEquals("Test Movie", result.title)
        assertEquals("Test overview", result.overview)
        assertEquals("/poster.jpg", result.posterPath)
        assertEquals("/backdrop.jpg", result.backdropPath)
        assertEquals("2024-01-01", result.releaseDate)
        assertEquals(8.5, result.voteAverage, 0.01)
        assertEquals(1000, result.voteCount)
        assertEquals(100.0, result.popularity, 0.01)
    }

    @Test
    fun `MovieDto toDomain should handle null paths`() {
        // Arrange
        val movieDto =
            MovieDto(
                adult = false,
                backdropPath = null,
                genreIds = emptyList(),
                id = 2,
                originalLanguage = "en",
                originalTitle = "Test",
                overview = "Overview",
                popularity = 50.0,
                posterPath = null,
                releaseDate = null,
                title = "Test",
                video = false,
                voteAverage = 7.0,
                voteCount = 500,
            )

        // Act
        val result = movieDto.toDomain()

        // Assert
        assertNull(result.posterPath)
        assertNull(result.backdropPath)
        assertNull(result.releaseDate)
    }

    @Test
    fun `FavoriteMovieEntity toDomain should convert correctly`() {
        // Arrange
        val entity =
            FavoriteMovieEntity(
                id = 1,
                title = "Test Movie",
                overview = "Overview",
                posterPath = "/poster.jpg",
                backdropPath = "/backdrop.jpg",
                releaseDate = "2024-01-01",
                voteAverage = 8.5,
                voteCount = 1000,
                popularity = 100.0,
            )

        // Act
        val result = entity.toDomain()

        // Assert
        assertEquals(1, result.id)
        assertEquals("Test Movie", result.title)
        assertEquals("Overview", result.overview)
        assertEquals("/poster.jpg", result.posterPath)
        assertEquals("/backdrop.jpg", result.backdropPath)
        assertEquals("2024-01-01", result.releaseDate)
        assertEquals(8.5, result.voteAverage, 0.01)
        assertEquals(1000, result.voteCount)
        assertEquals(100.0, result.popularity, 0.01)
    }

    @Test
    fun `FavoriteMovieEntity toMovieDetail should convert correctly with genres`() {
        // Arrange
        val genresJson = """[{"id":28,"name":"Action"},{"id":12,"name":"Adventure"}]"""
        val entity =
            FavoriteMovieEntity(
                id = 1,
                title = "Test Movie",
                overview = "Overview",
                posterPath = "/poster.jpg",
                backdropPath = "/backdrop.jpg",
                releaseDate = "2024-01-01",
                voteAverage = 8.5,
                voteCount = 1000,
                popularity = 100.0,
                runtime = 120,
                genresJson = genresJson,
                tagline = "Tagline",
                budget = 1000000L,
                revenue = 5000000L,
                status = "Released",
                homepage = "https://example.com",
            )

        // Act
        val result = entity.toMovieDetail()

        // Assert
        assertEquals(1, result.id)
        assertEquals("Test Movie", result.title)
        assertEquals(120, result.runtime)
        assertEquals(2, result.genres.size)
        assertEquals("Action", result.genres[0].name)
        assertEquals("Adventure", result.genres[1].name)
        assertEquals("Tagline", result.tagline)
        assertEquals(1000000L, result.budget)
        assertEquals(5000000L, result.revenue)
        assertEquals("Released", result.status)
        assertEquals("https://example.com", result.homepage)
    }

    @Test
    fun `FavoriteMovieEntity toMovieDetail should handle null genresJson`() {
        // Arrange
        val entity =
            FavoriteMovieEntity(
                id = 1,
                title = "Test Movie",
                overview = "Overview",
                posterPath = "/poster.jpg",
                backdropPath = "/backdrop.jpg",
                releaseDate = "2024-01-01",
                voteAverage = 8.5,
                voteCount = 1000,
                popularity = 100.0,
                genresJson = null,
            )

        // Act
        val result = entity.toMovieDetail()

        // Assert
        assertEquals(0, result.genres.size)
    }

    @Test
    fun `FavoriteMovieEntity toMovieDetail should handle invalid genresJson`() {
        // Arrange
        val entity =
            FavoriteMovieEntity(
                id = 1,
                title = "Test Movie",
                overview = "Overview",
                posterPath = "/poster.jpg",
                backdropPath = "/backdrop.jpg",
                releaseDate = "2024-01-01",
                voteAverage = 8.5,
                voteCount = 1000,
                popularity = 100.0,
                genresJson = "invalid json",
            )

        // Act
        val result = entity.toMovieDetail()

        // Assert
        assertEquals(0, result.genres.size)
    }

    @Test
    fun `Movie toEntity should convert correctly`() {
        // Arrange
        val movie =
            Movie(
                id = 1,
                title = "Test Movie",
                overview = "Overview",
                posterPath = "/poster.jpg",
                backdropPath = "/backdrop.jpg",
                releaseDate = "2024-01-01",
                voteAverage = 8.5,
                voteCount = 1000,
                popularity = 100.0,
            )

        // Act
        val result = movie.toEntity()

        // Assert
        assertEquals(1, result.id)
        assertEquals("Test Movie", result.title)
        assertEquals("Overview", result.overview)
        assertEquals("/poster.jpg", result.posterPath)
        assertEquals("/backdrop.jpg", result.backdropPath)
        assertEquals("2024-01-01", result.releaseDate)
        assertEquals(8.5, result.voteAverage, 0.01)
        assertEquals(1000, result.voteCount)
        assertEquals(100.0, result.popularity, 0.01)
    }

    @Test
    fun `MovieDetail toEntity should convert correctly with genres`() {
        // Arrange
        val movieDetail =
            MovieDetail(
                id = 1,
                title = "Test Movie",
                overview = "Overview",
                posterPath = "/poster.jpg",
                backdropPath = "/backdrop.jpg",
                releaseDate = "2024-01-01",
                voteAverage = 8.5,
                voteCount = 1000,
                popularity = 100.0,
                runtime = 120,
                genres = listOf(Genre(28, "Action"), Genre(12, "Adventure")),
                tagline = "Tagline",
                budget = 1000000L,
                revenue = 5000000L,
                status = "Released",
                homepage = "https://example.com",
            )

        // Act
        val result = movieDetail.toEntity()

        // Assert
        assertEquals(1, result.id)
        assertEquals("Test Movie", result.title)
        assertEquals(120, result.runtime)
        assertNotNull(result.genresJson)
        assertEquals("Tagline", result.tagline)
        assertEquals(1000000L, result.budget)
        assertEquals(5000000L, result.revenue)
        assertEquals("Released", result.status)
        assertEquals("https://example.com", result.homepage)
    }

    @Test
    fun `MovieDetail toEntity should handle empty genres`() {
        // Arrange
        val movieDetail =
            MovieDetail(
                id = 1,
                title = "Test Movie",
                overview = "Overview",
                posterPath = "/poster.jpg",
                backdropPath = "/backdrop.jpg",
                releaseDate = "2024-01-01",
                voteAverage = 8.5,
                voteCount = 1000,
                popularity = 100.0,
                runtime = 120,
                genres = emptyList(),
                tagline = "Tagline",
                budget = 1000000L,
                revenue = 5000000L,
                status = "Released",
                homepage = "https://example.com",
            )

        // Act
        val result = movieDetail.toEntity()

        // Assert
        assertNotNull(result.genresJson)
    }

    @Test
    fun `MovieDetailDto toDomain should convert correctly`() {
        // Arrange
        val movieDetailDto =
            MovieDetailDto(
                adult = false,
                backdropPath = "/backdrop.jpg",
                budget = 1000000L,
                genres = listOf(GenreDto(28, "Action")),
                homepage = "https://example.com",
                id = 1,
                imdbId = "tt123456",
                originalLanguage = "en",
                originalTitle = "Test Movie",
                overview = "Overview",
                popularity = 100.0,
                posterPath = "/poster.jpg",
                productionCompanies = emptyList(),
                productionCountries = emptyList(),
                releaseDate = "2024-01-01",
                revenue = 5000000L,
                runtime = 120,
                spokenLanguages = emptyList(),
                status = "Released",
                tagline = "Tagline",
                title = "Test Movie",
                video = false,
                voteAverage = 8.5,
                voteCount = 1000,
            )

        // Act
        val result = movieDetailDto.toDomain()

        // Assert
        assertEquals(1, result.id)
        assertEquals("Test Movie", result.title)
        assertEquals("Overview", result.overview)
        assertEquals("/poster.jpg", result.posterPath)
        assertEquals("/backdrop.jpg", result.backdropPath)
        assertEquals("2024-01-01", result.releaseDate)
        assertEquals(8.5, result.voteAverage, 0.01)
        assertEquals(1000, result.voteCount)
        assertEquals(100.0, result.popularity, 0.01)
        assertEquals(120, result.runtime)
        assertEquals(1, result.genres.size)
        assertEquals("Action", result.genres[0].name)
        assertEquals("Tagline", result.tagline)
        assertEquals(1000000L, result.budget)
        assertEquals(5000000L, result.revenue)
        assertEquals("Released", result.status)
        assertEquals("https://example.com", result.homepage)
    }

    @Test
    fun `GenreDto toDomain should convert correctly`() {
        // Arrange
        val genreDto = GenreDto(id = 28, name = "Action")

        // Act
        val result = genreDto.toDomain()

        // Assert
        assertEquals(28, result.id)
        assertEquals("Action", result.name)
    }
}
