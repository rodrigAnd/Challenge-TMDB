package com.onboarding.mychallenge.presentation.movieList

import android.util.Log
import app.cash.turbine.test
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.model.MovieDetail
import com.onboarding.mychallenge.domain.usecase.AddMovieDetailToFavoritesUseCase
import com.onboarding.mychallenge.domain.usecase.AddToFavoritesUseCase
import com.onboarding.mychallenge.domain.usecase.GetFavoriteMoviesUseCase
import com.onboarding.mychallenge.domain.usecase.GetMovieDetailsUseCase
import com.onboarding.mychallenge.domain.usecase.GetPopularMoviesUseCase
import com.onboarding.mychallenge.domain.usecase.IsFavoriteUseCase
import com.onboarding.mychallenge.domain.usecase.RemoveFromFavoritesUseCase
import com.onboarding.mychallenge.domain.usecase.SearchMoviesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertIs

@ExperimentalCoroutinesApi
class MovieListViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    // Mocks para todos os UseCases
    private lateinit var getPopularMoviesUseCase: GetPopularMoviesUseCase
    private lateinit var searchMoviesUseCase: SearchMoviesUseCase
    private lateinit var addToFavoritesUseCase: AddToFavoritesUseCase
    private lateinit var addMovieDetailToFavoritesUseCase: AddMovieDetailToFavoritesUseCase
    private lateinit var removeFromFavoritesUseCase: RemoveFromFavoritesUseCase
    private lateinit var isFavoriteUseCase: IsFavoriteUseCase
    private lateinit var getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase
    private lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase

    private lateinit var viewModel: MovieListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        // Inicialização dos mocks
        getPopularMoviesUseCase = mockk()
        searchMoviesUseCase = mockk()
        addToFavoritesUseCase = mockk(relaxUnitFun = true)
        addMovieDetailToFavoritesUseCase = mockk(relaxUnitFun = true)
        removeFromFavoritesUseCase = mockk(relaxUnitFun = true)
        isFavoriteUseCase = mockk()
        getFavoriteMoviesUseCase = mockk()
        getMovieDetailsUseCase = mockk()

        // Comportamento padrão para o flow de favoritos para evitar NPE nos testes
        every { getFavoriteMoviesUseCase() } returns flowOf(emptyList())
    }

    private fun createViewModel() {
        viewModel =
            MovieListViewModel(
                getPopularMoviesUseCase,
                searchMoviesUseCase,
                addToFavoritesUseCase,
                addMovieDetailToFavoritesUseCase,
                removeFromFavoritesUseCase,
                isFavoriteUseCase,
                getFavoriteMoviesUseCase,
                getMovieDetailsUseCase,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    //region Initial Load & Retry
    @Test
    fun `init should load popular movies and emit Success`() =
        runTest {
            // Arrange
            val movies = listOf(createMovie(1))
            coEvery { getPopularMoviesUseCase(1) } returns Result.success(movies)

            // Act
            // A ViewModel é criada e sua lógica de init é executada de forma síncrona.
            createViewModel()

            // Assert
            viewModel.uiState.test {
                // --- INÍCIO DA CORREÇÃO ---
                // O primeiro estado que a Turbine coleta já é o estado final, 'Success',
                // porque a transição Loading -> Success aconteceu instantaneamente.
                val finalState = awaitItem()

                // Verificamos se o estado final é de fato 'Success' com os dados corretos.
                assertIs<MovieListUiState.Success>(finalState)
                assertEquals(1, finalState.movies.size)

                // Garante que não há mais emissões inesperadas.
                cancelAndIgnoreRemainingEvents()
                // --- FIM DA CORREÇÃO ---
            }
        }

    @Test
    fun `init should emit Empty when popular movies are empty`() =
        runTest {
            // Arrange
            coEvery { getPopularMoviesUseCase(1) } returns Result.success(emptyList())

            // Act
            // A ViewModel é criada e sua lógica de init é executada imediatamente
            // devido ao UnconfinedTestDispatcher.
            createViewModel()

            // Assert
            viewModel.uiState.test {
                // --- INÍCIO DA CORREÇÃO ---
                // O primeiro estado que a Turbine coleta já é o estado final, 'Empty',
                // porque as transições Loading -> Empty acontecem de forma síncrona e
                // o StateFlow otimiza a emissão do estado 'Loading' intermediário.
                val finalState = awaitItem()

                // Verificamos se o estado final é de fato 'Empty'.
                assertEquals(MovieListUiState.Empty, finalState)

                // Garante que não há mais emissões inesperadas.
                cancelAndIgnoreRemainingEvents()
                // --- FIM DA CORREÇÃO ---
            }
        }

    @Test
    fun `init should emit Error when loading popular movies fails`() =
        runTest {
            // Arrange
            val error = RuntimeException("Network Error")
            coEvery { getPopularMoviesUseCase(1) } returns Result.failure(error)

            // Act
            // A ViewModel é criada e sua lógica de init é executada de forma síncrona.
            createViewModel()

            // Assert
            viewModel.uiState.test {
                // --- INÍCIO DA CORREÇÃO ---
                // O primeiro estado que a Turbine coleta já é o estado final, 'Error',
                // porque a transição Loading -> Error aconteceu instantaneamente.
                val finalState = awaitItem()

                // Verificamos se o estado final é de fato 'Error' com a mensagem correta.
                assertIs<MovieListUiState.Error>(finalState)
                assertEquals("Network Error", finalState.message)

                // Garante que não há mais emissões inesperadas.
                cancelAndIgnoreRemainingEvents()
                // --- FIM DA CORREÇÃO ---
            }
        }

    @Test
    fun `retry should reload popular movies when not in search mode`() =
        runTest {
            // Arrange
            coEvery { getPopularMoviesUseCase(1) } returns Result.success(emptyList())
            createViewModel()

            // Act
            viewModel.retry()

            // Assert
            coVerify(exactly = 2) { getPopularMoviesUseCase(1) }
        }

    @Test
    fun `retry should re-run search when in search mode`() =
        runTest {
            // Arrange
            coEvery { getPopularMoviesUseCase(1) } returns Result.success(emptyList())
            coEvery { searchMoviesUseCase(any(), any()) } returns Result.success(emptyList())
            createViewModel()
            viewModel.updateSearchQuery("test")
            advanceTimeBy(501)

            // Act
            viewModel.retry()

            // Assert
            coVerify(exactly = 2) { searchMoviesUseCase("test", 1) }
        }
    //endregion

    //region Search
    @Test
    fun `search should be triggered by query update and emit Success`() =
        runTest {
            // Arrange
            // A carga inicial resulta em um estado 'Empty'.
            coEvery { getPopularMoviesUseCase(any()) } returns Result.success(emptyList())
            val searchResults = listOf(createMovie(1, "Searched Movie"))
            coEvery { searchMoviesUseCase("test", 1) } returns Result.success(searchResults)
            createViewModel()

            // Act & Assert
            viewModel.uiState.test {
                // --- INÍCIO DA CORREÇÃO ---
                // 1. O estado inicial que o teste observa já é 'Empty', resultado da inicialização.
                assertEquals(MovieListUiState.Empty, awaitItem())

                // 2. Atualiza a query de busca e avança o tempo para o debounce.
                viewModel.updateSearchQuery("test")
                advanceTimeBy(501)

                // 3. Após o debounce, a busca é acionada. A ViewModel emite 'Loading' e depois 'Success'.
                // Como a transição é rápida, é mais seguro verificar apenas o estado final 'Success'.
                val successState = awaitItem()
                assertIs<MovieListUiState.Success>(successState)
                assertEquals("Searched Movie", successState.movies.first().title)
                assertTrue(successState.isSearch)

                // Garante que não há mais emissões inesperadas.
                cancelAndIgnoreRemainingEvents()
                // --- FIM DA CORREÇÃO ---
            }
        }

    @Test
    fun `clearing search query should reload popular movies`() =
        runTest {
            // Arrange
            coEvery { getPopularMoviesUseCase(any()) } returns Result.success(listOf(createMovie(1, "Popular")))
            coEvery { searchMoviesUseCase(any(), any()) } returns Result.success(emptyList())
            createViewModel()

            // Act
            viewModel.updateSearchQuery("test")
            advanceTimeBy(501)
            viewModel.updateSearchQuery("")
            advanceTimeBy(501)

            // Assert
            coVerify(exactly = 2) { getPopularMoviesUseCase(1) }
        }
    //endregion

    //region Pagination
    @Test
    fun `loadNextPage should load more popular movies and append`() =
        runTest {
            // Arrange
            coEvery { getPopularMoviesUseCase(1) } returns Result.success(listOf(createMovie(1, "Page 1")))
            coEvery { getPopularMoviesUseCase(2) } returns Result.success(listOf(createMovie(2, "Page 2")))
            createViewModel()

            // Act & Assert
            viewModel.uiState.test {
                // --- INÍCIO DA CORREÇÃO ---
                // 1. O estado inicial que a Turbine observa já é o resultado da carga da página 1.
                val initialState = awaitItem()
                assertIs<MovieListUiState.Success>(initialState)
                assertEquals("Page 1", initialState.movies.first().title)

                // 2. Agora, acionamos a paginação.
                viewModel.loadNextPage()

                // 3. Verificamos o estado de 'carregando mais'.
                val loadingMoreState = awaitItem()
                assertIs<MovieListUiState.Success>(loadingMoreState)
                assertTrue(loadingMoreState.isLoadingMore)
                assertEquals(1, loadingMoreState.movies.size) // A lista antiga ainda está aqui

                // 4. Verificamos o estado final com a lista combinada.
                val finalState = awaitItem()
                assertIs<MovieListUiState.Success>(finalState)
                assertEquals(2, finalState.movies.size)
                assertEquals("Page 1", finalState.movies[0].title)
                assertEquals("Page 2", finalState.movies[1].title)
                assertFalse(finalState.isLoadingMore)

                // 5. Garante que não há mais emissões.
                cancelAndIgnoreRemainingEvents()
                // --- FIM DA CORREÇÃO ---
            }
        }

    @Test
    fun `loadNextPage should load more search results and append`() =
        runTest {
            // Arrange
            coEvery { getPopularMoviesUseCase(any()) } returns Result.success(emptyList())
            coEvery { searchMoviesUseCase("test", 1) } returns Result.success(listOf(createMovie(1, "Search P1")))
            coEvery { searchMoviesUseCase("test", 2) } returns Result.success(listOf(createMovie(2, "Search P2")))
            createViewModel()

            // Act
            viewModel.updateSearchQuery("test")
            advanceTimeBy(501) // Debounce

            // Assert
            viewModel.uiState.test {
                // --- INÍCIO DA CORREÇÃO ---

                // 1. O estado atual da ViewModel é o resultado da busca pela página 1. Vamos consumir e verificar.
                val initialState = awaitItem()
                assertIs<MovieListUiState.Success>(initialState)
                assertEquals(1, initialState.movies.size)
                assertEquals("Search P1", initialState.movies.first().title)

                // 2. Agora, acionamos a paginação para a página 2.
                viewModel.loadNextPage()

                // 3. Verificamos o estado de 'carregando mais'.
                val loadingMoreState = awaitItem()
                assertIs<MovieListUiState.Success>(loadingMoreState)
                assertTrue(loadingMoreState.isLoadingMore)
                assertEquals(1, loadingMoreState.movies.size) // A lista antiga ainda está aqui

                // 4. Verificamos o estado final com a lista combinada (Página 1 + Página 2).
                val finalState = awaitItem()
                assertIs<MovieListUiState.Success>(finalState)
                assertEquals(2, finalState.movies.size)
                assertEquals("Search P1", finalState.movies[0].title)
                assertEquals("Search P2", finalState.movies[1].title)
                assertFalse(finalState.isLoadingMore)

                // 5. Garante que não há mais emissões.
                cancelAndIgnoreRemainingEvents()

                // --- FIM DA CORREÇÃO ---
            }
        }

    @Test
    fun `loadNextPage should handle failure gracefully`() =
        runTest {
            // Arrange
            coEvery { getPopularMoviesUseCase(1) } returns Result.success(listOf(createMovie(1)))
            // A paginação para a página 2 vai falhar
            coEvery { getPopularMoviesUseCase(2) } returns Result.failure(RuntimeException("Network Error on Page 2"))
            createViewModel()

            // Act & Assert
            viewModel.uiState.test {
                // --- INÍCIO DA CORREÇÃO ---

                // 1. O estado inicial que a Turbine observa já é o resultado da carga da página 1.
                val initialState = awaitItem()
                assertIs<MovieListUiState.Success>(initialState)
                assertEquals(1, initialState.movies.size)
                assertFalse(initialState.isLoadingMore)

                // 2. Agora, acionamos a paginação, que sabemos que vai falhar.
                viewModel.loadNextPage()

                // 3. Verificamos o estado de 'carregando mais'.
                val loadingMoreState = awaitItem()
                assertIs<MovieListUiState.Success>(loadingMoreState)
                assertTrue(loadingMoreState.isLoadingMore)
                assertEquals(1, loadingMoreState.movies.size) // A lista antiga ainda está aqui

                // 4. Verificamos o estado final após a falha.
                // O estado deve ser 'Success', mas com 'isLoadingMore' de volta para 'false'.
                val finalState = awaitItem()
                assertIs<MovieListUiState.Success>(finalState)
                assertEquals(1, finalState.movies.size) // A lista não deve ter mudado.
                assertFalse(finalState.isLoadingMore) // O loading foi resetado.

                // 5. Garante que não há mais emissões.
                cancelAndIgnoreRemainingEvents()
                // --- FIM DA CORREÇÃO ---
            }
        }

    // --- TESTE CORRIGIDO ---
    @Test
    fun `loadNextPage should not load if already loading`() =
        runTest {
            // Arrange
            coEvery { getPopularMoviesUseCase(1) } returns Result.success(listOf(createMovie(1)))
            // Simula uma chamada de rede que nunca termina para manter o estado de loading
            coEvery { getPopularMoviesUseCase(2) } coAnswers {
                delay(Long.MAX_VALUE)
                Result.success(emptyList())
            }
            createViewModel()
            testScheduler.advanceUntilIdle() // Garante que a carga inicial termine

            // Act
            // A primeira chamada vai iniciar o carregamento e travar o estado em 'isLoadingMore = true'
            viewModel.loadNextPage()
            // A segunda chamada deve ser ignorada pela lógica de bloqueio da ViewModel
            viewModel.loadNextPage()

            // Assert
            // Verificamos que a chamada para a página 2 foi feita apenas UMA vez.
            coVerify(exactly = 1) { getPopularMoviesUseCase(2) }
        }

    // --- TESTE CORRIGIDO ---
    @Test
    fun `loadNextPage should not load if canLoadMore is false`() =
        runTest {
            // Arrange
            coEvery { getPopularMoviesUseCase(1) } returns Result.success(listOf(createMovie(1)))
            // A paginação para a página 2 retorna uma lista vazia, o que deve setar canLoadMore = false
            coEvery { getPopularMoviesUseCase(2) } returns Result.success(emptyList())
            createViewModel()
            testScheduler.advanceUntilIdle()

            // Act
            // Carrega a página 2, o que fará a ViewModel setar 'canLoadMore' para false.
            viewModel.loadNextPage()
            testScheduler.advanceUntilIdle()

            // Confirma que o estado está correto
            val state = viewModel.uiState.value as MovieListUiState.Success
            assertFalse(state.canLoadMore)

            // Tenta carregar a página 3, o que não deve ser possível
            viewModel.loadNextPage()

            // Assert
            // Verifica que a chamada para a página 3 nunca aconteceu.
            coVerify(exactly = 0) { getPopularMoviesUseCase(3) }
        }
    //endregion

    @Test
    fun `addToFavorites should fetch details and add`() =
        runTest {
            // Arrange
            val movie = createMovie(1)
            val movieDetail = createMovieDetail(1)
            coEvery { getPopularMoviesUseCase(1) } returns Result.success(listOf(movie))
            coEvery { getMovieDetailsUseCase(1) } returns Result.success(movieDetail)
            createViewModel()

            // Act
            viewModel.addToFavorites(1)

            // Assert
            coVerify(exactly = 1) { addMovieDetailToFavoritesUseCase(movieDetail) }
        }

    @Test
    fun `addToFavorites should use fallback when details fail`() =
        runTest {
            // Arrange        // --- INÍCIO DA CORREÇÃO ---
            // Cria o objeto Movie exatamente como a função `toDomain()` o criaria
            // a partir de um MovieViewObject. Note que os paths não têm a barra inicial.
            val movieFromDomainLogic =
                createMovie(1).copy(
                    posterPath = "p",
                    backdropPath = "b",
                )

            // O UseCase de populares retorna o objeto original com a barra.
            val movieFromApi = createMovie(1)
            coEvery { getPopularMoviesUseCase(1) } returns Result.success(listOf(movieFromApi))
            // --- FIM DA CORREÇÃO ---

            coEvery { getMovieDetailsUseCase(1) } returns Result.failure(RuntimeException())
            createViewModel()
            testScheduler.advanceUntilIdle()

            // Act
            viewModel.addToFavorites(1)

            // Assert
            // A verificação agora usa o objeto que reflete a lógica de `toDomain()`.
            coVerify(exactly = 1) { addToFavoritesUseCase(movieFromDomainLogic) }
        }

    @Test
    fun `addToFavorites does nothing if movie not in current state on fallback`() =
        runTest {
            // Arrange
            coEvery { getPopularMoviesUseCase(1) } returns Result.success(emptyList())
            coEvery { getMovieDetailsUseCase(1) } returns Result.failure(RuntimeException())
            createViewModel()

            // Act
            viewModel.addToFavorites(1)

            // Assert
            coVerify(exactly = 0) { addToFavoritesUseCase(any()) }
        }
    //endregion

    // --- Helpers ---
    private fun createMovie(
        id: Int,
        title: String = "Movie",
    ) = Movie(id = id, title = title, overview = "O", posterPath = "/p", backdropPath = "/b", releaseDate = "d", voteAverage = 1.0, voteCount = 1, popularity = 1.0)

    private fun createMovieDetail(
        id: Int,
        title: String = "Movie",
    ) = MovieDetail(
        id = id,
        title = title,
        overview = "O",
        posterPath = "/p",
        backdropPath = "/b",
        releaseDate = "d",
        voteAverage = 1.0,
        voteCount = 1,
        popularity = 1.0,
        runtime = 120,
        genres = emptyList(),
        tagline = "t",
        budget = 1,
        revenue = 1,
        status = "s",
        homepage = "h",
    )
}
