package systems.alderaan.bookworm.data

import androidx.room.*
import systems.alderaan.bookworm.data.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(book: Book)

    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getItem(id: String): Book?

    @Query("SELECT * FROM books ORDER BY id DESC")
    fun getAllBooks(): Flow<List<Book>>

}