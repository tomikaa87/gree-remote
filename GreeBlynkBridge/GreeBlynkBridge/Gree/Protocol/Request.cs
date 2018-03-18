namespace GreeBlynkBridge.Gree.Protocol
{
    using Newtonsoft.Json;

    internal class Request : PackInfo
    {
        [JsonProperty("i")]
        public int I { get; set; }

        [JsonProperty("tcid")]
        public string TargetClientId { get; set; }

        [JsonProperty("uid")]
        public int UID { get; set; }

        [JsonProperty("pack")]
        public string Pack { get; set; }

        public static Request Create(string targetClientId, string pack, int i = 0)
        {
            return new Request()
            {
                ClientId = "app",
                Type = "pack",
                I = i,
                TargetClientId = targetClientId,
                Pack = pack,
                UID = 0
            };
        }
    }
}
