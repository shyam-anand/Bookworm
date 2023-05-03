package systems.alderaan.bookworm.data

import systems.alderaan.bookworm.data.model.Book
import kotlinx.coroutines.flow.Flow

interface BooksRepository {

    fun getAllBooksStream(): Flow<List<Book>>

    suspend fun getBook(id: String): Book?

    suspend fun addBook(book: Book)

    suspend fun deleteBook(book: Book)

    suspend fun updateBook(book: Book)
}