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

import android.content.Context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
class NModManager {
    private final Context mContext;
    private ArrayList<NMod> mEnabledNMods = new ArrayList<>();
    private ArrayList<NMod> mAllNMods = new ArrayList<>();
    private ArrayList<NMod> mDisabledNMods = new ArrayList<>();

    NModManager(Context context) {
        this.mContext = context;
    }

    ArrayList<NMod> getEnabledNMods() {
        return mEnabledNMods;
    }

    ArrayList<NMod> getEnabledNModsIsValidBanner() {
        ArrayList<NMod> ret = new ArrayList<>();
        for (NMod nmod : getEnabledNMods()) {
            if (nmod.isValidBanner())
                ret.add(nmod);
        }
        return ret;
    }

    ArrayList<NMod> getAllNMods() {
        return mAllNMods;
    }

    void init() {
        mAllNMods = new ArrayList<>();
        mEnabledNMods = new ArrayList<>();
        mDisabledNMods = new ArrayList<>();

        NModDataLoader dataloader = new NModDataLoader(mContext);

        for (String item : dataloader.getAllList()) {
            if (!PackageNameChecker.isValidPackageName(item)) {
                dataloader.removeByName(item);
            }
        }

        forEachItemToAddNMod(dataloader.getEnabledList(), true);
        forEachItemToAddNMod(dataloader.getDisabledList(), false);
        refreshDatas();
    }

    void removeImportedNMod(NMod nmod) {
        mEnabledNMods.remove(nmod);
        mDisabledNMods.remove(nmod);
        mAllNMods.remove(nmod);
        NModDataLoader dataloader = new NModDataLoader(mContext);
        dataloader.removeByName(nmod.getPackageName());
        if (nmod.getNModType() == NMod.NMOD_TYPE_ZIPPED) {
            String zippedNModPath = new NModFilePathManager(mContext).getNModsDir() + File.separator + nmod.getPackageName();
            File file = new File(zippedNModPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void forEachItemToAddNMod(@NotNull ArrayList<String> list, boolean enabled) {
        for (String packageName : list) {
            try {
                String zippedNModPath = new NModFilePathManager(mContext).getNModsDir() + File.separator + packageName;
                ZippedNMod zippedNMod = new ZippedNMod(packageName, mContext, new File(zippedNModPath));
                importNMod(zippedNMod, enabled);
                continue;
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                NModExtractor extractor = new NModExtractor(mContext);
                PackagedNMod packagedNMod = extractor.archiveFromInstalledPackage(packageName);
                importNMod(packagedNMod, enabled);
            } catch (ExtractFailedException e) {
                e.printStackTrace();
            }
        }
    }

    boolean importNMod(NMod newNMod, boolean enabled) {
        boolean replaced = false;
        Iterator<NMod> iterator = mAllNMods.iterator();
        while (iterator.hasNext()) {
            NMod nmod = iterator.next();
            if (nmod.equals(newNMod)) {
                //iterator.remove();
                mEnabledNMods.remove(nmod);
                mDisabledNMods.remove(nmod);
                replaced = true;
            }
        }

        mAllNMods.add(newNMod);
        if (enabled)
            setEnabled(newNMod);
        else
            setDisable(newNMod);
        return replaced;
    }


    private void refreshDatas() {
        NModDataLoader dataloader = new NModDataLoader(mContext);

        for (String item : dataloader.getAllList()) {
            if (getImportedNMod(item) == null) {
                dataloader.removeByName(item);
            }
        }
    }

    @Nullable
    private NMod getImportedNMod(String pkgname) {
        for (NMod nmod : mAllNMods)
            if (nmod.getPackageName().equals(pkgname))
                return nmod;
        return null;
    }

    void makeUp(NMod nmod) {
        NModDataLoader dataloader = new NModDataLoader(mContext);
        dataloader.upNMod(nmod);
        refreshEnabledOrderList();
    }

    void makeDown(NMod nmod) {
        NModDataLoader dataloader = new NModDataLoader(mContext);
        dataloader.downNMod(nmod);
        refreshEnabledOrderList();
    }

    private void refreshEnabledOrderList() {
        NModDataLoader dataloader = new NModDataLoader(mContext);
        ArrayList<String> enabledList = dataloader.getEnabledList();
        mEnabledNMods.clear();
        for (String pkgName : enabledList) {
            NMod nmod = getImportedNMod(pkgName);
            if (nmod != null) {
                mEnabledNMods.add(nmod);
            }
        }
    }

    void setEnabled(@NotNull NMod nmod) {
        if (nmod.isBugPack())
            return;
        NModDataLoader dataloader = new NModDataLoader(mContext);
        dataloader.setIsEnabled(nmod, true);
        mEnabledNMods.add(nmod);
        mDisabledNMods.remove(nmod);
    }

    void setDisable(NMod nmod) {
        NModDataLoader dataloader = new NModDataLoader(mContext);
        dataloader.setIsEnabled(nmod, false);
        mDisabledNMods.add(nmod);
        mEnabledNMods.remove(nmod);
    }

    ArrayList<NMod> getDisabledNMods() {
        return mDisabledNMods;
    }
}
