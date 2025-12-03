package com.onboarding.mychallenge.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data Transfer Object (DTO) para representar a resposta da API do TMDb com lista de filmes.
 * 
 * Esta classe representa a estrutura JSON retornada pela API do TMDb para endpoints
 * que retornam listas paginadas de filmes (popular, search).
 * 
 * @property page Número da página atual.
 * @property results Lista de filmes retornados nesta página.
 * @property totalPages Número total de páginas disponíveis.
 * @property totalResults Número total de resultados disponíveis.
 * 
 * @constructor Cria uma nova instância de [MoviesResponseDto] com os parâmetros especificados.
 */
@JsonClass(generateAdapter = true)
data class MoviesResponseDto(
    val page: Int,
    val results: List<MovieDto>,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
)

