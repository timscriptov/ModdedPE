#include "CydiaSubstrate.h"
#include "Debug.hpp"

#include <stdint.h>
#include <stdlib.h>
#include <stdio.h>

_extern bool MSDebug;
bool MSDebug = false;

static char _MSHexChar(uint8_t value) {
    return value < 0x20 || value >= 0x80 ? '.' : value;
}

#define HexWidth_ 16
#define HexDepth_ 4

void MSLogHexEx(const void *vdata, size_t size, size_t stride, const char *mark) {
    const uint8_t *data((const uint8_t *) vdata);

    size_t i(0), j;

    char d[256];
    size_t b(0);
    d[0] = '\0';

    while (i != size) {
        if (i % HexWidth_ == 0) {
            if (mark != NULL)
                b += sprintf(d + b, "[%s] ", mark);
            b += sprintf(d + b, "0x%.3zx:", i);
        }

        b += sprintf(d + b, " ");

        for (size_t q(0); q != stride; ++q)
            b += sprintf(d + b, "%.2x", data[i + stride - q - 1]);

        i += stride;

        for (size_t q(1); q != stride; ++q)
            b += sprintf(d + b, " ");

        if (i % HexDepth_ == 0)
            b += sprintf(d + b, " ");

        if (i % HexWidth_ == 0) {
            b += sprintf(d + b, " ");
            for (j = i - HexWidth_; j != i; ++j)
                b += sprintf(d + b, "%c", _MSHexChar(data[j]));

            lprintf("%s", d);
            b = 0;
            d[0] = '\0';
        }
    }

    if (i % HexWidth_ != 0) {
        for (j = i % HexWidth_; j != HexWidth_; ++j)
            b += sprintf(d + b, "   ");
        for (j = 0; j != (HexWidth_ - i % HexWidth_ + HexDepth_ - 1) / HexDepth_; ++j)
            b += sprintf(d + b, " ");
        b += sprintf(d + b, " ");
        for (j = i / HexWidth_ * HexWidth_; j != i; ++j)
            b += sprintf(d + b, "%c", _MSHexChar(data[j]));

        lprintf("%s", d);
        b = 0;
        d[0] = '\0';
    }
}

void MSLogHex(const void *vdata, size_t size, const char *mark) {
    return MSLogHexEx(vdata, size, 1, mark);
}
