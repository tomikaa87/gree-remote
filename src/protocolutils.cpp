#include "protocolutils.h"

#include <QJsonDocument>
#include <QJsonObject>

QByteArray ProtocolUtils::createBindingRequest(const DeviceDescriptor &device)
{
    QJsonObject json
    {
        { "mac", device.id },
        { "t", "bind" },
        { "uid", 0 }
    };

    return QJsonDocument{ json }.toJson(QJsonDocument::Compact);
}

QByteArray ProtocolUtils::createDeviceRequest(const QByteArray &encryptedPack, int i)
{
    QJsonObject json
    {
        { "cid", "app" },
        { "i", i },
        { "t", "pack" },
        { "uid", 0 },
        { "pack", QString::fromUtf8(encryptedPack) }
    };

    return QJsonDocument{ json }.toJson(QJsonDocument::Compact);
}
