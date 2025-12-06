package com.onboarding.mychallenge.data.local.converter

import android.util.Log
import com.onboarding.mychallenge.domain.model.Genre
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GenreListConverterTest {

    // A instância da classe que vamos testar
    private lateinit var converter: GenreListConverter

    @Before
    fun setUp() {
        // Mocka as chamadas estáticas para android.util.Log para evitar o erro "not mocked"
        mockkStatic(Log::class)
        // Define que qualquer chamada a Log.e(...) deve ser ignorada e retornar 0 (um Int)
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0 // Sobrecarga com exceção

        // Cria uma nova instância do converter antes de cada teste
        converter = GenreListConverter()
    }

    @Test
    fun `fromGenreList should return a valid JSON string for a non-empty list`() {
        // Arrange (Organizar)
        // 1. Cria uma lista de gêneros de exemplo
        val genres = listOf(
            Genre(id = 28, name = "Action"),
            Genre(id = 12, name = "Adventure")
        )
        // 2. Define a string JSON que esperamos como resultado
        val expectedJson = """[{"id":28,"name":"Action"},{"id":12,"name":"Adventure"}]"""

        // Act (Agir)
        // 3. Chama a função de conversão
        val jsonResult = converter.fromGenreList(genres)

        // Assert (Verificar)
        // 4. Verifica se a string JSON gerada é igual à esperada
        assertEquals(expectedJson, jsonResult)
    }

    @Test
    fun `fromGenreList should return a valid JSON string for an empty list`() {
        // Arrange
        val genres = emptyList<Genre>()
        val expectedJson = "[]"

        // Act
        val jsonResult = converter.fromGenreList(genres)

        // Assert
        assertEquals(expectedJson, jsonResult)
    }

    @Test
    fun `fromGenreList should return null for a null list`() {
        // Arrange
        val genres: List<Genre>? = null

        // Act
        val jsonResult = converter.fromGenreList(genres)

        // Assert
        assertNull("A conversão de uma lista nula deveria resultar em nulo", jsonResult)
    }

    @Test
    fun `toGenreList should return a valid list for a non-empty JSON string`() {
        // Arrange
        // 1. A string JSON que simulamos ter vindo do banco de dados
        val jsonString = """[{"id":28,"name":"Action"},{"id":12,"name":"Adventure"}]"""
        // 2. A lista de gêneros que esperamos como resultado
        val expectedGenres = listOf(
            Genre(id = 28, name = "Action"),
            Genre(id = 12, name = "Adventure")
        )

        // Act
        // 3. Chama a função de conversão
        val genresResult = converter.toGenreList(jsonString)

        // Assert
        // 4. Verifica se a lista gerada é igual à esperada
        assertEquals(expectedGenres, genresResult)
    }

    @Test
    fun `toGenreList should return an empty list for an empty JSON array`() {
        // Arrange
        val jsonString = "[]"
        val expectedGenres = emptyList<Genre>()

        // Act
        val genresResult = converter.toGenreList(jsonString)

        // Assert
        assertEquals(expectedGenres, genresResult)
    }



    @Test
    fun `toGenreList should return null for a null JSON string`() {
        // Arrange
        val jsonString: String? = null

        // Act
        val genresResult = converter.toGenreList(jsonString)

        // Assert
        assertNull("A conversão de uma string nula deveria resultar em nulo", genresResult)
    }

    @Test
    fun `toGenreList should return null for an invalid JSON string`() {
        // Arrange
        // Uma string JSON mal formatada (falta uma vírgula)
        val invalidJsonString = """[{"id":28,"name":"Action"}{"id":12,"name":"Adventure"}]"""

        // Act
        val genresResult = converter.toGenreList(invalidJsonString)

        // Assert
        // A sua implementação com try-catch deve retornar null em caso de erro de parsing
        assertNull("A conversão de JSON inválido deveria resultar em nulo", genresResult)
    }
}

