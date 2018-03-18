namespace GreeBlynkBridge.Gree
{
    using System.Collections.Generic;
    using System.Net.Sockets;
    using System.Text;
    using System.Threading.Tasks;
    using GreeBlynkBridge.Gree.Protocol;
    using Microsoft.Extensions.Logging;
    using Newtonsoft.Json;

    internal static class Scanner
    {
        private static ILogger log = Logging.Logger.CreateLogger("Scanner");

        public static async Task<List<Database.AirConditionerModel>> Scan(string broadcastAddresses)
        {
            var foundUnits = new List<Database.AirConditionerModel>();

            var responses = await DiscoverLocalDevices(broadcastAddresses);

            foreach (var response in responses)
            {
                var responsePackInfo = JsonConvert.DeserializeObject<ResponsePackInfo>(response.Json);
                if (responsePackInfo.Type != "pack")
                {
                    continue;
                }

                var decryptedPack = Crypto.DecryptGenericData(responsePackInfo.Pack);

                var packInfo = JsonConvert.DeserializeObject<PackInfo>(decryptedPack);
                if (packInfo.Type != "dev")
                {
                    continue;
                }

                var deviceInfo = JsonConvert.DeserializeObject<DeviceInfoResponsePack>(decryptedPack);

                log.LogInformation($"Found: " +
                    $"ClientId={deviceInfo.ClientId}, " +
                    $"FirmwareVersion={deviceInfo.FirmwareVersion}, " +
                    $"Name={deviceInfo.FriendlyName}, " +
                    $"Address={response.Address}");

                //// TODO check if already bound

                log.LogDebug("  Binding");

                var bindRequestPack = new BindRequestPack() { MAC = deviceInfo.ClientId };
                var request = Request.Create(deviceInfo.ClientId, Crypto.EncryptGenericData(JsonConvert.SerializeObject(bindRequestPack)), 1);
                var requestJson = JsonConvert.SerializeObject(request);

                var datagram = Encoding.ASCII.GetBytes(requestJson);

                using (var udp = new UdpClient())
                {
                    var sent = await udp.SendAsync(datagram, datagram.Length, response.Address, 7000);

                    if (sent != datagram.Length)
                    {
                        log.LogWarning("  Binding request cannot be sent");
                        continue;
                    }

                    for (int i = 0; i < 50; ++i)
                    {
                        if (udp.Available > 0)
                        {
                            var result = await udp.ReceiveAsync();
                            if (result.RemoteEndPoint.Address.ToString() != response.Address)
                            {
                                log.LogWarning($"  Got binding response from the wrong device: {result.RemoteEndPoint.Address.ToString()}");
                                continue;
                            }

                            var responseJson = Encoding.ASCII.GetString(result.Buffer);

                            responsePackInfo = JsonConvert.DeserializeObject<ResponsePackInfo>(responseJson);
                            if (responsePackInfo.Type != "pack")
                            {
                                continue;
                            }

                            var bindResponse = JsonConvert.DeserializeObject<BindResponsePack>(Crypto.DecryptGenericData(responsePackInfo.Pack));

                            log.LogDebug($"  Success. Key: {bindResponse.Key}");

                            foundUnits.Add(new Database.AirConditionerModel()
                            {
                                ID = deviceInfo.ClientId,
                                Name = deviceInfo.FriendlyName,
                                Address = result.RemoteEndPoint.Address.ToString(),
                                PrivateKey = bindResponse.Key
                            });

                            break;
                        }

                        await Task.Delay(100);
                    }
                }
            }

            log.LogInformation("Scan finished");

            return foundUnits;
        }

        private static async Task<List<DeviceDiscoveryResponse>> DiscoverLocalDevices(string broadcastAddress)
        {
            var responses = new List<DeviceDiscoveryResponse>();

            using (var udp = new UdpClient())
            {
                udp.EnableBroadcast = true;

                log.LogDebug("Sending scan packet");

                var bytes = Encoding.ASCII.GetBytes("{ \"t\": \"scan\" }");

                var sent = await udp.SendAsync(bytes, bytes.Length, broadcastAddress, 7000);

                log.LogDebug($"Sent bytes: {sent}");

                for (int i = 0; i < 20; ++i)
                {
                    if (udp.Available > 0)
                    {
                        var result = await udp.ReceiveAsync();

                        responses.Add(new DeviceDiscoveryResponse()
                        {
                            Json = Encoding.ASCII.GetString(result.Buffer),
                            Address = result.RemoteEndPoint.Address.ToString()
                        });

                        log.LogDebug($"Got response from {result.RemoteEndPoint.Address.ToString()}");
                    }

                    await Task.Delay(100);
                }
            }

            return responses;
        }
    }
}
