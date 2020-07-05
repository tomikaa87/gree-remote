//
// PrivateConfig.h must contain the following:
// namespace Config
// {
//     static constexpr auto WiFiSSID = "<your AP's SSID>";
//     static constexpr auto WiFiPassword = "<your very secret password>";
// }
//
#include "PrivateConfig.h"

#include "Gree.h"

#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <ESP8266WiFiSTA.h>

#include <memory>

std::unique_ptr<Gree> _gree;

void setup()
{
    static uint8_t FakeMac[] = { 0x40, 0xA3, 0xCC, 0xA1, 0xB2, 0xC3 };
    wifi_set_macaddr(0, FakeMac);

    Serial.begin(74880);
    WiFi.begin(Config::WiFiSSID, Config::WiFiPassword);

    static const IPAddress BroadcastAddr{ 192, 168, 30, 255 };
    _gree.reset(new Gree{ BroadcastAddr });
}

void loop()
{
    static bool scanned = false;
    if (WiFi.isConnected() && !scanned) {
        scanned = true;
        if (_gree)
            _gree->scan();
    } 

    if (_gree)
        _gree->task();
}