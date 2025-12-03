package com.onboarding.mychallenge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.onboarding.mychallenge.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal do aplicativo que gerencia a navegação entre fragmentos.
 *
 * Esta activity configura a navegação inferior (bottom navigation) conectando
 * o [NavHostFragment] ao [BottomNavigationView] para permitir navegação entre
 * as diferentes telas do aplicativo.
 *
 * @constructor Cria uma nova instância da MainActivity.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /**
     * Inicializa a activity e configura a navegação.
     *
     * @param savedInstanceState Se a activity está sendo recriada a partir de um estado salvo anteriormente,
     * este é o estado.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
    }

    /**
     * Configura a navegação inferior conectando o [bottomNavigationView]
     * ao [NavHostFragment] para permitir navegação entre fragmentos.
     */
    private fun setupBottomNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)
    }
}
