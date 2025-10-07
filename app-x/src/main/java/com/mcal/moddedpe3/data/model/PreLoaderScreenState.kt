package com.mcal.moddedpe3.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PreLoaderScreenState(
    val logs: List<String> = emptyList(),
    val progress: Float = -1f,
    val currentStatus: String = "",
) : Parcelable