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
        holder.bind(movie)
    }

    override fun submitList(list: List<MovieViewObject>?) {
        super.submitList(list)
    }

    class MovieViewHolder(
        private val binding: ItemMovieBinding,
        private val onItemClick: (MovieViewObject) -> Unit,
        private val onFavoriteClick: ((MovieViewObject) -> Unit)? = null,
        private val onImageLoaded: (() -> Unit)? = null,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: MovieViewObject) {
            binding.apply {
                loadPosterImage(movie)
                titleTextView.text = movie.title
                releaseDateTextView.text = movie.formattedReleaseDate
                ratingTextView.text = "⭐ ${movie.formattedRating}"
                voteCountTextView.text = "• ${movie.voteCount} votos"
                overviewTextView.text = movie.overview
                setupFavoriteButton(movie)
                favoriteButton.setOnClickListener {
                    if (!movie.isLoadingFavorite) {
                        onFavoriteClick?.invoke(movie)
                    }
                }
                root.setOnClickListener {
                    onItemClick(movie)
                }
            }
        }

        private fun loadPosterImage(movie: MovieViewObject) {
            if (movie.hasPoster) {
                binding.posterImageView.visibility = android.view.View.VISIBLE
                binding.posterImageView.alpha = 0f
                binding.posterImageView.load(
                    movie.posterUrl,
                ) {
                    crossfade(300)
                    error(android.R.drawable.ic_menu_report_image)
                    listener(
                        onSuccess = { _, _ ->
                            binding.posterImageView.alpha = 1f
                            onImageLoaded?.invoke()
                        },
                        onError = { _, _ ->
                            binding.posterImageView.alpha = 1f
                            onImageLoaded?.invoke()
                        },
                    )
                }
            } else {
                binding.posterImageView.visibility = android.view.View.GONE
                onImageLoaded?.invoke()
            }
        }

        private fun setupFavoriteButton(movie: MovieViewObject) {
            if (movie.isLoadingFavorite) {
                showFavoriteLoading()
            } else {
                showFavoriteButton(movie)
            }
        }

        private fun showFavoriteLoading() {
            binding.favoriteButton.visibility = android.view.View.INVISIBLE
            binding.favoriteShimmerLayout.visibility = android.view.View.VISIBLE
            binding.favoriteButton.isEnabled = false
        }

        private fun showFavoriteButton(movie: MovieViewObject) {
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
            binding.favoriteButton.contentDescription =
                if (movie.isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos"
            val tintColor = getFavoriteButtonTintColor(movie.isFavorite)
            binding.favoriteButton.imageTintList = ColorStateList.valueOf(tintColor)
        }

        private fun getFavoriteButtonTintColor(isFavorite: Boolean): Int {
            return if (isFavorite) {
                Color.parseColor("#FFD700")
            } else {
                getThemeColorPrimary()
            }
        }

        private fun getThemeColorPrimary(): Int {
            return try {
                val typedValue = TypedValue()
                val theme = binding.root.context.theme
                if (
                    theme.resolveAttribute(
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
                android.util.Log.e("MovieAdapter", "Error getting theme color", e)
                Color.parseColor("#FB8C00")
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
