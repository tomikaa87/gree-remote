using Newtonsoft.Json;

namespace GreeBlynkBridge.Gree.Protocol
{
    class PackInfo
    {
        [JsonProperty("t")]
        public string Type { get; set; }

        [JsonProperty("cid")]
        public string ClientId { get; set; }
    }
}
