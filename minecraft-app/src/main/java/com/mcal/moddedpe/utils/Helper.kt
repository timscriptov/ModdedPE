package com.mcal.moddedpe.utils

import android.content.Context
import android.net.ConnectivityManager

object Helper {
    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnectedOrConnecting ?: false
    }
}
