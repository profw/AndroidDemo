package ru.profw.repofinder.dao

import android.content.ContentValues
import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.runBlocking
import ru.profw.repofinder.utils.toLikedRepository

@Dao
interface LikedRepositoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(repository: LikedRepository): Long

    @Update
    suspend fun update(repository: LikedRepository): Int

    @Delete
    suspend fun delete(repository: LikedRepository)

    @Query("SELECT * FROM liked_repositories")
    suspend fun getAll(): List<LikedRepository>

    @Query("SELECT * FROM liked_repositories WHERE id = :id")
    suspend fun getById(id: Long): LikedRepository?


    @Query("SELECT * FROM liked_repositories")
    fun getAllCursor(): Cursor

    @Query("SELECT * FROM liked_repositories WHERE id = :id")
    fun getByIdCursor(id: Long?): Cursor

    @Query("DELETE FROM liked_repositories WHERE id = :id")
    fun deleteById(id: Long?): Int

    fun insertFromContentValues(values: ContentValues?): Long {
        if (values == null) return -1

        val repository = values.toLikedRepository()

        return runBlocking {
            val existingRepo = getById(repository.id)
            if (existingRepo == null) {
                // Если репозитория нет в базе, вставляем его
                insert(repository)
            } else {
                // Если репозиторий уже есть, обновляем его
                update(repository)
                repository.id // Возвращаем id существующего репозитория
            }
        }
    }

    fun updateFromContentValues(id: Long?, values: ContentValues?): Int {
        if (id == null || values == null) return 0
        val repository = values.toLikedRepository().copy(id = id)
        return runBlocking {
            update(repository)
        }
    }
}
