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
 * Este módulo instala suas dependências no [SingletonComponent], garantindo
 * que as instâncias fornecidas sejam de escopo de aplicação (Singleton).
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    /**
     * Fornece uma instância singleton de [AppDatabase].
     *
     * O banco de dados é construído usando [Room.databaseBuilder] e configurado
     * para migração destrutiva em caso de mudanças de esquema.
     *
     * @param context O contexto da aplicação, injetado pelo Hilt.
     * @return Uma instância de [AppDatabase].
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
     * Fornece uma instância de [FavoriteMovieDao] a partir do [AppDatabase].
     *
     * @param database A instância do [AppDatabase].
     * @return Uma instância de [FavoriteMovieDao].
     */
    @Provides
    fun provideFavoriteMovieDao(database: AppDatabase): FavoriteMovieDao {
        return database.favoriteMovieDao()
    }
}
