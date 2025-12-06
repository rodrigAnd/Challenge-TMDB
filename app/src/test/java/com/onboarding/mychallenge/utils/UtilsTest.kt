package com.onboarding.mychallenge.utils

import com.onboarding.mychallenge.data.remote.dto.GenreDto
import com.onboarding.mychallenge.data.remote.dto.MovieDetailDto
import com.onboarding.mychallenge.data.remote.dto.ProductionCompanyDto
import com.onboarding.mychallenge.data.remote.dto.ProductionCountryDto
import com.onboarding.mychallenge.data.remote.dto.SpokenLanguageDto
import com.onboarding.mychallenge.domain.model.Genre
import com.onboarding.mychallenge.domain.model.Movie
import com.onboarding.mychallenge.domain.model.MovieDetail
import com.onboarding.mychallenge.presentation.movieList.MovieViewObject

fun createMovieDetailDto(id: Int, title: String): MovieDetailDto {
    val exampleCompany =
        ProductionCompanyDto(id = 1, logoPath = "/logo.png", name = "Test Pictures", originCountry = "US")
    val exampleCountry = ProductionCountryDto(iso31661 = "US", name = "United States of America")
    val exampleLanguage = SpokenLanguageDto(englishName = "English", iso6391 = "en", name = "English")
    val exampleGenre = GenreDto(id = 28, name = "Action")

    return MovieDetailDto(
        adult = false, // Campo adicionado
        backdropPath = "/backdrop.jpg",
        budget = 50000000L,
        genres = listOf(exampleGenre), // Preenchido com um exemplo
        homepage = "https://example.com", // CORRIGIDO: URL completa
        id = id,
        imdbId = "tt1234567", // Campo adicionado
        originalLanguage = "en", // Campo adicionado
        originalTitle = title, // Campo adicionado
        overview = "Overview",
        popularity = 100.0,
        posterPath = "/poster.jpg",
        productionCompanies = listOf(exampleCompany), // Campo adicionado
        productionCountries = listOf(exampleCountry), // Campo adicionado
        releaseDate = "2024-01-01",
        revenue = 200000000L,
        runtime = 120,
        spokenLanguages = listOf(exampleLanguage), // Campo adicionado
        status = "Released",
        tagline = "Tagline",
        title = title,
        video = false, // Campo adicionado
        voteAverage = 8.5,
        voteCount = 100
    )
}

fun createMockMovieDetail(id: Int, title: String): MovieDetail {
    return MovieDetail(
        id = id,
        title = title,
        overview = "Overview",
        posterPath = "/poster.jpg",
        backdropPath = "/backdrop.jpg",
        releaseDate = "2024-01-01",
        voteAverage = 8.5,
        voteCount = 100,
        popularity = 100.0,
        runtime = 120,
        genres = listOf(Genre(1, "Action")),
        tagline = "Tagline",
        budget = 50000000L,
        revenue = 200000000L,
        status = "Released",
        homepage = "https://example.com"
    )
}

fun createMovieViewObject(
    id: Int,
    title: String,
    isFavorite: Boolean = false,
): MovieViewObject {
    return MovieViewObject(
        id = id,
        title = title,
        overview = "Overview",
        // CORRIGIDO: URL completa para o poster
        posterUrl = "https://image.tmdb.org/t/p/w500/poster.jpg",
        // CORRIGIDO: URL completa para o backdrop
        backdropUrl = "https://image.tmdb.org/t/p/w500/backdrop.jpg",
        releaseDate = "2024-01-01",
        formattedReleaseDate = "01/01/2024",
        rating = 8.5,
        formattedRating = "8.5",
        voteCount = 100,
        popularity = 100.0,
        isFavorite = isFavorite,
        // ADICIONADO: Campo que faltava, com valor padrão
        isLoadingFavorite = false
    )
}

fun createMovie(id: Int, title: String): Movie {
    return Movie(
        id = id,
        title = title,
        overview = "Overview",
        posterPath = "/poster.jpg",
        backdropPath = "/backdrop.jpg",
        releaseDate = "2024-01-01",
        voteAverage = 8.5,
        voteCount = 100,
        popularity = 100.0
    )
}

