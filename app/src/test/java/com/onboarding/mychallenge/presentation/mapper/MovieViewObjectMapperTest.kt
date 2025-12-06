package com.onboarding.mychallenge.presentation.mapper
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.presentation.movieList.MovieViewObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MovieViewObjectMapperTest {

    @Test
    fun `toViewObject should map Movie domain object to MovieViewObject correctly`() {
        // Arrange (Organizar)
        val movieDomain = Movie(
            id = 123,
            title = "Filme de Teste",
            overview = "Overview",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            releaseDate = "2024-07-26",
            voteAverage = 8.5,
            voteCount = 100,
            popularity = 100.0,
        )

        // Act (Agir)
        val viewObject = movieDomain.toViewObject()

        // Assert (Verificar)
        assertEquals(123, viewObject.id)
        assertEquals("Filme de Teste", viewObject.title)
        assertEquals("Overview", viewObject.overview)
        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", viewObject.posterUrl)
        assertEquals("https://image.tmdb.org/t/p/w1280/backdrop.jpg", viewObject.backdropUrl)
        assertEquals("2024-07-26", viewObject.releaseDate)
        assertEquals("26/07/2024", viewObject.formattedReleaseDate) // Verifica a formatação da data
        assertEquals(8.5, viewObject.rating, 0.0) // O terceiro parâmetro é uma tolerância para floats/doubles
        assertEquals("8,5", viewObject.formattedRating)
        assertEquals(100, viewObject.voteCount)
        assertEquals(100.0, viewObject.popularity, 0.0)
        assertEquals(false, viewObject.isFavorite) // Verifica o valor padrão
        assertEquals(false, viewObject.isLoadingFavorite) // Verifica o valor padrão
    }

    @Test
    fun `toViewObject should handle null release date`() {
        val movie = createMovie(1, "Movie 1", null)
        val viewObject = movie.toViewObject()
        assertEquals("Data não disponível", viewObject.formattedReleaseDate)
    }

    @Test
    fun `toViewObject should handle invalid date format`() {
        val movie = createMovie(1, "Movie 1", "invalid-date")
        val viewObject = movie.toViewObject()
        assertEquals("invalid-date", viewObject.formattedReleaseDate)
    }

    @Test
    fun `toViewObject should handle empty release date`() {
        val movie = createMovie(1, "Movie 1", "")
        val viewObject = movie.toViewObject()
        assertEquals("Data não disponível", viewObject.formattedReleaseDate)
    }

    @Test
    fun `toViewObjectList should map list of Movies to MovieViewObjects`() {
        val movies =
            listOf(
                createMovie(1, "Movie 1"),
                createMovie(2, "Movie 2"),
            )
        val favoriteIds = setOf(1)
        val loadingFavoriteIds = setOf(2)
        val viewObjects = movies.toViewObjectList(favoriteIds, loadingFavoriteIds)
        assertEquals(2, viewObjects.size)
        assertTrue(viewObjects[0].isFavorite)
        assertFalse(viewObjects[0].isLoadingFavorite)
        assertFalse(viewObjects[1].isFavorite)
        assertTrue(viewObjects[1].isLoadingFavorite)
    }

    @Test
    fun `toViewObjectList should handle empty list`() {
        val movies = emptyList<Movie>()
        val viewObjects = movies.toViewObjectList()
        assertTrue(viewObjects.isEmpty())
    }

    @Test
    fun `toViewObject should format date correctly`() {
        val movie = createMovie(1, "Movie 1", "2023-12-25")
        val viewObject = movie.toViewObject()
        assertEquals("25/12/2023", viewObject.formattedReleaseDate)
    }

    @Test
    fun `toViewObject should handle isLoadingFavorite flag`() {
        val movie = createMovie(1, "Movie 1")
        val viewObject = movie.toViewObject(isFavorite = false, isLoadingFavorite = true)
        assertFalse(viewObject.isFavorite)
        assertTrue(viewObject.isLoadingFavorite)
    }

    private fun createMovie(
        id: Int,
        title: String,
        releaseDate: String? = "2024-01-01",
    ): Movie {
        return Movie(
            id = id,
            title = title,
            overview = "Overview",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            releaseDate = releaseDate,
            voteAverage = 8.5,
            voteCount = 100,
            popularity = 100.0,
        )
    }
}

