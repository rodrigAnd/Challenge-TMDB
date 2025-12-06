package com.onboarding.mychallenge.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class MovieDetailTest {
    @Test
    fun `posterUrl should return full URL when posterPath is not null`() {
        // Arrange
        val movieDetail =
            createMovieDetail(
                posterPath = "poster.jpg",
            )

        // Act
        val result = movieDetail.posterUrl

        // Assert
        assertEquals("https://image.tmdb.org/t/p/w500poster.jpg", result)
    }

    @Test
    fun `posterUrl should return empty string when posterPath is null`() {
        // Arrange
        val movieDetail = createMovieDetail(posterPath = null)

        // Act
        val result = movieDetail.posterUrl

        // Assert
        assertEquals("", result)
    }

    @Test
    fun `backdropUrl should return full URL when backdropPath is not null`() {
        // Arrange
        val movieDetail =
            createMovieDetail(
                backdropPath = "backdrop.jpg",
            )

        // Act
        val result = movieDetail.backdropUrl

        // Assert
        assertEquals("https://image.tmdb.org/t/p/w1280backdrop.jpg", result)
    }

    @Test
    fun `backdropUrl should return empty string when backdropPath is null`() {
        // Arrange
        val movieDetail = createMovieDetail(backdropPath = null)

        // Act
        val result = movieDetail.backdropUrl

        // Assert
        assertEquals("", result)
    }

    @Test
    fun `formattedRating should format voteAverage correctly`() {
        // Arrange
        val movieDetail =
            createMovieDetail(
                voteAverage = 8.567,
            )

        // Act
        val result = movieDetail.formattedRating

        // Assert
        assertEquals("8,6", result)
    }

    @Test
    fun `formattedRuntime should format hours and minutes correctly`() {
        // Arrange
        val movieDetail =
            createMovieDetail(
                runtime = 150,
            )

        // Act
        val result = movieDetail.formattedRuntime

        // Assert
        assertEquals("2h 30min", result)
    }

    @Test
    fun `formattedRuntime should format only minutes when less than 60`() {
        // Arrange
        val movieDetail =
            createMovieDetail(
                runtime = 45,
            )

        // Act
        val result = movieDetail.formattedRuntime

        // Assert
        assertEquals("45min", result)
    }

    @Test
    fun `formattedRuntime should return N_A when runtime is null`() {
        // Arrange
        val movieDetail =
            createMovieDetail(
                runtime = null,
            )

        // Act
        val result = movieDetail.formattedRuntime

        // Assert
        assertEquals("N/A", result)
    }

    @Test
    fun `genreNames should return list of genre names`() {
        // Arrange
        val movieDetail =
            createMovieDetail(
                genres =
                    listOf(
                        Genre(28, "Action"),
                        Genre(12, "Adventure"),
                    ),
            )

        // Act
        val result = movieDetail.genreNames

        // Assert
        assertEquals(2, result.size)
        assertEquals("Action", result[0])
        assertEquals("Adventure", result[1])
    }

    @Test
    fun `genresString should return comma-separated genre names`() {
        // Arrange
        val movieDetail =
            createMovieDetail(
                genres =
                    listOf(
                        Genre(28, "Action"),
                        Genre(12, "Adventure"),
                        Genre(878, "Science Fiction"),
                    ),
            )

        // Act
        val result = movieDetail.genresString

        // Assert
        assertEquals("Action, Adventure, Science Fiction", result)
    }

    @Test
    fun `genresString should return empty string when genres list is empty`() {
        // Arrange
        val movieDetail =
            createMovieDetail(
                genres = emptyList(),
            )

        // Act
        val result = movieDetail.genresString

        // Assert
        assertEquals("", result)
    }

    private fun createMovieDetail(
        id: Int = 1,
        title: String = "Test Movie",
        overview: String = "Overview",
        posterPath: String? = "/poster.jpg",
        backdropPath: String? = "/backdrop.jpg",
        releaseDate: String? = "2024-01-01",
        voteAverage: Double = 8.5,
        voteCount: Int = 1000,
        popularity: Double = 100.0,
        runtime: Int? = 120,
        genres: List<Genre> = emptyList(),
        tagline: String? = "Tagline",
        budget: Long = 1000000L,
        revenue: Long = 5000000L,
        status: String = "Released",
        homepage: String? = "https://example.com",
    ) = MovieDetail(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
        runtime = runtime,
        genres = genres,
        tagline = tagline,
        budget = budget,
        revenue = revenue,
        status = status,
        homepage = homepage,
    )
}
