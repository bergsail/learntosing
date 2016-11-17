TARGET = qmir
TEMPLATE = lib
CONFIG += staticlib

HEADERS += \
    spectrum.h \
    wfft.h \
    feature.h \
    config.h \
    processor.h \
    axes.h

SOURCES += \
    spectrum.cpp \
    wfft.cpp \
    feature.cpp \
    processor.cpp \
    axes.cpp
