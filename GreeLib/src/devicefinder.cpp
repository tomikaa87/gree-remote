#include "devicefinder.h"
#include "crypto.h"
#include "protocolutils.h"

#include <QDebug>
#include <QJsonDocument>
#include <QJsonObject>
#include <QLoggingCategory>
#include <QTimer>
#include <QUdpSocket>

Q_DECLARE_LOGGING_CATEGORY(DeviceFinderLog)
Q_LOGGING_CATEGORY(DeviceFinderLog, "DeviceFinder")

DeviceFinder::DeviceFinder(QObject *parent)
    : QObject(parent)
    , m_socket(new QUdpSocket(this))
    , m_timer(new QTimer(this))
{
    qCInfo(DeviceFinderLog) << "initializing";

    connect(m_socket, &QUdpSocket::readyRead, this, &DeviceFinder::socketReadyRead);

    m_timer->setSingleShot(true);
    connect(m_timer, &QTimer::timeout, this, &DeviceFinder::timerTimeout);
}

void DeviceFinder::scan()
{
    if (m_state != State::Idle)
    {
        qCWarning(DeviceFinderLog) << "scanning can only be initiated in Idle state";
        return;
    }

    m_state = State::Scanning;

    qCInfo(DeviceFinderLog) << "scanning started";

    if (!m_socket->isOpen())
    {
        qCDebug(DeviceFinderLog) << "opening UDP socket";
        m_socket->open(QIODevice::ReadWrite);
    }

    auto written = m_socket->writeDatagram(R"({"t":"scan"})", QHostAddress{ "192.168.1.255" }, 7000);
    qCDebug(DeviceFinderLog) << "written datagram length:" << written;

    m_timer->start(2000);
}

const DeviceFinder::DeviceVector &DeviceFinder::availableDevices() const
{
    return m_descriptors;
}

QPointer<Device> DeviceFinder::getDevice(const DeviceDescriptor &descriptor)
{
    return getDeviceById(descriptor.id);
}

QPointer<Device> DeviceFinder::getDeviceById(const QString& id)
{
    try
    {
        return m_devices.at(id);
    }
    catch (std::out_of_range)
    {
        auto&& existing = std::find_if(m_descriptors.cbegin(), m_descriptors.cend(), [&id](const DeviceDescriptor& d) {
            return id == d.id;
        });

        if (existing == m_descriptors.cend())
        {
            qCWarning(DeviceFinderLog) << "no descriptor found for the device ID" << id;
            return {};
        }

        auto&& device = new Device(*existing, this);
        m_devices[id] = device;
        return device;
    }
}

void DeviceFinder::socketReadyRead()
{
    qCDebug(DeviceFinderLog) << "socket ready read";

    char datagram[65536] = { 0 };
    QHostAddress remoteAddress;
    uint16_t remotePort = 0;
    auto length = m_socket->readDatagram(datagram, sizeof(datagram), &remoteAddress, &remotePort);
    qCDebug(DeviceFinderLog) << "received datagram from" << remoteAddress << ":" << remotePort << ", length:" << length;

    if (m_state == State::Scanning)
    {
        qCInfo(DeviceFinderLog) << "processing scan results";
        processScanResponse(QByteArray(datagram, length), remoteAddress, remotePort);
        m_timer->start();
    }
    else if (m_state == State::Binding)
    {
        qCInfo(DeviceFinderLog) << "processing bind results";
        processBindResponse(QByteArray(datagram, length));
        m_timer->start();
    }
}

void DeviceFinder::timerTimeout()
{
    qCDebug(DeviceFinderLog) << "timer timeout";

    if (m_state == State::Scanning)
    {
        qCInfo(DeviceFinderLog) << "scanning finished";

        m_state = State::Idle;
        emit scanFinshed();
        bindDevices();
    }
    else if (m_state == State::Binding)
    {
        qCInfo(DeviceFinderLog) << "binding finished";

        m_state = State::Idle;
        emit bindingFinished();
    }
    else
    {
        qCWarning(DeviceFinderLog) << "timer timeout in Idle state";
    }
}

void DeviceFinder::processScanResponse(const QByteArray response, const QHostAddress& remoteAddress, uint16_t remotePort)
{
    qCDebug(DeviceFinderLog) << "processing scan response" << response;

    QJsonObject pack;
    if (!ProtocolUtils::readPackFromResponse(response, Crypto::GenericAESKey, pack))
    {
        qCWarning(DeviceFinderLog) << "failed to read pack from response";
        return;
    }

    auto&& id = pack["cid"].toString();

    auto&& existing = std::find_if(m_descriptors.cbegin(), m_descriptors.cend(), [&id](const DeviceDescriptor& descriptor) {
        return descriptor.id == id;
    });

    if (existing != m_descriptors.cend())
    {
        qCInfo(DeviceFinderLog) << "device already added:" << id;
        return;
    }

    DeviceDescriptor device;
    device.id = id;
    device.name = pack["name"].toString();
    device.address = remoteAddress;
    device.port = remotePort;

    m_descriptors.push_back(device);
}

void DeviceFinder::bindDevices()
{
    if (m_state != State::Idle)
    {
        qCWarning(DeviceFinderLog) << "binding can only be initiated in Idle state";
        return;
    }

    m_state = State::Binding;

    bool hasPending = false;

    std::for_each(m_descriptors.cbegin(), m_descriptors.cend(), [this, &hasPending](const DeviceDescriptor& device) {
        // Check if device is already bound
        if (device.bound)
            return;

        hasPending = true;

        if (!m_socket->isOpen())
            m_socket->open(QIODevice::ReadWrite);

        auto&& bindingPacket = ProtocolUtils::createBindingRequest(device);
        auto&& encryptedBindingPacket = Crypto::encryptPack(bindingPacket, Crypto::GenericAESKey);
        auto&& request = ProtocolUtils::createDeviceRequest(encryptedBindingPacket, 1);

        qCDebug(DeviceFinderLog) << "sending bind request to" << device.address << ":" << device.port << ":" << request;

        m_socket->writeDatagram(request, device.address, device.port);
    });

    if (hasPending){
        m_timer->start();
    }
    else{
        //end bind state - nothing new to bind.
        qCInfo(DeviceFinderLog) << "binding finished without new devices found";

        m_state = State::Idle;
    }
}

void DeviceFinder::processBindResponse(const QByteArray &response)
{
    qCDebug(DeviceFinderLog) << "processing bind response:" << response;

    QJsonObject pack;
    if (!ProtocolUtils::readPackFromResponse(response, Crypto::GenericAESKey, pack))
    {
        qCWarning(DeviceFinderLog) << "failed to read pack from response";
        return;
    }

    qCDebug(DeviceFinderLog) << "bind response JSON:" << pack;

    auto&& key = pack["key"].toString();
    auto&& mac = pack["mac"].toString();

    if (key.isEmpty() || mac.isEmpty())
    {
        qCWarning(DeviceFinderLog) << "binding failed, response misses manadtory fields";
        return;
    }

    auto&& device = std::find_if(m_descriptors.begin(), m_descriptors.end(), [&mac](const DeviceDescriptor& dev) {
        return dev.id == mac;
    });

    if (device == m_descriptors.end())
    {
        qCWarning(DeviceFinderLog) << "no device found for this binding response";
        return;
    }

    device->key = key;
    device->bound = true;

    qCInfo(DeviceFinderLog) << "device bound:" << mac;

    emit deviceBound(*device);
}
