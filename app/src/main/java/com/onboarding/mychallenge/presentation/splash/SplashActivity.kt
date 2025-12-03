package com.onboarding.mychallenge.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.onboarding.mychallenge.MainActivity
import com.onboarding.mychallenge.databinding.ActivitySplashBinding

/**
 * Splash Screen Activity exibida no início do aplicativo.
 * Exibe a logo do aplicativo por um período determinado antes de navegar para a tela principal.
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    companion object {
        private const val SPLASH_DELAY_MS = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigateToMain()
    }

    /**
     * Navega para a MainActivity após o delay configurado.
     */
    private fun navigateToMain() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY_MS)
    }
}

