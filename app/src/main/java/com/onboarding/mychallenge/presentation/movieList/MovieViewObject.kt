package com.onboarding.mychallenge.presentation.movieList

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Representa um objeto de filme pronto para ser exibido na UI (View).
 *
 * Esta classe é responsável por conter não apenas os dados brutos,
 * mas também as versões formatadas desses dados (como datas e avaliações),
 * garantindo que a lógica de apresentação fique isolada do domínio.
 *
 * @property id O identificador único do filme.
 * @property title O título do filme.
 * @property overview A sinopse do filme.
 * @property posterUrl A URL completa para a imagem do pôster.
 * @property backdropUrl A URL completa para a imagem de fundo.
 * @property releaseDate A data de lançamento original (formato "YYYY-MM-DD").
 * @property voteAverage A média de votos bruta (ex: 8.567).
 * @property voteCount O número total de votos.
 * @property popularity A popularidade do filme.
 * @property isFavorite Indica se o filme está marcado como favorito.
 * @property isLoadingFavorite Indica se uma operação de favoritar está em andamento para este filme.
 */
data class MovieViewObject(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String,
    val releaseDate: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val isFavorite: Boolean = false,
    val isLoadingFavorite: Boolean = false,
) {
    /**
     * Formata a avaliação para exibição, arredondando para uma casa decimal (ex: "8.6").
     *
     * Usa Locale.US para garantir que o separador decimal seja sempre um PONTO (.),
     * evitando inconsistências entre diferentes configurações de sistema (local vs. CI/CD).
     */
    val formattedRating: String
        get() = String.format(Locale.US, "%.1f", voteAverage)

    /**
     * Formata a data de lançamento para o padrão brasileiro (dd/MM/yyyy).
     *
     * Retorna "Data não disponível" se a data original for nula, vazia ou inválida.
     */
    val formattedReleaseDate: String
        get() {
            if (releaseDate.isNullOrBlank()) {
                return "Data não disponível"
            }
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formatter.format(parser.parse(releaseDate)!!)
            } catch (e: Exception) {
                releaseDate // Retorna a data original se o parsing falhar
            }
        }

    /**
     * Extrai apenas o ano da data de lançamento.
     *
     * Retorna "N/A" se a data não estiver disponível.
     */
    val releaseYear: String
        get() = releaseDate?.take(4) ?: "N/A"

    /**
     * Indica se existe uma URL de pôster válida.
     */
    val hasPoster: Boolean
        get() = posterUrl.isNotEmpty()

    /**
     * Indica se existe uma URL de backdrop válida.
     */
    val hasBackdrop: Boolean
        get() = backdropUrl.isNotEmpty()
}
