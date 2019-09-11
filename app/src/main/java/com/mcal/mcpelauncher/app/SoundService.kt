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
package com.mcal.mcpelauncher.app

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

import com.mcal.mcpelauncher.R

class SoundService : Service() {
    internal lateinit var player: MediaPlayer

    override fun onBind(intent: Intent): IBinder? {
        return SoundBinder()
    }

    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer.create(applicationContext, R.raw.calm1)
        player.isLooping = true
        player.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        player.release()
    }

    fun pause() {
        player.pause()
    }

    fun play() {
        player.start()
    }

    inner class SoundBinder : Binder() {
        val service: SoundService
            get() = this@SoundService
    }
}
