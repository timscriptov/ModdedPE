//-------------------------------------------------------------
// Includes
//-------------------------------------------------------------

#include <jni.h>
#include <string>
#include <cxxabi.h>
#include <dlfcn.h>

//-------------------------------------------------------------
// Variants
//-------------------------------------------------------------

JavaVM* mJvm = nullptr;
//std::string* mAddrAndroidAppDataPath = nullptr;

std::string mMCPENativeLibPath;

//-------------------------------------------------------------
// Methods Definition
//-------------------------------------------------------------

std::string toString(JNIEnv* env, jstring j_str) {
    //DO NOT RELEASE.
    const char * c_str = env->GetStringUTFChars(j_str, nullptr);
    std::string cpp_str = c_str;
    return cpp_str;
}

//-------------------------------------------------------------
// Native Methods
//-------------------------------------------------------------

namespace NModAPI {
    //void nativeSetDataDirectory(JNIEnv*env,jobject thiz,jstring directory) {
    //    *mAddrAndroidAppDataPath = toString(env,directory);
    //}

    jboolean nativeCallOnActivityFinish(JNIEnv*env,jobject thiz,jstring libname,jobject mainActivity) {
        void* image=dlopen(toString(env,libname).c_str(),RTLD_LAZY);
        void (*NMod_onActivityFinish)(JNIEnv*env,jobject thiz)=
        (void (*)(JNIEnv*,jobject)) dlsym(image,"NMod_OnActivityFinish");
        if(NMod_onActivityFinish)
        {
            NMod_onActivityFinish(env,mainActivity);
        }
        dlclose(image);
        return 0;
    }
    jboolean nativeCallOnLoad(JNIEnv*env,jobject thiz,jstring libname,jstring mcVer,jstring apiVersion) {
        void* image=dlopen(toString(env,libname).c_str(),RTLD_LAZY);
        void (*NMod_onLoad)(JavaVM*,JNIEnv*,const char*,const char*,const char*)=
        (void (*)(JavaVM*,JNIEnv*,const char*,const char*,const char*)) dlsym(image,"NMod_OnLoad");
        if(NMod_onLoad)
        {
            NMod_onLoad(mJvm,env,toString(env,mcVer).c_str(),toString(env,apiVersion).c_str(),mMCPENativeLibPath.c_str());
        }
        dlclose(image);
        return 0;
    }
    jboolean nativeCallOnActivityCreate(JNIEnv*env,jobject thiz,jstring libname,jobject mainActivity,jobject bundle) {
        void* image=dlopen(toString(env,libname).c_str(),RTLD_LAZY);
        void (*NMod_onActivityCreate)(JNIEnv*env,jobject thiz,jobject savedInstanceState)=
        (void (*)(JNIEnv*,jobject,jobject)) dlsym(image,"NMod_OnActivityCreate");
        if(NMod_onActivityCreate) {
            NMod_onActivityCreate(env,mainActivity,bundle);
        }
        dlclose(image);
        return 0;
    }
}

//-------------------------------------------------------------
// Register Natives
//-------------------------------------------------------------

extern "C" JNIEXPORT jboolean JNICALL Java_com_mcal_pesdk_nmod_NModLib_nativeRegisterNatives(JNIEnv*env, jclass thiz, jclass cls) {
    JNINativeMethod methods[] = {
        {"nativeCallOnActivityFinish", "(Ljava/lang/String;Lcom/mojang/minecraftpe/MainActivity;)Z", (void *)&NModAPI::nativeCallOnActivityFinish},
        {"nativeCallOnLoad", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z", (void *)&NModAPI::nativeCallOnLoad},
        {"nativeCallOnActivityCreate", "(Ljava/lang/String;Lcom/mojang/minecraftpe/MainActivity;Landroid/os/Bundle;)Z", (void *)&NModAPI::nativeCallOnActivityCreate}
    };
    
    if (env->RegisterNatives(cls,methods,sizeof(methods)/sizeof(JNINativeMethod)) < 0)
        return JNI_FALSE;
    return JNI_TRUE;
}

extern "C" JNIEXPORT void JNICALL Java_com_mcal_pesdk_nativeapi_LibraryLoader_nativeOnNModAPILoaded(JNIEnv*env, jclass thiz, jstring libPath) {
    const char *mNativeLibPath;
    mNativeLibPath = toString(env, libPath).c_str();
    mMCPENativeLibPath = mNativeLibPath;
    void* imageMCPE = (void*)dlopen(mNativeLibPath,RTLD_LAZY);

    // Deleted in Minecraft 1.16.210
    //mAddrAndroidAppDataPath = ((std::string*)dlsym(imageMCPE,"_ZN19AppPlatform_android20ANDROID_APPDATA_PATHE"));
    dlclose(imageMCPE);
}

//-------------------------------------------------------------
// On Load
//-------------------------------------------------------------

JNIEXPORT jint JNI_OnLoad(JavaVM*vm,void*) {
    mJvm=vm;

    return JNI_VERSION_1_6;
}

