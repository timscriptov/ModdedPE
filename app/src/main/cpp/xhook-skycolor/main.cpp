//
// Created by TimScriptov on 15.12.2020.
//

#include <jni.h>
#include <dlfcn.h>
#include <string>
#include <minecraft/Color.h>
#include "xhook.h"

static Color(*getColor)(void*, float);
static Color getColor_hook(void* a, float v) {
    return Color(1,0,0);
}

JNIEXPORT jint JNI_OnLoad(JavaVM*, void*) {
    // For Minecraft 1.16.201
    xhook_register(".*/libminecraftpe\\.so$", "_ZN5Biome11getSkyColorEf", (void*) &getColor_hook, (void**) &getColor);
    xhook_refresh(1);
    xhook_enable_debug(1);
    return JNI_VERSION_1_6;
}