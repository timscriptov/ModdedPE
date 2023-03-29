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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class NModTextEditor {
    private final NMod mTargetNMod;
    private final NModFilePathManager mManager;
    private final File[] mParents;

    public NModTextEditor(Context context, NMod nmod, File[] parents) {
        mTargetNMod = nmod;
        mManager = new NModFilePathManager(context);
        mParents = parents;
    }

    public File edit() throws IOException {
        File dir = mManager.getNModTextDir();
        dir.mkdirs();
        File file = mManager.getNModTextPath(mTargetNMod);
        file.createNewFile();
        ZipOutputStream zipOutPut = new ZipOutputStream(new FileOutputStream(file));
        zipOutPut.putNextEntry(new ZipEntry("AndroidManifest.xml"));
        zipOutPut.closeEntry();
        for (NMod.NModTextEditBean textEdit : mTargetNMod.getInfo().text_edit) {
            String src = readTextFromParents(textEdit.path);
            String srcThis = readTextFromThis(textEdit.path);
            if (textEdit.mode.equals(NMod.NModTextEditBean.MODE_REPLACE)) {
                zipOutPut.putNextEntry(new ZipEntry("assets" + File.separator + textEdit.path));
                zipOutPut.write(srcThis.getBytes());
                zipOutPut.closeEntry();
            } else if (textEdit.mode.equals(NMod.NModTextEditBean.MODE_APPEND)) {
                String result = src + srcThis;
                zipOutPut.putNextEntry(new ZipEntry("assets" + File.separator + textEdit.path));
                zipOutPut.write(result.getBytes());
                zipOutPut.closeEntry();
            } else if (textEdit.mode.equals(NMod.NModTextEditBean.MODE_PREPEND)) {
                String result = srcThis + src;
                zipOutPut.putNextEntry(new ZipEntry("assets" + File.separator + textEdit.path));
                zipOutPut.write(result.getBytes());
                zipOutPut.closeEntry();
            }
        }
        zipOutPut.flush();
        zipOutPut.close();
        return file;
    }

    private String readTextFromParents(String path) throws IOException {
        for (int index = mParents.length - 1; index >= 0; --index) {
            File parentItem = mParents[index];
            ZipFile zipFile = new ZipFile(parentItem);
            ZipEntry entry = zipFile.getEntry("assets" + File.separator + path);
            if (entry == null)
                continue;
            InputStream input = zipFile.getInputStream(entry);
            int byteRead;
            byte[] buffer = new byte[1024];
            String tmp = "";
            while ((byteRead = input.read(buffer)) > 0) {
                tmp += new String(buffer, 0, byteRead);
            }
            return tmp;
        }
        throw new FileNotFoundException(path);
    }

    private String readTextFromThis(String path) throws IOException {
        InputStream input = mTargetNMod.getAssets().open(path);
        int byteRead;
        byte[] buffer = new byte[1024];
        String tmp = "";
        while ((byteRead = input.read(buffer)) > 0) {
            tmp += new String(buffer, 0, byteRead);
        }
        return tmp;
    }
}
