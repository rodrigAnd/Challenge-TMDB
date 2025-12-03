package com.onboarding.mychallenge.data.di

import android.content.Context
import androidx.room.Room
import com.onboarding.mychallenge.data.local.dao.FavoriteMovieDao
import com.onboarding.mychallenge.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para configuração do banco de dados Room.
 * 
 * Fornece as dependências necessárias para acesso ao banco de dados local,
 * incluindo a instância do [AppDatabase] e os DAOs associados.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Fornece uma instância do banco de dados Room.
     * 
     * Cria e configura o banco de dados [AppDatabase] usando o Room.
     * Configurado com fallback para migração destrutiva, o que significa que
     * em caso de mudança de versão do schema, o banco será recriado.
     * 
     * @param context Contexto da aplicação para criar o banco de dados.
     * @return Instância do [AppDatabase] configurada.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    /**
     * Fornece uma instância do DAO para filmes favoritos.
     * 
     * Obtém o DAO do banco de dados para realizar operações CRUD
     * com filmes favoritos.
     * 
     * @param database Instância do [AppDatabase].
     * @return Instância do [FavoriteMovieDao].
     */
    @Provides
    fun provideFavoriteMovieDao(database: AppDatabase): FavoriteMovieDao {
        return database.favoriteMovieDao()
    }
}

