package com.example.todonotion.data.token

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenDao {
    // TODO: implement a method to retrieve all User from the database
    @Query("SELECT * from token ORDER BY id ASC")
    fun getTokens(): Flow<List<Token>>
    // TODO: implement a method to retrieve a User from the database by id
    @Query("SELECT * from token WHERE id = :id")
    fun getToken(id: Int): Flow<Token>
   // //find value by name
   // @Query("SELECT * from token WHERE userId = :userId LIMIT 1")
   // fun getTokenByUserId(userId: Long): Flow<Token>
    // TODO: implement a method to insert a User into the database
    //  (use OnConflictStrategy.REPLACE)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(token: Token)

    // TODO: implement a method to update a User that is already in the database
    @Update
    suspend fun update(token: Token)

    // TODO: implement a method to delete a User from the database.
    @Delete
    suspend fun delete(token: Token)
}