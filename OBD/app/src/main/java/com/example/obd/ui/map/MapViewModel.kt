package com.example.obd.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.data.firebase.CO2Repository
import com.example.domain.CO2Val
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ro.upt.ac.chiuitter.data.firebase.FirebaseCO2Store

class MapViewModel : ViewModel() {

    val co2ValLiveData = MutableLiveData<List<CO2Val>>()
    private val dbStore: CO2Repository = FirebaseCO2Store()

    fun fetchValues(){
        GlobalScope.launch {
            val values = dbStore.getAll()
            co2ValLiveData.postValue(values)
        }
    }
}