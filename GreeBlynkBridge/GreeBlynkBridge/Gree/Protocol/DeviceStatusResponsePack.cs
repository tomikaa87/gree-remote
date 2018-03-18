namespace GreeBlynkBridge.Gree.Protocol
{
    using System.Collections.Generic;
    using Newtonsoft.Json;

    internal class DeviceStatusResponsePack
    {
        [JsonProperty("cols")]
        public List<string> Columns { get; set; }

        [JsonProperty("dat")]
        public List<int> Values { get; set; }
    }
}