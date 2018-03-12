using Newtonsoft.Json;

namespace GreeBlynkBridge.Gree.Protocol
{
    class ResponsePackInfo : PackInfo
    {
        [JsonProperty("uid", NullValueHandling=NullValueHandling.Ignore)]
        public int? UID { get; set; }

        [JsonProperty("tcid")]
        public string TargetClientId { get; set; }

        [JsonProperty("pack")]
        public string Pack { get; set; }
    }
}
