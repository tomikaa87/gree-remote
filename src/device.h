#ifndef DEVICE_H
#define DEVICE_H

#include <QObject>

#include "devicedescriptor.h"

class QUdpSocket;

class Device : public QObject
{
    Q_OBJECT

public:
    explicit Device(const DeviceDescriptor& descriptor, QObject *parent = nullptr);

signals:

public slots:

private:
    DeviceDescriptor m_descriptor;
    QUdpSocket* m_socket;
};

#endif // DEVICE_H
