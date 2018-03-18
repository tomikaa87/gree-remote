namespace GreeBlynkBridge.Gree.Protocol
{
    using Newtonsoft.Json;

    internal class PackInfo
    {
        [JsonProperty("t")]
        public string Type { get; set; }

        [JsonProperty("cid")]
        public string ClientId { get; set; }
    }
}
