package systems.alderaan.bookworm.network

import systems.alderaan.bookworm.data.*
import systems.alderaan.bookworm.network.model.SearchResult
import systems.alderaan.bookworm.network.model.SearchResultItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleApiService {

    @GET("volumes")
    suspend fun search(
        @Query(value = "q", encoded = true) title: String): SearchResult

    @GET("volumes/{id}")
    suspend fun getDetails(@Path("id") id: String): SearchResultItem
}