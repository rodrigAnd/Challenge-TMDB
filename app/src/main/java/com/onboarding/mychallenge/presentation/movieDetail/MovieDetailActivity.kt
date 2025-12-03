package com.onboarding.mychallenge.presentation.movieDetail
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.onboarding.mychallenge.databinding.ActivityMovieDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
@AndroidEntryPoint
class MovieDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailBinding
    private val viewModel: MovieDetailViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val movieId = intent.getIntExtra("movie_id", 0)
        if (movieId == 0) {
            Toast.makeText(this, "ID do filme inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        setupToolbar()
        observeUiState()
        viewModel.loadMovieDetails(movieId)
    }
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is MovieDetailUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.scrollView.visibility = View.GONE
                    }
                    is MovieDetailUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.scrollView.visibility = View.VISIBLE
                        displayMovieDetail(state.movieDetail)
                    }
                    is MovieDetailUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@MovieDetailActivity,
                            state.message,
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                }
            }
        }
    }
    private fun displayMovieDetail(movieDetail: com.onboarding.mychallenge.domain.model.MovieDetail) {
        binding.apply {
            if (movieDetail.backdropUrl.isNotEmpty()) {
                backdropImageView.visibility = View.VISIBLE
                backdropImageView.load(movieDetail.backdropUrl) {
                    crossfade(300)
                    placeholder(android.R.drawable.ic_menu_gallery)
                    error(android.R.drawable.ic_menu_report_image)
                }
            } else {
                backdropImageView.visibility = View.GONE
            }
            detailTitleTextView.text = movieDetail.title
            detailRatingRuntimeTextView.text = "⭐ ${movieDetail.formattedRating} • ${movieDetail.formattedRuntime}"
            if (movieDetail.releaseDate != null) {
                detailReleaseDateTextView.text = "${getString(com.onboarding.mychallenge.R.string.lancamento)}: ${movieDetail.releaseDate}"
                detailReleaseDateTextView.visibility = View.VISIBLE
            } else {
                detailReleaseDateTextView.visibility = View.GONE
            }
            if (movieDetail.genres.isNotEmpty()) {
                detailGenresTextView.text = "${getString(com.onboarding.mychallenge.R.string.generos)}: ${movieDetail.genresString}"
                detailGenresTextView.visibility = View.VISIBLE
            } else {
                detailGenresTextView.visibility = View.GONE
            }
            if (!movieDetail.tagline.isNullOrBlank()) {
                detailTaglineTextView.text = "\"${movieDetail.tagline}\""
                detailTaglineTextView.visibility = View.VISIBLE
            } else {
                detailTaglineTextView.visibility = View.GONE
            }
            detailOverviewTextView.text = movieDetail.overview.ifBlank { "Sinopse não disponível." }
            if (movieDetail.budget > 0 || movieDetail.revenue > 0) {
                additionalInfoTitleTextView.visibility = View.VISIBLE
                infoCardsContainer.visibility = View.VISIBLE
                if (movieDetail.budget > 0) {
                    budgetCard.visibility = View.VISIBLE
                    detailBudgetTextView.text = formatCurrency(movieDetail.budget)
                } else {
                    budgetCard.visibility = View.GONE
                }
                if (movieDetail.revenue > 0) {
                    revenueCard.visibility = View.VISIBLE
                    detailRevenueTextView.text = formatCurrency(movieDetail.revenue)
                } else {
                    revenueCard.visibility = View.GONE
                }
            } else {
                additionalInfoTitleTextView.visibility = View.GONE
                infoCardsContainer.visibility = View.GONE
            }
            detailStatusTextView.text = movieDetail.status
        }
    }
    private fun formatCurrency(amount: Long): String {
        return if (amount >= 1_000_000) {
            String.format("$%.1fM", amount / 1_000_000.0)
        } else if (amount >= 1_000) {
            String.format("$%.1fK", amount / 1_000.0)
        } else {
            "$$amount"
        }
    }
}
