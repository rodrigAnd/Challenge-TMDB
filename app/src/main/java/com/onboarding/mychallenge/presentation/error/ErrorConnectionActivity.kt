package com.onboarding.mychallenge.presentation.error

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.onboarding.mychallenge.MainActivity
import com.onboarding.mychallenge.databinding.ActivityErrorConnectionBinding

/**
 * Activity exibida quando não há conexão com a internet.
 * Permite ao usuário tentar novamente navegando para a tela principal.
 */
class ErrorConnectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityErrorConnectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityErrorConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRetryButton()
    }

    /**
     * Configura o botão de tentar novamente para navegar para a MainActivity.
     */
    private fun setupRetryButton() {
        binding.retryButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}

