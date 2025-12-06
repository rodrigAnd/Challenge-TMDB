package com.onboarding.mychallenge.presentation.favorites
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.repository.MovieRepository
import com.onboarding.mychallenge.presentation.movieList.MovieViewObject
import com.onboarding.mychallenge.presentation.mapper.toViewObject
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
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {
    companion object {
        private const val TAG = "FavoritesViewModel"
        private const val SEARCH_DEBOUNCE_MS = 500L
    }
    private val searchQuery = MutableStateFlow("")
    private var isFirstEmission = true
    private val loadingFavoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()
    init {
        Log.d(TAG, "init: Inicializando FavoritesViewModel")
        viewModelScope.launch {
            try {
                Log.d(TAG, "init: Iniciando observação de favoritos e pesquisa")
                searchQuery
                    .onStart {
                        Log.d(TAG, "init: Flow iniciado, emitindo valor inicial imediatamente: '${searchQuery.value}'")
                        isFirstEmission = true
                        emit(searchQuery.value)
                    }
                    .distinctUntilChanged()
                    .flatMapLatest { query ->
                        val isFirst = isFirstEmission
                        if (isFirst) {
                            isFirstEmission = false
                            Log.d(TAG, "init: Primeira emissão (sem debounce): '$query'")
                        } else {
                            Log.d(TAG, "init: Query de pesquisa alterada: '$query' - aplicando debounce de ${SEARCH_DEBOUNCE_MS}ms")
                        }
                        val queryFlow = if (isFirst) {
                            flowOf(query)
                        } else {
                            flowOf(query).debounce(SEARCH_DEBOUNCE_MS)
                        }
                        queryFlow.flatMapLatest { finalQuery ->
                            Log.d(TAG, "init: Processando query: '$finalQuery'")
                            movieRepository.getFavoriteMovies()
                                .map { favorites ->
                                    Log.d(TAG, "init: Recebidos ${favorites.size} favoritos do repositório")
                                    if (finalQuery.isBlank()) {
                                        favorites
                                    } else {
                                        val filtered = favorites.filter { movie ->
                                            movie.title.contains(finalQuery, ignoreCase = true) ||
                                            movie.overview.contains(finalQuery, ignoreCase = true)
                                        }
                                        Log.d(TAG, "init: Após filtro: ${filtered.size} filmes")
                                        filtered
                                    }
                                }
                        }
                    }
                    .distinctUntilChanged { old, new ->
                        val isEqual = old.size == new.size && old.map { it.id }.toSet() == new.map { it.id }.toSet()
                        if (!isEqual) {
                            Log.d(TAG, "init: Lista de favoritos mudou: ${old.size} -> ${new.size}")
                        }
                        isEqual
                    }
                    .collect { filteredMovies ->
                        try {
                            Log.d(TAG, "init: Processando ${filteredMovies.size} filmes filtrados")
                            val currentLoadingIds = loadingFavoriteIds.value
                            Log.d(TAG, "init: IDs em loading: $currentLoadingIds")
                            if (filteredMovies.isEmpty()) {
                                Log.d(TAG, "init: Lista vazia, atualizando para Empty")
                                if (_uiState.value !is FavoritesUiState.Empty) {
                                    _uiState.value = FavoritesUiState.Empty
                                    Log.d(TAG, "init: Estado atualizado para Empty")
                                } else {
                                    Log.d(TAG, "init: Já está em Empty, ignorando atualização")
                                }
                            } else {
                                val viewObjects = filteredMovies.map { movie ->
                                    movie.toViewObject(
                                        isFavorite = true,
                                        isLoadingFavorite = currentLoadingIds.contains(movie.id)
                                    )
                                }
                                Log.d(TAG, "init: Criados ${viewObjects.size} ViewObjects")
                                val currentState = _uiState.value
                                if (currentState is FavoritesUiState.Loading) {
                                    Log.d(TAG, "init: Estado atual é Loading, atualizando para Success/Empty")
                                    if (viewObjects.isEmpty()) {
                                        _uiState.value = FavoritesUiState.Empty
                                    } else {
                                        _uiState.value = FavoritesUiState.Success(viewObjects)
                                    }
                                    return@collect
                                }
                                val currentMovieIds = if (currentState is FavoritesUiState.Success) {
                                    currentState.movies.map { it.id }.toSet()
                                } else {
                                    emptySet()
                                }
                                val newMovieIds = viewObjects.map { it.id }.toSet()
                                Log.d(TAG, "init: Comparando IDs - Atual: $currentMovieIds, Novo: $newMovieIds")
                                val shouldUpdate = currentState !is FavoritesUiState.Success ||
                                    currentMovieIds != newMovieIds ||
                                    (currentState is FavoritesUiState.Success && currentState.movies.any { movie ->
                                        viewObjects.find { it.id == movie.id }?.isLoadingFavorite != movie.isLoadingFavorite
                                    })
                                if (shouldUpdate) {
                                    Log.d(TAG, "init: Atualizando estado para Success com ${viewObjects.size} filmes")
                                    _uiState.value = FavoritesUiState.Success(viewObjects)
                                } else {
                                    Log.d(TAG, "init: Estado não mudou, ignorando atualização")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "init: Erro ao processar favoritos", e)
                            _uiState.value = FavoritesUiState.Error(
                                e.message ?: "Erro ao carregar favoritos"
                            )
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "init: Erro ao observar favoritos", e)
                _uiState.value = FavoritesUiState.Error(
                    e.message ?: "Erro ao carregar favoritos"
                )
            }
        }
        viewModelScope.launch {
            Log.d(TAG, "init: Iniciando observação de loadingFavoriteIds")
            loadingFavoriteIds.collect { loadingIds ->
                Log.d(TAG, "init: loadingFavoriteIds mudou: $loadingIds")
                updateFavoriteStates()
            }
        }
        Log.d(TAG, "init: FavoritesViewModel inicializado")
    }
    fun loadFavorites() {
        Log.d(TAG, "loadFavorites: Carregando favoritos")
        Log.d(TAG, "loadFavorites: Flow já está observando, aguardando emissão")
    }
    fun updateSearchQuery(query: String) {
        Log.d(TAG, "updateSearchQuery: Atualizando query para: '$query'")
        searchQuery.value = query
        Log.d(TAG, "updateSearchQuery: Query atualizada")
    }
    fun removeFromFavorites(movieId: Int) {
        Log.d(TAG, "removeFromFavorites: Iniciando remoção do filme $movieId")
        viewModelScope.launch {
            try {
                Log.d(TAG, "removeFromFavorites: Adicionando $movieId à lista de loading")
                loadingFavoriteIds.update { it + movieId }
                Log.d(TAG, "removeFromFavorites: loadingFavoriteIds atualizado: ${loadingFavoriteIds.value}")
                Log.d(TAG, "removeFromFavorites: Chamando repository.removeFromFavorites($movieId)")
                movieRepository.removeFromFavorites(movieId)
                Log.d(TAG, "removeFromFavorites: Filme $movieId removido dos favoritos com sucesso")
                Log.d(TAG, "removeFromFavorites: Removendo $movieId da lista de loading")
                loadingFavoriteIds.update { it - movieId }
                Log.d(TAG, "removeFromFavorites: loadingFavoriteIds atualizado: ${loadingFavoriteIds.value}")
                Log.d(TAG, "removeFromFavorites: Remoção concluída com sucesso")
            } catch (e: Exception) {
                Log.e(TAG, "removeFromFavorites: Erro ao remover favorito $movieId", e)
                loadingFavoriteIds.update { it - movieId }
                _uiState.value = FavoritesUiState.Error(
                    e.message ?: "Erro ao remover dos favoritos"
                )
                Log.e(TAG, "removeFromFavorites: Estado atualizado para Error")
            }
        }
    }
    private fun updateFavoriteStates() {
        Log.d(TAG, "updateFavoriteStates: Verificando se precisa atualizar estados")
        val currentState = _uiState.value
        if (currentState is FavoritesUiState.Success) {
            val currentLoadingIds = loadingFavoriteIds.value
            Log.d(TAG, "updateFavoriteStates: Estado atual é Success com ${currentState.movies.size} filmes")
            Log.d(TAG, "updateFavoriteStates: IDs em loading: $currentLoadingIds")
            val updatedMovies = currentState.movies.map { movie ->
                movie.copy(
                    isLoadingFavorite = currentLoadingIds.contains(movie.id)
                )
            }
            val hasChanged = currentState.movies.zip(updatedMovies).any { (old, new) ->
                old.isLoadingFavorite != new.isLoadingFavorite
            }
            if (hasChanged) {
                Log.d(TAG, "updateFavoriteStates: Estado de loading mudou, atualizando UI")
                _uiState.value = currentState.copy(movies = updatedMovies)
                Log.d(TAG, "updateFavoriteStates: Estado atualizado")
            } else {
                Log.d(TAG, "updateFavoriteStates: Nenhuma mudança detectada, ignorando atualização")
            }
        } else {
            Log.d(TAG, "updateFavoriteStates: Estado atual não é Success (é ${currentState::class.simpleName}), ignorando")
        }
    }
}
sealed class FavoritesUiState {
    data object Loading : FavoritesUiState()
    data class Success(val movies: List<MovieViewObject>) : FavoritesUiState()
    data class Error(val message: String) : FavoritesUiState()
    data object Empty : FavoritesUiState()
}
