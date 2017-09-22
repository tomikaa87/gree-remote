#include "device.h"

#include <QLoggingCategory>
#include <QUdpSocket>

Q_DECLARE_LOGGING_CATEGORY(DeviceLog)
Q_LOGGING_CATEGORY(DeviceLog, "Device")

Device::Device(const DeviceDescriptor &descriptor, QObject *parent)
    : QObject(parent)
    , m_descriptor(descriptor)
    , m_socket(new QUdpSocket(this))
{
    qCInfo(DeviceLog) << "device controller created for" << descriptor.name << "(" << descriptor.id << ")";
}
