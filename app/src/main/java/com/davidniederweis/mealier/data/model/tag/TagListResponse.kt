package com.davidniederweis.mealier.data.model.tag

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TagListResponse(
    @SerialName("items")
    val items: List<Tag>,

    @SerialName("total")
    val total: Int? = null,

    @SerialName("page")
    val page: Int? = null,

    @SerialName("perPage")
    val perPage: Int? = null,

    @SerialName("totalPages")
    val totalPages: Int? = null
)
