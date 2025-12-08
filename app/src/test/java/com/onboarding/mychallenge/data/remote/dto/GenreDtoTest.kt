package com.onboarding.mychallenge.data.remote.dto

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class GenreDtoTest {
    private lateinit var moshi: Moshi

    @Before
    fun setUp() {
        moshi =
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
    }

    @Test
    fun `GenreDto should be parsed correctly from JSON string`() {
        val jsonString =
            """
            |{
            |    "id": 28,
            |    "name": "Action"
            |}
            """.trimMargin()

        val adapter = moshi.adapter(GenreDto::class.java)
        val genreDto = adapter.fromJson(jsonString)

        assertNotNull("O objeto GenreDto não deveria ser nulo", genreDto)
        assertEquals("O ID do gênero deveria ser 28", 28, genreDto?.id)
        assertEquals("O nome do gênero deveria ser 'Action'", "Action", genreDto?.name)
    }

    @Test
    fun `GenreDto should handle different data correctly`() {
        val jsonString =
            """
            |{
            |    "id": 12,
            |    "name": "Adventure"
            |}
            """.trimMargin()
        val adapter = moshi.adapter(GenreDto::class.java)
        val genreDto = adapter.fromJson(jsonString)

        assertNotNull(genreDto)
        assertEquals(12, genreDto?.id)
        assertEquals("Adventure", genreDto?.name)
    }
}
