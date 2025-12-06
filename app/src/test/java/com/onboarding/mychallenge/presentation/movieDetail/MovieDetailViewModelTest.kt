package com.onboarding.mychallenge.presentation.movieDetail

import app.cash.turbine.test
import com.onboarding.mychallenge.domain.model.Genre
import com.onboarding.mychallenge.domain.model.MovieDetail
import com.onboarding.mychallenge.domain.usecase.GetMovieDetailsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MovieDetailViewModelTest {
    // Dispatcher de teste para controlar a execução das corrotinas
    private val testDispatcher = StandardTestDispatcher()

    // Mock do nosso UseCase
    private lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase

    // A instância da ViewModel que vamos testar
    private lateinit var viewModel: MovieDetailViewModel

    @Before
    fun setUp() {
        // Configura o dispatcher principal para ser o nosso dispatcher de teste
        Dispatchers.setMain(testDispatcher)

        // Cria o mock para o UseCase
        getMovieDetailsUseCase = mockk()

        // Instancia a ViewModel com a dependência mockada
        viewModel = MovieDetailViewModel(getMovieDetailsUseCase)
    }

    @After
    fun tearDown() {
        // Reseta o dispatcher principal após cada teste
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading`() =
        runTest {
            // Assert
            // Verifica se o estado inicial emitido pela ViewModel é Loading
            assertEquals(MovieDetailUiState.Loading, viewModel.uiState.value)
        }

    @Test
    fun `loadMovieDetails should emit Success state when use case returns success`() =
        runTest {
            // Arrange
            val movieId = 123
            val mockMovieDetail = createMockMovieDetail(movieId, "Filme de Teste")

            // Configura o mock: quando o use case for chamado, deve retornar sucesso
            coEvery { getMovieDetailsUseCase(movieId) } returns Result.success(mockMovieDetail)

            // Act & Assert
            viewModel.uiState.test {
                // 1. O estado inicial já é Loading, vamos consumi-lo
                assertEquals(MovieDetailUiState.Loading, awaitItem())

                // 2. Chama a função que queremos testar
                viewModel.loadMovieDetails(movieId)

                // --- INÍCIO DA CORREÇÃO ---
                // 3. A ViewModel irá diretamente para o estado de Sucesso.
                // Não verificamos o Loading intermediário, pois ele é otimizado e não garantido.
                val successState = awaitItem()
                assertTrue("O estado deveria ser Success", successState is MovieDetailUiState.Success)
                assertEquals(mockMovieDetail, (successState as MovieDetailUiState.Success).movieDetail)
                // --- FIM DA CORREÇÃO ---

                // Garante que não há mais emissões de estado
                ensureAllEventsConsumed()
            }

            // Verifica se o use case foi chamado exatamente uma vez com o ID correto
            coVerify(exactly = 1) { getMovieDetailsUseCase(movieId) }
        }

    @Test
    fun `loadMovieDetails should emit Error state when use case returns failure`() =
        runTest {
            // Arrange
            val movieId = 456
            val errorMessage = "Erro de rede simulado"

            // Configura o mock: quando o use case for chamado, deve retornar uma falha
            coEvery { getMovieDetailsUseCase(movieId) } returns Result.failure(Exception(errorMessage))

            // Act & Assert
            viewModel.uiState.test {
                // 1. Consome o estado inicial de Loading
                assertEquals(MovieDetailUiState.Loading, awaitItem())

                // 2. Chama a função
                viewModel.loadMovieDetails(movieId)

                // --- INÍCIO DA CORREÇÃO ---
                // 3. A ViewModel irá diretamente para o estado de Erro.
                // Não verificamos mais o Loading intermediário.
                val errorState = awaitItem()
                assertTrue("O estado deveria ser Error", errorState is MovieDetailUiState.Error)
                assertEquals(errorMessage, (errorState as MovieDetailUiState.Error).message)
                // --- FIM DA CORREÇÃO ---

                // Garante que não há mais emissões
                ensureAllEventsConsumed()
            }

            // Verifica se o use case foi chamado
            coVerify(exactly = 1) { getMovieDetailsUseCase(movieId) }
        }

    @Test
    fun `loadMovieDetails should emit Error state with default message on null exception message`() =
        runTest {
            // Arrange
            val movieId = 789

            // Configura o mock para retornar uma falha com exceção sem mensagem
            coEvery { getMovieDetailsUseCase(movieId) } returns Result.failure(Exception(null as String?))

            // Act & Assert
            viewModel.uiState.test {
                // 1. Consome o estado inicial de Loading
                assertEquals(MovieDetailUiState.Loading, awaitItem())

                // 2. Chama a função que queremos testar
                viewModel.loadMovieDetails(movieId)

                // --- INÍCIO DA CORREÇÃO ---
                // 3. A ViewModel irá diretamente para o estado de Erro.
                // Não verificamos o Loading intermediário, pois ele pode ser otimizado.
                val errorState = awaitItem()
                assertTrue(errorState is MovieDetailUiState.Error)
                assertEquals("Erro ao carregar detalhes do filme", (errorState as MovieDetailUiState.Error).message)

                // 4. Garante que não há mais emissões de estado
                ensureAllEventsConsumed()
                // --- FIM DA CORREÇÃO ---
            }

            // Verifica se o use case foi chamado
            coVerify(exactly = 1) { getMovieDetailsUseCase(movieId) }
        }

    @Test
    fun `resetState should emit Loading state`() =
        runTest {
            // Arrange: Coloca a ViewModel em um estado de Sucesso primeiro para garantir a transição.
            val mockMovieDetail = createMockMovieDetail(1, "Filme Qualquer")
            coEvery { getMovieDetailsUseCase(1) } returns Result.success(mockMovieDetail)
            viewModel.loadMovieDetails(1)

            // Garante que a corrotina de loadMovieDetails termine antes de prosseguirmos.
            // Isso assegura que o estado da ViewModel é 'Success' antes do bloco de teste começar.
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.uiState.test {
                // 1. O estado atual já é 'Success'. Vamos consumi-lo.
                val initialState = awaitItem()
                assertTrue("O estado inicial deveria ser Success", initialState is MovieDetailUiState.Success)

                // Act: Chama a função que queremos testar.
                viewModel.resetState()

                // Assert: Verifica se o novo estado emitido é 'Loading', como esperado.
                assertEquals(MovieDetailUiState.Loading, awaitItem())

                // Garante que não há mais emissões de estado
                ensureAllEventsConsumed()
            }
        }
}

// --- Função de Apoio (Helper) ---
private fun createMockMovieDetail(
    id: Int,
    title: String,
): MovieDetail {
    return MovieDetail(
        id = id,
        title = title,
        overview = "Esta é a sinopse de um filme de teste.",
        posterPath = "/poster.jpg",
        backdropPath = "/backdrop.jpg",
        releaseDate = "2024-01-01",
        voteAverage = 8.5,
        voteCount = 1000,
        popularity = 120.0,
        runtime = 150,
        genres = listOf(Genre(1, "Ação")),
        tagline = "Uma frase de efeito para o teste.",
        budget = 100000000L,
        revenue = 500000000L,
        status = "Released",
        homepage = "http://example.com",
    )
}
