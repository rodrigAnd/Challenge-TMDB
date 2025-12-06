package com.onboarding.mychallenge.presentation.movieDetail
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onboarding.mychallenge.domain.usecase.GetMovieDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estados possíveis da UI da tela de detalhes do filme.
 */
sealed class MovieDetailUiState {
    /**
     * Estado de carregamento inicial ou de recarregamento.
     */
    data object Loading : MovieDetailUiState()

    /**
     * Estado de sucesso, contendo os detalhes completos do filme para exibição.
     *
     * @property movieDetail O [com.onboarding.mychallenge.domain.model.MovieDetail] a ser exibido.
     */
    data class Success(val movieDetail: com.onboarding.mychallenge.domain.model.MovieDetail) : MovieDetailUiState()

    /**
     * Estado de erro, contendo uma mensagem para o usuário.
     *
     * @property message A mensagem de erro a ser exibida.
     */
    data class Error(val message: String) : MovieDetailUiState()
}

/**
 * ViewModel para a tela de detalhes do filme.
 *
 * Gerencia o estado da UI para a tela de detalhes de um filme específico,
 * incluindo o carregamento dos detalhes e o tratamento de estados de carregamento,
 * sucesso e erro.
 *
 * @param getMovieDetailsUseCase UseCase para buscar detalhes completos de um filme.
 */
@HiltViewModel
class MovieDetailViewModel
    @Inject
    constructor(
        private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<MovieDetailUiState>(MovieDetailUiState.Loading)

        /**
         * O [StateFlow] que representa o estado atual da UI dos detalhes do filme.
         * Os coletores devem observar este Flow para reagir às mudanças de estado.
         */
        val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

        /**
         * Carrega os detalhes de um filme específico.
         *
         * Atualiza o [uiState] para [MovieDetailUiState.Loading] e, em seguida, para
         * [MovieDetailUiState.Success] com os detalhes do filme ou [MovieDetailUiState.Error]
         * em caso de falha.
         *
         * @param movieId O ID do filme para o qual carregar os detalhes.
         */
        fun loadMovieDetails(movieId: Int) {
            viewModelScope.launch {
                _uiState.value = MovieDetailUiState.Loading
                getMovieDetailsUseCase(movieId)
                    .onSuccess { movieDetail ->
                        _uiState.value = MovieDetailUiState.Success(movieDetail)
                    }
                    .onFailure { exception ->
                        _uiState.value =
                            MovieDetailUiState.Error(
                                exception.message ?: "Erro ao carregar detalhes do filme",
                            )
                    }
            }
        }

        /**
         * Reseta o estado da UI para [MovieDetailUiState.Loading].
         * Útil para preparar a ViewModel para um novo carregamento ou para limpar o estado.
         */
        fun resetState() {
            _uiState.value = MovieDetailUiState.Loading
        }
    }
