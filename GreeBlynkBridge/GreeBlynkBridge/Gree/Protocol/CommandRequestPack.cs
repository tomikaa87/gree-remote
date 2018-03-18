namespace GreeBlynkBridge.Gree.Protocol
{
    using System.Collections.Generic;
    using Newtonsoft.Json;

    internal class CommandRequestPack : RequestPackInfo
    {
        [JsonProperty("opt")]
        public List<string> Columns { get; set; }

        [JsonProperty("p")]
        public List<int> Values { get; set; }

        public static CommandRequestPack Create(string clientId, List<string> columns, List<int> values)
        {
            return new CommandRequestPack()
            {
                Type = "cmd",
                MAC = clientId,
                Columns = columns,
                Values = values,
                UID = null
            };
        }
    }
}
