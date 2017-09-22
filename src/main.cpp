#include "devicefinder.h"
#include "mainwindow.h"

#include <QApplication>
#include <QLoggingCategory>
#include <QObject>

Q_DECLARE_LOGGING_CATEGORY(MainLog)
Q_LOGGING_CATEGORY(MainLog, "Main")

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);

    qCInfo(MainLog) << "initializing";

    MainWindow w;

    DeviceFinder dh;

    QObject::connect(&dh, &DeviceFinder::scanFinshed, [] {
        qCInfo(MainLog) << "scanning finished";
    });
    QObject::connect(&w, &MainWindow::scanInitiated, &dh, &DeviceFinder::scan);
    QObject::connect(&dh, &DeviceFinder::scanFinshed, &w, &MainWindow::onScanFinished);

    auto exitCode = a.exec();

    qCInfo(MainLog) << "finished with exit code" << exitCode;

    return exitCode;
}
