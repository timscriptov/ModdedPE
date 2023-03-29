package com.mojang.minecraftpe;

import android.content.Intent;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public interface ActivityListener {
    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onDestroy();

    void onResume();

    void onStop();
}