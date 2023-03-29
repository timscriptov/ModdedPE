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

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Process
import com.mcal.mcpelauncher.activities.ExceptionActivity
import org.jetbrains.annotations.Contract
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import kotlin.system.exitProcess

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
class ExceptionHandler @Contract(pure = true) constructor(private val mContext: Activity) :
    Thread.UncaughtExceptionHandler {
    override fun uncaughtException(thread: Thread, th: Throwable) {
        val stringWriter: Writer = StringWriter()
        th.printStackTrace(PrintWriter(stringWriter))
        val stringBuilder = StringBuilder()
        stringBuilder.append("************ APPLICATION ERROR ************\n\n")
        stringBuilder.append(stringWriter.toString())
        stringBuilder.append("\n************ DEVICE INFORMATION ***********\n")
        stringBuilder.append("Brand: ")
        stringBuilder.append(Build.BRAND)
        stringBuilder.append("\n")
        stringBuilder.append("Device: ")
        stringBuilder.append(Build.DEVICE)
        stringBuilder.append("\n")
        stringBuilder.append("Model: ")
        stringBuilder.append(Build.MODEL)
        stringBuilder.append("\n")
        stringBuilder.append("Id: ")
        stringBuilder.append(Build.ID)
        stringBuilder.append("\n")
        stringBuilder.append("Product: ")
        stringBuilder.append(Build.PRODUCT)
        stringBuilder.append("\n")
        stringBuilder.append("\n************ FIRMWARE ************\n")
        stringBuilder.append("SDK: ")
        stringBuilder.append(Build.VERSION.SDK)
        stringBuilder.append("\n")
        stringBuilder.append("Release: ")
        stringBuilder.append(Build.VERSION.RELEASE)
        stringBuilder.append("\n")
        stringBuilder.append("Incremental: ")
        stringBuilder.append(Build.VERSION.INCREMENTAL)
        stringBuilder.append("\n")
        try {
            val intent = Intent(mContext, ExceptionActivity::class.java)
            intent.putExtra("error", stringBuilder.toString())
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            mContext.startActivity(intent)
            Process.killProcess(Process.myPid())
            exitProcess(10)
        } catch (e: Throwable) {
            throw NoClassDefFoundError(e.message)
        }
    }
}