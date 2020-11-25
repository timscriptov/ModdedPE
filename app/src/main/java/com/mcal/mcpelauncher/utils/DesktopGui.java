package com.mcal.mcpelauncher.utils;

import com.mcal.mcpelauncher.data.Preferences;

import org.zeroturnaround.zip.commons.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DesktopGui {
    public static void run() {
        File optionsFileDir = new File(ScopedStorage.getStorageDirectory() + "/games/com.mojang/minecraftpe");
        File optionsFile = new File(optionsFileDir + "/options.txt");

        if (!optionsFileDir.exists()) {
            optionsFileDir.mkdirs();
        }

        if (!optionsFile.exists()) {
            try {
                FileWriter writer = new FileWriter(optionsFile);
                writer.write("gfx_guiscale_offset:0");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String fileContent = null;
        try {
            fileContent = FileUtils.readFileToString(optionsFile);
        } catch (IOException ignored) {
        }

        if (Preferences.DesktopGui()) {
            try {
                FileWriter writer = new FileWriter(optionsFile);
                writer.write(fileContent.replaceFirst("gfx_guiscale_offset:(\\d|-\\d)", "gfx_guiscale_offset:-1"));
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileWriter writer = new FileWriter(optionsFile);
                writer.write(fileContent.replaceFirst("gfx_guiscale_offset:(\\d|-\\d)", "gfx_guiscale_offset:0"));
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
