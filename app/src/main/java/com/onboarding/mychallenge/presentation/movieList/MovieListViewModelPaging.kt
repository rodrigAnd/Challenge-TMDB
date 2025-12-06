package com.onboarding.mychallenge.presentation.movieList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.onboarding.mychallenge.data.repository.MovieRepositoryImpl
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.usecase.AddMovieDetailToFavoritesUseCase
import com.onboarding.mychallenge.domain.usecase.AddToFavoritesUseCase
import com.onboarding.mychallenge.domain.usecase.GetFavoriteMoviesUseCase
import com.onboarding.mychallenge.domain.usecase.GetMovieDetailsUseCase
import com.onboarding.mychallenge.domain.usecase.IsFavoriteUseCase
import com.onboarding.mychallenge.domain.usecase.RemoveFromFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para a lista de filmes usando Paging 3.
 *
 * Implementa paginação usando a biblioteca Paging 3, que gerencia automaticamente
 * o carregamento de páginas, cache e estados de loading/error.
 */
@HiltViewModel
class MovieListViewModelPaging
    @Inject
    constructor(
        private val movieRepository: MovieRepositoryImpl,
        private val addToFavoritesUseCase: AddToFavoritesUseCase,
        private val addMovieDetailToFavoritesUseCase: AddMovieDetailToFavoritesUseCase,
        private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
        private val isFavoriteUseCase: IsFavoriteUseCase,
        private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase,
        private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    ) : ViewModel() {
        private val _searchQuery = MutableStateFlow("")
        val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

        private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
        val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

        private val _loadingFavoriteIds = MutableStateFlow<Set<Int>>(emptySet())
        val loadingFavoriteIds: StateFlow<Set<Int>> = _loadingFavoriteIds.asStateFlow()

        /**
         * Configuração de paginação.
         * pageSize: número de itens por página
         * enablePlaceholders: habilita placeholders durante o carregamento
         * prefetchDistance: distância antes do final da lista para pré-carregar
         */
        private val pagingConfig =
            PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 5,
            )

        /**
         * Flow de PagingData para filmes populares.
         * Usa cachedIn para manter o cache durante o ciclo de vida do ViewModel.
         */
        val popularMovies: Flow<PagingData<Movie>> =
            Pager(config = pagingConfig) {
                movieRepository.getPopularMoviesPagingSource()
            }.flow.cachedIn(viewModelScope)

        /**
         * Flow de PagingData para resultados de busca.
         * Reage a mudanças na query de busca com debounce.
         */
        @OptIn(FlowPreview::class)
        val searchMovies: Flow<PagingData<Movie>> =
            searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        popularMovies
                    } else {
                        Pager(config = pagingConfig) {
                            movieRepository.getSearchMoviesPagingSource(query)
                        }.flow.cachedIn(viewModelScope)
                    }
                }

        /**
         * Flow de PagingData para filmes.
         * O mapeamento para MovieViewObject será feito no Adapter usando os favoritos.
         */
        val movies: Flow<PagingData<Movie>> = searchMovies

        init {
            observeFavorites()
        }

        /**
         * Observa os favoritos para atualizar o estado isFavorite na lista.
         */
        private fun observeFavorites() {
            viewModelScope.launch {
                try {
                    getFavoriteMoviesUseCase()
                        .collect { favorites ->
                            _favoriteIds.value = favorites.map { it.id }.toSet()
                        }
                } catch (e: Exception) {
                    // Erro ao observar favoritos - falha silenciosa
                }
            }
        }

        /**
         * Atualiza a query de pesquisa.
         * O debounce será aplicado automaticamente.
         */
        fun updateSearchQuery(query: String) {
            _searchQuery.value = query
        }

        /**
         * Alterna favorito de um filme.
         * Busca detalhes completos antes de favoritar para salvar no Room.
         */
        fun toggleFavorite(movie: MovieViewObject) {
            viewModelScope.launch {
                try {
                    _loadingFavoriteIds.update { it + movie.id }
                    val isFavoriteResult = isFavoriteUseCase(movie.id)
                    isFavoriteResult.onSuccess { isFavorite ->
                        if (isFavorite) {
                            removeFromFavoritesUseCase(movie.id)
                        } else {
                            val detailsResult = getMovieDetailsUseCase(movie.id)
                            detailsResult.onSuccess { movieDetail ->
                                addMovieDetailToFavoritesUseCase(movieDetail)
                                    .onFailure {
                                        val domainMovie = movie.toDomain()
                                        addToFavoritesUseCase(domainMovie)
                                    }
                            }.onFailure {
                                val domainMovie = movie.toDomain()
                                addToFavoritesUseCase(domainMovie)
                            }
                        }
                    }
                    _loadingFavoriteIds.update { it - movie.id }
                } catch (e: Exception) {
                    _loadingFavoriteIds.update { it - movie.id }
                }
            }
        }

        /**
         * Adiciona um filme aos favoritos pelo ID.
         */
        fun addToFavorites(movieId: Int) {
            viewModelScope.launch {
                try {
                    val detailsResult = getMovieDetailsUseCase(movieId)
                    detailsResult.onSuccess { movieDetail ->
                        addMovieDetailToFavoritesUseCase(movieDetail)
                            .onFailure {
                                // Fallback para adicionar sem detalhes completos
                            }
                    }.onFailure {
                        // Erro ao buscar detalhes
                    }
                } catch (e: Exception) {
                    // Erro ao adicionar favorito
                }
            }
        }

        /**
         * Remove um filme dos favoritos pelo ID.
         */
        fun removeFromFavorites(movieId: Int) {
            viewModelScope.launch {
                removeFromFavoritesUseCase(movieId)
            }
        }
    }

/**
 * Extension function para converter MovieViewObject para Domain Model.
 */
private fun MovieViewObject.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterUrl.substringAfterLast("/").takeIf { posterUrl.isNotEmpty() },
        backdropPath = backdropUrl.substringAfterLast("/").takeIf { backdropUrl.isNotEmpty() },
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
    )
}
