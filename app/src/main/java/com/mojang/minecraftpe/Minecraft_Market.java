package com.mojang.minecraftpe;

import android.content.Intent;
import android.net.Uri;

public class Minecraft_Market extends MainActivity {
    public void buyGame() {
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.mcal.mcpelauncher")));
    }
}
