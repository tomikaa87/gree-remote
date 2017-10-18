#ifndef DEVICEHANDLER_H
#define DEVICEHANDLER_H

#include "device.h"
#include "devicedescriptor.h"

#include <QPointer>
#include <QObject>
#include <QByteArray>

#include <map>
#include <vector>

class QTimer;
class QUdpSocket;

class DeviceFinder : public QObject
{
    Q_OBJECT

public:
    using DeviceVector = std::vector<DeviceDescriptor>;

    explicit DeviceFinder(QObject *parent = nullptr);

    void scan();
    const DeviceVector& availableDevices() const;

    QPointer<Device> getDevice(const DeviceDescriptor& descriptor);
    QPointer<Device> getDeviceById(const QString& id);

signals:
    void scanFinshed();
    void bindingFinished();

    void deviceBound(const DeviceDescriptor& descriptor);

private:
    QUdpSocket* m_socket;
    QTimer* m_timer;

    enum class State
    {
        Idle,
        Scanning,
        Binding
    };
    State m_state = State::Idle;

    std::vector<DeviceDescriptor> m_descriptors;
    std::map<QString, Device*> m_devices;

    void socketReadyRead();
    void timerTimeout();

    void processScanResponse(const QByteArray response, const QHostAddress &remoteAddress, uint16_t remotePort);

    void bindDevices();
    void processBindResponse(const QByteArray& response);
};

#endif // DEVICEHANDLER_H
