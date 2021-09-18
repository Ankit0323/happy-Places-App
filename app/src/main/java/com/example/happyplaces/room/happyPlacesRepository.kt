package com.example.happyplaces.room

import androidx.lifecycle.LiveData

class happyPlacesRepository(val dao: dao) {
    var allData : LiveData<List<happyPlacesData>> = dao.getAllData()
    suspend fun insert(happyPlacesData: happyPlacesData){
        dao.insert(happyPlacesData)
    }
    suspend fun delete(happyPlacesData: happyPlacesData){
        dao.delete(happyPlacesData)
    }
    suspend fun update(happyPlacesData: happyPlacesData){
        dao.update(happyPlacesData)
    }
}