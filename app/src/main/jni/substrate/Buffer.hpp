#ifndef SUBSTRATE_BUFFER_HPP
#define SUBSTRATE_BUFFER_HPP

#include <string.h>

template <typename Type_>
_disused static _finline void MSWrite(uint8_t *&buffer, Type_ value) {
    *reinterpret_cast<Type_ *>(buffer) = value;
    buffer += sizeof(Type_);
}

_disused static _finline void MSWrite(uint8_t *&buffer, uint8_t *data, size_t size) {
    memcpy(buffer, data, size);
    buffer += size;
}

#endif//SUBSTRATE_BUFFER_HPP
