package com.onboarding.mychallenge.presentation.favorites
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel
    @Inject
    constructor(
        private val movieRepository: MovieRepository,
    ) : ViewModel() {
        companion object {
            private const val SEARCH_DEBOUNCE_MS = 500L
        }

        private val searchQuery = MutableStateFlow("")
        private var isFirstEmission = true
        private val loadingFavoriteIds = MutableStateFlow<Set<Int>>(emptySet())
        private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
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
                            old.size == new.size && old.map { it.id }.toSet() == new.map { it.id }.toSet()
                        }
                        .collect { filteredMovies ->
                            try {
                                val currentLoadingIds = loadingFavoriteIds.value
                                if (filteredMovies.isEmpty()) {
                                    if (_uiState.value !is FavoritesUiState.Empty) {
                                        _uiState.value = FavoritesUiState.Empty
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
                                                        viewObjects.find { it.id == movie.id }?.isLoadingFavorite != movie.isLoadingFavorite
                                                    }
                                            )
                                    if (shouldUpdate) {
                                        _uiState.value = FavoritesUiState.Success(viewObjects)
                                    }
                                }
                            } catch (e: Exception) {
                                _uiState.value =
                                    FavoritesUiState.Error(
                                        e.message ?: "Erro ao carregar favoritos",
                                    )
                            }
                        }
                } catch (e: Exception) {
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

        fun updateSearchQuery(query: String) {
            searchQuery.value = query
        }

        fun removeFromFavorites(movieId: Int) {
            viewModelScope.launch {
                try {
                    loadingFavoriteIds.update { it + movieId }
                    movieRepository.removeFromFavorites(movieId)
                    loadingFavoriteIds.update { it - movieId }
                } catch (e: Exception) {
                    loadingFavoriteIds.update { it - movieId }
                    _uiState.value =
                        FavoritesUiState.Error(
                            e.message ?: "Erro ao remover dos favoritos",
                        )
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
                }
            }
        }
    }

sealed class FavoritesUiState {
    data object Loading : FavoritesUiState()

    data class Success(val movies: List<MovieViewObject>) : FavoritesUiState()

    data class Error(val message: String) : FavoritesUiState()

    data object Empty : FavoritesUiState()
}
