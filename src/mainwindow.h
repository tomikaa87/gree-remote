#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>

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

    void onScanFinished();

signals:
    void scanInitiated();

private:
    Ui::MainWindow *ui;
    QSystemTrayIcon* m_trayIcon;
    QAction* m_scanAction;
};

#endif // MAINWINDOW_H
