package com.onboarding.mychallenge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.onboarding.mychallenge.data.local.converter.GenreListConverter

/**
 * Entidade Room para armazenar filmes favoritos localmente com detalhes completos.
 * 
 * Representa a estrutura de dados para armazenar filmes favoritos no banco de dados local.
 * Inclui campos básicos de [Movie] e campos adicionais de [MovieDetail] para permitir
 * acesso offline aos detalhes completos dos filmes favoritos.
 * 
 * @property id Identificador único do filme (chave primária).
 * @property title Título do filme.
 * @property overview Sinopse ou descrição do filme.
 * @property posterPath Caminho relativo da imagem do pôster (pode ser null).
 * @property backdropPath Caminho relativo da imagem de fundo (pode ser null).
 * @property releaseDate Data de lançamento no formato YYYY-MM-DD (pode ser null).
 * @property voteAverage Média de avaliações (0.0 a 10.0).
 * @property voteCount Número total de avaliações recebidas.
 * @property popularity Pontuação de popularidade do filme.
 * @property runtime Duração do filme em minutos (pode ser null).
 * @property genresJson String JSON contendo a lista de gêneros serializados (pode ser null).
 * @property tagline Slogan ou frase de efeito do filme (pode ser null).
 * @property budget Orçamento do filme em dólares (padrão: 0L).
 * @property revenue Receita do filme em dólares (padrão: 0L).
 * @property status Status de lançamento do filme (pode ser null).
 * @property homepage URL da página oficial do filme (pode ser null).
 * @property addedAt Timestamp de quando o filme foi adicionado aos favoritos (padrão: tempo atual).
 *                    Usado para ordenar os favoritos por data de adição.
 * 
 * @constructor Cria uma nova instância de [FavoriteMovieEntity] com os parâmetros especificados.
 */
@Entity(tableName = "favorite_movies")
@TypeConverters(GenreListConverter::class)
data class FavoriteMovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    // Campos de MovieDetail
    val runtime: Int? = null,
    val genresJson: String? = null, // JSON string dos gêneros
    val tagline: String? = null,
    val budget: Long = 0L,
    val revenue: Long = 0L,
    val status: String? = null,
    val homepage: String? = null,
    val addedAt: Long = System.currentTimeMillis() // Timestamp para ordenar por data de adição
)

