package com.mojang.minecraftpe;

import android.content.Intent;

/**
 * 13.08.2022
 *
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */
public interface FilePickerManagerHandler {
    void startPickerActivity(Intent intent, int requestCode);
}