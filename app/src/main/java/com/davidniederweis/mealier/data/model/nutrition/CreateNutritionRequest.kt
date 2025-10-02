package com.davidniederweis.mealier.data.model.nutrition

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateNutritionRequest(
    @SerializedName("calories")
    val calories: String? = null,

    @SerializedName("fatContent")
    val fatContent: String? = null,

    @SerializedName("proteinContent")
    val proteinContent: String? = null,

    @SerializedName("carbohydrateContent")
    val carbohydrateContent: String? = null,

    @SerializedName("fiberContent")
    val fiberContent: String? = null,

    @SerializedName("sugarContent")
    val sugarContent: String? = null,

    @SerializedName("sodiumContent")
    val sodiumContent: String? = null
)