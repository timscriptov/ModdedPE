package com.mojang.minecraftpe;

import android.content.Intent;

/**
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */
public interface ActivityListener {
    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onDestroy();

    void onResume();

    void onStop();

    void onShowedAds();
}
