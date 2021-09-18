package com.example.happyplaces.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(happyPlacesData::class), version = 1, exportSchema = false)
public abstract class happyPlacesDatabase : RoomDatabase() {

    abstract fun getDao(): dao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: happyPlacesDatabase? = null

        fun getDatabase(context: Context): happyPlacesDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    happyPlacesDatabase::class.java,
                    "happyPlaces_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}