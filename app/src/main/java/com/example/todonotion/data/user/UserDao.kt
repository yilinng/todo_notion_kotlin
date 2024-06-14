package com.example.todonotion.data.user
import kotlinx.coroutines.flow.Flow
import androidx.room.*

@Dao
interface UserDao {
    // TODO: implement a method to retrieve all User from the database
    @Query("SELECT * from user ORDER BY email ASC")
    fun getUsers(): Flow<List<User>>
    // TODO: implement a method to retrieve a User from the database by id
    @Query("SELECT * from user WHERE id = :id")
    fun getUser(id: Int): Flow<User>
    //find value by name
    @Query("SELECT * from user WHERE name = :userName LIMIT 1")
    fun getUserByUserName(userName: String): Flow<User>
    // TODO: implement a method to insert a User into the database
    //  (use OnConflictStrategy.REPLACE)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    // TODO: implement a method to update a User that is already in the database
    @Update
    suspend fun update(user: User)

    // TODO: implement a method to delete a User from the database.
    @Delete
    suspend fun delete(user: User)
}