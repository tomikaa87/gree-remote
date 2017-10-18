#include "deviceitem.h"
#ifdef Q_OS_MAC
#include "mac/macnativelabel.h"
#endif

#include <QEvent>
#include <QDebug>
#include <QGridLayout>
#include <QLabel>
#include <QPushButton>
#include <QSlider>

DeviceItem::DeviceItem(const QPointer<Device> &device, QObject *parent)
    : QWidgetAction(parent)
    , m_device(device)
{
//    installEventFilter(this);

//    startTimer(100);
}

QWidget *DeviceItem::createWidget(QWidget *parent)
{
    if (m_widget)
        return m_widget;

    parent->installEventFilter(this);

//    auto&& contextMenu = m_trayIcon->contextMenu();
//    contextMenu->addSeparator();

//    auto&& deviceMenu = new QMenu(contextMenu);
//    deviceMenu->setTitle(device->descritptor().name);
//    contextMenu->addMenu(deviceMenu);
//    deviceMenu->addAction("Power on", []{});
//    deviceMenu->addAction("Power off", []{});

    m_widget = new QWidget(parent);
    auto l = new QVBoxLayout;

    l->setSpacing(0);
    l->setContentsMargins(20, 0, 20, 0);

    m_widget->setSizePolicy(QSizePolicy::Maximum, QSizePolicy::Maximum);
    m_widget->setLayout(l);

    auto buttonLayout = new QHBoxLayout;
    buttonLayout->setContentsMargins(0, 0, 0, 0);
    buttonLayout->setSpacing(8);
    auto slider = new QSlider(Qt::Horizontal, m_widget);
    slider->setMinimum(16);
    slider->setMaximum(30);
    slider->setMinimumWidth(100);

#ifdef Q_OS_MAC
    // We must use a native label (NSTextField) since QLabel's text color doesn't change
    // when Dark theme is selected
    auto&& label = new MacNativeLabel(m_widget);
#else
    auto&& label = new QLabel(m_widget);
#endif

    label->setMinimumWidth(30);
    connect(slider, &QSlider::valueChanged, [label](int value) {
        label->setText(QString{ "%1 C" }.arg(value));
    });

    buttonLayout->addWidget(slider);
    buttonLayout->addWidget(label);

    l->addLayout(buttonLayout);

    return m_widget;

//    auto action = new QWidgetAction(contextMenu);
//    action->setDefaultWidget(w);
    //    m_trayIcon->contextMenu()->addAction(action);
}

bool DeviceItem::event(QEvent* e)
{
    qInfo() << "DeviceItem::event:" << e->type();

//    if (e->type() == QEvent::Timer)
//        m_widget->update();

    return QWidgetAction::event(e);
}

bool DeviceItem::eventFilter(QObject* o, QEvent* e)
{
    qInfo() << "DeviceItem::eventFilter::event:" << e->type();

    return QWidgetAction::eventFilter(o, e);
}
