package com.onboarding.mychallenge.domain.model

/**
 * Modelo de domínio para representar um filme.
 *
 * Esta classe contém os dados essenciais de um filme, independentemente
 * da sua origem (API ou banco de dados local).
 *
 * @property id O identificador único do filme.
 * @property title O título do filme.
 * @property overview A sinopse ou descrição breve do filme.
 * @property posterPath O caminho relativo para a imagem do pôster do filme. Pode ser nulo.
 * @property backdropPath O caminho relativo para a imagem de fundo do filme. Pode ser nulo.
 * @property releaseDate A data de lançamento do filme no formato "YYYY-MM-DD". Pode ser nulo.
 * @property voteAverage A média de votos do filme.
 * @property voteCount O número total de votos recebidos pelo filme.
 * @property popularity A popularidade do filme.
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
    val popularity: Double,
) {
    /**
     * URL completa da imagem do pôster.
     *
     * Retorna uma string vazia se [posterPath] for nulo.
     */
    val posterUrl: String
        get() =
            if (posterPath != null) {
                "https://image.tmdb.org/t/p/w500$posterPath"
            } else {
                ""
            }

    /**
     * URL completa da imagem de backdrop.
     *
     * Retorna uma string vazia se [backdropPath] for nulo.
     */
    val backdropUrl: String
        get() =
            if (backdropPath != null) {
                "https://image.tmdb.org/t/p/w1280$backdropPath"
            } else {
                ""
            }

    /**
     * Formata a avaliação para exibição (ex: "8.5").
     */
    val formattedRating: String
        get() = String.format("%.1f", voteAverage)
}
