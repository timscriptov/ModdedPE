LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)  
LOCAL_LDLIBS := -llog
LOCAL_MODULE    := nmod-core
LOCAL_SRC_FILES := nmod.cpp
include $(BUILD_SHARED_LIBRARY)  
