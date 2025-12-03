package com.onboarding.mychallenge.presentation.favorites

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.onboarding.mychallenge.databinding.FragmentFavoritesBinding
import com.onboarding.mychallenge.presentation.movieList.MovieAdapter
import com.onboarding.mychallenge.presentation.movieList.MovieViewObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    companion object {
        private const val TAG = "FavoritesFragment"
    }

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: Criando view do FavoritesFragment")
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView: View criada com sucesso")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Inicializando componentes da view")

        setupRecyclerView()
        setupSearch()
        observeUiState()
        
        // Não precisa chamar loadFavorites() pois o Flow já está observando no init do ViewModel
        // e vai emitir os dados automaticamente
        Log.d(TAG, "onViewCreated: Inicialização concluída - Flow já está observando favoritos")
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView: Configurando RecyclerView")
        movieAdapter = MovieAdapter(
            onItemClick = { movie ->
                Log.d(TAG, "setupRecyclerView: Clique no filme ${movie.id} - ${movie.title}")
                // Abre Activity de detalhes
                val intent = android.content.Intent(requireContext(), com.onboarding.mychallenge.presentation.movieDetail.MovieDetailActivity::class.java)
                intent.putExtra("movie_id", movie.id)
                startActivity(intent)
            },
            onFavoriteClick = { movie ->
                Log.d(TAG, "setupRecyclerView: Clique no favorito do filme ${movie.id} - ${movie.title}")
                // Remove dos favoritos
                viewModel.removeFromFavorites(movie.id)
            },
            onImageLoaded = null
        )

        binding.moviesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
        }
        Log.d(TAG, "setupRecyclerView: RecyclerView configurado")
    }

    private fun setupSearch() {
        Log.d(TAG, "setupSearch: Configurando campo de pesquisa")
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString() ?: ""
                Log.d(TAG, "setupSearch: Texto alterado para: '$query'")
                viewModel.updateSearchQuery(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        Log.d(TAG, "setupSearch: Campo de pesquisa configurado")
    }

    private fun observeUiState() {
        Log.d(TAG, "observeUiState: Iniciando observação do estado da UI")
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                Log.d(TAG, "observeUiState: Estado recebido: ${state::class.simpleName}")
                
                // Verifica se a view ainda está disponível
                if (_binding == null) {
                    Log.w(TAG, "observeUiState: Binding é null, ignorando atualização")
                    return@collectLatest
                }
                
                when (state) {
                    is FavoritesUiState.Loading -> {
                        Log.d(TAG, "observeUiState: Mostrando loading")
                        binding.loadingProgressBar.visibility = View.VISIBLE
                        binding.moviesRecyclerView.visibility = View.GONE
                        binding.errorLayout.visibility = View.GONE
                        binding.emptyTextView.visibility = View.GONE
                    }

                    is FavoritesUiState.Success -> {
                        Log.d(TAG, "observeUiState: Success - ${state.movies.size} filmes")
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.moviesRecyclerView.visibility = View.VISIBLE
                        binding.errorLayout.visibility = View.GONE
                        binding.emptyTextView.visibility = View.GONE
                        // Usa submitList de forma segura
                        try {
                            Log.d(TAG, "observeUiState: Atualizando adapter com ${state.movies.size} filmes")
                            movieAdapter.submitList(state.movies) {
                                Log.d(TAG, "observeUiState: Adapter atualizado com sucesso")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "observeUiState: Erro ao atualizar lista", e)
                        }
                    }

                    is FavoritesUiState.Error -> {
                        Log.e(TAG, "observeUiState: Error - ${state.message}")
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.moviesRecyclerView.visibility = View.GONE
                        binding.errorLayout.visibility = View.VISIBLE
                        binding.errorTextView.text = state.message
                        binding.emptyTextView.visibility = View.GONE
                        // Mostra feedback visual para o usuário
                        android.widget.Toast.makeText(
                            requireContext(),
                            state.message,
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }

                    is FavoritesUiState.Empty -> {
                        Log.d(TAG, "observeUiState: Empty - Nenhum favorito encontrado")
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.moviesRecyclerView.visibility = View.GONE
                        binding.errorLayout.visibility = View.GONE
                        binding.emptyTextView.visibility = View.VISIBLE
                    }
                }
            }
        }
        Log.d(TAG, "observeUiState: Observação iniciada")
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView: Destruindo view do FavoritesFragment")
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "onDestroyView: View destruída")
    }
}

