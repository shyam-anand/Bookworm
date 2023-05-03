package systems.alderaan.bookworm.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import systems.alderaan.bookworm.data.model.Book

@Database(entities = [Book::class], version = 3, exportSchema = false)
abstract class BookDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: BookDatabase? = null

        fun getDatabase(context: Context): BookDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, BookDatabase::class.java, "book_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}