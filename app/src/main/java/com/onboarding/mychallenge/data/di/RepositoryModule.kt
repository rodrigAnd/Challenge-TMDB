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
 * Este módulo abstrato usa a anotação [Binds] para fornecer a implementação
 * concreta de [MovieRepository] ([MovieRepositoryImpl]) quando a interface
 * [MovieRepository] é solicitada.
 * As dependências são instaladas no [SingletonComponent], garantindo
 * que o repositório seja um singleton.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    /**
     * Vincula a implementação de [MovieRepositoryImpl] à interface [MovieRepository].
     *
     * @param movieRepositoryImpl A implementação concreta do repositório de filmes.
     * @return A interface [MovieRepository].
     */
    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository
}
