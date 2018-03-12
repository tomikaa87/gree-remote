using Newtonsoft.Json;
using System.Collections.Generic;

namespace GreeBlynkBridge.Gree.Protocol
{
    class CommandResponsePack
    {
        [JsonProperty("opt")]
        public List<string> Columns { get; set; }

        [JsonProperty("p")]
        public List<int> Values { get; set; }
    }
}
