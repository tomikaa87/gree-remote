#ifndef PROTOCOLUTILS_H
#define PROTOCOLUTILS_H

#include <QByteArray>

#include "devicedescriptor.h"

namespace ProtocolUtils
{
    QByteArray createBindingRequest(const DeviceDescriptor& device);
    QByteArray createDeviceRequest(const QString& id, const QByteArray& encryptedPack, int i = 0);
}

#endif // PROTOCOLUTILS_H
