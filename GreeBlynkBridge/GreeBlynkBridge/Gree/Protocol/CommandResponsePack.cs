namespace GreeBlynkBridge.Gree.Protocol
{
    using System.Collections.Generic;
    using Newtonsoft.Json;

    internal class CommandResponsePack
    {
        [JsonProperty("opt")]
        public List<string> Columns { get; set; }

        [JsonProperty("p")]
        public List<int> Values { get; set; }
    }
}
