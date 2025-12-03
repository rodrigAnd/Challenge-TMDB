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
            displayBackdrop(movieDetail)
            detailTitleTextView.text = movieDetail.title
            detailRatingRuntimeTextView.text = "⭐ ${movieDetail.formattedRating} • ${movieDetail.formattedRuntime}"
            displayReleaseDate(movieDetail)
            displayGenres(movieDetail)
            displayTagline(movieDetail)
            detailOverviewTextView.text = movieDetail.overview.ifBlank { "Sinopse não disponível." }
            displayFinancialInfo(movieDetail)
            detailStatusTextView.text = movieDetail.status
        }
    }
    
    private fun displayBackdrop(movieDetail: com.onboarding.mychallenge.domain.model.MovieDetail) {
        if (movieDetail.backdropUrl.isNotEmpty()) {
            binding.backdropImageView.visibility = View.VISIBLE
            binding.backdropImageView.load(movieDetail.backdropUrl) {
                crossfade(300)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_report_image)
            }
        } else {
            binding.backdropImageView.visibility = View.GONE
        }
    }
    
    private fun displayReleaseDate(movieDetail: com.onboarding.mychallenge.domain.model.MovieDetail) {
        if (movieDetail.releaseDate != null) {
            binding.detailReleaseDateTextView.text = "${getString(com.onboarding.mychallenge.R.string.lancamento)}: ${movieDetail.releaseDate}"
            binding.detailReleaseDateTextView.visibility = View.VISIBLE
        } else {
            binding.detailReleaseDateTextView.visibility = View.GONE
        }
    }
    
    private fun displayGenres(movieDetail: com.onboarding.mychallenge.domain.model.MovieDetail) {
        if (movieDetail.genres.isNotEmpty()) {
            binding.detailGenresTextView.text = "${getString(com.onboarding.mychallenge.R.string.generos)}: ${movieDetail.genresString}"
            binding.detailGenresTextView.visibility = View.VISIBLE
        } else {
            binding.detailGenresTextView.visibility = View.GONE
        }
    }
    
    private fun displayTagline(movieDetail: com.onboarding.mychallenge.domain.model.MovieDetail) {
        if (!movieDetail.tagline.isNullOrBlank()) {
            binding.detailTaglineTextView.text = "\"${movieDetail.tagline}\""
            binding.detailTaglineTextView.visibility = View.VISIBLE
        } else {
            binding.detailTaglineTextView.visibility = View.GONE
        }
    }
    
    private fun displayFinancialInfo(movieDetail: com.onboarding.mychallenge.domain.model.MovieDetail) {
        if (movieDetail.budget > 0 || movieDetail.revenue > 0) {
            binding.additionalInfoTitleTextView.visibility = View.VISIBLE
            binding.infoCardsContainer.visibility = View.VISIBLE
            displayBudget(movieDetail.budget)
            displayRevenue(movieDetail.revenue)
        } else {
            binding.additionalInfoTitleTextView.visibility = View.GONE
            binding.infoCardsContainer.visibility = View.GONE
        }
    }
    
    private fun displayBudget(budget: Long) {
        if (budget > 0) {
            binding.budgetCard.visibility = View.VISIBLE
            binding.detailBudgetTextView.text = formatCurrency(budget)
        } else {
            binding.budgetCard.visibility = View.GONE
        }
    }
    
    private fun displayRevenue(revenue: Long) {
        if (revenue > 0) {
            binding.revenueCard.visibility = View.VISIBLE
            binding.detailRevenueTextView.text = formatCurrency(revenue)
        } else {
            binding.revenueCard.visibility = View.GONE
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
