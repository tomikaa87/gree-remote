#-------------------------------------------------
#
# Project created by QtCreator 2017-09-18T14:29:43
#
#-------------------------------------------------

QT       += core gui network

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = GreeRemote
TEMPLATE = app

CONFIG += c++14

# The following define makes your compiler emit warnings if you use
# any feature of Qt which has been marked as deprecated (the exact warnings
# depend on your compiler). Please consult the documentation of the
# deprecated API in order to know how to port your code away from it.
DEFINES += QT_DEPRECATED_WARNINGS

# You can also make your code fail to compile if you use deprecated APIs.
# In order to do so, uncomment the following line.
# You can also select to disable deprecated APIs only up to a certain version of Qt.
DEFINES += QT_DISABLE_DEPRECATED_BEFORE=0x060000    # disables all the APIs deprecated before Qt 6.0.0

SOURCES += \
    src/main.cpp \
    src/mainwindow.cpp \
    src/deviceviewmodel.cpp \
    src/deviceitem.cpp

HEADERS += \
    src/mainwindow.h \
    src/deviceviewmodel.h \
    src/deviceitem.h

FORMS += \
    src/mainwindow.ui

RESOURCES += \
    resources/res.qrc

INCLUDEPATH += \
    $$PWD/GreeLib/include

LIBS += \
    -L$$PWD/lib \
    -L$$PWD/3rdparty/cryptopp \
    -lcryptopp

CONFIG(debug, debug|release) {
    message(Debug build)

    DESTDIR = $$PWD/bin
    TARGET = $$join(TARGET,,,d)

    LIBS += \
        -lGreeLibd
} else {
    message(Release build)

    DESTDIR = $$PWD/bin

    LIBS += \
        -lGreeLib
}

macx {
    message(Building for macOS)

    QT += macextras
    QMAKE_INFO_PLIST = Info.plist
    LIBS += -framework AppKit

    SOURCES += \
        src/mac/macnativelabel.mm

    HEADERS += \
        src/mac/macnativelabel.h
}
