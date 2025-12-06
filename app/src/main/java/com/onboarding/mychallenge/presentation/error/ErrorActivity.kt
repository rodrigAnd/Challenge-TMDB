package com.onboarding.mychallenge.presentation.error
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.onboarding.mychallenge.MainActivity
import com.onboarding.mychallenge.databinding.ActivityErrorBinding

class ErrorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityErrorBinding
    private var retryCount = 0

    companion object {
        private const val MAX_RETRY_ATTEMPTS = 2
        const val EXTRA_ERROR_MESSAGE = "error_message"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val errorMessage = intent.getStringExtra(EXTRA_ERROR_MESSAGE)
        setupErrorContent(errorMessage)
        setupRetryButton()
    }

    private fun setupErrorContent(errorMessage: String?) {
        binding.errorMessageTextView.text = errorMessage ?: getString(com.onboarding.mychallenge.R.string.error_loading_movies)
    }

    private fun setupRetryButton() {
        binding.retryButton.setOnClickListener {
            if (retryCount < MAX_RETRY_ATTEMPTS) {
                retryCount++
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                binding.retryButton.isEnabled = false
                binding.retryButton.text = getString(com.onboarding.mychallenge.R.string.max_retries_reached)
            }
        }
    }
}
