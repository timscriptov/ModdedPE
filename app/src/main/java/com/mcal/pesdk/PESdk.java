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
package com.mcal.pesdk;

import android.content.Context;

import com.mcal.pesdk.nmod.NModAPI;
import com.mcal.pesdk.utils.LauncherOptions;
import com.mcal.pesdk.utils.MinecraftInfo;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class PESdk {
    private MinecraftInfo mMinecraftInfo;
    private NModAPI mNModAPI;
    private LauncherOptions mLauncherOptions;
    private GameManager mGameManager;
    private boolean mIsInited;

    public PESdk(Context context, LauncherOptions options) {
        mMinecraftInfo = new MinecraftInfo(context, options);
        mNModAPI = new NModAPI(context);
        mLauncherOptions = options;
        mGameManager = new GameManager(this);
        mIsInited = false;
    }

    public void init() {
        mNModAPI.initNModDatas();
        mIsInited = true;
    }

    public boolean isInited() {
        return mIsInited;
    }

    public NModAPI getNModAPI() {
        return mNModAPI;
    }

    public MinecraftInfo getMinecraftInfo() {
        return mMinecraftInfo;
    }

    public LauncherOptions getLauncherOptions() {
        return mLauncherOptions;
    }

    public GameManager getGameManager() {
        return mGameManager;
    }
}
