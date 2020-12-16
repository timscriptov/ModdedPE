//
// Created by TimScriptov on 15.12.2020.
//

#include <jni.h>
#include <dlfcn.h>
#include <string>
#include <minecraft/Color.h>
#include "../../../../xhook/src/main/cpp/xhook.h"

//struct GuiData {
//    void displayClientMessage(std::__ndk1::basic_string<char, std::__ndk1::char_traits<char>, std::__ndk1::allocator<char> > const&);
//};

//struct Player;

//struct ClientInstance {
//    GuiData* getGuiData();
//};

//void (*onPlayerLoaded)(ClientInstance*, Player&);
//void onPlayerLoaded_hook(ClientInstance*mc, Player&p) {
//    onPlayerLoaded(mc, p);
//    mc->getGuiData()->displayClientMessage("ModdedPE");
//}

//static std::string (*getVersion)(std::string const&);
//static std::string getVersion_hook(std::string const& str){
//    return "ModdedPE";
//}

static Color(*getColor)(void*, float);
static Color getColor_hook(void* a, float v) {
    return Color(1,0,0);
}

JNIEXPORT jint JNI_OnLoad(JavaVM*, void*) {
    //xhook_register(".*/libminecraftpe\\.so$", "_ZN13MinecraftGame14onPlayerLoadedER15IClientInstancer6Player", (void*)&onPlayerLoaded_hook, (void**)&onPlayerLoaded);
    //xhook_register(".*/libminecraftpe\\.so$", "_ZN6Common20getGameVersionStringEv", (void*) &getVersion_hook, (void**) &getVersion);
    xhook_register(".*/libminecraftpe\\.so$", "_ZN5Biome11getSkyColorEf", (void*) &getColor_hook, (void**) &getColor);
    xhook_refresh(1);
    xhook_enable_debug(1);
    return JNI_VERSION_1_6;
}