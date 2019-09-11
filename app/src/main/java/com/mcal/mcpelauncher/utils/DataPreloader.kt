/*
 * Copyright (C) 2018-2019 Тимашков Иван
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

class DataPreloader(private val mListener: PreloadingFinishedListener) {
    private var mIsSleepingFinished = false
    private var mIsPreloadingFinished = false

    fun preload(context_a: Context) {
        object : Thread() {
            override fun run() {
                ModdedPEApplication.mPESdk = PESdk(context_a, UtilsSettings(context_a))
                ModdedPEApplication.mPESdk.init()
                mIsPreloadingFinished = true
                checkFinish()
            }
        }.start()

        object : Thread() {
            override fun run() {
                try {
                    sleep(5000)
                } catch (e: InterruptedException) {

                }

                mIsSleepingFinished = true
                checkFinish()
            }
        }.start()
    }

    private fun checkFinish() {
        if (mIsPreloadingFinished && mIsSleepingFinished)
            mListener.onPreloadingFinished()
    }

    interface PreloadingFinishedListener {
        fun onPreloadingFinished()
    }
}
