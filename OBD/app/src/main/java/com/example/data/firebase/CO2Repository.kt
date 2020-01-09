package com.example.data.firebase

import com.example.domain.CO2Val

interface CO2Repository {
    suspend fun getAll() : List<CO2Val>
    suspend fun addVal(co2Val: CO2Val)
    suspend fun removeVal(co2Val: CO2Val)
}