#include "mainwindow.h"
#include "ui_mainwindow.h"

#include <QDebug>
#include <QMenu>
#include <QSystemTrayIcon>

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
    , m_trayIcon(new QSystemTrayIcon(this))
{
    ui->setupUi(this);

    QIcon icon{ ":/icon" };
    m_trayIcon->setIcon(icon);
    m_trayIcon->show();

    m_scanAction = new QAction("Scan devices");
    connect(m_scanAction, &QAction::triggered, [this] {
        emit scanInitiated();
        m_scanAction->setEnabled(false);
    });

    auto trayMenu = new QMenu{ this };
    trayMenu->addAction(m_scanAction);
    trayMenu->addAction("Quit", [] {
        qApp->exit();
    });

    m_trayIcon->setContextMenu(trayMenu);
}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::onScanFinished()
{
    m_scanAction->setEnabled(true);
}
