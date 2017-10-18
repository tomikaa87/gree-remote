TEMPLATE = subdirs

SUBDIRS = \
    GreeLib \
    GreeRemoteGuiQt

lib.subdir = GreeLib

app.subdir = GreeRemoteGuiQt
app.depends = lib

CONFIG += ordered
