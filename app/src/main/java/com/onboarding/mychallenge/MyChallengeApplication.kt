package com.onboarding.mychallenge

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Classe Application personalizada para inicializar o Hilt Dependency Injection.
 * 
 * Esta classe é necessária para que o Hilt possa gerenciar a injeção de dependências
 * em todo o aplicativo. O Hilt gera o código necessário para criar o componente
 * de dependências durante a inicialização do aplicativo.
 * 
 * @constructor Cria uma nova instância da MyChallengeApplication.
 */
@HiltAndroidApp
class MyChallengeApplication : Application()
