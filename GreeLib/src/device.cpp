#include "device.h"
#include "crypto.h"
#include "protocolutils.h"

#include <QJsonObject>
#include <QLoggingCategory>
#include <QNetworkDatagram>
#include <QUdpSocket>
#include <QTimer>

Q_DECLARE_LOGGING_CATEGORY(DeviceLog)
Q_LOGGING_CATEGORY(DeviceLog, "Device")

Device::Device(const DeviceDescriptor &descriptor, QObject *parent)
    : QObject(parent)
    , m_device(descriptor)
    , m_socket(new QUdpSocket(this))
    , m_pollTimer(new QTimer(this))
{
    connect(m_socket, &QUdpSocket::readyRead, this, &Device::onSocketReadyRead);
    connect(m_pollTimer, &QTimer::timeout, this, &Device::onPollTimerTimeout);

    qCInfo(DeviceLog) << "device controller created for" << descriptor.name << "(" << descriptor.id << ")";

    m_pollTimer->start(2000);
}

Device::~Device()
{
    if (m_socket->isOpen())
        m_socket->close();
}

void Device::deviceRequest(const QByteArray& request)
{
    openSocket();
    auto written = m_socket->writeDatagram(request, m_device.address, m_device.port);
    qCDebug(DeviceLog) << m_device.id << "sending request datagram. Written bytes:" << written;
}

void Device::processStatusUpdateResponse(const QByteArray &response)
{
    qCDebug(DeviceLog) << "processing status update response:" << response;

    QJsonObject pack;
    if (!ProtocolUtils::readPackFromResponse(response, m_device.key, pack))
    {
        qCWarning(DeviceLog) << "failed read pack from status update response";
        return;
    }

    auto&& map = ProtocolUtils::readStatusMapFromPack(pack);
    if (map.isEmpty())
    {
        qCWarning(DeviceLog) << "failed process status update";
        return;
    }

    m_powered = map["Pow"] == 1;
    m_health = map["Health"] == 1;
    m_turbo = map["Tur"] == 1;
    m_quiet = map["Quiet"] == 1;
    m_light = map["Lig"] == 1;
    m_xfanEnabled = map["Blo"] == 1;
    m_airModeEnabled = map["Air"] == 1;
    m_mode = map["Mod"];
    m_temperature = map["SetTem"];
    m_fanSpeed = map["WdSpd"];
    m_verticalSwingMode = map["SwUpDn"];
    m_horizontalSwingMode = map["SwingLfRig"];
    m_sleepModeEnabled = map["SwhSlp"];
    m_savingModeEnabled = map["SvSt"];

    emit statusUpdated();
}

void Device::processCommandResponse(const QByteArray& response)
{
    qCDebug(DeviceLog) << "processing command response:" << response;

    QJsonObject pack;
    if (!ProtocolUtils::readPackFromResponse(response, m_device.key, pack))
    {
        qCWarning(DeviceLog) << "failed read pack from command response";
        return;
    }

    if (pack["r"] != 200)
    {
        qCWarning(DeviceLog) << "command failed. Result:" << pack["r"];
        return;
    }

    // TODO handle new values in the response
}

void Device::updateFanParameters()
{
    setParameters(ParameterMap{
        { "WdSpd", m_fanSpeed },
        { "Quiet", m_quiet ? 1 : 0 },
        { "Tur", m_turbo ? 1 : 0 },

        // TODO figure out what 'NoiseSet' does
        { "NoiseSet", 0 },
    });
}

void Device::updateStatus()
{
    if (m_state != State::Idle)
    {
        qCWarning(DeviceLog) << "device is busy";
        return;
    }

    auto&& pack = ProtocolUtils::createDeviceStatusRequestPack(m_device.id);
    auto&& encryptedPack = Crypto::encryptPack(pack, m_device.key);
    auto&& request = ProtocolUtils::createDeviceRequest(encryptedPack, 0);

    m_state = State::StatusUpdate;

    deviceRequest(request);
}

void Device::setXfanBlowEnabled(bool enabled)
{
    setParameters(ParameterMap{
        { "Blo", enabled ? 1 : 0 }
    });
}

void Device::setAirModeEnabled(bool enabled)
{
    setParameters(ParameterMap{
        { "Air", enabled ? 1 : 0 }
                  });
}

void Device::setSleepModeEnabled(bool enabled)
{
    setParameters(ParameterMap{
        { "SwhSlp", enabled ? 1 : 0 }
    });
}

void Device::setSavingModeEnabled(bool enabled)
{
    setParameters(ParameterMap{
        { "SvSt", enabled ? 1 : 0 }
    });
}

void Device::setPoweredOn(bool on)
{
    setParameters(ParameterMap{
        { "Pow", on ? 1 : 0 }
    });
}

void Device::setHealthEnabled(bool enabled)
{
    setParameters(ParameterMap{
        { "Health", enabled ? 1 : 0 }
    });
}

void Device::setTurboEnabled(bool enabled)
{
    m_turbo = enabled;
    updateFanParameters();
}

void Device::setQuietModeEnabled(bool enabled)
{
    m_quiet = enabled;
    updateFanParameters();
}

void Device::setLightEnabled(bool enabled)
{
    setParameters(ParameterMap{
        { "Lig", enabled ? 1 : 0 }
    });
}

void Device::setMode(int mode)
{
    setParameters(ParameterMap{
        { "Mod", mode }
    });
}

void Device::setTemperature(int temperature)
{
    setParameters(ParameterMap{
        { "TemUn", 0 },
        { "SetTem", temperature }
    });
}

void Device::setFanSpeed(int speed)
{
    m_fanSpeed = speed;
    updateFanParameters();
}

void Device::setVerticalSwingMode(int mode)
{
    setParameters(ParameterMap{
        { "SwUpDn", mode }
    });
}

void Device::setParameters(const Device::ParameterMap& parameters)
{
    auto&& pack = ProtocolUtils::createDeviceCommandPack(parameters);
    auto&& encryptedPack = Crypto::encryptPack(pack, m_device.key);
    auto&& request = ProtocolUtils::createDeviceRequest(encryptedPack, 0);

    m_state = State::Command;

    deviceRequest(request);
}

void Device::openSocket()
{
    if (m_socket->isOpen())
        return;

    qCDebug(DeviceLog) << m_device.id << "opening socket";
    m_socket->open(QIODevice::ReadWrite);

    qCDebug(DeviceLog) << m_device.id << "binding to" << m_device.address << ":" << m_device.port;
    if (!m_socket->bind(m_device.address, m_device.port, QUdpSocket::ShareAddress))
    {
        qCWarning(DeviceLog) << m_device.id << "binding failed. Error:" << m_socket->errorString();
        return;
    }

    m_state = State::Idle;
}

void Device::onPollTimerTimeout()
{
    qCDebug(DeviceLog) << m_device.id << "poll timer timeout";

    updateStatus();
}

void Device::onSocketReadyRead()
{
    qCDebug(DeviceLog) << m_device.id << "socket ready read";

    auto&& datagram = m_socket->receiveDatagram();
    qCDebug(DeviceLog) << "received datagram from" << datagram.senderAddress() << ":" << datagram.senderPort();

    if (m_state == State::StatusUpdate)
    {
        processStatusUpdateResponse(datagram.data());
        m_state = State::Idle;
    }
    else if (m_state == State::Command)
    {
        processCommandResponse(datagram.data());
        m_state = State::Idle;
    }
}
