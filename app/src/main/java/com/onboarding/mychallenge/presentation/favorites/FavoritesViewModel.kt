package com.onboarding.mychallenge.presentation.favorites
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onboarding.mychallenge.domain.repository.MovieRepository
import com.onboarding.mychallenge.presentation.mapper.toViewObject
import com.onboarding.mychallenge.presentation.movieList.MovieViewObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para a tela de filmes favoritos.
 *
 * Gerencia o estado da UI para a tela de favoritos, incluindo:
 * - Carregamento e observação em tempo real da lista de filmes favoritos.
 * - Pesquisa de filmes dentro da lista de favoritos com debounce.
 * - Remoção de filmes dos favoritos.
 * - Tratamento de estados de carregamento, sucesso, erro e vazio.
 *
 * @param movieRepository O repositório de filmes para acessar os dados de favoritos.
 */
@HiltViewModel
class FavoritesViewModel
    @Inject
    constructor(
        private val movieRepository: MovieRepository,
    ) : ViewModel() {
        companion object {
            private const val TAG = "FavoritesViewModel"
            private const val SEARCH_DEBOUNCE_MS = 500L
        }

        private val searchQuery = MutableStateFlow("")
        private var isFirstEmission = true
        private val loadingFavoriteIds = MutableStateFlow<Set<Int>>(emptySet())
        private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)

        /**
         * O [StateFlow] que representa o estado atual da UI da lista de favoritos.
         * Os coletores devem observar este Flow para reagir às mudanças de estado.
         */
        val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

        init {
            viewModelScope.launch {
                try {
                    searchQuery
                        .onStart {
                            isFirstEmission = true
                            emit(searchQuery.value)
                        }
                        .distinctUntilChanged()
                        .flatMapLatest { query ->
                            val isFirst = isFirstEmission
                            if (isFirst) {
                                isFirstEmission = false
                            }
                            val queryFlow =
                                if (isFirst) {
                                    flowOf(query)
                                } else {
                                    flowOf(query).debounce(SEARCH_DEBOUNCE_MS)
                                }
                            queryFlow.flatMapLatest { finalQuery ->
                                movieRepository.getFavoriteMovies()
                                    .map { favorites ->
                                        if (finalQuery.isBlank()) {
                                            favorites
                                        } else {
                                            val filtered =
                                                favorites.filter { movie ->
                                                    movie.title.contains(finalQuery, ignoreCase = true) ||
                                                        movie.overview.contains(finalQuery, ignoreCase = true)
                                                }
                                            filtered
                                        }
                                    }
                            }
                        }
                        .distinctUntilChanged { old, new ->
                            val isEqual = old.size == new.size && old.map { it.id }.toSet() == new.map { it.id }.toSet()
                            if (!isEqual) {
                            }
                            isEqual
                        }
                        .collect { filteredMovies ->
                            try {
                                val currentLoadingIds = loadingFavoriteIds.value
                                if (filteredMovies.isEmpty()) {
                                    if (_uiState.value !is FavoritesUiState.Empty) {
                                        _uiState.value = FavoritesUiState.Empty
                                    } else {
                                    }
                                } else {
                                    val viewObjects =
                                        filteredMovies.map { movie ->
                                            movie.toViewObject(
                                                isFavorite = true,
                                                isLoadingFavorite = currentLoadingIds.contains(movie.id),
                                            )
                                        }
                                    val currentState = _uiState.value
                                    if (currentState is FavoritesUiState.Loading) {
                                        if (viewObjects.isEmpty()) {
                                            _uiState.value = FavoritesUiState.Empty
                                        } else {
                                            _uiState.value = FavoritesUiState.Success(viewObjects)
                                        }
                                        return@collect
                                    }
                                    val currentMovieIds =
                                        if (currentState is FavoritesUiState.Success) {
                                            currentState.movies.map { it.id }.toSet()
                                        } else {
                                            emptySet()
                                        }
                                    val newMovieIds = viewObjects.map { it.id }.toSet()
                                    val shouldUpdate =
                                        currentState !is FavoritesUiState.Success ||
                                            currentMovieIds != newMovieIds ||
                                            (
                                                currentState is FavoritesUiState.Success &&
                                                    currentState.movies.any { movie ->
                                                        viewObjects.find { it.id == movie.id }
                                                            ?.isLoadingFavorite != movie.isLoadingFavorite
                                                    }
                                            )
                                    if (shouldUpdate) {
                                        _uiState.value = FavoritesUiState.Success(viewObjects)
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "init: Erro ao processar favoritos", e)
                                _uiState.value =
                                    FavoritesUiState.Error(
                                        e.message ?: "Erro ao carregar favoritos",
                                    )
                            }
                        }
                } catch (e: Exception) {
                    Log.e(TAG, "init: Erro ao observar favoritos", e)
                    _uiState.value =
                        FavoritesUiState.Error(
                            e.message ?: "Erro ao carregar favoritos",
                        )
                }
            }
            viewModelScope.launch {
                loadingFavoriteIds.collect { loadingIds ->
                    updateFavoriteStates()
                }
            }
        }

        /**
         * Inicia o carregamento dos filmes favoritos.
         *
         * Como a observação dos favoritos já é iniciada no `init` da ViewModel,
         * este método serve principalmente para sinalizar a intenção de carregar
         * e garantir que o Flow esteja ativo.
         */
        fun loadFavorites() {
        }

        /**
         * Atualiza a query de pesquisa para filtrar a lista de favoritos.
         *
         * A lógica de debounce e o filtro serão aplicados automaticamente
         * através da observação do [searchQuery].
         *
         * @param query A nova string de pesquisa.
         */
        fun updateSearchQuery(query: String) {
            searchQuery.value = query
        }

        /**
         * Remove um filme dos favoritos.
         *
         * Marca o filme como [isLoadingFavorite] na UI durante o processo de remoção
         * e atualiza o estado da UI após a conclusão ou em caso de erro.
         *
         * @param movieId O ID do filme a ser removido.
         */
        fun removeFromFavorites(movieId: Int) {
            viewModelScope.launch {
                try {
                    loadingFavoriteIds.update { it + movieId }
                    movieRepository.removeFromFavorites(movieId)
                    loadingFavoriteIds.update { it - movieId }
                } catch (e: Exception) {
                    Log.e(TAG, "removeFromFavorites: Erro ao remover favorito $movieId", e)
                    loadingFavoriteIds.update { it - movieId }
                    _uiState.value =
                        FavoritesUiState.Error(
                            e.message ?: "Erro ao remover dos favoritos",
                        )
                    Log.e(TAG, "removeFromFavorites: Estado atualizado para Error")
                }
            }
        }

        private fun updateFavoriteStates() {
            val currentState = _uiState.value
            if (currentState is FavoritesUiState.Success) {
                val currentLoadingIds = loadingFavoriteIds.value
                val updatedMovies =
                    currentState.movies.map { movie ->
                        movie.copy(
                            isLoadingFavorite = currentLoadingIds.contains(movie.id),
                        )
                    }
                val hasChanged =
                    currentState.movies.zip(updatedMovies).any { (old, new) ->
                        old.isLoadingFavorite != new.isLoadingFavorite
                    }
                if (hasChanged) {
                    _uiState.value = currentState.copy(movies = updatedMovies)
                } else {
                }
            } else {
                // Estado atual não é Success, ignorando atualização
            }
        }
    }

/**
 * Estados possíveis da UI da tela de favoritos.
 */
sealed class FavoritesUiState {
    /**
     * Estado de carregamento inicial ou de recarregamento.
     */
    data object Loading : FavoritesUiState()

    /**
     * Estado de sucesso, contendo a lista de filmes favoritos para exibição.
     *
     * @property movies A lista de [MovieViewObject] a serem exibidos.
     */
    data class Success(val movies: List<MovieViewObject>) : FavoritesUiState()

    /**
     * Estado de erro, contendo uma mensagem para o usuário.
     *
     * @property message A mensagem de erro a ser exibida.
     */
    data class Error(val message: String) : FavoritesUiState()

    /**
     * Estado de lista vazia, indicando que não há filmes favoritos para exibir.
     */
    data object Empty : FavoritesUiState()
}
