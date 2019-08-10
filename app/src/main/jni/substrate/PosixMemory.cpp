#define SubstrateInternal
#include "CydiaSubstrate.h"

#include "Log.hpp"

#include <sys/mman.h>

#include <errno.h>
#include <stdio.h>
#include <unistd.h>

extern "C" void __clear_cache (void *beg, void *end);

struct __SubstrateMemory {
    void *address_;
    size_t width_;

    __SubstrateMemory(void *address, size_t width) :
        address_(address),
        width_(width)
    {
    }
};

extern "C" SubstrateMemoryRef SubstrateMemoryCreate(SubstrateAllocatorRef allocator, SubstrateProcessRef process, void *data, size_t size) {
    if (allocator != NULL) {
        MSLog(MSLogLevelError, "MS:Error:allocator != NULL");
        return NULL;
    }

    if (size == 0)
        return NULL;

    int page(getpagesize());

    uintptr_t base(reinterpret_cast<uintptr_t>(data) / page * page);
    size_t width(((reinterpret_cast<uintptr_t>(data) + size - 1) / page + 1) * page - base);
    void *address(reinterpret_cast<void *>(base));

    if (mprotect(address, width, PROT_READ | PROT_WRITE | PROT_EXEC) == -1) {
        MSLog(MSLogLevelError, "MS:Error:mprotect() = %d", errno);
        return NULL;
    }

    return new __SubstrateMemory(address, width);
}

extern "C" void SubstrateMemoryRelease(SubstrateMemoryRef memory) {
    if (mprotect(memory->address_, memory->width_, PROT_READ | PROT_WRITE | PROT_EXEC) == -1)
        MSLog(MSLogLevelError, "MS:Error:mprotect() = %d", errno);

    __clear_cache(reinterpret_cast<char *>(memory->address_), reinterpret_cast<char *>(memory->address_) + memory->width_);

    delete memory;
}
