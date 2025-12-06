package com.onboarding.mychallenge.data.local.converter
import androidx.room.TypeConverter
import com.onboarding.mychallenge.domain.model.Genre
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class GenreListConverter {
    private val moshi =
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    private val listType = Types.newParameterizedType(List::class.java, Genre::class.java)
    private val adapter = moshi.adapter<List<Genre>>(listType)

    @TypeConverter
    fun fromGenreList(genres: List<Genre>?): String? {
        return genres?.let { adapter.toJson(it) }
    }

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
