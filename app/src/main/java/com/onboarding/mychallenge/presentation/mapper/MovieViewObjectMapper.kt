package com.onboarding.mychallenge.presentation.mapper

import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.presentation.movieList.MovieViewObject
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Mapper para converter Domain Models em ViewObjects
 * 
 * Responsabilidade: Converter dados da camada Domain para Presentation
 * Adiciona formatações específicas para UI sem poluir o Domain Model
 */

/**
 * Converte Movie (Domain) para MovieViewObject (Presentation)
 * Usado na lista de filmes
 */
fun Movie.toViewObject(isFavorite: Boolean = false, isLoadingFavorite: Boolean = false): MovieViewObject {
    return MovieViewObject(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        releaseDate = releaseDate,
        formattedReleaseDate = formatReleaseDate(releaseDate),
        rating = voteAverage,
        formattedRating = formattedRating,
        voteCount = voteCount,
        popularity = popularity,
        isFavorite = isFavorite,
        isLoadingFavorite = isLoadingFavorite
    )
}

/**
 * Converte lista de Movies para lista de MovieViewObjects
 */
fun List<Movie>.toViewObjectList(
    favoriteIds: Set<Int> = emptySet(),
    loadingFavoriteIds: Set<Int> = emptySet()
): List<MovieViewObject> {
    return map { movie ->
        movie.toViewObject(
            isFavorite = favoriteIds.contains(movie.id),
            isLoadingFavorite = loadingFavoriteIds.contains(movie.id)
        )
    }
}

/**
 * Formata a data de lançamento para exibição
 * Exemplo: "2024-10-01" → "01/10/2024"
 */
private fun formatReleaseDate(dateString: String?): String {
    if (dateString.isNullOrBlank()) return "Data não disponível"
    
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString // Retorna a data original se houver erro no parsing
    }
}

