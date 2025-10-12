package com.mcal.pesdk3.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NModJsonEditBean(
    @SerialName("path")
    var path: String? = null,
    @SerialName("mode")
    var mode: String? = MODE_REPLACE,
) {
    companion object {
        const val MODE_REPLACE = "replace"
        const val MODE_MERGE = "merge"
    }
}