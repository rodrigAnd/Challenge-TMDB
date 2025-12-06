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
    fun setUp() {
        apiService = mockk()
        favoriteDao = mockk(relaxUnitFun = true)
        repository = MovieRepositoryImpl(apiService, favoriteDao)
    }

    //region getPopularMovies
    @Test
    fun `getPopularMovies should return success with movie list when API call is successful`() =
        runTest {
            // Arrange
            val movieDto = createMovieDto(1, "Movie 1")
            val response = MoviesResponseDto(1, listOf(movieDto), 10, 100)
            coEvery { apiService.getPopularMovies(1, "pt-BR") } returns response

            // Act
            val result = repository.getPopularMovies(1)

            // Assert
            assertTrue(result.isSuccess)
            val movies = result.getOrNull()
            assertEquals(1, movies?.size)
            assertEquals("Movie 1", movies?.first()?.title)
        }

    @Test
    fun `getPopularMovies should return failure when API call throws exception`() =
        runTest {
            // Arrange
            // A exceção que a camada de rede lança (simulada).
            val apiException = RuntimeException("Network Error")
            coEvery { apiService.getPopularMovies(1, "pt-BR") } throws apiException

            // Act
            val result = repository.getPopularMovies(1)

            // Assert
            // 1. Primeiro, confirme que a operação realmente falhou.
            assertTrue(result.isFailure)

            // --- INÍCIO DA CORREÇÃO ---
            // 2. Obtenha a exceção que o *repositório* de fato retornou.
            val actualException = result.exceptionOrNull()

            // 3. Verifique se a MENSAGEM da exceção é a que o repositório define em seu tratamento de erro.
            assertEquals("Erro ao carregar filmes. Tente novamente.", actualException?.message)
            // --- FIM DA CORREÇÃO ---
        }

    //endregion

    //region getMovieDetails
    @Test
    fun `getMovieDetails should return success with detail when API call is successful`() =
        runTest {
            // Arrange
            val movieDetailDto = createMovieDetailDto(1, "Detailed Movie")
            coEvery { apiService.getMovieDetails(1, "pt-BR") } returns movieDetailDto

            // Act
            val result = repository.getMovieDetails(1)

            // Assert
            assertTrue(result.isSuccess)
            assertEquals("Detailed Movie", result.getOrNull()?.title)
        }

    @Test
    fun `getMovieDetails should return failure when API call throws exception`() =
        runTest {
            // Arrange
            // A exceção que a camada de rede lança.
            val apiException = RuntimeException("API Error")
            coEvery { apiService.getMovieDetails(1, "pt-BR") } throws apiException

            // Act
            val result = repository.getMovieDetails(1)

            // Assert
            // 1. Verifica se o resultado é de fato uma falha.
            assertTrue(result.isFailure)

            // --- INÍCIO DA CORREÇÃO ---
            // 2. Pega a exceção que o *repositório* criou.
            val actualException = result.exceptionOrNull()

            // 3. Verifica se a mensagem da exceção é a mensagem personalizada definida no repositório.
            assertEquals("Erro ao carregar detalhes do filme. Tente novamente.", actualException?.message)
            // --- FIM DA CORREÇÃO ---
        }

    //endregion

    //region searchMovies
    @Test
    fun `searchMovies should return success when API search is successful`() =
        runTest {
            // Arrange
            val movieDto = createMovieDto(1, "Searched Movie")
            val response = MoviesResponseDto(1, listOf(movieDto), 1, 1)
            coEvery { apiService.searchMovies("query", 1, "pt-BR") } returns response

            // Act
            val result = repository.searchMovies("query", 1)

            // Assert
            assertTrue(result.isSuccess)
            assertEquals("Searched Movie", result.getOrNull()?.first()?.title)
        }

    @Test
    fun `searchMovies should return failure when API search throws exception`() =
        runTest {
            // Arrange
            // A exceção que a camada de rede lança (simulada).
            val apiException = RuntimeException("Search Error")
            coEvery { apiService.searchMovies("query", 1, "pt-BR") } throws apiException

            // Act
            val result = repository.searchMovies("query", 1)

            // Assert
            // 1. Confirma que a operação realmente resultou em uma falha.
            assertTrue(result.isFailure)

            // --- INÍCIO DA CORREÇÃO ---
            // 2. Obtém a exceção que o *repositório* de fato retornou.
            val actualException = result.exceptionOrNull()

            // 3. Verifica se a MENSAGEM da exceção é a que o repositório define em seu tratamento de erro.
            assertEquals("Erro ao buscar filmes. Tente novamente.", actualException?.message)
            // --- FIM DA CORREÇÃO ---
        }

    //region Favorites
    @Test
    fun `getFavoriteMovies should return flow from DAO`() =
        runTest {
            // Arrange
            val entity = createFavoriteEntity(1, "Favorite Movie")
            every { favoriteDao.getAllFavorites() } returns flowOf(listOf(entity))

            // Act
            val flow = repository.getFavoriteMovies()

            // Assert
            flow.collect { movies ->
                assertEquals(1, movies.size)
                assertEquals("Favorite Movie", movies.first().title)
            }
        }

    @Test
    fun `getFavoriteMovieDetails should return success when movie is in DAO`() =
        runTest {
            // Arrange
            val entity = createFavoriteEntity(1, "Favorite Detail")
            coEvery { favoriteDao.getFavoriteById(1) } returns entity

            // Act
            val result = repository.getFavoriteMovieDetails(1)

            // Assert
            assertTrue(result.isSuccess)
            assertEquals("Favorite Detail", result.getOrNull()?.title)
        }

    @Test
    fun `getFavoriteMovieDetails should return failure when movie is not in DAO`() =
        runTest {
            // Arrange
            coEvery { favoriteDao.getFavoriteById(1) } returns null

            // Act
            val result = repository.getFavoriteMovieDetails(1)

            // Assert
            assertTrue(result.isFailure)
        }

    @Test
    fun `addMovieDetailToFavorites should call DAO insert`() =
        runTest {
            // Arrange
            val movieDetail = createMovieDetail(1, "Movie to Add")

            // Act
            repository.addMovieDetailToFavorites(movieDetail)

            // Assert
            coVerify(exactly = 1) { favoriteDao.insertFavorite(any()) }
        }

    @Test
    fun `removeFromFavorites should call DAO delete`() =
        runTest {
            // Arrange
            val movieId = 1

            // Act
            repository.removeFromFavorites(movieId)

            // Assert
            coVerify(exactly = 1) { favoriteDao.deleteFavorite(movieId) }
        }

    @Test
    fun `isFavorite should return success with true when movie exists in DAO`() =
        runTest {
            // Arrange
            coEvery { favoriteDao.isFavorite(1) } returns true

            // Act
            val result = repository.isFavorite(1)

            // Assert
            assertTrue(result)
        }

    @Test
    fun `isFavorite should return success with false when movie does not exist in DAO`() =
        runTest {
            // Arrange
            coEvery { favoriteDao.isFavorite(1) } returns false

            // Act
            val result = repository.isFavorite(1)

            // Assert
            assertEquals(false, result)
        }

    // --- Funções de Apoio (Helpers) ---
    private fun createMovieDto(
        id: Int,
        title: String,
    ) = MovieDto(
        id = id, title = title, originalTitle = title, overview = "Overview", posterPath = "/poster.jpg", backdropPath = "/backdrop.jpg", releaseDate = "2024-01-01", voteAverage = 8.5, voteCount = 100, adult = false,
        genreIds =
            listOf(
                1,
            ),
        originalLanguage = "en", popularity = 100.0, video = false,
    )

    private fun createMovieDetailDto(
        id: Int,
        title: String,
    ) = MovieDetailDto(
        id = id,
        title = title,
        originalTitle = title,
        overview = "Overview",
        posterPath = "/poster.jpg",
        backdropPath = "/backdrop.jpg",
        releaseDate = "2024-01-01",
        voteAverage = 8.5,
        voteCount = 100,
        adult = false,
        originalLanguage = "en",
        popularity = 100.0,
        video = false,
        budget = 1L,
        genres = emptyList(),
        homepage = "",
        imdbId = "id",
        productionCompanies = emptyList(),
        productionCountries = emptyList(),
        revenue = 1L,
        runtime = 120,
        spokenLanguages = emptyList(),
        status = "Released",
        tagline = "Tagline",
    )

    private fun createMovieDetail(
        id: Int,
        title: String,
    ) = MovieDetail(
        id = id, title = title, overview = "Overview", posterPath = "/poster.jpg", backdropPath = "/backdrop.jpg", releaseDate = "2024-01-01", voteAverage = 8.5, voteCount = 100, popularity = 100.0, runtime = 120,
        genres =
            listOf(
                Genre(1, "Action"),
            ),
        tagline = "Tagline", budget = 1L, revenue = 1L, status = "Released", homepage = "",
    )

    private fun createFavoriteEntity(
        id: Int,
        title: String,
    ) = FavoriteMovieEntity(
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
        genresJson = "[]",
        tagline = "Tagline",
        budget = 1L,
        revenue = 1L,
        status = "Released",
        homepage = "",
    )
}
