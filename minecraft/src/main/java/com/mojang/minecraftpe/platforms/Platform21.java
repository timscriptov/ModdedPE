package com.mojang.minecraftpe.platforms;

import android.os.Build;

import java.util.Arrays;

/**
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */

public class Platform21 extends Platform19 {
    public Platform21(boolean initEventHandler) {
        super(initEventHandler);
    }

    public String getABIS() {
        return Arrays.toString(Build.SUPPORTED_ABIS);
    }
}