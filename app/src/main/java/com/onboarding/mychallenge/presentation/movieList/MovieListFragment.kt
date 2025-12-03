package com.onboarding.mychallenge.presentation.movieList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.onboarding.mychallenge.databinding.FragmentMovieListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieListFragment : Fragment() {
    companion object {
        private const val TAG = "MovieListFragment"
    }

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() =
        _binding ?: error("Binding is null. Fragment view may have been destroyed.")
    private val viewModel: MovieListViewModel by viewModels()
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var shimmerAdapter: ShimmerAdapter
    private var imagesLoadedCount: Int = 0
    private var totalMoviesCount: Int = 0

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
        observeUiState()
        viewModel.loadPopularMovies()
    }

    private fun setupRecyclerView() {
        movieAdapter =
            MovieAdapter(
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
                    imagesLoadedCount++
                    if (imagesLoadedCount == 1) {
                        _binding?.shimmerRecyclerView?.visibility = View.GONE
                    }
                },
            )
        shimmerAdapter = ShimmerAdapter(itemCount = 5)
        binding.moviesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
            addOnScrollListener(
                object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                    override fun onScrolled(
                        recyclerView: androidx.recyclerview.widget.RecyclerView,
                        dx: Int,
                        dy: Int,
                    ) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (dy <= 0) return
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                        val totalItemCount = layoutManager.itemCount
                        val currentState = viewModel.uiState.value
                        if (currentState is MovieListUiState.Success && currentState.isLoadingMore) {
                            return
                        }
                        if (lastVisibleItemPosition >= totalItemCount - 3 &&
                            currentState is MovieListUiState.Success &&
                            currentState.canLoadMore
                        ) {
                            viewModel.loadNextPage()
                        }
                    }
                },
            )
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
                    // No action needed before text changes
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
                    // No action needed after text changes
                }
            },
        )
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                if (_binding == null) {
                    Log.w(TAG, "observeUiState: Binding é null, ignorando atualização")
                    return@collectLatest
                }
                handleUiState(state)
            }
        }
    }

    private fun handleUiState(state: MovieListUiState) {
        when (state) {
            is MovieListUiState.Loading -> handleLoadingState()
            is MovieListUiState.Success -> handleSuccessState(state)
            is MovieListUiState.Error -> handleErrorState(state)
            is MovieListUiState.Empty -> handleEmptyState()
        }
    }

    private fun handleLoadingState() {
        hideShimmer()
        hideError()
        hideEmpty()
    }

    private fun handleSuccessState(state: MovieListUiState.Success) {
        hideError()
        hideEmpty()
        if (state.isLoadingMore) {
            movieAdapter.submitList(state.movies)
            return
        }
        updateMoviesList(state.movies)
    }

    private fun updateMoviesList(movies: List<MovieViewObject>) {
        totalMoviesCount = movies.size
        if (_binding?.shimmerRecyclerView?.visibility == View.VISIBLE) {
            imagesLoadedCount = 0
        }
        movieAdapter.submitList(movies)
    }

    private fun handleErrorState(state: MovieListUiState.Error) {
        hideShimmer()
        val isConnectionError = isConnectionError(state.message)
        if (isConnectionError) {
            navigateToErrorConnection()
        } else {
            navigateToError(state.message)
        }
    }

    private fun isConnectionError(message: String): Boolean {
        return message.contains("conexão", ignoreCase = true) ||
            message.contains("internet", ignoreCase = true) ||
            message.contains("conexão com a internet", ignoreCase = true)
    }

    private fun handleEmptyState() {
        hideShimmer()
        hideError()
        showEmpty()
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
