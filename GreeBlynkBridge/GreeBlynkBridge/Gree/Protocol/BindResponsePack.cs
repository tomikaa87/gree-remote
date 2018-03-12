using Newtonsoft.Json;

namespace GreeBlynkBridge.Gree.Protocol
{
    // TODO inherit from response pack info header or something
    class BindResponsePack
    {
        [JsonProperty("mac")]
        public string MAC { get; set; }

        [JsonProperty("key")]
        public string Key { get; set; }
    }
}
