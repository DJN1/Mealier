package com.davidniederweis.mealier.data.model.food

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class FoodListResponse(
    @SerializedName("items")
    val items: List<Food>,

    @SerializedName("total")
    val total: Int? = null,

    @SerializedName("page")
    val page: Int? = null,

    @SerializedName("perPage")
    val perPage: Int? = null,

    @SerializedName("totalPages")
    val totalPages: Int? = null
)
