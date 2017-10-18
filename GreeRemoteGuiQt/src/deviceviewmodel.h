#ifndef DEVICEVIEWMODEL_H
#define DEVICEVIEWMODEL_H

#include <QObject>
#include <QPointer>

class Device;

class DeviceViewModel : public QObject
{
    Q_OBJECT

public:
    DeviceViewModel(const QPointer<Device>& device, QObject *parent = nullptr);

signals:

public slots:

private:
    QPointer<Device> m_device;
};

#endif // DEVICEVIEWMODEL_H
