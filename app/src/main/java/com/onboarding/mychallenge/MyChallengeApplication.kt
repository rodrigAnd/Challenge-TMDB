package com.onboarding.mychallenge
import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Classe Application para inicializar o Hilt.
 *
 * Esta classe é o ponto de entrada para a injeção de dependência Hilt
 * em todo o aplicativo.
 *
 * @constructor Cria uma nova instância da MyChallengeApplication.
 */
@HiltAndroidApp
class MyChallengeApplication : Application()
