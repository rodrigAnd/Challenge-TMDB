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

/**
 * Mapper para converter entre DTOs, Entities e Models de domínio
 * 
 * Responsabilidade: Converter dados da camada Data para Domain
 * Nunca expor DTOs ou Entities diretamente para outras camadas
 */

/**
 * Converte MovieDto (API) para Movie (Domain)
 */
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

/**
 * Converte FavoriteMovieEntity (Room) para Movie (Domain)
 */
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

/**
 * Converte FavoriteMovieEntity (Room) para MovieDetail (Domain)
 */
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

/**
 * Converte Movie (Domain) para FavoriteMovieEntity (Room)
 */
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

/**
 * Converte MovieDetail (Domain) para FavoriteMovieEntity (Room)
 */
fun MovieDetail.toEntity(): FavoriteMovieEntity {
    val genresJson = try {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val listType = Types.newParameterizedType(List::class.java, Genre::class.java)
        val adapter = moshi.adapter<List<Genre>>(listType)
        adapter.toJson(genres)
    } catch (e: Exception) {
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

/**
 * Converte MovieDetailDto (API) para MovieDetail (Domain)
 */
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

/**
 * Converte GenreDto para Genre
 */
fun GenreDto.toDomain(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

