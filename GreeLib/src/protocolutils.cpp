#include "protocolutils.h"
#include "crypto.h"

#include <QJsonArray>
#include <QJsonDocument>
#include <QJsonObject>
#include <QJsonValue>
#include <QLoggingCategory>

Q_DECLARE_LOGGING_CATEGORY(ProtocolUtilsLog)
Q_LOGGING_CATEGORY(ProtocolUtilsLog, "ProtocolUtils")

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

QByteArray ProtocolUtils::createDeviceStatusRequestPack(const QString& id)
{
    QJsonObject json
    {
        { "cols", QJsonArray {
                "Pow",
                "Mod",
                "SetTem",
                "WdSpd",
                "Air",
                "Blo",
                "Health",
                "SwhSlp",
                "Lig",
                "SwingLfRig",
                "SwUpDn",
                "Quiet",
                "Tur",
                "StHt",
                "TemUn",
                "HeatCoolType",
                "TemRec",
                "SvSt"
            }
        },
        { "mac", id },
        { "t", "status" }
    };

    return QJsonDocument{ json }.toJson(QJsonDocument::Compact);
}

bool ProtocolUtils::readPackFromResponse(const QByteArray& response,
                                         const QString& decryptionKey,
                                         QJsonObject& pack)
{
    qCDebug(ProtocolUtilsLog) << "reading pack from response:" << response;

    QJsonParseError parseError;
    auto&& responseJsonDocument = QJsonDocument::fromJson(response, &parseError);
    if (parseError.error != QJsonParseError::NoError)
    {
        qCWarning(ProtocolUtilsLog) << "response is not a valid JSON object. Parse error:" << parseError.errorString();
        return false;
    }

    auto&& responseJson = responseJsonDocument.object();

    auto&& encryptedPack = responseJson["pack"].toString();
    if (encryptedPack.isEmpty())
    {
        qCWarning(ProtocolUtilsLog) << "response doesn't have a 'pack' field which is mandatory";
        return false;
    }

    auto&& decryptedPack = Crypto::decryptPack(encryptedPack.toUtf8(), decryptionKey);
    qCDebug(ProtocolUtilsLog) << "decrypted pack:" << decryptedPack;

    auto&& packJsonDocument = QJsonDocument::fromJson(decryptedPack, &parseError);
    if (parseError.error != QJsonParseError::NoError)
    {
        qCWarning(ProtocolUtilsLog) << "decrypted pack is not a valid JSON object. Parse error:" << parseError.errorString();
        return false;
    }

    pack = packJsonDocument.object();

    return true;
}

ProtocolUtils::DeviceStatusMap ProtocolUtils::readStatusMapFromPack(const QJsonObject& pack)
{
    if (pack["t"] != "dat")
    {
        qCWarning(ProtocolUtilsLog) << "failed to read status map from pack, pack type mismatch:" << pack["t"];
        return{};
    }

    auto&& cols = pack["cols"];
    if (!cols.isArray())
    {
        qCWarning(ProtocolUtilsLog) << "failed to read status map from pack, 'cols' is not an array";
        return{};
    }

    auto&& colsArray = cols.toArray();
    if (colsArray.isEmpty())
    {
        qCWarning(ProtocolUtilsLog) << "failed to read status map from pack, 'cols' is empty";
        return{};
    }

    auto&& dat = pack["dat"];
    if (!dat.isArray())
    {
        qCWarning(ProtocolUtilsLog) << "failed to read status map from pack, 'dat' is not an array";
        return{};
    }

    auto&& datArray = dat.toArray();
    if (datArray.isEmpty())
    {
        qCWarning(ProtocolUtilsLog) << "failed to read status map from pack, 'dat' is empty";
        return{};
    }

    if (colsArray.size() != datArray.size())
    {
        qCWarning(ProtocolUtilsLog) << "failed to read status map from pack, 'dat' size mismatch";
        return{};
    }

    DeviceStatusMap map;
    for (int i = 0; i < colsArray.size(); i++)
        map[colsArray[i].toString()] = datArray[i].toInt();

    return map;
}
