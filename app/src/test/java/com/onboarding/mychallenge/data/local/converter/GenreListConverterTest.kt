package com.onboarding.mychallenge.data.local.converter

import com.onboarding.mychallenge.domain.model.Genre
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GenreListConverterTest {
    private lateinit var converter: GenreListConverter

    @Before
    fun setUp() {
        converter = GenreListConverter()
    }

    @Test
    fun `fromGenreList should return a valid JSON string for a non-empty list`() {
        val genres =
            listOf(
                Genre(id = 28, name = "Action"),
                Genre(id = 12, name = "Adventure"),
            )
        val expectedJson = """[{"id":28,"name":"Action"},{"id":12,"name":"Adventure"}]"""
        val jsonResult = converter.fromGenreList(genres)

        assertEquals(expectedJson, jsonResult)
    }

    @Test
    fun `fromGenreList should return a valid JSON string for an empty list`() {
        val genres = emptyList<Genre>()
        val expectedJson = "[]"

        val jsonResult = converter.fromGenreList(genres)

        assertEquals(expectedJson, jsonResult)
    }

    @Test
    fun `fromGenreList should return null for a null list`() {
        val genres: List<Genre>? = null

        val jsonResult = converter.fromGenreList(genres)

        assertNull("A conversão de uma lista nula deveria resultar em nulo", jsonResult)
    }

    @Test
    fun `toGenreList should return a valid list for a non-empty JSON string`() {
        val jsonString = """[{"id":28,"name":"Action"},{"id":12,"name":"Adventure"}]"""
        val expectedGenres =
            listOf(
                Genre(id = 28, name = "Action"),
                Genre(id = 12, name = "Adventure"),
            )

        val genresResult = converter.toGenreList(jsonString)

        assertEquals(expectedGenres, genresResult)
    }

    @Test
    fun `toGenreList should return an empty list for an empty JSON array`() {
        val jsonString = "[]"
        val expectedGenres = emptyList<Genre>()

        val genresResult = converter.toGenreList(jsonString)

        assertEquals(expectedGenres, genresResult)
    }

    @Test
    fun `toGenreList should return null for a null JSON string`() {
        val jsonString: String? = null

        val genresResult = converter.toGenreList(jsonString)

        assertNull("A conversão de uma string nula deveria resultar em nulo", genresResult)
    }

    @Test
    fun `toGenreList should return null for an invalid JSON string`() {
        val invalidJsonString = """[{"id":28,"name":"Action"}{"id":12,"name":"Adventure"}]"""

        val genresResult = converter.toGenreList(invalidJsonString)

        assertNull("A conversão de JSON inválido deveria resultar em nulo", genresResult)
    }
}
