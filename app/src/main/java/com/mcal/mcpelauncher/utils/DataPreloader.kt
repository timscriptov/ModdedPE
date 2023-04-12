/*
 * Copyright (C) 2018-2021 Тимашков Иван
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.mcal.mcpelauncher.utils

import android.content.Context
import com.mcal.mcpelauncher.ModdedPEApplication
import com.mcal.pesdk.PESdk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
class DataPreloader(private val mListener: PreloadingFinishedListener) {
    private var mIsSleepingFinished = false
    private var mIsPreloadingFinished = false
    fun preload(context_a: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            ModdedPEApplication.mPESdk = PESdk(context_a)
            ModdedPEApplication.mPESdk.init()
            mIsPreloadingFinished = true
            checkFinish()
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                delay(5000L)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            mIsSleepingFinished = true
            checkFinish()
        }
    }

    private fun checkFinish() {
        if (mIsPreloadingFinished && mIsSleepingFinished) {
            mListener.onPreloadingFinished()
        }
    }
}