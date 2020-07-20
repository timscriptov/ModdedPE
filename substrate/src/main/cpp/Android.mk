LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)  
LOCAL_LDLIBS := -llog
LOCAL_MODULE    := substrate
LOCAL_SRC_FILES := substrate/hde64.c substrate/Hooker.cpp substrate/Debug.cpp substrate/PosixMemory.cpp
include $(BUILD_SHARED_LIBRARY)  
