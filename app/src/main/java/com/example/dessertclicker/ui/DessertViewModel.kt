package com.example.dessertclicker.ui

import androidx.lifecycle.ViewModel
import com.example.dessertclicker.data.Datasource
import com.example.dessertclicker.model.Dessert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DessertViewModel : ViewModel() {
    // Create a mutable copy of the dessert list
    private val desserts = Datasource.dessertList.toMutableList()
    private val soldOutDesserts = mutableSetOf<Dessert>()

    private val _uiState = MutableStateFlow(
        DessertUiState(
            firstDessert = desserts[0],
            secondDessert = desserts[1]
        )
    )
    val uiState: StateFlow<DessertUiState> = _uiState.asStateFlow()

    fun onDessertClicked(clickedDessert: Dessert) {
        val currentState = _uiState.value
        val newRevenue = currentState.revenue + clickedDessert.price
        val newDessertsSold = currentState.dessertsSold + 1

        var newFirstDessert = currentState.firstDessert
        var newFirstDessertAmount = currentState.firstDessertAmount
        var newSecondDessert = currentState.secondDessert
        var newSecondDessertAmount = currentState.secondDessertAmount

        if (clickedDessert == currentState.firstDessert) {
            newFirstDessertAmount -= 1
            // Update the dessert in our mutable list using property equality
            updateDessertInList(clickedDessert, newFirstDessertAmount)

            if (newFirstDessertAmount <= 0) {
                soldOutDesserts.add(newFirstDessert)
                // Replace with a new dessert that is not the secondDessert and is available
                newFirstDessert = findNewDessert(currentState.secondDessert)
                newFirstDessertAmount = newFirstDessert.amount
            }
        } else { // clickedDessert is secondDessert
            newSecondDessertAmount -= 1
            updateDessertInList(clickedDessert, newSecondDessertAmount)

            if (newSecondDessertAmount <= 0) {
                soldOutDesserts.add(newSecondDessert)
                newSecondDessert = findNewDessert(currentState.firstDessert)
                newSecondDessertAmount = newSecondDessert.amount
            }
        }

        _uiState.value = currentState.copy(
            revenue = newRevenue,
            dessertsSold = newDessertsSold,
            firstDessert = newFirstDessert.copy(amount = newFirstDessertAmount),
            firstDessertAmount = newFirstDessertAmount,
            secondDessert = newSecondDessert.copy(amount = newSecondDessertAmount),
            secondDessertAmount = newSecondDessertAmount
        )
    }

    private fun updateDessertInList(dessert: Dessert, newAmount: Int) {
        // Use a unique property (e.g., imageId) for matching instead of identity (===)
        val index = desserts.indexOfFirst { it.imageId == dessert.imageId }
        if (index >= 0) {
            desserts[index] = desserts[index].copy(amount = newAmount)
        }
    }

    private fun findNewDessert(otherDessert: Dessert): Dessert {
        // Filter available desserts excluding the one displayed as the other dessert and sold out ones.
        val availableDesserts = desserts.filter {
            it.amount > 0 && it.imageId != otherDessert.imageId && !soldOutDesserts.contains(it)
        }
        return if (availableDesserts.isNotEmpty()) {
            availableDesserts.first()
        } else {
            // If no new dessert is available, reuse the other dessert (or you could default to desserts.first())
            otherDessert
        }
    }
}
