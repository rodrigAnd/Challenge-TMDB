package com.onboarding.mychallenge.domain.model

/**
 * Modelo de domínio para representar um filme
 */
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double
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
     * URL completa da imagem de backdrop
     */
    val backdropUrl: String
        get() = if (backdropPath != null) {
            "https://image.tmdb.org/t/p/w1280$backdropPath"
        } else {
            ""
        }
    
    /**
     * Formata a avaliação para exibição (ex: 8.5)
     */
    val formattedRating: String
        get() = String.format("%.1f", voteAverage)
}

