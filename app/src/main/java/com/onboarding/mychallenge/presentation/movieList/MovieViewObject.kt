package com.onboarding.mychallenge.presentation.movieList

/**
 * ViewObject para representar um filme na UI
 * Otimizado para exibição na tela de lista de filmes
 */
data class MovieViewObject(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String,
    val releaseDate: String?,
    val formattedReleaseDate: String,
    val rating: Double,
    val formattedRating: String,
    val voteCount: Int,
    val popularity: Double,
    val isFavorite: Boolean = false,
    val isLoadingFavorite: Boolean = false // Indica se está processando favorito
) {
    val releaseYear: String
        get() = releaseDate?.take(4) ?: "N/A"
    
    val hasPoster: Boolean
        get() = posterUrl.isNotEmpty()
    
    val hasBackdrop: Boolean
        get() = backdropUrl.isNotEmpty()
}
