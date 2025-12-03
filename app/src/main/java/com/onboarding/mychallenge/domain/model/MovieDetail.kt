package com.onboarding.mychallenge.domain.model

/**
 * Modelo de domínio para detalhes completos de um filme.
 * 
 * Esta classe estende as informações básicas de [Movie] com dados adicionais
 * obtidos da API do TMDb, incluindo informações financeiras, gêneros, runtime
 * e outros metadados detalhados.
 * 
 * @property id Identificador único do filme.
 * @property title Título do filme.
 * @property overview Sinopse ou descrição completa do filme.
 * @property posterPath Caminho relativo da imagem do pôster (pode ser null).
 * @property backdropPath Caminho relativo da imagem de fundo (pode ser null).
 * @property releaseDate Data de lançamento no formato YYYY-MM-DD (pode ser null).
 * @property voteAverage Média de avaliações (0.0 a 10.0).
 * @property voteCount Número total de avaliações recebidas.
 * @property popularity Pontuação de popularidade do filme.
 * @property runtime Duração do filme em minutos (pode ser null).
 * @property genres Lista de gêneros associados ao filme.
 * @property tagline Slogan ou frase de efeito do filme (pode ser null).
 * @property budget Orçamento do filme em dólares.
 * @property revenue Receita do filme em dólares.
 * @property status Status de lançamento do filme (ex: "Released", "Post Production").
 * @property homepage URL da página oficial do filme (pode ser null).
 * 
 * @constructor Cria uma nova instância de [MovieDetail] com os parâmetros especificados.
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
     * URL completa da imagem do pôster.
     * 
     * Constrói a URL completa usando o caminho base do TMDb e o tamanho w500.
     * Retorna uma string vazia se [posterPath] for null.
     * 
     * @return URL completa da imagem do pôster ou string vazia.
     */
    val posterUrl: String
        get() = if (posterPath != null) {
            "https://image.tmdb.org/t/p/w500$posterPath"
        } else {
            ""
        }
    
    /**
     * URL completa da imagem de backdrop em alta resolução.
     * 
     * Constrói a URL completa usando o caminho base do TMDb e o tamanho w1280.
     * Retorna uma string vazia se [backdropPath] for null.
     * 
     * @return URL completa da imagem de backdrop ou string vazia.
     */
    val backdropUrl: String
        get() = if (backdropPath != null) {
            "https://image.tmdb.org/t/p/w1280$backdropPath"
        } else {
            ""
        }
    
    /**
     * Formata a avaliação média para exibição.
     * 
     * Converte [voteAverage] para uma string formatada com uma casa decimal.
     * 
     * @return String formatada da avaliação (ex: "8.5").
     */
    val formattedRating: String
        get() = String.format("%.1f", voteAverage)
    
    /**
     * Formata a duração do filme em horas e minutos.
     * 
     * Converte [runtime] (em minutos) para uma string legível no formato
     * "Xh Ymin" ou apenas "Ymin" se for menor que uma hora.
     * Retorna "N/A" se [runtime] for null.
     * 
     * @return String formatada da duração ou "N/A" se não disponível.
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
     * Lista de nomes dos gêneros do filme.
     * 
     * Extrai apenas os nomes da lista de [genres].
     * 
     * @return Lista de strings com os nomes dos gêneros.
     */
    val genreNames: List<String>
        get() = genres.map { it.name }
    
    /**
     * String formatada com gêneros separados por vírgula.
     * 
     * Converte a lista de gêneros em uma string legível para exibição.
     * 
     * @return String com gêneros separados por vírgula (ex: "Ação, Drama, Suspense").
     */
    val genresString: String
        get() = genreNames.joinToString(", ")
}

/**
 * Modelo de domínio para representar um gênero de filme.
 * 
 * @property id Identificador único do gênero.
 * @property name Nome do gênero (ex: "Ação", "Drama", "Comédia").
 * 
 * @constructor Cria uma nova instância de [Genre] com os parâmetros especificados.
 */
data class Genre(
    val id: Int,
    val name: String
)

