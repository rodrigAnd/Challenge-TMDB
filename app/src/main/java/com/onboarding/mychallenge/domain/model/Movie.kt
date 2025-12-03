package com.onboarding.mychallenge.domain.model

/**
 * Modelo de domínio para representar um filme.
 * 
 * Esta classe encapsula as informações básicas de um filme obtidas da API do TMDb,
 * incluindo dados de identificação, informações de mídia e métricas de avaliação.
 * 
 * @property id Identificador único do filme.
 * @property title Título do filme.
 * @property overview Sinopse ou descrição do filme.
 * @property posterPath Caminho relativo da imagem do pôster (pode ser null).
 * @property backdropPath Caminho relativo da imagem de fundo (pode ser null).
 * @property releaseDate Data de lançamento do filme no formato YYYY-MM-DD (pode ser null).
 * @property voteAverage Média de avaliações (0.0 a 10.0).
 * @property voteCount Número total de avaliações recebidas.
 * @property popularity Pontuação de popularidade do filme.
 * 
 * @constructor Cria uma nova instância de [Movie] com os parâmetros especificados.
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
     * URL completa da imagem de backdrop.
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
}

