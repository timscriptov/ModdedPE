package com.mojang.minecraftpe;

/**
 * 13.08.2022
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class PlayIntegrity {
    native void nativePlayIntegrityComplete(int errorCode, String packageName, String appRecognitionVerdict, String deviceIntegrity, String appLicensingVerdict);
}