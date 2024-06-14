package com.example.todonotion.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todonotion.data.keyword.KeywordDao
import com.example.todonotion.data.keyword.Keyword
import com.example.todonotion.data.token.Token
import com.example.todonotion.data.token.TokenDao
import com.example.todonotion.data.user.UserDao
import com.example.todonotion.data.user.User

/**
 * Room database to persist data for the Forage app.
 * This database stores a [Keyword] entity
 * https://stackoverflow.com/questions/44197309/room-cannot-verify-the-data-integrity
 */
// TODO: create the database with all necessary annotations, methods, variables, etc.
@Database(entities = [Keyword::class, User::class, Token::class], version = 2, exportSchema = false)
abstract class KeyDatabase : RoomDatabase(){
    abstract fun keyDao(): KeywordDao
    abstract fun userDao(): UserDao
    abstract fun tokenDao(): TokenDao


    companion object {
        @Volatile
        private var INSTANCE: KeyDatabase? = null

        fun getDatabase(context: Context): KeyDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KeyDatabase::class.java,
                    "forageable_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}