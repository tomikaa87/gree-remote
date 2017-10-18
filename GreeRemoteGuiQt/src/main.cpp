#include "mainwindow.h"

#include <devicefinder.h>

#include <QApplication>
#include <QLoggingCategory>
#include <QObject>

Q_DECLARE_LOGGING_CATEGORY(MainLog)
Q_LOGGING_CATEGORY(MainLog, "Main")

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);

    a.setAttribute(Qt::AA_EnableHighDpiScaling);

    qCInfo(MainLog) << "initializing";

    DeviceFinder dh;

    MainWindow w(dh);
    w.show();

    QObject::connect(&dh, &DeviceFinder::scanFinshed, [] {
        qCInfo(MainLog) << "scanning finished";
    });
    QObject::connect(&w, &MainWindow::scanInitiated, &dh, &DeviceFinder::scan);
    QObject::connect(&dh, &DeviceFinder::scanFinshed, &w, &MainWindow::onScanFinished);
    QObject::connect(&dh, &DeviceFinder::deviceBound, [&w, &dh](const DeviceDescriptor& descriptor) {
        w.addDevice(dh.getDevice(descriptor));
    });

    dh.scan();

    auto exitCode = a.exec();

    qCInfo(MainLog) << "finished with exit code" << exitCode;

    return exitCode;
}
