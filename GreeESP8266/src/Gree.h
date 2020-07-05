#pragma once

#include <IPAddress.h>
#include <WiFiUdp.h>

#include <cstdint>
#include <string>
#include <vector>

class Gree
{
public:
    static constexpr auto ScanTimeout = 5000;
    static constexpr auto BindTimeout = 5000;

    explicit Gree(const IPAddress& broadcastAddr);

    void task();

    void scan();

private:
    const IPAddress _broadcastAddr;
    WiFiUDP _socket;

    enum class State {
        Idle,
        InitiateScan,
        WaitForScanResults,
        BindDevices,
        WaitForBindResult
    } _state = State::Idle;

    int32_t _replyTimer = 0;

    struct Device
    {
        std::string cid;
        IPAddress ip;
        bool bound = false;
        std::string key;
        std::string name;
    };

    std::vector<Device> _devices;

    void processScanResponse(const IPAddress& remoteIp, const std::vector<char>& packet);

    static std::string encrypt(const std::string& key, const std::string& data);
    static std::string decrypt(const std::string& key, const std::string& data);
    static std::string pad(const std::string& key, unsigned blockSize);
    static std::string createRequest(const std::string& tcid, const std::string& pack, int i = 0);
};