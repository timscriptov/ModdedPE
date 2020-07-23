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
package com.mcal.mcpelauncher.utils;

import android.content.Context;

import com.mcal.mcpelauncher.ModdedPEApplication;
import com.mcal.pesdk.PESdk;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class DataPreloader {
    private PreloadingFinishedListener mListener;
    private boolean mIsSleepingFinished = false;
    private boolean mIsPreloadingFinished = false;

    public DataPreloader(PreloadingFinishedListener litenser) {
        mListener = litenser;
    }

    public void preload(Context context_a) {
        final Context context = context_a;
        new Thread() {
            public void run() {
                ModdedPEApplication.mPESdk = new PESdk(context);
                ModdedPEApplication.mPESdk.init();
                mIsPreloadingFinished = true;
                checkFinish();
            }
        }.start();

        new Thread() {
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mIsSleepingFinished = true;
                checkFinish();
            }
        }.start();
    }

    private void checkFinish() {
        if (mIsPreloadingFinished && mIsSleepingFinished)
            mListener.onPreloadingFinished();
    }
}
