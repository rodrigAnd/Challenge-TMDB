package com.onboarding.mychallenge.data.mapper
import com.onboarding.mychallenge.data.local.entity.FavoriteMovieEntity
import com.onboarding.mychallenge.data.remote.dto.GenreDto
import com.onboarding.mychallenge.data.remote.dto.MovieDetailDto
import com.onboarding.mychallenge.data.remote.dto.MovieDto
import com.onboarding.mychallenge.domain.model.Genre
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.model.MovieDetail
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity
    )
}
fun FavoriteMovieEntity.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity
    )
}
fun FavoriteMovieEntity.toMovieDetail(): MovieDetail {
    val genres = genresJson?.let { json ->
        try {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val listType = Types.newParameterizedType(List::class.java, Genre::class.java)
            val adapter = moshi.adapter<List<Genre>>(listType)
            adapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            android.util.Log.e("MovieMapper", "Error parsing genres JSON: $json", e)
            emptyList()
        }
    } ?: emptyList()
    return MovieDetail(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
        runtime = runtime,
        genres = genres,
        tagline = tagline,
        budget = budget,
        revenue = revenue,
        status = status ?: "Unknown",
        homepage = homepage
    )
}
fun Movie.toEntity(): FavoriteMovieEntity {
    return FavoriteMovieEntity(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity
    )
}
fun MovieDetail.toEntity(): FavoriteMovieEntity {
    val genresJson = try {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val listType = Types.newParameterizedType(List::class.java, Genre::class.java)
        val adapter = moshi.adapter<List<Genre>>(listType)
        adapter.toJson(genres)
    } catch (e: Exception) {
        android.util.Log.e("MovieMapper", "Error converting genres to JSON", e)
        null
    }
    return FavoriteMovieEntity(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
        runtime = runtime,
        genresJson = genresJson,
        tagline = tagline,
        budget = budget,
        revenue = revenue,
        status = status,
        homepage = homepage
    )
}
fun MovieDetailDto.toDomain(): MovieDetail {
    return MovieDetail(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
        runtime = runtime,
        genres = genres.map { it.toDomain() },
        tagline = tagline,
        budget = budget,
        revenue = revenue,
        status = status,
        homepage = homepage
    )
}
fun GenreDto.toDomain(): Genre {
    return Genre(
        id = id,
        name = name
    )
}
