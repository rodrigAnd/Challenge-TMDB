package com.onboarding.mychallenge.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data Transfer Object (DTO) para representar detalhes completos de um filme da API do TMDb.
 * 
 * Esta classe representa a estrutura JSON retornada pela API do TMDb para detalhes
 * completos de um filme específico. Inclui informações adicionais não presentes em [MovieDto].
 * 
 * @property adult Indica se o filme é para adultos.
 * @property backdropPath Caminho relativo da imagem de fundo (pode ser null).
 * @property budget Orçamento do filme em dólares.
 * @property genres Lista de gêneros do filme.
 * @property homepage URL da página oficial do filme (pode ser null).
 * @property id Identificador único do filme.
 * @property imdbId ID do filme no IMDB (pode ser null).
 * @property originalLanguage Idioma original do filme.
 * @property originalTitle Título original do filme.
 * @property overview Sinopse ou descrição completa do filme.
 * @property popularity Pontuação de popularidade do filme.
 * @property posterPath Caminho relativo da imagem do pôster (pode ser null).
 * @property productionCompanies Lista de empresas de produção.
 * @property productionCountries Lista de países de produção.
 * @property releaseDate Data de lançamento no formato YYYY-MM-DD (pode ser null).
 * @property revenue Receita do filme em dólares.
 * @property runtime Duração do filme em minutos (pode ser null).
 * @property spokenLanguages Lista de idiomas falados no filme.
 * @property status Status de lançamento do filme (ex: "Released", "Post Production").
 * @property tagline Slogan ou frase de efeito do filme (pode ser null).
 * @property title Título do filme.
 * @property video Indica se o filme tem vídeo disponível.
 * @property voteAverage Média de avaliações (0.0 a 10.0).
 * @property voteCount Número total de avaliações recebidas.
 * 
 * @constructor Cria uma nova instância de [MovieDetailDto] com os parâmetros especificados.
 */
@JsonClass(generateAdapter = true)
data class MovieDetailDto(
    val adult: Boolean,
    @Json(name = "backdrop_path") val backdropPath: String?,
    val budget: Long,
    val genres: List<GenreDto>,
    val homepage: String?,
    val id: Int,
    @Json(name = "imdb_id") val imdbId: String?,
    @Json(name = "original_language") val originalLanguage: String,
    @Json(name = "original_title") val originalTitle: String,
    val overview: String,
    val popularity: Double,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "production_companies") val productionCompanies: List<ProductionCompanyDto>,
    @Json(name = "production_countries") val productionCountries: List<ProductionCountryDto>,
    @Json(name = "release_date") val releaseDate: String?,
    val revenue: Long,
    val runtime: Int?,
    @Json(name = "spoken_languages") val spokenLanguages: List<SpokenLanguageDto>,
    val status: String,
    val tagline: String?,
    val title: String,
    val video: Boolean,
    @Json(name = "vote_average") val voteAverage: Double,
    @Json(name = "vote_count") val voteCount: Int
)

/**
 * Data Transfer Object (DTO) para representar um gênero da API do TMDb.
 * 
 * @property id Identificador único do gênero.
 * @property name Nome do gênero (ex: "Ação", "Drama", "Comédia").
 * 
 * @constructor Cria uma nova instância de [GenreDto] com os parâmetros especificados.
 */
@JsonClass(generateAdapter = true)
data class GenreDto(
    val id: Int,
    val name: String
)

/**
 * Data Transfer Object (DTO) para representar uma empresa de produção da API do TMDb.
 * 
 * @property id Identificador único da empresa.
 * @property logoPath Caminho relativo do logo da empresa (pode ser null).
 * @property name Nome da empresa de produção.
 * @property originCountry País de origem da empresa.
 * 
 * @constructor Cria uma nova instância de [ProductionCompanyDto] com os parâmetros especificados.
 */
@JsonClass(generateAdapter = true)
data class ProductionCompanyDto(
    val id: Int,
    @Json(name = "logo_path") val logoPath: String?,
    val name: String,
    @Json(name = "origin_country") val originCountry: String
)

/**
 * Data Transfer Object (DTO) para representar um país de produção da API do TMDb.
 * 
 * @property iso31661 Código ISO 3166-1 do país.
 * @property name Nome do país.
 * 
 * @constructor Cria uma nova instância de [ProductionCountryDto] com os parâmetros especificados.
 */
@JsonClass(generateAdapter = true)
data class ProductionCountryDto(
    @Json(name = "iso_3166_1") val iso31661: String,
    val name: String
)

/**
 * Data Transfer Object (DTO) para representar um idioma falado da API do TMDb.
 * 
 * @property englishName Nome do idioma em inglês.
 * @property iso6391 Código ISO 639-1 do idioma.
 * @property name Nome do idioma.
 * 
 * @constructor Cria uma nova instância de [SpokenLanguageDto] com os parâmetros especificados.
 */
@JsonClass(generateAdapter = true)
data class SpokenLanguageDto(
    @Json(name = "english_name") val englishName: String,
    @Json(name = "iso_639_1") val iso6391: String,
    val name: String
)

