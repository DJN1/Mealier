package com.davidniederweis.mealier.data.model.unit

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UnitListResponse(
    @SerializedName("items")
    val items: List<RecipeUnit>,

    @SerializedName("total")
    val total: Int,

    @SerializedName("page")
    val page: Int,

    @SerializedName("perPage")
    val perPage: Int,

    @SerializedName("totalPages")
    val totalPages: Int
)
