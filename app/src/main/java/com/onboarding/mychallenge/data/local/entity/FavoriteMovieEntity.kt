package com.onboarding.mychallenge.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.onboarding.mychallenge.data.local.converter.GenreListConverter
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
    val runtime: Int? = null,
    val genresJson: String? = null,
    val tagline: String? = null,
    val budget: Long = 0L,
    val revenue: Long = 0L,
    val status: String? = null,
    val homepage: String? = null,
    val addedAt: Long = System.currentTimeMillis()
)
