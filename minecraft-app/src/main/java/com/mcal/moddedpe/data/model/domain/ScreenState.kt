package com.mcal.moddedpe.data.model.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScreenState(
    val isLoading: Boolean = false,
    val isOnline: Boolean = true,
    val isError: Boolean = false,
) : Parcelable
