package com.onboarding.mychallenge.presentation.movieList

/**
 * ViewObject para representar um filme na UI.
 * 
 * Otimizado para exibição na tela de lista de filmes, contendo apenas os dados
 * necessários para renderização e estado da UI. Esta classe é usada para separar
 * os modelos de domínio da camada de apresentação.
 * 
 * @property id Identificador único do filme.
 * @property title Título do filme.
 * @property overview Sinopse ou descrição do filme.
 * @property posterUrl URL completa da imagem do pôster.
 * @property backdropUrl URL completa da imagem de fundo.
 * @property releaseDate Data de lançamento no formato YYYY-MM-DD (pode ser null).
 * @property formattedReleaseDate Data de lançamento formatada para exibição.
 * @property rating Média de avaliações (0.0 a 10.0).
 * @property formattedRating Avaliação formatada para exibição (ex: "8.5").
 * @property voteCount Número total de avaliações recebidas.
 * @property popularity Pontuação de popularidade do filme.
 * @property isFavorite Indica se o filme está nos favoritos (padrão: false).
 * @property isLoadingFavorite Indica se está processando uma operação de favorito (padrão: false).
 * 
 * @constructor Cria uma nova instância de [MovieViewObject] com os parâmetros especificados.
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
    /**
     * Ano de lançamento extraído da data.
     * 
     * Extrai os primeiros 4 caracteres de [releaseDate] para obter o ano.
     * Retorna "N/A" se [releaseDate] for null.
     * 
     * @return String com o ano de lançamento ou "N/A".
     */
    val releaseYear: String
        get() = releaseDate?.take(4) ?: "N/A"
    
    /**
     * Indica se o filme possui uma URL de pôster válida.
     * 
     * @return `true` se [posterUrl] não estiver vazia, `false` caso contrário.
     */
    val hasPoster: Boolean
        get() = posterUrl.isNotEmpty()
    
    /**
     * Indica se o filme possui uma URL de backdrop válida.
     * 
     * @return `true` se [backdropUrl] não estiver vazia, `false` caso contrário.
     */
    val hasBackdrop: Boolean
        get() = backdropUrl.isNotEmpty()
}
