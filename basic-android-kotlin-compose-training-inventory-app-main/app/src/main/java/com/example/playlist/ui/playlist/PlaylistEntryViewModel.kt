/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.playlist.ui.playlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.playlist.data.Playlist
import com.example.playlist.data.PlaylistRepository

/**
 * ViewModel to validate and insert items in the Room database.
 */
class ItemEntryViewModel(private val playlistRepository: PlaylistRepository) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var itemUiState by mutableStateOf(ItemUiState())
        private set

    /**
     * Updates the [itemUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState =
            ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }

    /**
     * Inserts a [Playlist] in the Room database
     */
    suspend fun saveItem() {
        if (validateInput()) {
            playlistRepository.insertItem(itemUiState.itemDetails.toItem())
        }
    }

    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && descripcion.isNotBlank() && genero.isNotBlank()
        }
    }
}

/**
 * Represents Ui State for an Item.
 */
data class ItemUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val isEntryValid: Boolean = false
)

data class ItemDetails(
    val id: Int = 0,
    val name: String = "",
    val descripcion: String = "",
    val genero: String = "",
    val numcam: String = ""
)

/**
 * Extension function to convert [ItemDetails] to [Playlist]. If the value of [ItemDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly, if the value of
 * [ItemDetails] is not a valid [Int], then the quantity will be set to 0
 */
fun ItemDetails.toItem(): Playlist = Playlist(
    id = id,
    nombre = name,
    descripcion = descripcion,
    genero = genero,
    numcan = numcam.toIntOrNull() ?: 0
)



/**
 * Extension function to convert [Playlist] to [ItemUiState]
 */
fun Playlist.toItemUiState(isEntryValid: Boolean = false): ItemUiState = ItemUiState(
    itemDetails = this.toItemDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Playlist] to [ItemDetails]
 */
fun Playlist.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    name = nombre,
    descripcion = descripcion,
    genero = genero,
    numcam = numcan.toString()
)

