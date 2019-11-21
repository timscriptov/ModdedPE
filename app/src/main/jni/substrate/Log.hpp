#ifndef SUBSTRATE_LOG_HPP
#define SUBSTRATE_LOG_HPP

#if 0

#include <CoreFoundation/CFLogUtilities.h>

#define MSLog(level, format, ...) CFLog(level, CFSTR(format), ## __VA_ARGS__)

#define MSLogLevelNotice kCFLogLevelNotice
#define MSLogLevelWarning kCFLogLevelWarning
#define MSLogLevelError kCFLogLevelError

#else

#include <syslog.h>

#if __COREFOUNDATION__

#define MSLog(level, format, ...) do { \
    CFStringRef _formatted(CFStringCreateWithFormat(kCFAllocatorDefault, NULL, CFSTR(format), ## __VA_ARGS__)); \
    size_t _size(CFStringGetMaximumSizeForEncoding(CFStringGetLength(_formatted), kCFStringEncodingUTF8)); \
    char _utf8[_size + sizeof('\0')]; \
    CFStringGetCString(_formatted, _utf8, sizeof(_utf8), kCFStringEncodingUTF8); \
    CFRelease(_formatted); \
    syslog(level, "%s", _utf8); \
} while (false)

#else

#define MSLog(level, format, ...) do { \
    syslog(level, format, ## __VA_ARGS__); \
} while (false)

#endif

#define MSLogLevelNotice LOG_NOTICE
#define MSLogLevelWarning LOG_WARNING
#define MSLogLevelError LOG_ERR

#endif

#endif//SUBSTRATE_LOG_HPP
