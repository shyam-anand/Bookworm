package com.shyamanand.bookworm.data

import android.util.Log
import com.shyamanand.bookworm.TAG
import com.shyamanand.bookworm.network.model.VolumeInfo
import com.shyamanand.bookworm.network.model.SearchResult
import com.shyamanand.bookworm.network.GoogleApiService
import com.shyamanand.bookworm.network.model.SearchResultItem

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

