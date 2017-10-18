#ifndef DEVICE_H
#define DEVICE_H

#include <QObject>

#include "devicedescriptor.h"

class QUdpSocket;
class QTimer;

class Device : public QObject
{
    Q_OBJECT

public:
    Device(const DeviceDescriptor& descriptor, QObject *parent = nullptr);
    virtual ~Device();

    const DeviceDescriptor& descritptor() const { return m_device; }

    void updateStatus();

    bool isPoweredOn() const { return m_powered; }
    bool isHealthEnabled() const { return m_health; }
    bool isTurboEnabled() const { return m_turbo; }
    bool isQuietModeEnabled() const { return m_quiet; }
    bool isLightEnabled() const { return m_light; }

    // Auto: 0, Cool: 1, Dry: 2, Fan: 3, Heat: 4
    int mode() const { return m_mode; }

    // In Celsius
    int temperature() const { return m_temperature; }

    // Auto: 0, 1-5 (or 1-3-5 for 3-speed)
    int fanSpeed() const { return m_fanSpeed; }

    // Default: 0, Full swipe: 1,
    // Static, from the upmost position: 1/5: 2, 2/5: 3, 3/5: 4, 4/5: 5, 5/5: 6
    // Scan, from the upmost region: 1/3: 11, 2/3: 9, 3/3: 7 (probably there are more steps, maybe 5, for 8 and 10)
    int verticalSwing() const { return m_verticalSwing; }

    // Default: 0
    int horizontalSwing() const { return m_horizontalSwing; }

signals:
    void statusUpdated();

public slots:

private:
    enum class State
    {
        Unbound,
        Idle,
        StatusUpdate,
        DeviceUpdate
    };

    State m_state = State::Unbound;
    DeviceDescriptor m_device;
    QUdpSocket* m_socket;
    QTimer* m_pollTimer;

    bool m_powered = false;
    bool m_health = false;
    bool m_turbo = false;
    bool m_light = false;
    bool m_quiet = false;
    int m_mode = 0;
    int m_temperature = 0;
    int m_fanSpeed = 0;
    int m_verticalSwing = 0;
    int m_horizontalSwing = 0;

    void openSocket();

    void onPollTimerTimeout();
    void onSocketReadyRead();
    void deviceRequest(const QByteArray &request);

    void processStatusUpdateResponse(const QByteArray& response);
};

#endif // DEVICE_H
