package systems.alderaan.bookworm.data

import android.util.Log
import systems.alderaan.bookworm.TAG
import systems.alderaan.bookworm.network.model.SearchResult
import systems.alderaan.bookworm.network.GoogleApiService
import systems.alderaan.bookworm.network.model.SearchResultItem

interface BooksOnlineRepository {

    suspend fun search(title: String): SearchResult

    suspend fun getDetails(id: String): SearchResultItem
}

class GoogleBooksRepository(
    private val googleApiService: GoogleApiService
) : BooksOnlineRepository {

    override suspend fun getDetails(id: String): SearchResultItem {
        Log.i(TAG, "getDetails: $id")
        return googleApiService.getDetails(id)
    }

    override suspend fun search(title: String): SearchResult {
        Log.i(TAG, "search: $title")
        return googleApiService.search(title)
    }
}

