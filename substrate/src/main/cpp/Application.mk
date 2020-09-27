APP_STL := system
APP_ABI := armeabi-v7a arm64-v8a x86 x86_64
APP_PLATFORM := android-16
#APP_CFLAGS := -O2 -Wno-logical-op-parentheses

ifneq ($(APP_OPTIM), debug)
  $(info APP_OPTIM is $(APP_OPTIM) ...)
  APP_LDFLAGS  += -Wl,--strip-all
  APP_CFLAGS   += -fvisibility=hidden -fvisibility-inlines-hidden
  APP_CFLAGS   += -g0 -O3 -fomit-frame-pointer -ffunction-sections -fdata-sections
  APP_CPPFLAGS += -std=c++11
endif