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
package com.mcal.pesdk.nmod;

import android.os.Bundle;

import com.mojang.minecraftpe.MainActivity;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class NModLib {
    static {
        nativeRegisterNatives(NModLib.class);
    }

    private String mName;

    public NModLib(String name) {
        mName = name;
    }

    private static native boolean nativeRegisterNatives(Class cls);

    private static native boolean nativeCallOnActivityFinish(String name, MainActivity mainActivity);

    private static native boolean nativeCallOnLoad(String name, String mcVersion, String apiVersion);

    private static native boolean nativeCallOnActivityCreate(String mame, MainActivity mainActivity, Bundle savedInstanceState);

    public boolean callOnActivityCreate(com.mojang.minecraftpe.MainActivity mainActivity, Bundle bundle) {
        return nativeCallOnActivityCreate(mName, mainActivity, bundle);
    }

    public boolean callOnActivityFinish(com.mojang.minecraftpe.MainActivity mainActivity) {
        return nativeCallOnActivityFinish(mName, mainActivity);
    }

    public boolean callOnLoad(String mcver, String apiVer) {
        return nativeCallOnLoad(mName, mcver, apiVer);
    }
}
