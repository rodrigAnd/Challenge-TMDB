package com.onboarding.mychallenge.data.remote.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor para adicionar o Bearer token de autenticação em todas as requisições HTTP.
 * 
 * Este interceptor intercepta todas as requisições HTTP feitas pelo OkHttpClient
 * e adiciona automaticamente o header de autorização com o Bearer token necessário
 * para autenticar requisições à API do TMDb.
 * 
 * @property bearerToken Token de autenticação Bearer para a API do TMDb.
 * 
 * @constructor Cria uma nova instância do [AuthInterceptor] com o token especificado.
 */
class AuthInterceptor(
    private val bearerToken: String
) : Interceptor {
    
    companion object {
        private const val TAG = "AuthInterceptor"
    }
    
    /**
     * Intercepta a requisição HTTP e adiciona os headers de autenticação.
     * 
     * Adiciona os seguintes headers à requisição:
     * - `Authorization`: Bearer token para autenticação
     * - `accept`: application/json para indicar o tipo de conteúdo esperado
     * 
     * @param chain Cadeia de interceptores que contém a requisição original.
     * @return Resposta HTTP após processar a requisição modificada.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $bearerToken")
            .header("accept", "application/json")
            .build()
        
        Log.d(TAG, "intercept: URL = ${newRequest.url}")
        Log.d(TAG, "intercept: Headers = ${newRequest.headers}")
        
        val response = chain.proceed(newRequest)
        Log.d(TAG, "intercept: Response Status = ${response.code}")
        return response
    }
}

