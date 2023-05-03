package systems.alderaan.bookworm.container

import systems.alderaan.bookworm.data.BooksOnlineRepository
import systems.alderaan.bookworm.data.BooksRepository
import systems.alderaan.bookworm.network.TextDetectionRepository

interface AppContainer {
    val booksOnlineRepository: BooksOnlineRepository
    val booksOfflineRepository: BooksRepository
    val textDetectionRepository: TextDetectionRepository
}