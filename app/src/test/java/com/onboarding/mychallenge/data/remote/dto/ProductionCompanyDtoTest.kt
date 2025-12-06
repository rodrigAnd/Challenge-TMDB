package com.onboarding.mychallenge.data.remote.dto

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ProductionCompanyDtoTest {
    // Instância do Moshi que será usada para os testes.
    private lateinit var moshi: Moshi

    @Before
    fun setUp() {
        // Configura o Moshi com o adapter para classes Kotlin.
        moshi =
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
    }

    @Test
    fun `ProductionCompanyDto should be parsed correctly from a full JSON`() {
        // Arrange (Organizar)
        // 1. Cria uma String JSON que simula uma produtora vinda da API, com todos os campos.
        val jsonString =
            """
            |{
            |    "id": 508,
            |    "logo_path": "/7PzJdsLGlR7oW4J0J5Xcd0pHGRg.png",
            |    "name": "Regency Enterprises",
            |    "origin_country": "US"
            |}
            """.trimMargin()

        // Act (Agir)
        // 2. Cria um adapter para a classe ProductionCompanyDto e converte o JSON.
        val adapter = moshi.adapter(ProductionCompanyDto::class.java)
        val companyDto = adapter.fromJson(jsonString)

        // Assert (Verificar)
        // 3. Verifica se o objeto não é nulo e se os campos foram preenchidos corretamente.
        assertNotNull("O objeto ProductionCompanyDto não deveria ser nulo", companyDto)
        assertEquals("O ID deveria ser 508", 508, companyDto?.id)
        assertEquals("O nome deveria ser 'Regency Enterprises'", "Regency Enterprises", companyDto?.name)
        assertEquals("O origin_country deveria ser 'US'", "US", companyDto?.originCountry)
        assertEquals("O logo_path deveria corresponder", "/7PzJdsLGlR7oW4J0J5Xcd0pHGRg.png", companyDto?.logoPath)
    }

    @Test
    fun `ProductionCompanyDto should handle null for logoPath`() {
        // Arrange
        // 1. Cria uma String JSON onde o campo 'logo_path' é explicitamente nulo.
        val jsonString =
            """
            |{
            |    "id": 999,
            |    "logo_path": null,
            |    "name": "No Logo Pictures",
            |    "origin_country": "CA"
            |}
            """.trimMargin()

        // Act
        // 2. Converte a String JSON no objeto DTO.
        val adapter = moshi.adapter(ProductionCompanyDto::class.java)
        val companyDto = adapter.fromJson(jsonString)

        // Assert
        // 3. Verifica se o campo 'logoPath' no objeto Kotlin é de fato nulo.
        assertNotNull(companyDto)
        assertEquals(999, companyDto?.id)
        assertEquals("No Logo Pictures", companyDto?.name)
        assertNull("O logoPath deveria ser nulo", companyDto?.logoPath)
    }

    @Test
    fun `ProductionCountryDto should be parsed correctly from JSON`() {
        // Arrange
        // 1. Simula a resposta JSON da API para um país de produção.
        val jsonString =
            """
            |{
            |    "iso_3166_1": "US",
            |    "name": "United States of America"
            |}
            """.trimMargin()

        // Act
        // 2. Converte a String JSON no objeto DTO.
        val adapter = moshi.adapter(ProductionCountryDto::class.java)
        val countryDto = adapter.fromJson(jsonString)

        // Assert
        // 3. Verifica se o objeto não é nulo e se os campos correspondem.
        assertNotNull("O objeto ProductionCountryDto não deveria ser nulo", countryDto)
        assertEquals("O código ISO deveria ser 'US'", "US", countryDto?.iso31661)
        assertEquals("O nome deveria ser 'United States of America'", "United States of America", countryDto?.name)
    }

    @Test
    fun `SpokenLanguageDto should be parsed correctly from JSON`() {
        // Arrange
        // 1. Simula a resposta JSON da API para um idioma falado.
        val jsonString =
            """
            |{
            |    "english_name": "English",
            |    "iso_639_1": "en",
            |    "name": "English"
            |}
            """.trimMargin()

        // Act
        // 2. Converte a String JSON no objeto DTO.
        val adapter = moshi.adapter(SpokenLanguageDto::class.java)
        val languageDto = adapter.fromJson(jsonString)

        // Assert
        // 3. Verifica se o objeto não é nulo e se os campos correspondem.
        assertNotNull("O objeto SpokenLanguageDto não deveria ser nulo", languageDto)
        assertEquals("O english_name deveria ser 'English'", "English", languageDto?.englishName)
        assertEquals("O código ISO deveria ser 'en'", "en", languageDto?.iso6391)
        assertEquals("O nome deveria ser 'English'", "English", languageDto?.name)
    }
}
