package com.mcal.moddedpe.data.repository

import android.app.Activity
import android.content.Context

interface LauncherRepository {
    suspend fun installServers()
    fun isInstalledServers(): Boolean

    suspend fun installResources()
    fun isInstalledResources(): Boolean

    suspend fun installNatives(): Int
    fun isInstalledNatives(): Boolean

    fun startGame(activity: Activity)
    fun isOnline(context: Context): Boolean
}
