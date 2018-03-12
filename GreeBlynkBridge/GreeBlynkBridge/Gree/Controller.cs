using System;
using Microsoft.Extensions.Logging;
using GreeBlynkBridge.Database;
using GreeBlynkBridge.Logging;
using System.Threading.Tasks;
using System.Net.Sockets;
using Newtonsoft.Json;
using System.Text;
using System.Collections.Generic;
using System.Linq;

namespace GreeBlynkBridge.Gree
{
    class Controller
    {
        private readonly AirConditionerModel m_model;
        private readonly ILogger m_log;
        private DeviceStatus m_deviceStatus;

        public delegate void DeviceStatusChangedEventHandler(object sender);
        public event DeviceStatusChangedEventHandler DeviceStatusChanged;

        public Controller(AirConditionerModel model)
        {
            m_model = model;
            m_log = Logger.CreateLogger($"Controller({model.Name}/{model.ID})");

            m_log.LogDebug("Controller created");
        }

        public async Task UpdateDeviceStatus()
        {
            m_log.LogDebug("Updating device status");

            var columns = typeof(DeviceStatusKeys).GetFields()
                .Where((f) => f.FieldType == typeof(string))
                .Select((f) => f.GetValue(null) as string)
                .ToList();

            var pack = DeviceStatusRequestPack.Create(m_model.ID, columns);
            var json = JsonConvert.SerializeObject(pack);
            var request = Request.Create(m_model.ID, Crypto.EncryptData(json, m_model.PrivateKey));
            var response = await SendDeviceRequest(request);
            json = Crypto.DecryptData(response.Pack, m_model.PrivateKey);
            var responsePack = JsonConvert.DeserializeObject<DeviceStatusResponsePack>(json);

            var deviceStatus = new DeviceStatus()
            {
                Parameters = responsePack.Columns
                    .Zip(responsePack.Values, (k, v) => new { k, v })
                    .ToDictionary(x => x.k, x => x.v)
            };

            if (m_deviceStatus != deviceStatus)
            {
                m_deviceStatus = deviceStatus;
                DeviceStatusChanged?.Invoke(this);
            }
        }

        private async Task<ResponsePackInfo> SendDeviceRequest(Request request)
        {
            m_log.LogDebug($"Sending device request");

            var datagram = Encoding.ASCII.GetBytes(JsonConvert.SerializeObject(request));
            m_log.LogDebug($"{datagram.Length} bytes will be sent");

            using (var udp = new UdpClient())
            {
                var sent = await udp.SendAsync(datagram, datagram.Length, m_model.Address, 7000);
                m_log.LogDebug($"{sent} bytes sent to {m_model.Address}");

                for (int i = 0; i < 20; ++i)
                {
                    if (udp.Available > 0)
                    {
                        var results = await udp.ReceiveAsync();
                        m_log.LogDebug($"Got response, {results.Buffer.Length} bytes");

                        var json = Encoding.ASCII.GetString(results.Buffer);
                        var response = JsonConvert.DeserializeObject<ResponsePackInfo>(json);

                        return response;
                    }

                    await Task.Delay(100);
                }

                m_log.LogWarning("Request timed out");

                return null;
            }
        }
    }
}