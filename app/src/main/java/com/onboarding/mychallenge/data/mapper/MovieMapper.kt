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
 * Mapper para converter entre DTOs, Entities e Models de domínio.
 * 
 * Responsabilidade: Converter dados da camada Data para Domain.
 * Nunca expor DTOs ou Entities diretamente para outras camadas.
 * 
 * Este arquivo contém funções de extensão para realizar conversões entre:
 * - DTOs (Data Transfer Objects) da API remota
 * - Entities do banco de dados local (Room)
 * - Models de domínio
 */

/**
 * Converte [MovieDto] (API) para [Movie] (Domain).
 * 
 * Transforma um DTO recebido da API do TMDb em um modelo de domínio,
 * extraindo apenas os campos necessários para a camada de domínio.
 * 
 * @return [Movie] com os dados convertidos do DTO.
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
 * Converte [FavoriteMovieEntity] (Room) para [Movie] (Domain).
 * 
 * Transforma uma entidade do banco de dados local em um modelo de domínio,
 * extraindo apenas os campos básicos do filme.
 * 
 * @return [Movie] com os dados convertidos da entidade.
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
 * Converte [FavoriteMovieEntity] (Room) para [MovieDetail] (Domain).
 * 
 * Transforma uma entidade do banco de dados local em um modelo de domínio
 * com detalhes completos, incluindo a deserialização dos gêneros do JSON.
 * 
 * @return [MovieDetail] com os dados convertidos da entidade.
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
 * Converte [Movie] (Domain) para [FavoriteMovieEntity] (Room).
 * 
 * Transforma um modelo de domínio em uma entidade do banco de dados local
 * para armazenamento. Apenas campos básicos são salvos.
 * 
 * @return [FavoriteMovieEntity] com os dados convertidos do modelo de domínio.
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
 * Converte [MovieDetail] (Domain) para [FavoriteMovieEntity] (Room).
 * 
 * Transforma um modelo de domínio com detalhes completos em uma entidade
 * do banco de dados local, incluindo a serialização dos gêneros para JSON.
 * 
 * @return [FavoriteMovieEntity] com os dados convertidos do modelo de domínio.
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
 * Converte [MovieDetailDto] (API) para [MovieDetail] (Domain).
 * 
 * Transforma um DTO recebido da API do TMDb em um modelo de domínio com
 * detalhes completos, incluindo a conversão dos gêneros.
 * 
 * @return [MovieDetail] com os dados convertidos do DTO.
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
 * Converte [GenreDto] (API) para [Genre] (Domain).
 * 
 * Transforma um DTO de gênero recebido da API do TMDb em um modelo de domínio.
 * 
 * @return [Genre] com os dados convertidos do DTO.
 */
fun GenreDto.toDomain(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

