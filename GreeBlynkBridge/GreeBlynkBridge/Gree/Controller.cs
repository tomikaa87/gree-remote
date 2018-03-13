namespace GreeBlynkBridge.Gree
{
    using System.Collections.Generic;
    using System.Linq;
    using System.Net.Sockets;
    using System.Text;
    using System.Threading.Tasks;
    using GreeBlynkBridge.Database;
    using GreeBlynkBridge.Gree.Protocol;
    using GreeBlynkBridge.Logging;
    using Microsoft.Extensions.Logging;
    using Newtonsoft.Json;

    internal class Controller
    {
        private AirConditionerModel model;
        private ILogger log;

        public Controller(AirConditionerModel model)
        {
            this.Parameters = new Dictionary<string, int>();
            this.model = model;

            this.log = Logger.CreateLogger($"Controller({this.model.Name}/{this.model.ID})");

            this.log.LogDebug("Controller created");
        }

        public delegate void DeviceStatusChangedEventHandler(object sender, DeviceStatusChangedEventArgs e);

        public event DeviceStatusChangedEventHandler DeviceStatusChanged;

        public string DeviceName { get => this.model.Name; private set { } }

        public string DeviceID { get => this.model.ID; private set { } }

        public Dictionary<string, int> Parameters { get; private set; }

        public async Task UpdateDeviceStatus()
        {
            this.log.LogDebug("Updating device status");

            var columns = typeof(DeviceParameterKeys).GetProperties()
                .Where((f) => f.PropertyType == typeof(string))
                .Select((f) => f.GetValue(null) as string)
                .ToList();

            var pack = DeviceStatusRequestPack.Create(this.model.ID, columns);
            var json = JsonConvert.SerializeObject(pack);

            var encrypted = Crypto.EncryptData(json, this.model.PrivateKey);
            if (encrypted == null)
            {
                this.log.LogWarning("Failed to encrypt DeviceStatusRequestPack");
                return;
            }

            var request = Request.Create(this.model.ID, encrypted);

            var response = await this.SendDeviceRequest(request);
            if (response == null)
            {
                this.log.LogWarning("Failed to send DeviceStatusRequestPack");
                return;
            }

            json = Crypto.DecryptData(response.Pack, this.model.PrivateKey);
            if (json == null)
            {
                this.log.LogWarning("Failed to decrypt DeviceStatusResponsePack");
                return;
            }

            var responsePack = JsonConvert.DeserializeObject<DeviceStatusResponsePack>(json);
            if (responsePack == null)
            {
                this.log.LogWarning("Failed to deserialize DeviceStatusReponsePack");
                return;
            }

            var updatedParameters = responsePack.Columns
                .Zip(responsePack.Values, (k, v) => new { k, v })
                .ToDictionary(x => x.k, x => x.v);

            bool parametersChanged = !this.Parameters.OrderBy(pair => pair.Key)
                .SequenceEqual(updatedParameters.OrderBy(pair => pair.Key));

            if (parametersChanged)
            {
                this.log.LogDebug("Device parameters updated");
                this.Parameters = updatedParameters;

                this.DeviceStatusChanged?.Invoke(
                    this, 
                    new DeviceStatusChangedEventArgs()
                    {
                        Parameters = updatedParameters
                    });
            }
        }

        public async Task SetDeviceParameter(string name, int value)
        {
            this.log.LogDebug($"Setting parameter: {name}={value}");

            var pack = CommandRequestPack.Create(
                this.DeviceID,
                new List<string>() { name },
                new List<int>() { value });

            var json = JsonConvert.SerializeObject(pack);
            var request = Request.Create(this.DeviceID, Crypto.EncryptData(json, this.model.PrivateKey));

            ResponsePackInfo response;
            try
            {
                response = await this.SendDeviceRequest(request);
            }
            catch (System.IO.IOException e)
            {
                this.log.LogWarning($"Failed to send CommandRequestPack: {e.Message}");
                return;
            }

            json = Crypto.DecryptData(response.Pack, this.model.PrivateKey);
            var responsePack = JsonConvert.DeserializeObject<CommandResponsePack>(json);

            if (!responsePack.Columns.Contains(name))
            {
                this.log.LogWarning("Parameter cannot be changed.");
            }
        }

        /// <summary>
        /// Sends a request to the actual device and waits a few seconds for the response.
        /// </summary>
        /// <param name="request">Request object which encapsulates the encrypted pack</param>
        /// <returns>The response object which encapsulates the encrypted response pack</returns>
        /// <exception cref="System.IO.IOException"/>
        private async Task<ResponsePackInfo> SendDeviceRequest(Request request)
        {
            this.log.LogDebug($"Sending device request");

            var datagram = Encoding.ASCII.GetBytes(JsonConvert.SerializeObject(request));
            this.log.LogDebug($"{datagram.Length} bytes will be sent");

            using (var udp = new UdpClient())
            {
                var sent = await udp.SendAsync(datagram, datagram.Length, this.model.Address, 7000);
                this.log.LogDebug($"{sent} bytes sent to {this.model.Address}");

                for (int i = 0; i < 20; ++i)
                {
                    if (udp.Available > 0)
                    {
                        var results = await udp.ReceiveAsync();
                        this.log.LogDebug($"Got response, {results.Buffer.Length} bytes");

                        var json = Encoding.ASCII.GetString(results.Buffer);
                        var response = JsonConvert.DeserializeObject<ResponsePackInfo>(json);

                        return response;
                    }

                    await Task.Delay(100);
                }

                this.log.LogWarning("Request timed out");

                throw new System.IO.IOException("Device request timed out");
            }
        }
    }
}