package com.shyamanand.bookworm.container

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.shyamanand.bookworm.data.*
import com.shyamanand.bookworm.network.BookwormTextDetectionRepository
import com.shyamanand.bookworm.network.TextDetectionRepository
import com.shyamanand.bookworm.network.TextDetectionService
import com.shyamanand.bookworm.network.GoogleApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val googleBooksBaseUrl = "https://www.googleapis.com/books/v1/"
    private val textDetectionBaseUrl = "https://bookwormapp.co.in/photos/detectText/"

    private val responseFormat = Json { ignoreUnknownKeys = true }

    private val jsonConverterFactory = responseFormat
        .asConverterFactory("application/json".toMediaType())

    private val googleBooksApiClient = Retrofit.Builder()
        .addConverterFactory(jsonConverterFactory)
        .baseUrl(googleBooksBaseUrl)
        .build()

    private val googleApiService : GoogleApiService by lazy {
        googleBooksApiClient.create(GoogleApiService::class.java)
    }

    private val textDetectionClient = Retrofit.Builder()
        .addConverterFactory(jsonConverterFactory)
        .baseUrl(textDetectionBaseUrl)
        .build()

    private val textDetectionService : TextDetectionService by lazy {
        textDetectionClient.create(TextDetectionService::class.java)
    }

    override val booksOnlineRepository: BooksOnlineRepository by lazy {
        GoogleBooksRepository(googleApiService)
    }

    override val booksOfflineRepository: BooksRepository by lazy {
        BooksOfflineRepository(BookDatabase.getDatabase(context).bookDao())
    }

    override val textDetectionRepository: TextDetectionRepository by lazy {
        BookwormTextDetectionRepository(textDetectionService)
    }

}