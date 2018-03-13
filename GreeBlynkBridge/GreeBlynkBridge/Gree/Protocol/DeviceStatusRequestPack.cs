namespace GreeBlynkBridge.Gree.Protocol
{
    using System.Collections.Generic;
    using Newtonsoft.Json;

    internal class DeviceStatusRequestPack : RequestPackInfo
    {
        [JsonProperty("cols")]
        public List<string> Columns { get; set; }

        public static DeviceStatusRequestPack Create(string clientId, List<string> columns)
        {
            return new DeviceStatusRequestPack()
            {
                Type = "status",
                MAC = clientId,
                Columns = columns,
                UID = null
            };
        }
    }
}