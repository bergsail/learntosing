#TEMPLATE = subdirs
QT+=core
QT+=gui
QT+=widgets

TARGET = test
CONFIG += console

TEMPLATE = app

SOURCES += main.cpp \
    filereader.cpp

win32:CONFIG(release, debug|release): LIBS += -L$$OUT_PWD/../mir/release/ -lqmir
else:win32:CONFIG(debug, debug|release): LIBS += -L$$OUT_PWD/../mir/debug/ -lqmir
else:unix: LIBS += -L$$OUT_PWD/../mir/ -lqmir

INCLUDEPATH += $$PWD/../mir
DEPENDPATH += $$PWD/../mir

win32:CONFIG(release, debug|release): PRE_TARGETDEPS += $$OUT_PWD/../mir/release/qmir.lib
else:win32:CONFIG(debug, debug|release): PRE_TARGETDEPS += $$OUT_PWD/../mir/debug/qmir.lib
else:unix: PRE_TARGETDEPS += $$OUT_PWD/../mir/libqmir.a

HEADERS += \
    filereader.h
