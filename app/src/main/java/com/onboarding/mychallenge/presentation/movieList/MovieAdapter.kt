package com.onboarding.mychallenge.presentation.movieList

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.onboarding.mychallenge.databinding.ItemMovieBinding

/**
 * Adapter para RecyclerView de filmes
 */
class MovieAdapter(
    private val onItemClick: (MovieViewObject) -> Unit,
    private val onFavoriteClick: ((MovieViewObject) -> Unit)? = null,
    private val onImageLoaded: (() -> Unit)? = null
) : ListAdapter<MovieViewObject, MovieAdapter.MovieViewHolder>(MovieDiffCallback()) {

    companion object {
        private const val TAG = "MovieAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding, onItemClick, onFavoriteClick, onImageLoaded)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        Log.d(TAG, "onBindViewHolder: Binding filme na posição $position - ID: ${movie.id}, Title: ${movie.title}, isFavorite: ${movie.isFavorite}, isLoadingFavorite: ${movie.isLoadingFavorite}")
        holder.bind(movie)
    }
    
    override fun submitList(list: List<MovieViewObject>?) {
        Log.d(TAG, "submitList: Submetendo lista com ${list?.size ?: 0} filmes")
        super.submitList(list)
    }

    class MovieViewHolder(
        private val binding: ItemMovieBinding,
        private val onItemClick: (MovieViewObject) -> Unit,
        private val onFavoriteClick: ((MovieViewObject) -> Unit)? = null,
        private val onImageLoaded: (() -> Unit)? = null
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieViewObject) {
            binding.apply {
                // Carrega imagem do pôster
                if (movie.hasPoster) {
                    posterImageView.visibility = android.view.View.VISIBLE
                    // Inicia invisível para não mostrar até carregar
                    posterImageView.alpha = 0f
                    posterImageView.load(movie.posterUrl) {
                        crossfade(300)
                        // Não usa placeholder para evitar mostrar imagem padrão
                        error(android.R.drawable.ic_menu_report_image)
                        listener(
                            onSuccess = { _, _ ->
                                // Imagem carregada com sucesso - torna visível e notifica
                                posterImageView.alpha = 1f
                                onImageLoaded?.invoke()
                            },
                            onError = { _, _ ->
                                // Erro ao carregar - torna visível mesmo assim e notifica
                                posterImageView.alpha = 1f
                                onImageLoaded?.invoke()
                            }
                        )
                    }
                } else {
                    posterImageView.visibility = android.view.View.GONE
                    // Se não tem poster, também notifica para remover shimmer
                    onImageLoaded?.invoke()
                }

                // Título
                titleTextView.text = movie.title

                // Data de lançamento
                releaseDateTextView.text = movie.formattedReleaseDate

                // Rating - apenas número com estrela
                ratingTextView.text = "⭐ ${movie.formattedRating}"

                // Vote Count
                voteCountTextView.text = "• ${movie.voteCount} votos"

                // Sinopse
                overviewTextView.text = movie.overview

                // Favorito - mostra shimmer ou ícone do botão
                if (movie.isLoadingFavorite) {
                    // Mostra Shimmer e esconde o botão
                    binding.favoriteButton.visibility = android.view.View.INVISIBLE
                    binding.favoriteShimmerLayout.visibility = android.view.View.VISIBLE
                    binding.favoriteButton.isEnabled = false
                } else {
                    // Mostra o botão e esconde o Shimmer
                    binding.favoriteButton.visibility = android.view.View.VISIBLE
                    binding.favoriteShimmerLayout.visibility = android.view.View.GONE
                    binding.favoriteButton.isEnabled = true
                    binding.favoriteButton.setImageResource(
                        if (movie.isFavorite) {
                            android.R.drawable.btn_star_big_on
                        } else {
                            android.R.drawable.btn_star_big_off
                        }
                    )
                    binding.favoriteButton.contentDescription = if (movie.isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos"
                    // Garante contraste visual - cor dourada quando favorito, cor primária (#FB8C00) quando não
                    val tintColor = if (movie.isFavorite) {
                        Color.parseColor("#FFD700") // Dourado quando favorito
                    } else {
                        // Usa a cor primária do tema (#FB8C00)
                        try {
                            val typedValue = TypedValue()
                            val theme = binding.root.context.theme
                            if (theme.resolveAttribute(
                                    android.R.attr.colorPrimary,
                                    typedValue,
                                    true
                                )) {
                                typedValue.data
                            } else {
                                Color.parseColor("#FB8C00") // Fallback com a cor primária laranja
                            }
                        } catch (e: Exception) {
                            Color.parseColor("#FB8C00") // Fallback com a cor primária laranja
                        }
                    }
                    binding.favoriteButton.imageTintList = ColorStateList.valueOf(tintColor)
                }
                
                // Clique no botão de favorito (só funciona se não estiver loading)
                binding.favoriteButton.setOnClickListener {
                    if (!movie.isLoadingFavorite) {
                        Log.d(TAG, "bind: Clique no favorito do filme ${movie.id} - ${movie.title}, isFavorite: ${movie.isFavorite}")
                        onFavoriteClick?.invoke(movie)
                    } else {
                        Log.d(TAG, "bind: Clique ignorado - filme ${movie.id} está em loading")
                    }
                }

                // Clique no item
                root.setOnClickListener {
                    onItemClick(movie)
                }
            }
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<MovieViewObject>() {
        override fun areItemsTheSame(oldItem: MovieViewObject, newItem: MovieViewObject): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieViewObject, newItem: MovieViewObject): Boolean {
            return oldItem == newItem
        }
    }
}

