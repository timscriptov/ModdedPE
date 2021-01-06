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
package com.mcal.pesdk.nmod;

import android.content.Context;

import com.mcal.mcpelauncher.data.Constants;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public final class NModAPI {
    private Context mContext;
    private NModManager mNModManager;
    private NModExtractor mExtractor;

    public NModAPI(Context context) {
        mContext = context;
        mNModManager = new NModManager(context);
        mExtractor = new NModExtractor(context);
    }

    public ZippedNMod archiveZippedNMod(String filePath) throws ExtractFailedException {
        return mExtractor.archiveFromZipped(filePath);
    }

    public void initNModDatas() {
        mNModManager.init();
    }

    public ArrayList<NMod> getLoadedNMods() {
        return mNModManager.getAllNMods();
    }

    public ArrayList<NMod> getImportedEnabledNMods() {
        return mNModManager.getEnabledNMods();
    }

    public ArrayList<NMod> getImportedDisabledNMods() {
        return mNModManager.getDisabledNMods();
    }

    public ArrayList<NMod> getImportedEnabledNModsHaveBanners() {
        return mNModManager.getEnabledNModsIsValidBanner();
    }

    public ArrayList<NMod> findInstalledNMods() {
        NModExtractor arvhiver = new NModExtractor(mContext);
        return arvhiver.archiveAllFromInstalled();
    }

    public boolean importNMod(NMod nmod) {
        return mNModManager.importNMod(nmod, false);
    }

    public void removeImportedNMod(NMod nmod) {
        mNModManager.removeImportedNMod(nmod);
    }

    public void setEnabled(NMod nmod, boolean enabled) {
        if (enabled)
            mNModManager.setEnabled(nmod);
        else
            mNModManager.setDisable(nmod);
    }

    public void upPosNMod(NMod nmod) {
        mNModManager.makeUp(nmod);
    }

    public void downPosNMod(NMod nmod) {
        mNModManager.makeDown(nmod);
    }

    public PackagedNMod archivePackagedNMod(String packageName) throws ExtractFailedException {
        NModExtractor extractor = new NModExtractor(mContext);
        return extractor.archiveFromInstalledPackage(packageName);
    }

    @NotNull
    @Contract(pure = true)
    public String getVersionName() {
        return Constants.NMOD_API_VERSION;
    }
}
