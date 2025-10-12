package com.mcal.pesdk3.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class NModWarning(
    val type: Int,
    @Contextual
    val cause: Throwable? = null
)