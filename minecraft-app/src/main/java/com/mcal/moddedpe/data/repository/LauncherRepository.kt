package com.mcal.moddedpe.data.repository

import android.app.Activity
import com.mcal.moddedpe.data.model.domain.AdConfigModel
import kotlinx.coroutines.flow.Flow

interface LauncherRepository {
    fun getDataFlow(): Flow<AdConfigModel>
    suspend fun getData(): AdConfigModel
    suspend fun updateData(): AdConfigModel

    suspend fun installServers()
    fun isInstalledServers(): Boolean

    suspend fun installResources()
    fun isInstalledResources(): Boolean

    suspend fun installNatives()
    fun isInstalledNatives(): Boolean

    fun startGame(activity: Activity)
}
