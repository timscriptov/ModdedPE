LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := nmod-core
LOCAL_SRC_FILES := nmod.cpp
LOCAL_LDLIBS    := -L$(LOCAL_PATH)/$(TARGET_ARCH_ABI) -llog -ldl -lsubstrate -lminecraftpe
TARGET_NO_UNDEFINED_LDFLAGS :=
include $(BUILD_SHARED_LIBRARY)
