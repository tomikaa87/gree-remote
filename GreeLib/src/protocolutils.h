#ifndef PROTOCOLUTILS_H
#define PROTOCOLUTILS_H

#include <QByteArray>
#include <QMap>

#include "devicedescriptor.h"

namespace ProtocolUtils
{
    QByteArray createBindingRequest(const DeviceDescriptor& device);
    QByteArray createDeviceRequest(const QByteArray& encryptedPack, int i = 0);
    QByteArray createDeviceStatusRequestPack(const QString& id);

    bool readPackFromResponse(const QByteArray& response,
                              const QString& decryptionKey,
                              QJsonObject& pack);

    using DeviceStatusMap = QMap<QString, int>;

    DeviceStatusMap readStatusMapFromPack(const QJsonObject& pack);
}

#endif // PROTOCOLUTILS_H
