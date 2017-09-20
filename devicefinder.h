#ifndef DEVICEHANDLER_H
#define DEVICEHANDLER_H

#include <devicedescriptor.h>

#include <QObject>
#include <QByteArray>

class QTimer;
class QUdpSocket;

class DeviceFinder : public QObject
{
    Q_OBJECT

public:
    explicit DeviceFinder(QObject *parent = nullptr);

    void scan();

signals:
    void scanFinshed();
    void bindingFinished();

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

    std::vector<DeviceDescriptor> m_devices;

    void socketReadyRead();
    void timerTimeout();

    void processScanResponse(const QByteArray response, const QHostAddress &remoteAddress, uint16_t remotePort);
    bool readPackFromResponse(const QJsonObject& response, QJsonObject& pack);

    void bindDevices();
    void processBindResponse(const QByteArray& response);
};

#endif // DEVICEHANDLER_H
