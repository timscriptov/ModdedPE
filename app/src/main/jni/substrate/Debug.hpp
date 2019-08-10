#ifndef SUBSTRATE_DEBUG_HPP
#define SUBSTRATE_DEBUG_HPP

#include "Log.hpp"
#define lprintf(format, ...) \
    MSLog(MSLogLevelNotice, format, ## __VA_ARGS__)

extern "C" bool MSDebug;
void MSLogHexEx(const void *vdata, size_t size, size_t stride, const char *mark = 0);
void MSLogHex(const void *vdata, size_t size, const char *mark = 0);

#endif//SUBSTRATE_DEBUG_HPP
