package com.onboarding.mychallenge.domain.model

/**
 * Modelo de domínio para detalhes completos de um filme.
 *
 * Esta classe estende as informações básicas de [Movie] com detalhes adicionais
 * obtidos da API, como tempo de duração, gêneros, orçamento, etc.
 *
 * @property id O identificador único do filme.
 * @property title O título do filme.
 * @property overview A sinopse ou descrição detalhada do filme.
 * @property posterPath O caminho relativo para a imagem do pôster do filme. Pode ser nulo.
 * @property backdropPath O caminho relativo para a imagem de fundo do filme. Pode ser nulo.
 * @property releaseDate A data de lançamento do filme no formato "YYYY-MM-DD". Pode ser nulo.
 * @property voteAverage A média de votos do filme.
 * @property voteCount O número total de votos recebidos pelo filme.
 * @property popularity A popularidade do filme.
 * @property runtime A duração do filme em minutos. Pode ser nulo.
 * @property genres A lista de gêneros associados ao filme.
 * @property tagline Uma frase de efeito ou slogan do filme. Pode ser nulo.
 * @property budget O orçamento de produção do filme.
 * @property revenue A receita total do filme.
 * @property status O status de lançamento do filme (ex: "Released").
 * @property homepage O URL da página inicial do filme. Pode ser nulo.
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
    val homepage: String?,
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
     * URL completa da imagem de backdrop (alta resolução).
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

    /**
     * Formata o runtime em horas e minutos (ex: "2h 30min").
     *
     * Retorna "N/A" se [runtime] for nulo.
     */
    val formattedRuntime: String
        get() =
            runtime?.let {
                val hours = it / 60
                val minutes = it % 60
                when {
                    hours > 0 -> "${hours}h ${minutes}min"
                    else -> "${minutes}min"
                }
            } ?: "N/A"

    /**
     * Lista de nomes dos gêneros.
     */
    val genreNames: List<String>
        get() = genres.map { it.name }

    /**
     * String formatada com gêneros separados por vírgula (ex: "Ação, Aventura, Ficção Científica").
     */
    val genresString: String
        get() = genreNames.joinToString(", ")
}

/**
 * Modelo de domínio para representar um gênero de filme.
 *
 * @property id O identificador único do gênero.
 * @property name O nome do gênero.
 */
data class Genre(
    val id: Int,
    val name: String,
)
