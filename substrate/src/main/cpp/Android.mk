LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)  
LOCAL_LDLIBS := -llog
LOCAL_MODULE    := substrate

LOCAL_CFLAGS := -Wno-error=format-security -fpermissive
LOCAL_CFLAGS += -fno-rtti -fno-exceptions

LOCAL_SRC_FILES := substrate/hde64.c \
           substrate/SubstrateDebug.cpp \
           substrate/SubstrateHook.cpp \
           substrate/SubstratePosixMemory.cpp
include $(BUILD_SHARED_LIBRARY)  
