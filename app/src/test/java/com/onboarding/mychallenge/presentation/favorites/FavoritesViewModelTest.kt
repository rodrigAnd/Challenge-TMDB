package com.onboarding.mychallenge.presentation.favorites

import app.cash.turbine.test
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertIs

@ExperimentalCoroutinesApi
class FavoritesViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    // Mock para o repositório
    private lateinit var movieRepository: MovieRepository

    // A instância da ViewModel que vamos testar
    private lateinit var viewModel: FavoritesViewModel

    // Usamos um SharedFlow para simular o Flow do Room, que pode ser atualizado
    private lateinit var favoritesFlow: MutableSharedFlow<List<Movie>>

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        movieRepository = mockk(relaxUnitFun = true)
        favoritesFlow = MutableSharedFlow(replay = 1)
        every { movieRepository.getFavoriteMovies() } returns favoritesFlow
        coEvery { movieRepository.removeFromFavorites(any()) } returns Unit

        // ViewModel é criada aqui, e já começa a coletar o favoritesFlow
        viewModel = FavoritesViewModel(movieRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should emit Loading and then Success state when favorites are loaded`() =
        runTest {
            // Arrange
            val movies = listOf(createMockMovie(1, "Inception"))
            favoritesFlow.emit(movies) // Simula o banco de dados emitindo a lista

            // Assert
            viewModel.uiState.test {
                // O primeiro estado é Loading
                assertEquals(FavoritesUiState.Loading, awaitItem())

                // O segundo estado deve ser Success com os filmes
                val successState = awaitItem()
                assertTrue(successState is FavoritesUiState.Success)
                assertEquals(1, (successState as FavoritesUiState.Success).movies.size)
                assertEquals("Inception", successState.movies[0].title)
            }
        }

    @Test
    fun `init should emit Empty state when favorites list is empty`() =
        runTest {
            // Arrange
            favoritesFlow.emit(emptyList()) // Simula o banco de dados emitindo uma lista vazia

            // Assert
            viewModel.uiState.test {
                assertEquals(FavoritesUiState.Loading, awaitItem())
                assertEquals(FavoritesUiState.Empty, awaitItem())
            }
        }

    @Test
    fun `updateSearchQuery should filter favorites list after debounce`() =
        runTest {
            // Arrange
            val movies =
                listOf(
                    createMockMovie(1, "Inception"),
                    createMockMovie(2, "Interstellar"),
                )
            favoritesFlow.emit(movies)

            // Act & Assert
            viewModel.uiState.test {
                // 1. Consome o estado inicial (Loading -> Success)
                assertEquals(FavoritesUiState.Loading, awaitItem())
                assertEquals(2, (awaitItem() as FavoritesUiState.Success).movies.size)

                // 2. Atualiza a query de busca
                viewModel.updateSearchQuery("Incep")
                // 3. Avança o tempo do dispatcher para passar do debounce
                advanceTimeBy(501)

                // 4. Verifica o resultado filtrado
                val filteredState = awaitItem()
                assertTrue(filteredState is FavoritesUiState.Success)
                assertEquals(1, (filteredState as FavoritesUiState.Success).movies.size)
                assertEquals("Inception", filteredState.movies[0].title)
            }
        }

    @Test
    fun `updateSearchQuery with blank query should show all favorites`() =
        runTest {
            // Arrange
            val movies =
                listOf(
                    createMockMovie(1, "Inception"),
                    createMockMovie(2, "Interstellar"),
                )
            favoritesFlow.emit(movies)

            // Act & Assert
            viewModel.uiState.test {
                // Consome estados iniciais
                awaitItem() // Loading
                awaitItem() // Success com 2 filmes

                // Simula uma busca
                viewModel.updateSearchQuery("Incep")
                advanceTimeBy(501)
                awaitItem() // Success com 1 filme (filtrado)

                // Limpa a busca
                viewModel.updateSearchQuery("")
                advanceTimeBy(501)

                // Verifica se a lista completa voltou
                val fullListState = awaitItem()
                assertTrue(fullListState is FavoritesUiState.Success)
                assertEquals(2, (fullListState as FavoritesUiState.Success).movies.size)
            }
        }

    // --- Função de Apoio (Helper) ---
    private fun createMockMovie(
        id: Int,
        title: String,
    ): Movie {
        return Movie(
            id = id,
            title = title,
            overview = "Overview of $title",
            posterPath = "/path.jpg",
            backdropPath = "/backdrop.jpg",
            releaseDate = "2024-01-01",
            voteAverage = 8.0,
            voteCount = 100,
            popularity = 100.0,
        )
    }

    //region Initial Load & General Errors
    @Test
    fun `init should emit Loading and then Success when favorites are loaded`() =
        runTest {
            // Arrange
            val movies = listOf(createMockMovie(1, "Inception"))

            viewModel.uiState.test {
                // 1. O estado inicial é Loading
                assertEquals(FavoritesUiState.Loading, awaitItem())

                // 2. Simula o banco de dados emitindo a lista
                favoritesFlow.emit(movies)
                advanceUntilIdle() // Garante que a coleta e o processamento aconteçam

                // 3. O estado final deve ser Success
                val successState = awaitItem()
                assertIs<FavoritesUiState.Success>(successState)
                assertEquals(1, successState.movies.size)
                assertEquals("Inception", successState.movies[0].title)
            }
        }

    @Test
    fun `init should emit Error when repository throws exception`() =
        runTest {
            // Arrange: Configura o Flow para lançar um erro
            val error = RuntimeException("Database error")
            every {
                movieRepository.getFavoriteMovies()
            } returns kotlinx.coroutines.flow.flow { throw error }
            // Assert
            viewModel.uiState.test {
                assertEquals(FavoritesUiState.Loading, awaitItem())

                val errorState = awaitItem()
                assertIs<FavoritesUiState.Error>(errorState)
                assertEquals("Database error", errorState.message)
            }
        }
    //endregion

    @Test
    fun `search should return Empty when no movies match query`() =
        runTest {
            val movies = listOf(createMockMovie(1, "Inception"))

            viewModel.uiState.test {
                assertEquals(FavoritesUiState.Loading, awaitItem())
                favoritesFlow.emit(movies)
                advanceUntilIdle()
                awaitItem() // consome o estado Success inicial

                viewModel.updateSearchQuery("Matrix") // Busca por algo que não existe
                advanceTimeBy(501)
                advanceUntilIdle()

                // O estado deve se tornar Empty
                assertEquals(FavoritesUiState.Empty, awaitItem())
            }
        }
    //endregion
}
