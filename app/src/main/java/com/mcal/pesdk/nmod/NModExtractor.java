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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
class NModExtractor {
    private Context mContext;

    NModExtractor(Context context) {
        mContext = context;
    }

    PackagedNMod archiveFromInstalledPackage(String packageName) throws ExtractFailedException {
        try {
            Context contextPackage = mContext.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
            contextPackage.getAssets().open(NMod.MANIFEST_NAME).close();
            return new PackagedNMod(packageName, mContext, contextPackage);
        } catch (IOException e) {
            throw new ExtractFailedException(ExtractFailedException.TYPE_NO_MANIFEST, e);
        } catch (PackageManager.NameNotFoundException notFoundE) {
            throw new ExtractFailedException(ExtractFailedException.TYPE_PACKAGE_NOT_FOUND, notFoundE);
        }
    }

    ZippedNMod archiveFromZipped(String path) throws ExtractFailedException {
        try {
            new ZipFile(new File(path));
        } catch (ZipException zipE) {
            throw new ExtractFailedException(ExtractFailedException.TYPE_DECODE_FAILED, zipE);
        } catch (IOException ioe) {
            throw new ExtractFailedException(ExtractFailedException.TYPE_IO_EXCEPTION, ioe);
        }

        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_CONFIGURATIONS);
        NMod.NModInfo nmodInfo = archiveInfoFromZipped(new File(path));

        if (packageInfo != null) {
            if (nmodInfo.package_name != null && !nmodInfo.package_name.equals(packageInfo.packageName))
                throw new ExtractFailedException(ExtractFailedException.TYPE_INEQUAL_PACKAGE_NAME, new RuntimeException("Package name defined in AndroidManifest.xml and nmod_manifest.json must equal!"));

            nmodInfo.package_name = packageInfo.packageName;

            try {
                File nmodDir = new NModFilePathManager(mContext).getNModCacheDir();
                nmodDir.mkdirs();
                File toFile = new NModFilePathManager(mContext).getNModCachePath();
                toFile.createNewFile();
                ZipFile zipFile = new ZipFile(path);
                String packageName = packageInfo.packageName;
                String versionName = packageInfo.versionName;
                int versionCode = packageInfo.versionCode;
                packageInfo.applicationInfo.sourceDir = path;
                packageInfo.applicationInfo.publicSourceDir = path;
                Drawable icon = packageManager.getApplicationIcon(packageInfo.applicationInfo);
                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(toFile));
                ZipInputStream zipInput = new ZipInputStream(new BufferedInputStream(new FileInputStream(path)));
                ZipEntry entry;

                while ((entry = zipInput.getNextEntry()) != null) {
                    if (!entry.isDirectory() && !(entry.getName().equals(NMod.MANIFEST_NAME) || entry.getName().endsWith(File.separator + NMod.MANIFEST_NAME))) {
                        zipOutputStream.putNextEntry(entry);
                        InputStream from = zipFile.getInputStream(entry);
                        int byteRead;
                        byte[] buffer = new byte[1024];
                        while ((byteRead = from.read(buffer)) != -1) {
                            zipOutputStream.write(buffer, 0, byteRead);
                        }
                        from.close();
                        zipOutputStream.closeEntry();
                    }
                }

                //Manifest
                nmodInfo.package_name = packageName;
                nmodInfo.version_code = versionCode;
                nmodInfo.version_name = versionName;
                zipOutputStream.putNextEntry(new ZipEntry(NMod.MANIFEST_NAME));
                zipOutputStream.write(new Gson().toJson(nmodInfo).getBytes());
                zipOutputStream.closeEntry();

                //Icon
                Bitmap bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), icon.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(bitmap);
                icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                icon.draw(canvas);
                zipOutputStream.putNextEntry(new ZipEntry("icon.png"));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                zipOutputStream.write(baos.toByteArray());
                zipOutputStream.closeEntry();

                zipOutputStream.flush();
                zipOutputStream.close();
                zipInput.close();

                return new ZippedNMod(packageName, mContext, copyCachedNModToData(toFile, packageName));
            } catch (IOException ioe) {
                throw new ExtractFailedException(ExtractFailedException.TYPE_IO_EXCEPTION, ioe);
            }
        } else {
            if (nmodInfo.package_name == null)
                throw new ExtractFailedException(ExtractFailedException.TYPE_UNDEFINED_PACKAGE_NAME, new RuntimeException("Undefined package name in manifest."));
            if (!PackageNameChecker.isValidPackageName(nmodInfo.package_name))
                throw new ExtractFailedException(ExtractFailedException.TYPE_INVAILD_PACKAGE_NAME, new RuntimeException("The provided package name is not a valid java-styled package name."));

            try {
                ZipFile zipFile = new ZipFile(path);
                ZipInputStream zipInput = new ZipInputStream(new BufferedInputStream(new FileInputStream(path)));
                File dir = new NModFilePathManager(mContext).getNModCacheDir();
                dir.mkdirs();
                File nmodFile = new NModFilePathManager(mContext).getNModCachePath();
                nmodFile.createNewFile();
                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(nmodFile));
                ZipEntry entry;
                while ((entry = zipInput.getNextEntry()) != null) {
                    if (!entry.isDirectory()) {
                        zipOutputStream.putNextEntry(entry);
                        InputStream from = zipFile.getInputStream(entry);
                        int byteRead = -1;
                        byte[] buffer = new byte[1024];
                        while ((byteRead = from.read(buffer)) != -1) {
                            zipOutputStream.write(buffer, 0, byteRead);
                        }
                        from.close();
                        zipOutputStream.closeEntry();
                    }
                }
                ZipEntry entryManifest = new ZipEntry("AndroidManifest.xml");
                zipOutputStream.putNextEntry(entryManifest);
                zipOutputStream.closeEntry();
                zipOutputStream.flush();
                zipOutputStream.close();
                zipInput.close();

                return new ZippedNMod(nmodInfo.package_name, mContext, copyCachedNModToData(nmodFile, nmodInfo.package_name));
            } catch (IOException ioe) {
                throw new ExtractFailedException(ExtractFailedException.TYPE_IO_EXCEPTION, ioe);
            }
        }
    }

    @NotNull
    private File copyCachedNModToData(File cachedNModFile, String packageName) throws ExtractFailedException {
        try {
            File finalFileDir = new NModFilePathManager(mContext).getNModsDir();
            finalFileDir.mkdirs();
            File finalFile = new File(new NModFilePathManager(mContext).getNModsDir() + File.separator + packageName);
            finalFile.createNewFile();
            FileOutputStream finalFileOutput = new FileOutputStream(finalFile);
            FileInputStream fileInput = new FileInputStream(cachedNModFile);
            int byteRead;
            byte[] buffer = new byte[1024];
            while ((byteRead = fileInput.read(buffer)) != -1) {
                finalFileOutput.write(buffer, 0, byteRead);
            }
            finalFileOutput.close();
            fileInput.close();
            cachedNModFile.delete();
            return finalFile;
        } catch (IOException ioe) {
            throw new ExtractFailedException(ExtractFailedException.TYPE_IO_EXCEPTION, ioe);
        }
    }

    ArrayList<NMod> archiveAllFromInstalled() {
        PackageManager packageManager = mContext.getPackageManager();
        List<PackageInfo> infos = packageManager.getInstalledPackages(0);
        ArrayList<NMod> list = new ArrayList<>();
        for (PackageInfo info : infos) {
            try {
                PackagedNMod packagedNMod = archiveFromInstalledPackage(info.packageName);
                list.add(packagedNMod);
            } catch (ExtractFailedException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private NMod.NModInfo archiveInfoFromZipped(File filePath) throws ExtractFailedException {
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(filePath);
        } catch (IOException e) {
            throw new ExtractFailedException(ExtractFailedException.TYPE_DECODE_FAILED, e);
        }
        ZipEntry manifest1 = zipFile.getEntry(NMod.MANIFEST_NAME);
        ZipEntry manifest2 = zipFile.getEntry("assets" + File.separator + NMod.MANIFEST_NAME);
        if (manifest1 != null && manifest2 != null)
            throw new ExtractFailedException(ExtractFailedException.TYPE_REDUNDANT_MANIFEST, new RuntimeException("NModAPI found two nmod_manifest.json in this file but didn't know which one to read.Please delete one.(/nmod_manifest.json or /assets/nmod_manifest.json)"));
        if (manifest1 == null && manifest2 == null)
            throw new ExtractFailedException(ExtractFailedException.TYPE_NO_MANIFEST, new RuntimeException("There is no nmod_manifest.json found in this file."));
        ZipEntry manifest = manifest1 == null ? manifest2 : manifest1;
        try {
            InputStream input = zipFile.getInputStream(manifest);
            int byteRead;
            byte[] buffer = new byte[1024];
            String tmp = "";
            while ((byteRead = input.read(buffer)) > 0) {
                tmp += new String(buffer, 0, byteRead);
            }
            return new Gson().fromJson(tmp, NMod.NModInfo.class);
        } catch (IOException ioe) {
            throw new ExtractFailedException(ExtractFailedException.TYPE_IO_EXCEPTION, ioe);
        } catch (JsonSyntaxException jsonSyntaxE) {
            throw new ExtractFailedException(ExtractFailedException.TYPE_JSON_SYNTAX_EXCEPTION, jsonSyntaxE);
        }
    }
}
