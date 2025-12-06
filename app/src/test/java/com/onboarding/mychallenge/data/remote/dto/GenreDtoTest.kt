package com.onboarding.mychallenge.data.remote.dto

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class GenreDtoTest {
    // Instância do Moshi para ser usada nos testes.
    private lateinit var moshi: Moshi

    @Before
    fun setUp() {
        // Configura o Moshi com o adapter para classes Kotlin,
        // exatamente como seria feito no seu app.
        moshi =
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
    }

    @Test
    fun `GenreDto should be parsed correctly from JSON string`() {
        // Arrange (Organizar)
        // 1. Cria uma String JSON que simula um objeto de gênero vindo da API do TMDb.
        val jsonString =
            """
            |{
            |    "id": 28,
            |    "name": "Action"
            |}
            """.trimMargin()

        // Act (Agir)
        // 2. Cria um adapter específico para a classe GenreDto.
        val adapter = moshi.adapter(GenreDto::class.java)
        // 3. Usa o adapter para converter (parsear) a String JSON no nosso objeto DTO.
        val genreDto = adapter.fromJson(jsonString)

        // Assert (Verificar)
        // 4. Garante que o objeto não é nulo e que seus campos têm os valores corretos.
        assertNotNull("O objeto GenreDto não deveria ser nulo", genreDto)
        assertEquals("O ID do gênero deveria ser 28", 28, genreDto?.id)
        assertEquals("O nome do gênero deveria ser 'Action'", "Action", genreDto?.name)
    }

    @Test
    fun `GenreDto should handle different data correctly`() {
        // Arrange
        // 1. Cria uma String JSON com dados diferentes para garantir a flexibilidade.
        val jsonString =
            """
            |{
            |    "id": 12,
            |    "name": "Adventure"
            |}
            """.trimMargin()

        // Act
        // 2. Converte a String JSON no objeto DTO.
        val adapter = moshi.adapter(GenreDto::class.java)
        val genreDto = adapter.fromJson(jsonString)

        // Assert
        // 3. Verifica os novos valores.
        assertNotNull(genreDto)
        assertEquals(12, genreDto?.id)
        assertEquals("Adventure", genreDto?.name)
    }
}
