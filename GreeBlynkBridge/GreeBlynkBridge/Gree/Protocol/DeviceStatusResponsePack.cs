using Newtonsoft.Json;
using System.Collections.Generic;

namespace GreeBlynkBridge.Gree.Protocol
{
    class DeviceStatusResponsePack
    {
        [JsonProperty("cols")]
        public List<string> Columns { get; set; }

        [JsonProperty("dat")]
        public List<int> Values { get; set; }
    }
}