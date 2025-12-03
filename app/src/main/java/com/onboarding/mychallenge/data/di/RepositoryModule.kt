package com.onboarding.mychallenge.data.di

import com.onboarding.mychallenge.data.repository.MovieRepositoryImpl
import com.onboarding.mychallenge.domain.repository.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para injeção de repositórios.
 * 
 * Define os bindings entre interfaces de repositório e suas implementações,
 * permitindo que o Hilt injete as implementações corretas onde necessário.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    /**
     * Vincula a implementação [MovieRepositoryImpl] à interface [MovieRepository].
     * 
     * Permite que o Hilt injete [MovieRepositoryImpl] sempre que [MovieRepository]
     * for solicitado como dependência.
     * 
     * @param movieRepositoryImpl Implementação concreta do repositório de filmes.
     * @return Instância da interface [MovieRepository].
     */
    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository
}

