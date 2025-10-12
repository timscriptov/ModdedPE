package com.mcal.pesdk3.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NModLibInfo(
    @SerialName("use_api")
    var useApi: Boolean = false,
    @SerialName("name")
    var name: String? = null,
)