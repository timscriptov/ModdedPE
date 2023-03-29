package com.mojang.minecraftpe;

import android.content.Intent;
import android.net.Uri;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class Minecraft_Market_Demo extends MainActivity {
    public void buyGame() {
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.mojang.minecraftpe")));
    }

    public boolean isDemo() {
        return true;
    }
}