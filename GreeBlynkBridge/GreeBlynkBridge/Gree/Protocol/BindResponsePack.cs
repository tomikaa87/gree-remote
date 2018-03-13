namespace GreeBlynkBridge.Gree.Protocol
{
    using Newtonsoft.Json;

    // TODO inherit from response pack info header or something
    internal class BindResponsePack
    {
        [JsonProperty("mac")]
        public string MAC { get; set; }

        [JsonProperty("key")]
        public string Key { get; set; }
    }
}
