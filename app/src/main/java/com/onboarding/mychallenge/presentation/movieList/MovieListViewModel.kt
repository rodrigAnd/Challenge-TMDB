package com.onboarding.mychallenge.presentation.movieList
import android.util.Log
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
 * Estados possíveis da UI da lista de filmes.
 */
sealed class MovieListUiState {
    /**
     * Estado de carregamento inicial ou de recarregamento.
     */
    data object Loading : MovieListUiState()

    /**
     * Estado de sucesso, contendo a lista de filmes para exibição.
     *
     * @property movies A lista de [MovieViewObject] a serem exibidos.
     * @property isSearch Indica se a lista atual é resultado de uma pesquisa.
     * @property currentPage A página atual que está sendo exibida.
     * @property canLoadMore Indica se há mais páginas de filmes para carregar.
     * @property isLoadingMore Indica se uma nova página está sendo carregada no momento.
     */
    data class Success(
        val movies: List<MovieViewObject>,
        val isSearch: Boolean = false,
        val currentPage: Int = 1,
        val canLoadMore: Boolean = true,
        val isLoadingMore: Boolean = false
    ) : MovieListUiState()

    /**
     * Estado de erro, contendo uma mensagem para o usuário.
     *
     * @property message A mensagem de erro a ser exibida.
     */
    data class Error(val message: String) : MovieListUiState()

    /**
     * Estado de lista vazia, indicando que não há filmes para exibir.
     */
    data object Empty : MovieListUiState()
}

/**
 * ViewModel para a lista de filmes.
 *
 * Gerencia o estado da UI para a tela de lista de filmes, incluindo:
 * - Carregamento de filmes populares.
 * - Pesquisa de filmes com debounce.
 * - Paginação.
 * - Gerenciamento de favoritos (adicionar/remover).
 * - Tratamento de estados de carregamento, sucesso, erro e vazio.
 *
 * Implementa Single Source of Truth para o estado da UI e tratamento robusto de erros.
 *
 * @param getPopularMoviesUseCase UseCase para buscar filmes populares.
 * @param searchMoviesUseCase UseCase para buscar filmes por termo de pesquisa.
 * @param addToFavoritesUseCase UseCase para adicionar um filme aos favoritos (modelo básico).
 * @param addMovieDetailToFavoritesUseCase UseCase para adicionar um filme aos favoritos (detalhes completos).
 * @param removeFromFavoritesUseCase UseCase para remover um filme dos favoritos.
 * @param isFavoriteUseCase UseCase para verificar se um filme é favorito.
 * @param getFavoriteMoviesUseCase UseCase para obter a lista de filmes favoritos.
 * @param getMovieDetailsUseCase UseCase para obter detalhes completos de um filme.
 */
@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val addMovieDetailToFavoritesUseCase: AddMovieDetailToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase
) : ViewModel() {
    companion object {
        private const val TAG = "MovieListViewModel"
    }

    private val _uiState = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)

    /**
     * O [StateFlow] que representa o estado atual da UI da lista de filmes.
     * Os coletores devem observar este Flow para reagir às mudanças de estado.
     */
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    /**
     * O [StateFlow] que representa a query de pesquisa atual.
     * As atualizações neste Flow são automaticamente "debounced" para evitar
     * chamadas excessivas à API.
     */
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    private val loadingFavoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    private var currentPage = 1
    private var isSearchMode = false
    init {
        Log.d(TAG, "init: ViewModel inicializado")
        loadPopularMovies()
        observeSearchQuery()
        observeFavorites()
    }
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
    private fun observeFavorites() {
        Log.d(TAG, "observeFavorites: Iniciando observação de favoritos")
        viewModelScope.launch {
            try {
                getFavoriteMoviesUseCase()
                    .collect { favorites ->
                        val newFavoriteIds = favorites.map { it.id }.toSet()
                        Log.d(TAG, "observeFavorites: Recebidos ${favorites.size} favoritos, IDs: $newFavoriteIds")
                        val oldFavoriteIds = favoriteIds.value
                        favoriteIds.value = newFavoriteIds
                        if (oldFavoriteIds != newFavoriteIds) {
                            Log.d(TAG, "observeFavorites: IDs de favoritos mudaram: $oldFavoriteIds -> $newFavoriteIds")
                            updateFavoriteStates()
                        } else {
                            Log.d(TAG, "observeFavorites: IDs de favoritos não mudaram, ignorando atualização")
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "observeFavorites: Erro ao observar favoritos", e)
            }
        }
    }
    private fun updateFavoriteStates() {
        Log.d(TAG, "updateFavoriteStates: Verificando se precisa atualizar estados")
        val currentState = _uiState.value
        if (currentState is MovieListUiState.Success) {
            val favoriteIdsSet = favoriteIds.value
            val loadingIdsSet = loadingFavoriteIds.value
            Log.d(TAG, "updateFavoriteStates: Estado atual é Success com ${currentState.movies.size} filmes")
            Log.d(TAG, "updateFavoriteStates: favoriteIds: $favoriteIdsSet, loadingIds: $loadingIdsSet")
            val updatedMovies = currentState.movies.map { movie ->
                val isFavorite = favoriteIdsSet.contains(movie.id)
                val isLoading = loadingIdsSet.contains(movie.id)
                movie.copy(
                    isFavorite = isFavorite,
                    isLoadingFavorite = isLoading
                )
            }
            val hasChanged = currentState.movies.zip(updatedMovies).any { (old, new) ->
                old.isFavorite != new.isFavorite || old.isLoadingFavorite != new.isLoadingFavorite
            }
            if (hasChanged) {
                Log.d(TAG, "updateFavoriteStates: Estado mudou, atualizando UI")
                _uiState.value = currentState.copy(movies = updatedMovies)
                Log.d(TAG, "updateFavoriteStates: Estado atualizado com sucesso")
            } else {
                Log.d(TAG, "updateFavoriteStates: Nenhuma mudança detectada, ignorando atualização")
            }
        } else {
            Log.d(TAG, "updateFavoriteStates: Estado atual não é Success (é ${currentState::class.simpleName}), ignorando")
        }
    }
    /**
     * Atualiza a query de pesquisa.
     *
     * A lógica de debounce e a chamada à API de pesquisa serão aplicadas automaticamente
     * através da observação do [searchQuery].
     *
     * @param query A nova string de pesquisa.
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Carrega filmes populares da API.
     *
     * Atualiza o [uiState] para [MovieListUiState.Loading] e, em seguida, para
     * [MovieListUiState.Success] com os filmes ou [MovieListUiState.Error] em caso de falha.
     *
     * @param page O número da página a ser carregada. Padrão é 1.
     */
    fun loadPopularMovies(page: Int = 1) {
        Log.d(TAG, "loadPopularMovies: Carregando página $page")
        viewModelScope.launch {
            _uiState.update { MovieListUiState.Loading }
            Log.d(TAG, "loadPopularMovies: Estado atualizado para Loading")
            getPopularMoviesUseCase(page)
                .onSuccess { movies ->
                    Log.d(TAG, "loadPopularMovies: Sucesso - ${movies.size} filmes recebidos")
                    currentPage = page
                    if (movies.isEmpty()) {
                        Log.d(TAG, "loadPopularMovies: Lista vazia")
                        _uiState.update { MovieListUiState.Empty }
                    } else {
                        val viewObjects = movies.toViewObjectList(favoriteIds.value, loadingFavoriteIds.value)
                        Log.d(TAG, "loadPopularMovies: ${viewObjects.size} ViewObjects criados")
                        _uiState.update {
                            MovieListUiState.Success(
                                movies = viewObjects,
                                isSearch = false,
                                currentPage = page,
                                canLoadMore = page < 500,
                                isLoadingMore = false
                            )
                        }
                        Log.d(TAG, "loadPopularMovies: Estado atualizado para Success")
                    }
                }
                .onFailure { exception ->
                    Log.e(TAG, "loadPopularMovies: Erro - ${exception.message}", exception)
                    _uiState.update {
                        MovieListUiState.Error(
                            exception.message ?: "Erro ao carregar filmes populares"
                        )
                    }
                }
        }
    }
    /**
     * Busca filmes da API com base na query de pesquisa.
     *
     * Atualiza o [uiState] para [MovieListUiState.Loading] e, em seguida, para
     * [MovieListUiState.Success] com os filmes ou [MovieListUiState.Error] em caso de falha.
     *
     * @param query O termo de busca.
     * @param page O número da página a ser carregada. Padrão é 1.
     */
    private fun searchMovies(query: String, page: Int = 1) {
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
                                isLoadingMore = false
                            )
                        }
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        MovieListUiState.Error(
                            exception.message ?: "Erro ao buscar filmes"
                        )
                    }
                }
        }
    }
    /**
     * Carrega a próxima página de filmes, seja para filmes populares ou resultados de pesquisa.
     *
     * Verifica o estado atual para evitar carregamentos múltiplos e atualiza o [uiState]
     * com os novos filmes adicionados à lista existente.
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
            val result = if (query.isBlank()) {
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
                            canLoadMore = newMovies.isNotEmpty() && nextPage < 500
                        )
                    }
                    Log.d(TAG, "loadNextPage: Página $nextPage carregada, ${newViewObjects.size} novos filmes")
                }
                .onFailure { exception ->
                    Log.e(TAG, "loadNextPage: Erro ao carregar página $nextPage", exception)
                    _uiState.update {
                        currentState.copy(isLoadingMore = false)
                    }
                }
        }
    }
    /**
     * Tenta recarregar os filmes com base na query atual (popular ou pesquisa).
     *
     * Útil para cenários de "tentar novamente" após um erro.
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
     * Alterna o estado de favorito de um filme.
     *
     * Adiciona ou remove o filme dos favoritos. Antes de adicionar, busca os detalhes
     * completos do filme para armazená-los no Room. Durante o processo, o filme
     * é marcado como [isLoadingFavorite] na UI.
     *
     * @param movie O [MovieViewObject] cujo estado de favorito será alternado.
     */
    fun toggleFavorite(movie: MovieViewObject) {
        Log.d(TAG, "toggleFavorite: Iniciando toggle do filme ${movie.id} - ${movie.title}, isFavorite atual: ${movie.isFavorite}")
        viewModelScope.launch {
            try {
                markFavoriteAsLoading(movie.id)
                val isFavoriteResult = isFavoriteUseCase(movie.id)
                isFavoriteResult.onSuccess { isFavorite ->
                    if (isFavorite) {
                        removeFavorite(movie.id)
                    } else {
                        addFavorite(movie)
                    }
                }.onFailure { exception ->
                    Log.e(TAG, "toggleFavorite: Erro ao verificar se é favorito", exception)
                }
                markFavoriteAsNotLoading(movie.id)
            } catch (e: Exception) {
                Log.e(TAG, "toggleFavorite: Erro inesperado", e)
                markFavoriteAsNotLoading(movie.id)
            }
        }
    }
    
    private fun markFavoriteAsLoading(movieId: Int) {
        Log.d(TAG, "toggleFavorite: Adicionando $movieId à lista de loading")
        loadingFavoriteIds.update { it + movieId }
        updateFavoriteStates()
    }
    
    private fun markFavoriteAsNotLoading(movieId: Int) {
        Log.d(TAG, "toggleFavorite: Removendo $movieId da lista de loading")
        loadingFavoriteIds.update { it - movieId }
        updateFavoriteStates()
        Log.d(TAG, "toggleFavorite: Toggle concluído para filme $movieId")
    }
    
    private suspend fun removeFavorite(movieId: Int) {
        Log.d(TAG, "toggleFavorite: Removendo filme $movieId dos favoritos")
        removeFromFavoritesUseCase(movieId)
            .onSuccess {
                Log.d(TAG, "toggleFavorite: Filme $movieId removido dos favoritos com sucesso")
            }
            .onFailure { exception ->
                Log.e(TAG, "toggleFavorite: Erro ao remover favorito", exception)
            }
    }
    
    private suspend fun addFavorite(movie: MovieViewObject) {
        Log.d(TAG, "toggleFavorite: Adicionando filme ${movie.id} aos favoritos")
        val detailsResult = getMovieDetailsUseCase(movie.id)
        detailsResult.onSuccess { movieDetail ->
            addFavoriteWithDetails(movieDetail, movie)
        }.onFailure { exception ->
            Log.e(TAG, "toggleFavorite: Erro ao buscar detalhes, usando Movie básico", exception)
            addFavoriteBasic(movie)
        }
    }
    
    private suspend fun addFavoriteWithDetails(movieDetail: com.onboarding.mychallenge.domain.model.MovieDetail, movie: MovieViewObject) {
        Log.d(TAG, "toggleFavorite: Detalhes obtidos, adicionando aos favoritos")
        addMovieDetailToFavoritesUseCase(movieDetail)
            .onSuccess {
                Log.d(TAG, "toggleFavorite: Filme ${movie.id} adicionado aos favoritos com detalhes")
            }
            .onFailure { exception ->
                Log.e(TAG, "toggleFavorite: Erro ao adicionar favorito com detalhes", exception)
                addFavoriteBasic(movie)
            }
    }
    
    private suspend fun addFavoriteBasic(movie: MovieViewObject) {
        Log.d(TAG, "toggleFavorite: Tentando fallback com Movie básico")
        val domainMovie = movie.toDomain()
        addToFavoritesUseCase(domainMovie)
            .onFailure { fallbackException ->
                Log.e(TAG, "toggleFavorite: Erro ao adicionar favorito (fallback)", fallbackException)
            }
    }
    /**
     * Adiciona um filme aos favoritos pelo seu ID.
     *
     * Busca os detalhes completos do filme antes de adicioná-lo ao banco de dados local.
     * Em caso de falha na obtenção dos detalhes, tenta adicionar o filme com informações básicas.
     *
     * @param movieId O ID do filme a ser adicionado aos favoritos.
     */
    fun addToFavorites(movieId: Int) {
        viewModelScope.launch {
            try {
                val detailsResult = getMovieDetailsUseCase(movieId)
                detailsResult.onSuccess { movieDetail ->
                    addMovieDetailToFavoritesUseCase(movieDetail)
                        .onFailure { exception ->
                            Log.e(TAG, "addToFavorites: Erro ao adicionar favorito com detalhes", exception)
                            val currentState = _uiState.value
                            if (currentState is MovieListUiState.Success) {
                                val movie = currentState.movies.find { it.id == movieId }
                                if (movie != null) {
                                    val domainMovie = movie.toDomain()
                                    addToFavoritesUseCase(domainMovie)
                                        .onFailure { fallbackException ->
                                            Log.e(TAG, "addToFavorites: Erro ao adicionar favorito (fallback)", fallbackException)
                                        }
                                }
                            }
                        }
                }.onFailure { exception ->
                    Log.e(TAG, "addToFavorites: Erro ao buscar detalhes do filme $movieId", exception)
                    val currentState = _uiState.value
                    if (currentState is MovieListUiState.Success) {
                        val movie = currentState.movies.find { it.id == movieId }
                        if (movie != null) {
                            val domainMovie = movie.toDomain()
                            addToFavoritesUseCase(domainMovie)
                                .onFailure { fallbackException ->
                                    Log.e(TAG, "addToFavorites: Erro ao adicionar favorito (fallback)", fallbackException)
                                }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "addToFavorites: Erro inesperado", e)
            }
        }
    }
    /**
     * Remove um filme dos favoritos pelo seu ID.
     *
     * @param movieId O ID do filme a ser removido dos favoritos.
     */
    fun removeFromFavorites(movieId: Int) {
        viewModelScope.launch {
            removeFromFavoritesUseCase(movieId)
                .onFailure { exception ->
                    Log.e(TAG, "removeFromFavorites: Erro ao remover favorito", exception)
                }
        }
    }
}

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
        popularity = popularity
    )
}
