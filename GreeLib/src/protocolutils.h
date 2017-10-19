#ifndef PROTOCOLUTILS_H
#define PROTOCOLUTILS_H

#include <QByteArray>
#include <QMap>

#include "devicedescriptor.h"

namespace ProtocolUtils
{
    using DeviceParameterMap = QMap<QString, int>;

    QByteArray createBindingRequest(const DeviceDescriptor& device);
    QByteArray createDeviceRequest(const QByteArray& encryptedPack, int i = 0);
    QByteArray createDeviceStatusRequestPack(const QString& id);
    QByteArray createDeviceCommandPack(const DeviceParameterMap& parameters);

    bool readPackFromResponse(const QByteArray& response,
                              const QString& decryptionKey,
                              QJsonObject& pack);

    DeviceParameterMap readStatusMapFromPack(const QJsonObject& pack);
}

#endif // PROTOCOLUTILS_H
