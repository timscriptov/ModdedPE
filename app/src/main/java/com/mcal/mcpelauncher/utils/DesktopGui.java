package com.mcal.mcpelauncher.utils;

import android.content.Context;

import com.mcal.mcpelauncher.data.Preferences;

import org.jetbrains.annotations.NotNull;
import org.zeroturnaround.zip.commons.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DesktopGui {
    public static File pathOptionsFileDir;
    public static File xpathOptionsFileDir;
    public static File pathOptionsFile;
    public static File xpathOptionsFile;

    public static void run(@NotNull Context context) {
        pathOptionsFileDir = new File(ScopedStorage.getStorageDirectory() + "/games/com.mojang/minecraftpe");
        xpathOptionsFileDir = new File(context.getFilesDir().getAbsolutePath() + "/com.mojang/minecraftpe/games/com.mojang/minecraftpe");

        pathOptionsFile = new File(pathOptionsFileDir + "/options.txt");
        xpathOptionsFile = new File(xpathOptionsFileDir + "/options.txt");

        if (!pathOptionsFileDir.exists()) {
            pathOptionsFileDir.mkdirs();
        }

        if (!xpathOptionsFileDir.exists()) {
            xpathOptionsFileDir.mkdirs();
        }

        if (!pathOptionsFile.exists()) {
            try {
                write(pathOptionsFile, "gfx_guiscale_offset:0");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!xpathOptionsFile.exists()) {
            try {
                write(xpathOptionsFile, "gfx_guiscale_offset:0");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String fileContent = null;
        String xFileContent = null;

        try {
            fileContent = FileUtils.readFileToString(pathOptionsFile);
            xFileContent = FileUtils.readFileToString(xpathOptionsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Preferences.DesktopGui()) {
            try {
                reWrite(fileContent, pathOptionsFile, "-1");
                reWrite(xFileContent, xpathOptionsFile, "-1");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                reWrite(fileContent, pathOptionsFile, "0");
                reWrite(xFileContent, xpathOptionsFile, "0");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void write(File filePath, String txt) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        writer.write(txt);
        writer.flush();
    }

    public static void reWrite(@NotNull String fileContent, File filePath, String i) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        writer.write(fileContent.replaceFirst("gfx_guiscale_offset:(\\d|-\\d)", "gfx_guiscale_offset:" + i));
        writer.flush();
    }
}
