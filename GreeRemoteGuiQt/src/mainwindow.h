#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QPointer>

class Device;
class QAction;
class QSystemTrayIcon;

namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
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

    void createDeviceMenuItem(const QPointer<Device>& device);
    void onLabelLinkClicked(const QString& link);
};

#endif // MAINWINDOW_H
