package com.onboarding.mychallenge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.onboarding.mychallenge.data.local.converter.GenreListConverter

/**
 * Entidade Room para armazenar filmes favoritos localmente com detalhes completos
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

