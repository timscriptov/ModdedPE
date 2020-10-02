package com.mojang.minecraftpe;

import android.content.Intent;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public interface ActivityListener {
    void onActivityResult(int i, int i2, Intent intent);

    void onDestroy();

    void onResume();

    void onStop();
}
