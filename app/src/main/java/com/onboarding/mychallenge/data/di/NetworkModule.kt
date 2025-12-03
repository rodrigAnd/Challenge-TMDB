package com.onboarding.mychallenge.data.di

import com.onboarding.mychallenge.data.remote.api.TmdbApiService
import com.onboarding.mychallenge.data.remote.interceptor.AuthInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Módulo Hilt para configuração da camada de rede.
 * 
 * Fornece as dependências necessárias para comunicação com a API do TMDb,
 * incluindo configuração do Retrofit, OkHttp, Moshi e interceptores.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val BEARER_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyNWQ2MzQ5NGU0NDVhZjYwNDM0M2VlMjg0OTQ2MTUyMiIsIm5iZiI6MTY5NDE1MDcyNy41MzIsInN1YiI6IjY0ZmFiMDQ3YTM1YzhlMDBmZmQwYzI4MCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.vyEV0eKplrHdXf_HFPKg38RN8tMp6ofP59Dnr-rDr2w"
    
    /**
     * Fornece uma instância do Moshi para serialização/desserialização JSON.
     * 
     * Configura o Moshi com suporte a classes Kotlin usando [KotlinJsonAdapterFactory].
     * 
     * @return Instância configurada do [Moshi].
     */
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    
    /**
     * Fornece um interceptor de logging HTTP para debug.
     * 
     * Configurado para logar o corpo completo das requisições e respostas HTTP.
     * 
     * @return Instância configurada do [HttpLoggingInterceptor].
     */
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    /**
     * Fornece um interceptor de autenticação para adicionar o Bearer token.
     * 
     * Cria uma instância do [AuthInterceptor] com o token de autenticação
     * necessário para acessar a API do TMDb.
     * 
     * @return Instância do [AuthInterceptor] configurada com o token.
     */
    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor {
        return AuthInterceptor(BEARER_TOKEN)
    }
    
    /**
     * Fornece uma instância do OkHttpClient configurada.
     * 
     * Configura o cliente HTTP com interceptores de autenticação e logging,
     * além de timeouts para conexão, leitura e escrita.
     * 
     * @param loggingInterceptor Interceptor para logging de requisições HTTP.
     * @param authInterceptor Interceptor para adicionar autenticação Bearer token.
     * @return Instância configurada do [OkHttpClient].
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // Adiciona Bearer token
            .addInterceptor(loggingInterceptor) // Logging para debug
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Fornece uma instância do Retrofit configurada.
     * 
     * Configura o Retrofit com a URL base da API do TMDb, cliente OkHttp
     * e conversor Moshi para serialização JSON.
     * 
     * @param okHttpClient Cliente HTTP configurado.
     * @param moshi Instância do Moshi para conversão JSON.
     * @return Instância configurada do [Retrofit].
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    
    /**
     * Fornece uma instância do serviço da API do TMDb.
     * 
     * Cria uma implementação da interface [TmdbApiService] usando o Retrofit.
     * 
     * @param retrofit Instância do Retrofit configurada.
     * @return Implementação do [TmdbApiService].
     */
    @Provides
    @Singleton
    fun provideTmdbApiService(retrofit: Retrofit): TmdbApiService {
        return retrofit.create(TmdbApiService::class.java)
    }
}

