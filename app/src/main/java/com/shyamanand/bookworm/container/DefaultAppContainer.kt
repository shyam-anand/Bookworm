package com.shyamanand.bookworm.container

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.shyamanand.bookworm.data.*
import com.shyamanand.bookworm.network.GoogleApiService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val googleBooksBaseUrl = "https://www.googleapis.com/books/v1/"

    private val responseFormat = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalSerializationApi::class)
    private val jsonConverterFactory = responseFormat
        .asConverterFactory("application/json".toMediaType())

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(jsonConverterFactory)
        .baseUrl(googleBooksBaseUrl)
        .build()

    private val retrofitService : GoogleApiService by lazy {
        retrofit.create(GoogleApiService::class.java)
    }

    override val booksOnlineRepository: BooksOnlineRepository by lazy {
        GoogleBooksRepository(retrofitService)
    }

    override val booksOfflineRepository: BooksRepository by lazy {
        BooksOfflineRepository(BookDatabase.getDatabase(context).bookDao())
    }

}