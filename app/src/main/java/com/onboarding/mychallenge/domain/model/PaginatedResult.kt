package com.onboarding.mychallenge.domain.model

/**
 * Resultado paginado de uma consulta.
 *
 * Encapsula os dados retornados junto com informações de paginação.
 *
 * @param data A lista de itens da página atual.
 * @param currentPage O número da página atual.
 * @param totalPages O número total de páginas disponíveis.
 * @param totalResults O número total de resultados disponíveis.
 */
data class PaginatedResult<T>(
    val data: List<T>,
    val currentPage: Int,
    val totalPages: Int,
    val totalResults: Int,
) {
    /**
     * Indica se há mais páginas disponíveis para carregar.
     */
    val hasMorePages: Boolean
        get() = currentPage < totalPages

    /**
     * Indica se esta é a última página.
     */
    val isLastPage: Boolean
        get() = currentPage >= totalPages
}
