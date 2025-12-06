package com.onboarding.mychallenge.presentation.movieList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onboarding.mychallenge.domain.usecase.AddMovieDetailToFavoritesUseCase
import com.onboarding.mychallenge.domain.usecase.AddToFavoritesUseCase
import com.onboarding.mychallenge.domain.usecase.GetFavoriteMoviesUseCase
import com.onboarding.mychallenge.domain.usecase.GetMovieDetailsUseCase
import com.onboarding.mychallenge.domain.usecase.GetPopularMoviesUseCase
import com.onboarding.mychallenge.domain.usecase.IsFavoriteUseCase
import com.onboarding.mychallenge.domain.usecase.RemoveFromFavoritesUseCase
import com.onboarding.mychallenge.domain.usecase.SearchMoviesUseCase
import com.onboarding.mychallenge.presentation.mapper.toViewObjectList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado da UI para lista de filmes
 */
sealed class MovieListUiState {
    data object Loading : MovieListUiState()

    data class Success(
        val movies: List<MovieViewObject>,
        val isSearch: Boolean = false,
        val currentPage: Int = 1,
        val canLoadMore: Boolean = true,
        val isLoadingMore: Boolean = false,
    ) : MovieListUiState()

    data class Error(val message: String) : MovieListUiState()

    data object Empty : MovieListUiState()
}

/**
 * ViewModel para a lista de filmes com debounce na pesquisa
 * Implementa Single Source of Truth e tratamento robusto de erros
 */
@HiltViewModel
class MovieListViewModel
    @Inject
    constructor(
        private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
        private val searchMoviesUseCase: SearchMoviesUseCase,
        private val addToFavoritesUseCase: AddToFavoritesUseCase,
        private val addMovieDetailToFavoritesUseCase: AddMovieDetailToFavoritesUseCase,
        private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
        private val isFavoriteUseCase: IsFavoriteUseCase,
        private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase,
        private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)
        val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()
        private val _searchQuery = MutableStateFlow("")
        val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
        private val favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
        private val loadingFavoriteIds = MutableStateFlow<Set<Int>>(emptySet())
        private var currentPage = 1
        private var isSearchMode = false

        init {
            loadPopularMovies()
            observeSearchQuery()
            observeFavorites()
        }

        /**
         * Observa a query de pesquisa com debounce
         * Só busca quando o usuário para de digitar por 500ms
         */
        @OptIn(FlowPreview::class)
        private fun observeSearchQuery() {
            viewModelScope.launch {
                searchQuery
                    .debounce(500)
                    .distinctUntilChanged()
                    .collect { query ->
                        if (query.isBlank()) {
                            isSearchMode = false
                            currentPage = 1
                            loadPopularMovies()
                        } else {
                            isSearchMode = true
                            currentPage = 1
                            searchMovies(query)
                        }
                    }
            }
        }

        /**
         * Observa os favoritos para atualizar o estado isFavorite na lista
         */
        private fun observeFavorites() {
            viewModelScope.launch {
                try {
                    getFavoriteMoviesUseCase()
                        .collect { favorites ->
                            val newFavoriteIds = favorites.map { it.id }.toSet()
                            val oldFavoriteIds = favoriteIds.value
                            favoriteIds.value = newFavoriteIds
                            if (oldFavoriteIds != newFavoriteIds) {
                                updateFavoriteStates()
                            }
                        }
                } catch (e: Exception) {
                }
            }
        }

        /**
         * Atualiza os estados de favorito na lista atual
         */
        private fun updateFavoriteStates() {
            val currentState = _uiState.value
            if (currentState is MovieListUiState.Success) {
                val favoriteIdsSet = favoriteIds.value
                val loadingIdsSet = loadingFavoriteIds.value
                val updatedMovies =
                    currentState.movies.map { movie ->
                        val isFavorite = favoriteIdsSet.contains(movie.id)
                        val isLoading = loadingIdsSet.contains(movie.id)
                        movie.copy(
                            isFavorite = isFavorite,
                            isLoadingFavorite = isLoading,
                        )
                    }
                val hasChanged =
                    currentState.movies.zip(updatedMovies).any { (old, new) ->
                        old.isFavorite != new.isFavorite || old.isLoadingFavorite != new.isLoadingFavorite
                    }
                if (hasChanged) {
                    _uiState.value = currentState.copy(movies = updatedMovies)
                }
            }
        }

        /**
         * Atualiza a query de pesquisa
         * O debounce será aplicado automaticamente
         */
        fun updateSearchQuery(query: String) {
            _searchQuery.value = query
        }

        /**
         * Carrega filmes populares
         */
        fun loadPopularMovies(page: Int = 1) {
            viewModelScope.launch {
                _uiState.update { MovieListUiState.Loading }
                getPopularMoviesUseCase(page)
                    .onSuccess { movies ->
                        currentPage = page
                        if (movies.isEmpty()) {
                            _uiState.update { MovieListUiState.Empty }
                        } else {
                            val viewObjects = movies.toViewObjectList(favoriteIds.value, loadingFavoriteIds.value)
                            _uiState.update {
                                MovieListUiState.Success(
                                    movies = viewObjects,
                                    isSearch = false,
                                    currentPage = page,
                                    canLoadMore = page < 500,
                                    isLoadingMore = false,
                                )
                            }
                        }
                    }
                    .onFailure { exception ->
                        _uiState.update {
                            MovieListUiState.Error(
                                exception.message ?: "Erro ao carregar filmes populares",
                            )
                        }
                    }
            }
        }

        /**
         * Busca filmes por termo
         */
        private fun searchMovies(
            query: String,
            page: Int = 1,
        ) {
            viewModelScope.launch {
                _uiState.update { MovieListUiState.Loading }
                searchMoviesUseCase(query, page)
                    .onSuccess { movies ->
                        currentPage = page
                        if (movies.isEmpty()) {
                            _uiState.update { MovieListUiState.Empty }
                        } else {
                            val viewObjects = movies.toViewObjectList(favoriteIds.value, loadingFavoriteIds.value)
                            _uiState.update {
                                MovieListUiState.Success(
                                    movies = viewObjects,
                                    isSearch = true,
                                    currentPage = page,
                                    canLoadMore = page < 500,
                                    isLoadingMore = false,
                                )
                            }
                        }
                    }
                    .onFailure { exception ->
                        _uiState.update {
                            MovieListUiState.Error(
                                exception.message ?: "Erro ao buscar filmes",
                            )
                        }
                    }
            }
        }

        /**
         * Carrega próxima página (paginação)
         */
        fun loadNextPage() {
            val currentState = _uiState.value
            if (currentState !is MovieListUiState.Success || !currentState.canLoadMore || currentState.isLoadingMore) {
                return
            }
            val query = searchQuery.value
            val nextPage = currentState.currentPage + 1
            _uiState.update {
                currentState.copy(isLoadingMore = true)
            }
            viewModelScope.launch {
                val result =
                    if (query.isBlank()) {
                        getPopularMoviesUseCase(nextPage)
                    } else {
                        searchMoviesUseCase(query, nextPage)
                    }
                result
                    .onSuccess { newMovies ->
                        currentPage = nextPage
                        val newViewObjects = newMovies.toViewObjectList(favoriteIds.value, loadingFavoriteIds.value)
                        _uiState.update {
                            currentState.copy(
                                movies = currentState.movies + newViewObjects,
                                currentPage = nextPage,
                                isLoadingMore = false,
                                canLoadMore = newMovies.isNotEmpty() && nextPage < 500,
                            )
                        }
                    }
                    .onFailure { exception ->
                        _uiState.update {
                            currentState.copy(isLoadingMore = false)
                        }
                    }
            }
        }

        /**
         * Retry - tenta carregar novamente
         */
        fun retry() {
            val query = searchQuery.value
            if (query.isBlank()) {
                loadPopularMovies()
            } else {
                searchMovies(query)
            }
        }

        /**
         * Alterna favorito de um filme
         * Single Source of Truth: atualiza o banco, a UI observa via Flow
         * Busca detalhes completos antes de favoritar para salvar no Room
         * Mostra loading durante o processo
         */
        fun toggleFavorite(movie: MovieViewObject) {
            viewModelScope.launch {
                try {
                    loadingFavoriteIds.update { it + movie.id }
                    updateFavoriteStates()
                    val isFavoriteResult = isFavoriteUseCase(movie.id)
                    isFavoriteResult.onSuccess { isFavorite ->
                        if (isFavorite) {
                            removeFromFavoritesUseCase(movie.id)
                                .onSuccess {
                                }
                                .onFailure { exception ->
                                }
                        } else {
                            val detailsResult = getMovieDetailsUseCase(movie.id)
                            detailsResult.onSuccess { movieDetail ->
                                addMovieDetailToFavoritesUseCase(movieDetail)
                                    .onSuccess { }
                                    .onFailure { exception ->
                                        val domainMovie = movie.toDomain()
                                        addToFavoritesUseCase(domainMovie)
                                            .onFailure { }
                                    }
                            }.onFailure { exception ->
                                val domainMovie = movie.toDomain()
                                addToFavoritesUseCase(domainMovie)
                                    .onFailure { }
                            }
                        }
                    }.onFailure { exception ->
                    }
                    loadingFavoriteIds.update { it - movie.id }
                    updateFavoriteStates()
                } catch (e: Exception) {
                    loadingFavoriteIds.update { it - movie.id }
                    updateFavoriteStates()
                }
            }
        }

        /**
         * Adiciona um filme aos favoritos pelo ID
         * Busca detalhes completos antes de favoritar para salvar no Room
         */
        fun addToFavorites(movieId: Int) {
            viewModelScope.launch {
                try {
                    val detailsResult = getMovieDetailsUseCase(movieId)
                    detailsResult.onSuccess { movieDetail ->
                        addMovieDetailToFavoritesUseCase(movieDetail)
                            .onFailure { exception ->
                                val currentState = _uiState.value
                                if (currentState is MovieListUiState.Success) {
                                    val movie = currentState.movies.find { it.id == movieId }
                                    if (movie != null) {
                                        val domainMovie = movie.toDomain()
                                        addToFavoritesUseCase(domainMovie)
                                            .onFailure { }
                                    }
                                }
                            }
                    }.onFailure { exception ->
                        val currentState = _uiState.value
                        if (currentState is MovieListUiState.Success) {
                            val movie = currentState.movies.find { it.id == movieId }
                            if (movie != null) {
                                val domainMovie = movie.toDomain()
                                addToFavoritesUseCase(domainMovie)
                                    .onFailure { }
                            }
                        }
                    }
                } catch (e: Exception) {
                }
            }
        }

        /**
         * Remove um filme dos favoritos pelo ID
         */
        fun removeFromFavorites(movieId: Int) {
            viewModelScope.launch {
                removeFromFavoritesUseCase(movieId)
                    .onFailure { exception ->
                    }
            }
        }
    }

/**
 * Extension function para converter MovieViewObject para Domain Model
 */
private fun MovieViewObject.toDomain(): com.onboarding.mychallenge.domain.model.Movie {
    return com.onboarding.mychallenge.domain.model.Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterUrl.substringAfterLast("/").takeIf { posterUrl.isNotEmpty() },
        backdropPath = backdropUrl.substringAfterLast("/").takeIf { backdropUrl.isNotEmpty() },
        releaseDate = releaseDate,
        voteAverage = rating,
        voteCount = voteCount,
        popularity = popularity,
    )
}
