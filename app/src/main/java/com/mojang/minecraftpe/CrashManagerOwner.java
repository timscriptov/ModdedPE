package com.mojang.minecraftpe;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

interface CrashManagerOwner {
    SessionInfo findSessionInfoForCrash(CrashManager crashManager, String str);
    String getCachedDeviceId(CrashManager crashManager);
    void notifyCrashUploadCompleted(CrashManager crashManager, SessionInfo sessionInfo);
}