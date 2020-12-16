LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE     := fmod
LOCAL_SRC_FILES  := $(TARGET_ARCH_ABI)/libfmod.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE     := xhook
LOCAL_SRC_FILES  := $(TARGET_ARCH_ABI)/libxhook.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE     := minecraftpe
LOCAL_SRC_FILES  := $(TARGET_ARCH_ABI)/libminecraftpe.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE     := xhook-skycolor
LOCAL_SRC_FILES  := test.cpp
TARGET_NO_UNDEFINED_LDFLAGS :=
LOCAL_LDLIBS := -L$(LOCAL_PATH)/$(TARGET_ARCH_ABI) -llog -lxhook -lminecraftpe -lfmod
include $(BUILD_SHARED_LIBRARY)