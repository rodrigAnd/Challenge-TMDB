package com.onboarding.mychallenge.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data Transfer Object (DTO) para representar um filme da API do TMDb.
 * 
 * Esta classe representa a estrutura JSON retornada pela API do TMDb para filmes
 * em listagens (popular, search). Utiliza anotações Moshi para mapear os campos
 * do JSON para propriedades Kotlin.
 * 
 * @property adult Indica se o filme é para adultos.
 * @property backdropPath Caminho relativo da imagem de fundo (pode ser null).
 * @property genreIds Lista de IDs dos gêneros do filme.
 * @property id Identificador único do filme.
 * @property originalLanguage Idioma original do filme.
 * @property originalTitle Título original do filme.
 * @property overview Sinopse ou descrição do filme.
 * @property popularity Pontuação de popularidade do filme.
 * @property posterPath Caminho relativo da imagem do pôster (pode ser null).
 * @property releaseDate Data de lançamento no formato YYYY-MM-DD (pode ser null).
 * @property title Título do filme.
 * @property video Indica se o filme tem vídeo disponível.
 * @property voteAverage Média de avaliações (0.0 a 10.0).
 * @property voteCount Número total de avaliações recebidas.
 * 
 * @constructor Cria uma nova instância de [MovieDto] com os parâmetros especificados.
 */
@JsonClass(generateAdapter = true)
data class MovieDto(
    val adult: Boolean,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "genre_ids") val genreIds: List<Int>,
    val id: Int,
    @Json(name = "original_language") val originalLanguage: String,
    @Json(name = "original_title") val originalTitle: String,
    val overview: String,
    val popularity: Double,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "release_date") val releaseDate: String?,
    val title: String,
    val video: Boolean,
    @Json(name = "vote_average") val voteAverage: Double,
    @Json(name = "vote_count") val voteCount: Int
)

