package com.shyamanand.bookworm.container

import com.shyamanand.bookworm.data.BooksOnlineRepository
import com.shyamanand.bookworm.data.BooksRepository

interface AppContainer {
    val booksOnlineRepository: BooksOnlineRepository
    val booksOfflineRepository: BooksRepository
}