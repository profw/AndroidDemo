package ru.profw.demo.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LikedRepositoryDao {

    @Insert
    suspend fun insert(repository: LikedRepository)

    @Delete
    suspend fun delete(repository: LikedRepository)

    @Query("SELECT * FROM liked_repositories")
    suspend fun getAll(): List<LikedRepository>

    @Query("SELECT * FROM liked_repositories WHERE id = :id")
    suspend fun getById(id: Long): LikedRepository?
}