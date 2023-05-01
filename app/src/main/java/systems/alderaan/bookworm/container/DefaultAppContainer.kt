package systems.alderaan.bookworm.container

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import systems.alderaan.bookworm.network.BookwormTextDetectionRepository
import systems.alderaan.bookworm.network.TextDetectionRepository
import systems.alderaan.bookworm.network.TextDetectionService
import systems.alderaan.bookworm.network.GoogleApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import systems.alderaan.bookworm.data.*

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val googleBooksBaseUrl = "https://www.googleapis.com/books/v1/"
    private val textDetectionBaseUrl = "https://bookwormapp.co.in/photos/detectText/"

    private fun httpInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        // ToDo Remove
//        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(httpInterceptor())
        .build()


    private val responseFormat = Json { ignoreUnknownKeys = true }

    private val jsonConverterFactory = responseFormat
        .asConverterFactory("application/json".toMediaType())

    private val googleBooksApiClient = Retrofit.Builder()
        .addConverterFactory(jsonConverterFactory)
        .baseUrl(googleBooksBaseUrl)
        .client(httpClient)
        .build()

    private val googleApiService: GoogleApiService by lazy {
        googleBooksApiClient.create(GoogleApiService::class.java)
    }

    private val textDetectionClient = Retrofit.Builder()
        .addConverterFactory(jsonConverterFactory)
        .baseUrl(textDetectionBaseUrl)
        .build()

    private val textDetectionService: TextDetectionService by lazy {
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