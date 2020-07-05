#include "Gree.h"

#include <AES.h>
#include <Arduino.h>
#include <base64.hpp>

// Avoid identifier collision with JSON library
#ifdef B1
#undef B1
#endif
#include <json.hpp>

#include <algorithm>

static constexpr auto GenericKey = "a3K8Bx%2r8Y7#xDh";

Gree::Gree(const IPAddress& broadcastAddr)
    : _broadcastAddr{ broadcastAddr }
{
    Serial.print("Gree: initializing with broadcast address: ");
    Serial.println(broadcastAddr);

    // Test encryption and decryption
    const std::string plainText{ "almafa kocsogtucsok kortefa 1234" };
    const auto encrypted = encrypt(GenericKey, plainText);
    const auto decrypted = decrypt(GenericKey, encrypted);
    if (plainText != decrypted) {
        Serial.println("Gree: encryption/decryption test failed");
    }

    _socket.begin(12300);
}

void Gree::task()
{
    switch (_state) {
        case State::Idle:
            break;

        case State::InitiateScan:
            Serial.println("Gree: initiating scan");

            _devices.clear();

            // Go back to Idle in case of an early break
            _state = State::Idle;

            if (!_socket.beginPacket(_broadcastAddr, 7000)) {
                Serial.println("Gree: failed to begin scan packet");
                break;
            }

            if (!_socket.write(R"({"t":"scan"})")) {
                Serial.println("Gree: failed to write scan packet payload");
                break;
            }

            if (!_socket.endPacket()) {
                Serial.println("Gree: failed to send scan packet");
                break;
            }

            _replyTimer = millis();
            _state = State::WaitForScanResults;
            break;

        case State::WaitForScanResults: {
            if (millis() - _replyTimer >= ScanTimeout) {
                Serial.println("Gree: scan finished");
                _state = State::BindDevices;
            }

            const auto packetSize = _socket.parsePacket();
            if (packetSize == 0) {
                break;
            }

            // Reset the timer
            _replyTimer = millis();

            std::vector<char> packet;
            packet.resize(packetSize);
            _socket.read(packet.data(), packet.size());
            Serial.print("Gree: incoming scan response: ");
            Serial.println(packet.data());

            processScanResponse(_socket.remoteIP(), packet);
            break;
        }

        case State::BindDevices: {
            const auto device = std::find_if(
                _devices.cbegin(),
                _devices.cend(),
                [](const Device& d) { return !d.bound; }
            );

            _state = State::Idle;

            if (device == _devices.cend()) {
                Serial.println("Gree: no devices to bind");
                break;
            }

            Serial.print("Binding device: ");
            Serial.println(device->ip);

            if (!_socket.beginPacket(device->ip, 7000)) {
                Serial.println("Gree: failed to begin bind packet");
                break;
            }

            nlohmann::json pack;
            pack["mac"] = device->cid;
            pack["t"] = "bind";
            pack["uid"] = 0;

            const auto payload = createRequest(device->cid, encrypt(GenericKey, pack.dump()));

            if (!_socket.write(payload.c_str(), payload.size())) {
                Serial.println("Gree: failed to write bind pack");
                break;
            }

            if (!_socket.endPacket()) {
                Serial.println("Gree: failed to send bind packet");
                break;
            }

            Serial.println("Gree: waiting for device to bind");

            _state = State::WaitForBindResult;
            _replyTimer = millis();

            break;
        }

        case State::WaitForBindResult: {
            if (millis() - _replyTimer >= BindTimeout) {
                Serial.println("Gree: bind timed out");
                _state = State::BindDevices;
            }

            const auto packetSize = _socket.parsePacket();
            if (packetSize == 0) {
                break;
            }

            std::vector<char> packet;
            packet.resize(packetSize);
            _socket.read(packet.data(), packet.size());
            Serial.print("Gree: incoming bind response: ");
            Serial.println(packet.data());

            // TODO process bind response

            _state = State::BindDevices;
            break;
        }
    }
}

void Gree::scan()
{
    if (_state != State::Idle) {
        Serial.println("Gree: can't initiate scan, operation is already in progress");
        return;
    }

    _state = State::InitiateScan;
}

void Gree::processScanResponse(const IPAddress& remoteIp, const std::vector<char>& packet)
{
    const auto json = nlohmann::json::parse(packet, nullptr, false);

    if (json.is_null() || !json.is_object() || !json.contains("cid") || !json.contains("pack")) {
        Serial.println("Gree::processScanResponse: invalid scan response");
        return;
    }

    Device device;
    device.cid = json["cid"];

    const auto decryptedPack = decrypt(GenericKey, json["pack"]);
    const auto pack = nlohmann::json::parse(decryptedPack, nullptr, false);

    if (pack.is_null() || !pack.is_object()) {
        Serial.print("Gree::processScanResponse: invalid pack found: ");
        Serial.println(decryptedPack.c_str());
        return;
    }

    device.ip = remoteIp;

    if (pack.contains("name")) {
        device.name = pack["name"];
    }

    _devices.push_back(std::move(device));
}

std::string Gree::encrypt(const std::string& key, const std::string& data)
{
    Serial.printf("Gree::encrypt(): encrypting data (%u): %s\n", data.size(), data.c_str());

    // Using AESTiny because it has a smaller memory footprint for encrypting
    AESTiny128 aes;
    aes.setKey(
        reinterpret_cast<const uint8_t*>(key.c_str()),
        key.size()
    );

    const auto padded = pad(data, aes.blockSize());
    const auto* paddedBuf = reinterpret_cast<const uint8_t*>(padded.c_str());

    Serial.printf("Gree::encrypt(): padded bytes (%u):", padded.size());
    for (const auto c : padded) {
        Serial.printf(" %02x", c);
    }
    Serial.println();

    Serial.print("Gree::encrypt(): encrypting block");
    std::vector<uint8_t> encrypted;
    encrypted.resize(padded.size());
    for (unsigned i = 0; i < padded.size(); i += aes.blockSize()) {
        Serial.printf(" %u", i);
        aes.encryptBlock(encrypted.data() + i, paddedBuf + i);
    }
    Serial.println();

    std::vector<uint8_t> encoded;
    encoded.resize(encode_base64_length(encrypted.size()) + 1);
    Serial.printf("Gree::encrypt(): encoded data size: %u\n", encoded.size());
    encode_base64(encrypted.data(), encrypted.size(), encoded.data());

    std::string encodedStr{ reinterpret_cast<char*>(encoded.data()), encoded.size() };
    Serial.printf("Gree::encrypt(): encrypted data: %s\n", encodedStr.c_str());

    return encodedStr;
}

std::string Gree::decrypt(const std::string& key, const std::string& data)
{
    Serial.printf("Gree::decrypt(): decrypting data: %s\n", data.c_str());

    // Using the full AES128 implementation because we need support for decryption
    AES128 aes;
    aes.setKey(
        reinterpret_cast<const uint8_t*>(key.c_str()),
        key.size()
    );

    // FIXME const_cast is needed because decode_base64* requires non-const array as an input
    auto* pData = reinterpret_cast<uint8_t*>(const_cast<char*>(data.c_str()));

    std::vector<uint8_t> decoded;
    decoded.resize(decode_base64_length(pData));
    decode_base64(pData, decoded.data());

    Serial.printf("Gree::decrypt(): decoded ciphertext bytes (%u):", decoded.size());
    for (unsigned i = 0; i < decoded.size(); ++i) {
        Serial.printf(" %02x", decoded[i]);
    }
    Serial.println();

    Serial.print("Gree::decrypt(): decrypting block");
    std::vector<uint8_t> decrypted;
    decrypted.resize(decoded.size());
    for (unsigned i = 0; i < decoded.size(); i += aes.blockSize()) {
        Serial.printf(" %u", i);
        aes.decryptBlock(decrypted.data() + i, decoded.data() + i);
    }
    Serial.println();

    auto payloadLength = decrypted.size();
    if (*decrypted.rbegin() < aes.blockSize()) {
        payloadLength -= *decrypted.rbegin();
    }

    Serial.printf("Gree::decrypt(): payload length without padding: %u\n", payloadLength);

    const std::string payload{ reinterpret_cast<char*>(decrypted.data()), payloadLength };
    Serial.printf("Gree::decrypt(): payload: %s\n", payload.c_str());

    return payload;
}

std::string Gree::pad(const std::string& s, const unsigned blockSize)
{
    if (s.size() % blockSize == 0) {
        return s;
    }

    std::string padded{ s };
    const auto padLength = blockSize - s.size() % blockSize;

    for (unsigned i = 0; i < padLength; ++i) {
        padded.push_back(padLength);
    }

    return padded;
}

std::string Gree::createRequest(const std::string& tcid, const std::string& pack, const int i)
{
    nlohmann::json req;
    req["cid"] = "app";
    req["i"] = i;
    req["t"] = "pack";
    req["uid"] = 0;
    req["tcid"] = tcid;
    req["pack"] = pack.c_str();

    const auto s = req.dump();

    Serial.printf("Gree::createRequest: %s\n", s.c_str());

    return s;
}