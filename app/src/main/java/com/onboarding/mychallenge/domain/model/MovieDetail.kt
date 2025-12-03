package com.onboarding.mychallenge.domain.model

/**
 * Modelo de domínio para detalhes completos de um filme
 */
data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val runtime: Int?,
    val genres: List<Genre>,
    val tagline: String?,
    val budget: Long,
    val revenue: Long,
    val status: String,
    val homepage: String?
) {
    /**
     * URL completa da imagem do pôster
     */
    val posterUrl: String
        get() = if (posterPath != null) {
            "https://image.tmdb.org/t/p/w500$posterPath"
        } else {
            ""
        }
    
    /**
     * URL completa da imagem de backdrop (alta resolução)
     */
    val backdropUrl: String
        get() = if (backdropPath != null) {
            "https://image.tmdb.org/t/p/w1280$backdropPath"
        } else {
            ""
        }
    
    /**
     * Formata a avaliação para exibição
     */
    val formattedRating: String
        get() = String.format("%.1f", voteAverage)
    
    /**
     * Formata o runtime em horas e minutos
     */
    val formattedRuntime: String
        get() = runtime?.let {
            val hours = it / 60
            val minutes = it % 60
            when {
                hours > 0 -> "${hours}h ${minutes}min"
                else -> "${minutes}min"
            }
        } ?: "N/A"
    
    /**
     * Lista de nomes dos gêneros
     */
    val genreNames: List<String>
        get() = genres.map { it.name }
    
    /**
     * String formatada com gêneros separados por vírgula
     */
    val genresString: String
        get() = genreNames.joinToString(", ")
}

/**
 * Modelo de domínio para gênero
 */
data class Genre(
    val id: Int,
    val name: String
)

