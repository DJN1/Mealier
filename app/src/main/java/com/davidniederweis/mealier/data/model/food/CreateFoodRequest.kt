package com.davidniederweis.mealier.data.model.food

import com.google.gson.annotations.SerializedName


data class CreateFoodRequest(
    @SerializedName("name")
    val name: String
)
