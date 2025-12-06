package com.onboarding.mychallenge.presentation.favorites
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.onboarding.mychallenge.databinding.FragmentFavoritesBinding
import com.onboarding.mychallenge.presentation.movieList.MovieAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
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
    }

    private fun setupRecyclerView() {
        movieAdapter =
            MovieAdapter(
                onItemClick = { movie ->
                    val intent = android.content.Intent(requireContext(), com.onboarding.mychallenge.presentation.movieDetail.MovieDetailActivity::class.java)
                    intent.putExtra("movie_id", movie.id)
                    startActivity(intent)
                },
                onFavoriteClick = { movie ->
                    viewModel.removeFromFavorites(movie.id)
                },
                onImageLoaded = null,
            )
        binding.moviesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
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
                    // Intencionalmente vazio - não há ação necessária antes da mudança de texto
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                    val query = s?.toString() ?: ""
                    viewModel.updateSearchQuery(query)
                }

                override fun afterTextChanged(s: Editable?) {
                    // Intencionalmente vazio - não há ação necessária após a mudança de texto
                }
            },
        )
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                if (_binding == null) {
                    return@collectLatest
                }
                when (state) {
                    is FavoritesUiState.Loading -> {
                        binding.loadingProgressBar.visibility = View.VISIBLE
                        binding.moviesRecyclerView.visibility = View.GONE
                        binding.errorLayout.visibility = View.GONE
                        binding.emptyTextView.visibility = View.GONE
                    }
                    is FavoritesUiState.Success -> {
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.moviesRecyclerView.visibility = View.VISIBLE
                        binding.errorLayout.visibility = View.GONE
                        binding.emptyTextView.visibility = View.GONE
                        try {
                            movieAdapter.submitList(state.movies)
                        } catch (e: Exception) {
                            // Erro ao atualizar lista - já tratado pelo estado de erro
                        }
                    }
                    is FavoritesUiState.Error -> {
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.moviesRecyclerView.visibility = View.GONE
                        binding.errorLayout.visibility = View.VISIBLE
                        binding.errorTextView.text = state.message
                        binding.emptyTextView.visibility = View.GONE
                        android.widget.Toast.makeText(
                            requireContext(),
                            state.message,
                            android.widget.Toast.LENGTH_SHORT,
                        ).show()
                    }
                    is FavoritesUiState.Empty -> {
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.moviesRecyclerView.visibility = View.GONE
                        binding.errorLayout.visibility = View.GONE
                        binding.emptyTextView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
