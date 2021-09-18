package com.example.happyplaces.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class happyPlacesViewModel(application: Application):AndroidViewModel(application) {
    var repository:happyPlacesRepository
    val getAllData:LiveData<List<happyPlacesData>>

    init {
        val myDao= happyPlacesDatabase.getDatabase(application).getDao()
         repository=happyPlacesRepository(myDao)
        getAllData =repository.allData
    }
    fun insertData(happyPlacesData: happyPlacesData)=viewModelScope.launch(Dispatchers.IO) {
        repository.insert(happyPlacesData)
    }
    fun deleteData(happyPlacesData: happyPlacesData)=viewModelScope.launch(Dispatchers.IO) {
        repository.delete(happyPlacesData)
    }
    fun updateData(happyPlacesData: happyPlacesData)=viewModelScope.launch(Dispatchers.IO) {
        repository.update(happyPlacesData)
    }
}