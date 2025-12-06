package com.onboarding.mychallenge.presentation.movieList

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.onboarding.mychallenge.databinding.FragmentMovieListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fragment para exibir lista de filmes usando Paging 3.
 *
 * Utiliza PagingDataAdapter que gerencia automaticamente:
 * - Carregamento de páginas sob demanda
 * - Estados de loading e erro
 * - Cache de dados
 * - Atualizações diferenciais
 */
@AndroidEntryPoint
class MovieListFragmentPaging : Fragment() {
    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null. Fragment view may have been destroyed.")
    private val viewModel: MovieListViewModelPaging by viewModels()
    private lateinit var movieAdapter: MoviePagingAdapter
    private lateinit var loadStateAdapter: MovieLoadStateAdapter
    private lateinit var shimmerAdapter: ShimmerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        observeMovies()
        observeFavorites()
        observeLoadState()
    }

    private fun setupRecyclerView() {
        loadStateAdapter = MovieLoadStateAdapter { movieAdapter.retry() }

        movieAdapter =
            MoviePagingAdapter(
                favoriteIds = emptySet(),
                loadingFavoriteIds = emptySet(),
                onItemClick = { movie ->
                    val intent =
                        android.content.Intent(
                            requireContext(),
                            com.onboarding.mychallenge.presentation.movieDetail.MovieDetailActivity::class.java,
                        )
                    intent.putExtra("movie_id", movie.id)
                    startActivity(intent)
                },
                onFavoriteClick = { movie ->
                    if (movie.isFavorite) {
                        viewModel.removeFromFavorites(movie.id)
                    } else {
                        viewModel.addToFavorites(movie.id)
                    }
                },
                onImageLoaded = {
                    _binding?.shimmerRecyclerView?.visibility = View.GONE
                },
            )

        shimmerAdapter = ShimmerAdapter(itemCount = 5)

        // ConcatAdapter combina o adapter principal com o LoadStateAdapter
        val concatAdapter = ConcatAdapter(movieAdapter, loadStateAdapter)

        binding.moviesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }

        binding.shimmerRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = shimmerAdapter
        }
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                    // Intencionalmente vazio
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                    viewModel.updateSearchQuery(s?.toString() ?: "")
                }

                override fun afterTextChanged(s: Editable?) {
                    // Intencionalmente vazio
                }
            },
        )
    }

    private fun observeMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.movies.collectLatest { pagingData ->
                movieAdapter.submitData(pagingData)
            }
        }
    }

    private fun observeFavorites() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteIds.collectLatest { favorites ->
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.loadingFavoriteIds.collectLatest { loading ->
                        movieAdapter.updateFavorites(favorites, loading)
                    }
                }
            }
        }
    }

    private fun observeLoadState() {
        viewLifecycleOwner.lifecycleScope.launch {
            movieAdapter.loadStateFlow.collectLatest { loadState ->
                val isLoading = loadState.refresh is LoadState.Loading
                val isError = loadState.refresh is LoadState.Error

                if (isLoading) {
                    hideError()
                    hideEmpty()
                } else if (isError) {
                    hideShimmer()
                    val error = (loadState.refresh as? LoadState.Error)?.error
                    val errorMessage = error?.message ?: "Erro ao carregar filmes"
                    val isConnectionError =
                        errorMessage.contains("conexão", ignoreCase = true) ||
                            errorMessage.contains("internet", ignoreCase = true) ||
                            errorMessage.contains("UnknownHostException", ignoreCase = true)

                    if (isConnectionError) {
                        navigateToErrorConnection()
                    } else {
                        navigateToError(errorMessage)
                    }
                } else {
                    hideShimmer()
                    hideError()
                    // Verifica se a lista está vazia
                    if (loadState.append.endOfPaginationReached && movieAdapter.itemCount == 0) {
                        showEmpty()
                    } else {
                        hideEmpty()
                    }
                }
            }
        }
    }

    private fun hideShimmer() {
        _binding?.let {
            it.shimmerRecyclerView.visibility = View.GONE
            it.loadingProgressBar.visibility = View.GONE
        }
    }

    private fun navigateToErrorConnection() {
        val intent =
            android.content.Intent(
                requireContext(),
                com.onboarding.mychallenge.presentation.error.ErrorConnectionActivity::class.java,
            )
        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    private fun navigateToError(message: String) {
        val intent =
            android.content.Intent(
                requireContext(),
                com.onboarding.mychallenge.presentation.error.ErrorActivity::class.java,
            )
        intent.putExtra(com.onboarding.mychallenge.presentation.error.ErrorActivity.EXTRA_ERROR_MESSAGE, message)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    private fun hideError() {
        _binding?.errorLayout?.visibility = View.GONE
    }

    private fun showEmpty() {
        _binding?.let { binding ->
            binding.emptyTextView.visibility = View.VISIBLE
            binding.moviesRecyclerView.visibility = View.GONE
        }
    }

    private fun hideEmpty() {
        _binding?.emptyTextView?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
