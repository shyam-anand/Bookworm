package com.shyamanand.bookworm.container

import com.shyamanand.bookworm.data.BooksOnlineRepository
import com.shyamanand.bookworm.data.BooksRepository
import com.shyamanand.bookworm.network.TextDetectionRepository

interface AppContainer {
    val booksOnlineRepository: BooksOnlineRepository
    val booksOfflineRepository: BooksRepository
    val textDetectionRepository: TextDetectionRepository
}