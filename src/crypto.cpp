#include "crypto.h"

#include <QDebug>
#include <QLoggingCategory>

#include <vector>

#include <aes.h>
#include <filters.h>
#include <modes.h>
using namespace CryptoPP;

Q_DECLARE_LOGGING_CATEGORY(CryptoLog)
Q_LOGGING_CATEGORY(CryptoLog, "Crypto")

namespace Crypto
{

QByteArray decryptPack(const QByteArray &packBase64, const QString &key)
{
    qCDebug(CryptoLog) << "decrypting pack:" << packBase64;

    auto&& pack = QByteArray::fromBase64(packBase64);

    ECB_Mode<AES>::Decryption decryption{ reinterpret_cast<const unsigned char*>(key.toUtf8().constData()), AES::DEFAULT_KEYLENGTH };
    StreamTransformationFilter decryptor{ decryption, nullptr };
    decryptor.Put(reinterpret_cast<const unsigned char *>(pack.constData()), pack.length());
    decryptor.MessageEnd();

    auto decryptedBytes = decryptor.MaxRetrievable();
    qCDebug(CryptoLog) << "decrypted data bytes:" << decryptedBytes;

    std::vector<unsigned char> decrypted;
    decrypted.resize(decryptedBytes);
    decryptor.Get(decrypted.data(), decryptedBytes);

    auto&& json = QByteArray(reinterpret_cast<const char*>(decrypted.data()), decryptedBytes);
    qCDebug(CryptoLog) << "decrypted JSON:" << json;

    return json;
}

QByteArray encryptPack(const QByteArray &pack, const QString &key)
{
    qCDebug(CryptoLog) << "encrypting pack:" << pack;

    QByteArray paddedPack{ pack };
    addPKCS7Padding(paddedPack);

    ECB_Mode<AES>::Encryption encryption{ reinterpret_cast<const unsigned char*>(key.toUtf8().constData()), AES::DEFAULT_KEYLENGTH };
    StreamTransformationFilter encryptor{ encryption, nullptr };
    encryptor.Put(reinterpret_cast<const unsigned char*>(paddedPack.constData()), paddedPack.length());
    encryptor.MessageEnd();

    auto encryptedBytes = encryptor.MaxRetrievable();
    qCDebug(CryptoLog) << "encrypted data bytes:" << encryptedBytes;

    QByteArray encrypted(encryptedBytes, 0);
    encryptor.Get(reinterpret_cast<unsigned char*>(encrypted.data()), encryptedBytes);

    auto&& packBase64 = encrypted.toBase64();
    qCDebug(CryptoLog) << "encrypted data:" << packBase64;

    return packBase64;
}

void addPKCS7Padding(QByteArray& packBase64)
{
    auto length = 16 - (packBase64.length() % 16);
    packBase64.append(QByteArray(length, length));
}

}
