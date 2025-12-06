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
 * Módulo Hilt para configuração e fornecimento de dependências de rede.
 *
 * Este módulo instala suas dependências no [SingletonComponent], garantindo
 * que as instâncias fornecidas sejam de escopo de aplicação (Singleton).
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    // Token de autenticação da API TMDb (JWT)
    private const val BEARER_TOKEN_PART1 =
        "eyJhbGciOiJIUzI1NiJ9." +
            "eyJhdWQiOiIyNWQ2MzQ5NGU0NDVhZjYwNDM0M2VlMjg0OTQ2MTUyMiIsIm5iZiI6" +
            "MTY5NDE1MDcyNy41MzIsInN1YiI6IjY0ZmFiMDQ3YTM1YzhlMDBmZmQwYzI4MCIs" +
            "InNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ"
    private const val BEARER_TOKEN_PART2 =
        "vyEV0eKplrHdXf_HFPKg38RN8tMp6ofP59Dnr-rDr2w"
    private const val BEARER_TOKEN = "$BEARER_TOKEN_PART1.$BEARER_TOKEN_PART2"

    /**
     * Fornece uma instância singleton de [Moshi] para serialização/desserialização JSON.
     *
     * @return Uma instância de [Moshi] configurada com [KotlinJsonAdapterFactory].
     */
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    /**
     * Fornece uma instância singleton de [HttpLoggingInterceptor] para log de requisições HTTP.
     *
     * O nível de log é definido como [HttpLoggingInterceptor.Level.BODY] para incluir
     * cabeçalhos e corpos de requisição/resposta.
     *
     * @return Uma instância de [HttpLoggingInterceptor].
     */
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    /**
     * Fornece uma instância singleton de [AuthInterceptor] para adicionar o token Bearer.
     *
     * @return Uma instância de [AuthInterceptor] com o token Bearer configurado.
     */
    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor {
        return AuthInterceptor(BEARER_TOKEN)
    }

    /**
     * Fornece uma instância singleton de [OkHttpClient] configurada com interceptores e timeouts.
     *
     * @param loggingInterceptor O interceptor para log de requisições.
     * @param authInterceptor O interceptor para adicionar o token de autenticação.
     * @return Uma instância de [OkHttpClient].
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Fornece uma instância singleton de [Retrofit] para comunicação com a API.
     *
     * @param okHttpClient O cliente HTTP configurado.
     * @param moshi A instância de Moshi para conversão JSON.
     * @return Uma instância de [Retrofit].
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * Fornece uma instância singleton de [TmdbApiService] para interagir com a API do TMDb.
     *
     * @param retrofit A instância de Retrofit configurada.
     * @return Uma implementação de [TmdbApiService].
     */
    @Provides
    @Singleton
    fun provideTmdbApiService(retrofit: Retrofit): TmdbApiService {
        return retrofit.create(TmdbApiService::class.java)
    }
}
