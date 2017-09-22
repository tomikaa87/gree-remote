#ifndef PROTOCOLUTILS_H
#define PROTOCOLUTILS_H

#include <QByteArray>

#include "devicedescriptor.h"

namespace ProtocolUtils
{
    QByteArray createBindingRequest(const DeviceDescriptor& device);
    QByteArray createDeviceRequest(const QByteArray& encryptedPack, int i = 0);
    QByteArray createDeviceStatusRequestPack(const QString& id);
}

#endif // PROTOCOLUTILS_H
