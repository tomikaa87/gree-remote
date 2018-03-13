namespace GreeBlynkBridge.Gree.Protocol
{
    using Newtonsoft.Json;

    internal class DeviceInfoResponsePack
    {
        [JsonProperty("bc")]
        public string BrandCode { get; set; }

        [JsonProperty("brand")]
        public string Brand { get; set; }

        [JsonProperty("catalog")]
        public string Catalog { get; set; }

        [JsonProperty("mac")]
        public string ClientId { get; set; }

        [JsonProperty("mid")]
        public string ModelId { get; set; }

        [JsonProperty("model")]
        public string Model { get; set; }

        [JsonProperty("name")]
        public string FriendlyName { get; set; }

        [JsonProperty("series")]
        public string Series { get; set; }

        [JsonProperty("vender")]
        public string Vendor { get; set; }

        [JsonProperty("ver")]
        public string FirmwareVersion { get; set; }

        [JsonProperty("lock")]
        public int LockState { get; set; }
    }
}
