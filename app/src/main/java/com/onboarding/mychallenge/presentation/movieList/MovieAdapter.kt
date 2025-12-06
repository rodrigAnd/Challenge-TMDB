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
    private val onImageLoaded: (() -> Unit)? = null,
) : ListAdapter<MovieViewObject, MovieAdapter.MovieViewHolder>(MovieDiffCallback()) {
    companion object {
        private const val TAG = "MovieAdapter"
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MovieViewHolder {
        val binding =
            ItemMovieBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return MovieViewHolder(binding, onItemClick, onFavoriteClick, onImageLoaded)
    }

    override fun onBindViewHolder(
        holder: MovieViewHolder,
        position: Int,
    ) {
        val movie = getItem(position)
        Log.d(
            TAG,
            "onBindViewHolder: Binding filme na posição $position - ID: ${movie.id}, Title: ${movie.title}, isFavorite: ${movie.isFavorite}, isLoadingFavorite: ${movie.isLoadingFavorite}",
        )
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
        private val onImageLoaded: (() -> Unit)? = null,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: MovieViewObject) {
            binding.apply {
                if (movie.hasPoster) {
                    posterImageView.visibility = android.view.View.VISIBLE
                    posterImageView.alpha = 0f
                    posterImageView.load(movie.posterUrl) {
                        crossfade(300)
                        error(android.R.drawable.ic_menu_report_image)
                        listener(
                            onSuccess = { _, _ ->
                                posterImageView.alpha = 1f
                                onImageLoaded?.invoke()
                            },
                            onError = { _, _ ->
                                posterImageView.alpha = 1f
                                onImageLoaded?.invoke()
                            },
                        )
                    }
                } else {
                    posterImageView.visibility = android.view.View.GONE
                    onImageLoaded?.invoke()
                }
                titleTextView.text = movie.title
                releaseDateTextView.text = movie.formattedReleaseDate
                ratingTextView.text = "⭐ ${movie.formattedRating}"
                voteCountTextView.text = "• ${movie.voteCount} votos"
                overviewTextView.text = movie.overview
                if (movie.isLoadingFavorite) {
                    binding.favoriteButton.visibility = android.view.View.INVISIBLE
                    binding.favoriteShimmerLayout.visibility = android.view.View.VISIBLE
                    binding.favoriteButton.isEnabled = false
                } else {
                    binding.favoriteButton.visibility = android.view.View.VISIBLE
                    binding.favoriteShimmerLayout.visibility = android.view.View.GONE
                    binding.favoriteButton.isEnabled = true
                    binding.favoriteButton.setImageResource(
                        if (movie.isFavorite) {
                            android.R.drawable.btn_star_big_on
                        } else {
                            android.R.drawable.btn_star_big_off
                        },
                    )
                    binding.favoriteButton.contentDescription = if (movie.isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos"
                    val tintColor =
                        if (movie.isFavorite) {
                            Color.parseColor("#FFD700")
                        } else {
                            try {
                                val typedValue = TypedValue()
                                val theme = binding.root.context.theme
                                if (theme.resolveAttribute(
                                        android.R.attr.colorPrimary,
                                        typedValue,
                                        true,
                                    )
                                ) {
                                    typedValue.data
                                } else {
                                    Color.parseColor("#FB8C00")
                                }
                            } catch (e: Exception) {
                                Color.parseColor("#FB8C00")
                            }
                        }
                    binding.favoriteButton.imageTintList = ColorStateList.valueOf(tintColor)
                }
                binding.favoriteButton.setOnClickListener {
                    if (!movie.isLoadingFavorite) {
                        Log.d(
                            TAG,
                            "bind: Clique no favorito do filme ${movie.id} - ${movie.title}, isFavorite: ${movie.isFavorite}",
                        )
                        onFavoriteClick?.invoke(movie)
                    } else {
                        Log.d(TAG, "bind: Clique ignorado - filme ${movie.id} está em loading")
                    }
                }
                root.setOnClickListener {
                    onItemClick(movie)
                }
            }
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<MovieViewObject>() {
        override fun areItemsTheSame(
            oldItem: MovieViewObject,
            newItem: MovieViewObject,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MovieViewObject,
            newItem: MovieViewObject,
        ): Boolean {
            return oldItem == newItem
        }
    }
}
