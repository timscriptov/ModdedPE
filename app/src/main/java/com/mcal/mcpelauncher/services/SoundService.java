/*
 * Copyright (C) 2018-2020 Тимашков Иван
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
package com.mcal.mcpelauncher.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.mcal.mcpelauncher.R;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class SoundService extends Service {
    MediaPlayer player;

    @Override
    public IBinder onBind(Intent intent) {
        return new SoundBinder();
    }

    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(getApplicationContext(), R.raw.calm1);
        player.setLooping(true);
        player.start();
    }

    public void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
    }

    public void pause() {
        player.pause();
    }

    public void play() {
        player.start();
    }

    public class SoundBinder extends Binder {
        public SoundService getService() {
            return SoundService.this;
        }
    }
}