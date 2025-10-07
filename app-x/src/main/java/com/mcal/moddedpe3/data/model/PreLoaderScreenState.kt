package com.mcal.moddedpe3.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PreLoaderScreenState(
    val items: List<String> = emptyList()
) : Parcelable