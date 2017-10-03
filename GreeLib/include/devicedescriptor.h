#ifndef DEVICEDESCRIPTOR_H
#define DEVICEDESCRIPTOR_H

#include <QHostAddress>
#include <QString>

struct DeviceDescriptor
{
    bool bound = false;

    QHostAddress address;
    uint16_t port = 0;

    QString key;
    QString name;
    QString id;
};

#endif // DEVICEDESCRIPTOR_H
