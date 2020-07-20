LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := launcher-core
LOCAL_SRC_FILES := launcher-core/launcher.cpp
LOCAL_SHARED_LIBRARIES := -L$(LOCAL_PATH)/$(TARGET_ARCH_ABI) -lsubstrate -lminecraftpe
LOCAL_LDLIBS    := -L$(LOCAL_PATH)/$(TARGET_ARCH_ABI) -llog -ldl
TARGET_NO_UNDEFINED_LDFLAGS :=
include $(BUILD_SHARED_LIBRARY)