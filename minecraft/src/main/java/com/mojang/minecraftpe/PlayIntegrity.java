package com.mojang.minecraftpe;

/**
 * 13.08.2022
 *
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */
public class PlayIntegrity {
    native void nativePlayIntegrityComplete(int errorCode, String packageName, String appRecognitionVerdict, String deviceIntegrity, String appLicensingVerdict);
}