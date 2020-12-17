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
LOCAL_MODULE    := nmod-core
LOCAL_SRC_FILES := nmod-core/main.cpp
LOCAL_LDLIBS    := -L$(LOCAL_PATH)/$(TARGET_ARCH_ABI) -llog -ldl -lsubstrate -lminecraftpe -lfmod
TARGET_NO_UNDEFINED_LDFLAGS :=
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := launcher-core
LOCAL_SRC_FILES := launcher-core/main.cpp
LOCAL_LDLIBS    := -L$(LOCAL_PATH)/$(TARGET_ARCH_ABI) -llog -ldl -lsubstrate -lminecraftpe -lfmod
TARGET_NO_UNDEFINED_LDFLAGS :=
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE     := xhook-skycolor
LOCAL_SRC_FILES  := xhook-skycolor/main.cpp
TARGET_NO_UNDEFINED_LDFLAGS :=
LOCAL_LDLIBS := -L$(LOCAL_PATH)/$(TARGET_ARCH_ABI) -llog -ldl -lxhook -lminecraftpe -lfmod
include $(BUILD_SHARED_LIBRARY)