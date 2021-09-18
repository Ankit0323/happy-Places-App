package com.example.happyplaces.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface dao {
    @Insert
    suspend fun insert(happyPlacesData : happyPlacesData)
@Delete
   suspend fun delete(happyPlacesData: happyPlacesData)

    @Update
    suspend fun update(happyPlacesData: happyPlacesData)
    @Query("SELECT * FROM happy_places ORDER BY id ASC")
    fun getAllData():LiveData<List<happyPlacesData>>
}