package com.onboarding.mychallenge.data.remote.dto

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ProductionCompanyDtoTest {
    private lateinit var moshi: Moshi

    @Before
    fun setUp() {
        moshi =
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
    }

    @Test
    fun `ProductionCompanyDto should be parsed correctly from a full JSON`() {
        val jsonString =
            """
            |{
            |    "id": 508,
            |    "logo_path": "/7PzJdsLGlR7oW4J0J5Xcd0pHGRg.png",
            |    "name": "Regency Enterprises",
            |    "origin_country": "US"
            |}
            """.trimMargin()

        val adapter = moshi.adapter(ProductionCompanyDto::class.java)
        val companyDto = adapter.fromJson(jsonString)

        assertNotNull("O objeto ProductionCompanyDto não deveria ser nulo", companyDto)
        assertEquals("O ID deveria ser 508", 508, companyDto?.id)
        assertEquals("O nome deveria ser 'Regency Enterprises'", "Regency Enterprises", companyDto?.name)
        assertEquals("O origin_country deveria ser 'US'", "US", companyDto?.originCountry)
        assertEquals("O logo_path deveria corresponder", "/7PzJdsLGlR7oW4J0J5Xcd0pHGRg.png", companyDto?.logoPath)
    }

    @Test
    fun `ProductionCompanyDto should handle null for logoPath`() {
        val jsonString =
            """
            |{
            |    "id": 999,
            |    "logo_path": null,
            |    "name": "No Logo Pictures",
            |    "origin_country": "CA"
            |}
            """.trimMargin()

        val adapter = moshi.adapter(ProductionCompanyDto::class.java)
        val companyDto = adapter.fromJson(jsonString)

        assertNotNull(companyDto)
        assertEquals(999, companyDto?.id)
        assertEquals("No Logo Pictures", companyDto?.name)
        assertNull("O logoPath deveria ser nulo", companyDto?.logoPath)
    }

    @Test
    fun `ProductionCountryDto should be parsed correctly from JSON`() {
        val jsonString =
            """
            |{
            |    "iso_3166_1": "US",
            |    "name": "United States of America"
            |}
            """.trimMargin()

        val adapter = moshi.adapter(ProductionCountryDto::class.java)
        val countryDto = adapter.fromJson(jsonString)

        assertNotNull("O objeto ProductionCountryDto não deveria ser nulo", countryDto)
        assertEquals("O código ISO deveria ser 'US'", "US", countryDto?.iso31661)
        assertEquals("O nome deveria ser 'United States of America'", "United States of America", countryDto?.name)
    }

    @Test
    fun `SpokenLanguageDto should be parsed correctly from JSON`() {
        val jsonString =
            """
            |{
            |    "english_name": "English",
            |    "iso_639_1": "en",
            |    "name": "English"
            |}
            """.trimMargin()

        val adapter = moshi.adapter(SpokenLanguageDto::class.java)
        val languageDto = adapter.fromJson(jsonString)

        assertNotNull("O objeto SpokenLanguageDto não deveria ser nulo", languageDto)
        assertEquals("O english_name deveria ser 'English'", "English", languageDto?.englishName)
        assertEquals("O código ISO deveria ser 'en'", "en", languageDto?.iso6391)
        assertEquals("O nome deveria ser 'English'", "English", languageDto?.name)
    }
}
