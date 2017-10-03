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

    void openSocket();

    void onPollTimerTimeout();
    void onSocketReadyRead();
    void deviceRequest(const QByteArray &request);

    void processStatusUpdateResponse(const QByteArray& response);
};

#endif // DEVICE_H
