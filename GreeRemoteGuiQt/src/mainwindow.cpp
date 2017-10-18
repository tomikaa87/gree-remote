#include "mainwindow.h"
#include "ui_mainwindow.h"
#include "device.h"
#include "deviceitem.h"

#include <QDebug>
#include <QDesktopServices>
#include <QGridLayout>
#include <QHBoxLayout>
#include <QLabel>
#include <QMenu>
#include <QSystemTrayIcon>
#include <QUrl>
#include <QVBoxLayout>

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
    , m_trayIcon(new QSystemTrayIcon())
{
    ui->setupUi(this);

    connect(ui->flaticonCreditsLabel, &QLabel::linkActivated, this, &MainWindow::onLabelLinkClicked);

    QIcon icon{ ":/icon-v2" };

    // Mask makes possible to render the icon properly on Dark menu bars
    icon.setIsMask(true);

    m_trayIcon->setIcon(icon);
    m_trayIcon->show();

    m_scanAction = new QAction("Scan devices");
    connect(m_scanAction, &QAction::triggered, [this] {
        emit scanInitiated();
        m_scanAction->setEnabled(false);
    });

    auto trayMenu = new QMenu{ this };
    trayMenu->addAction(m_scanAction);

    trayMenu->addAction("Preferences...", [this] {
        show();
    });

    trayMenu->addSeparator();
    m_separatorAction = trayMenu->addSeparator();
    trayMenu->addAction("Quit", [] {
        qApp->exit();
    });

    m_trayIcon->setContextMenu(trayMenu);
}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::addDevice(const QPointer<Device>& device)
{
    qInfo() << "device added:" << device->descritptor().id;

    createDeviceMenuItem(device);
}

void MainWindow::onScanFinished()
{
    m_scanAction->setEnabled(true);
}

void MainWindow::onBindingFinished()
{

}

void MainWindow::createDeviceMenuItem(const QPointer<Device> &device)
{
    if (!device)
        return;

    auto&& contextMenu = m_trayIcon->contextMenu();
//    contextMenu->addSeparator();

    auto&& deviceMenu = new QMenu(contextMenu);
    deviceMenu->setTitle(device->descritptor().name);
    contextMenu->insertMenu(m_separatorAction, deviceMenu);

    auto powerAction = new QAction(deviceMenu);
    powerAction->setText("Power: Off");
    powerAction->setCheckable(true);
    connect(powerAction, &QAction::toggled, [powerAction] {
        powerAction->setText(powerAction->isChecked() ? "Power: On" : "Power: Off");
    });

    deviceMenu->addAction(powerAction);

    auto modeAction = new QAction(deviceMenu);
    modeAction->setText("Mode: fan");
    auto modeMenu = new QMenu(deviceMenu);
    modeMenu->addAction("Fan", [modeAction] {
        modeAction->setText("Mode: fan");
    });
    modeMenu->addAction("Cool", [modeAction] {
        modeAction->setText("Mode: cool");
    });
    modeMenu->addAction("Heat", [modeAction] {
        modeAction->setText("Mode: heat");
    });
    modeMenu->addAction("Dry", [modeAction] {
        modeAction->setText("Mode: dry");
    });
    modeAction->setMenu(modeMenu);
    deviceMenu->addAction(modeAction);

//    auto w = new QWidget(contextMenu);
//    auto l = new QVBoxLayout;

//    l->setSpacing(0);
//    l->setContentsMargins(15, 4, 4, 4);

//    w->setSizePolicy(QSizePolicy::Maximum, QSizePolicy::Maximum);
//    w->setLayout(l);

//    auto buttonLayout = new QHBoxLayout;
//    buttonLayout->setContentsMargins(0, 0, 0, 0);
//    buttonLayout->addWidget(new QPushButton("Up", w));
//    buttonLayout->addWidget(new QPushButton("Down", w));
//    l->addLayout(buttonLayout);

//    auto action = new QWidgetAction(contextMenu);
//    action->setDefaultWidget(w);

    auto action = new DeviceItem(device, contextMenu);
    deviceMenu->addAction(action);
    //contextMenu->installEventFilter(action);
}

void MainWindow::onLabelLinkClicked(const QString& link)
{
    QDesktopServices::openUrl({ link });
}
