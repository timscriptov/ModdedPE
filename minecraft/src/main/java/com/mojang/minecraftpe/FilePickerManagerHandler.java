package com.mojang.minecraftpe;

import android.content.Intent;

/**
 * 13.08.2022
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public interface FilePickerManagerHandler {
    void startPickerActivity(Intent intent, int requestCode);
}