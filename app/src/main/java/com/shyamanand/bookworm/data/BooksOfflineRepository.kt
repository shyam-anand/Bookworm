package com.shyamanand.bookworm.data

import com.shyamanand.bookworm.data.model.Book
import kotlinx.coroutines.flow.Flow

class BooksOfflineRepository(private val itemDao: BookDao) : BooksRepository {
    override fun getAllBooksStream(): Flow<List<Book>> = itemDao.getAllBooks()

    override suspend fun getBook(id: String): Book? = itemDao.getItem(id)

    override suspend fun addBook(book: Book) = itemDao.insert(book)

    override suspend fun deleteBook(book: Book) = itemDao.delete(book)

    override suspend fun updateBook(book: Book) = itemDao.update(book)
}