#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QPointer>

class Device;
class DeviceFinder;
class QAction;
class QSystemTrayIcon;

namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(DeviceFinder& deviceFinder, QWidget *parent = 0);
    ~MainWindow();

    void addDevice(const QPointer<Device>& device);

    void onScanFinished();
    void onBindingFinished();

signals:
    void scanInitiated();

private:
    Ui::MainWindow *ui;
    QSystemTrayIcon* m_trayIcon;
    QAction* m_scanAction;
    QAction* m_separatorAction;
    DeviceFinder& m_deviceFinder;

    void createDeviceMenuItem(const QPointer<Device>& device);
    void onLabelLinkClicked(const QString& link);

    // For testing
    QPointer<Device> m_selectedDevice;

    void createDeviceTableEntry(const QPointer<Device>& device);
    void selectTestDevice(const QString& id);
    void updateTestDeviceStatus();

    void onScanButtonClicked();
    void onComboBoxIndexChanged(int index);
    void onPowerCheckBoxClicked();
    void onHealthModeCheckBoxClicked();
    void onTurboModeCheckBoxClicked();
    void onQuietModeCheckBoxClicked();
    void onLightCheckBoxClicked();
};

#endif // MAINWINDOW_H
