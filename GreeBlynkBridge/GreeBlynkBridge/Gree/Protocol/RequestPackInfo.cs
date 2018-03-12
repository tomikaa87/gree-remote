using Newtonsoft.Json;

namespace GreeBlynkBridge.Gree.Protocol
{
    class RequestPackInfo
    {
        [JsonProperty("t")]
        public string Type { get; set; }

        [JsonProperty("uid", NullValueHandling=NullValueHandling.Ignore)]
        public int? UID { get; set; }

        [JsonProperty("mac")]
        public string MAC { get; set; }

    }
}
