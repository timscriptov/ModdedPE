/*
 * Copyright (C) 2018-2025 Тимашков Иван
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

import android.content.Context;
import org.jetbrains.annotations.NotNull;

import java.io.File;

class NModFilePathManager {
    private static final String FILEPATH_DIR_NAME_NMOD_PACKS = "nmod_packs";
    private static final String FILEPATH_DIR_NAME_NMOD_LIBS = "nmod_libs";
    private static final String FILEPATH_DIR_NAME_NMOD_ICON = "nmod_icon";
    private static final String FILEPATH_FILE_NAME_NMOD_CAHCHE = "nmod_cached";
    private static final String FILEPATH_DIR_NAME_NMOD_JSON_PACKS = "nmod_json_packs";
    private static final String FILEPATH_DIR_NAME_NMOD_TEXT_PACKS = "nmod_text_packs";
    private final Context mContext;

    public NModFilePathManager(Context context) {
        this.mContext = context;
    }

    public File getNModsDir() {
        return new File(mContext.getFilesDir().getAbsolutePath() + File.separator + FILEPATH_DIR_NAME_NMOD_PACKS);
    }

    public File getNModJsonDir() {
        return new File(mContext.getFilesDir().getAbsolutePath() + File.separator + FILEPATH_DIR_NAME_NMOD_JSON_PACKS);
    }

    public File getNModJsonPath(@NotNull NMod nmod) {
        return new File(getNModJsonDir(), nmod.getPackageName());
    }

    public File getNModTextDir() {
        return new File(mContext.getFilesDir().getAbsolutePath() + File.separator + FILEPATH_DIR_NAME_NMOD_TEXT_PACKS);
    }

    public File getNModTextPath(@NotNull NMod nmod) {
        return new File(getNModTextDir(), nmod.getPackageName());
    }

    public File getNModLibsDir() {
        return new File(mContext.getFilesDir().getAbsolutePath() + File.separator + FILEPATH_DIR_NAME_NMOD_LIBS);
    }

    public File getNModCacheDir() {
        return new File(mContext.getCacheDir().getAbsolutePath());
    }

    public File getNModCachePath() {
        return new File(mContext.getCacheDir().getAbsolutePath() + File.separator + FILEPATH_FILE_NAME_NMOD_CAHCHE);
    }

    public File getNModIconDir() {
        return new File(mContext.getFilesDir().getAbsolutePath() + File.separator + FILEPATH_DIR_NAME_NMOD_ICON);
    }

    public File getNModIconPath(@NotNull NMod nmod) {
        return new File(getNModIconDir().getAbsolutePath() + File.separator + nmod.getPackageName());
    }
}
