package com.example.dessertclicker.ui

import com.example.dessertclicker.R
import com.example.dessertclicker.data.Datasource
import com.example.dessertclicker.model.Dessert

data class DessertUiState(
    val revenue: Int = 0,
    val dessertsSold: Int = 0,
    val firstDessert : Dessert,
    val secondDessert  : Dessert,
    val firstDessertAmount: Int = firstDessert.amount,
    val secondDessertAmount: Int = secondDessert.amount,
)
