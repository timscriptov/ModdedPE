package com.mcal.moddedpe.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServersModelNT(
    @SerialName("servers") var servers: List<String> = emptyList()
)
