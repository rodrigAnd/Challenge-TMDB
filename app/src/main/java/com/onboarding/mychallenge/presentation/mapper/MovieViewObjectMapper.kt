package com.onboarding.mychallenge.presentation.mapper

import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.presentation.movieList.MovieViewObject

/**
 * Converte um objeto [Movie] da camada de domínio para um [MovieViewObject] da camada de apresentação.
 *
 * @param isFavorite Indica se o filme está marcado como favorito.
 * @param isLoadingFavorite Indica se uma operação de favoritar está em andamento para este filme.
 * @return Um objeto [MovieViewObject] pronto para ser usado pela UI.
 */
fun Movie.toViewObject(
    isFavorite: Boolean = false,
    isLoadingFavorite: Boolean = false,
): MovieViewObject {
    return MovieViewObject(
        id = this.id,
        title = this.title,
        overview = this.overview,
        posterUrl = this.posterUrl,
        backdropUrl = this.backdropUrl,
        releaseDate = this.releaseDate,
        voteAverage = this.voteAverage,
        voteCount = this.voteCount,
        popularity = this.popularity,
        isFavorite = isFavorite,
        isLoadingFavorite = isLoadingFavorite,
    )
}

/**
 * Converte uma lista de [Movie] da camada de domínio para uma lista de [MovieViewObject].
 *
 * Esta função de conveniência aplica o mapeamento a cada item da lista,
 * enriquecendo cada `MovieViewObject` com o estado de 'favorito' e 'loading'
 * com base nos conjuntos de IDs fornecidos.
 *
 * @param favoriteIds Um conjunto de IDs de filmes que estão marcados como favoritos.
 * @param loadingFavoriteIds Um conjunto de IDs de filmes que estão com uma operação de favorito em andamento.
 * @return Uma lista de [MovieViewObject].
 */
fun List<Movie>.toViewObjectList(
    favoriteIds: Set<Int> = emptySet(),
    loadingFavoriteIds: Set<Int> = emptySet(),
): List<MovieViewObject> {
    return this.map { movie ->
        movie.toViewObject(
            isFavorite = favoriteIds.contains(movie.id),
            isLoadingFavorite = loadingFavoriteIds.contains(movie.id),
        )
    }
}
