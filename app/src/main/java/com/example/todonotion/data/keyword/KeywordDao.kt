package com.example.todonotion.data.keyword
import androidx.room.Dao
import kotlinx.coroutines.flow.Flow
import androidx.room.*
/**
 * Data Access Object for database interaction.
 * https://stackoverflow.com/questions/48653099/checking-if-roomdatabase-is-empty-while-using-livedata
 */
@Dao
interface KeywordDao {
    // TODO: implement a method to retrieve all KeyWord from the database
    @Query("SELECT * from keyword ORDER BY key_name ASC")
    fun getKeys(): Flow<List<Keyword>>
    // TODO: implement a method to retrieve a KeyWord from the database by id
    @Query("SELECT * from keyword WHERE id = :id")
    fun getKey(id: Int): Flow<Keyword>
    //find value by name
    @Query("SELECT * from keyword WHERE key_name = :keyName LIMIT 1")
    fun getKeyByName(keyName: String): Flow<Keyword>
    // TODO: implement a method to insert a KeyWord into the database
    //  (use OnConflictStrategy.REPLACE)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(keyword: Keyword)

    // TODO: implement a method to update a KeyWord that is already in the database
    @Update
    suspend fun update(keyword: Keyword)

    // TODO: implement a method to delete a KeyWord from the database.
    @Delete
    suspend fun delete(keyword: Keyword)
}