package com.onboarding.mychallenge.data.local.converter

import androidx.room.TypeConverter
import com.onboarding.mychallenge.domain.model.Genre
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * TypeConverter para converter [List] de [Genre] para JSON e vice-versa.
 * 
 * Permite que o Room armazene listas de gêneros como strings JSON no banco de dados,
 * já que o Room não suporta tipos complexos diretamente. Utiliza Moshi para
 * serialização e desserialização.
 * 
 * @constructor Cria uma nova instância do [GenreListConverter] com Moshi configurado.
 */
class GenreListConverter {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    
    private val listType = Types.newParameterizedType(List::class.java, Genre::class.java)
    private val adapter = moshi.adapter<List<Genre>>(listType)

    /**
     * Converte uma lista de gêneros para uma string JSON.
     * 
     * @param genres Lista de gêneros a ser serializada (pode ser null).
     * @return String JSON representando a lista de gêneros, ou null se a lista for null.
     */
    @TypeConverter
    fun fromGenreList(genres: List<Genre>?): String? {
        return genres?.let { adapter.toJson(it) }
    }

    /**
     * Converte uma string JSON para uma lista de gêneros.
     * 
     * @param genresJson String JSON a ser desserializada (pode ser null).
     * @return Lista de gêneros desserializada, ou null se a string for null ou inválida.
     */
    @TypeConverter
    fun toGenreList(genresJson: String?): List<Genre>? {
        return genresJson?.let {
            try {
                adapter.fromJson(it)
            } catch (e: Exception) {
                null
            }
        }
    }
}

