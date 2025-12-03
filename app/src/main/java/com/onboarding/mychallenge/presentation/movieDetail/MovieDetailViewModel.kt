package com.onboarding.mychallenge.presentation.movieDetail

import android.util.Log
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
 * Estado da UI para detalhes do filme
 */
sealed class MovieDetailUiState {
    data object Loading : MovieDetailUiState()
    data class Success(val movieDetail: com.onboarding.mychallenge.domain.model.MovieDetail) : MovieDetailUiState()
    data class Error(val message: String) : MovieDetailUiState()
}

/**
 * ViewModel para detalhes do filme
 */
@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase
) : ViewModel() {
    
    companion object {
        private const val TAG = "MovieDetailViewModel"
    }
    
    private val _uiState = MutableStateFlow<MovieDetailUiState>(MovieDetailUiState.Loading)
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()
    
    /**
     * Carrega os detalhes do filme
     * Busca da API (os favoritos são salvos com detalhes completos no Room)
     */
    fun loadMovieDetails(movieId: Int) {
        Log.d(TAG, "loadMovieDetails: Carregando detalhes do filme $movieId")
        viewModelScope.launch {
            _uiState.value = MovieDetailUiState.Loading
            
            getMovieDetailsUseCase(movieId)
                .onSuccess { movieDetail ->
                    Log.d(TAG, "loadMovieDetails: Sucesso ao carregar detalhes")
                    _uiState.value = MovieDetailUiState.Success(movieDetail)
                }
                .onFailure { exception ->
                    Log.e(TAG, "loadMovieDetails: Erro - ${exception.message}", exception)
                    _uiState.value = MovieDetailUiState.Error(
                        exception.message ?: "Erro ao carregar detalhes do filme"
                    )
                }
        }
    }
    
    /**
     * Reseta o estado
     */
    fun resetState() {
        _uiState.value = MovieDetailUiState.Loading
    }
}

