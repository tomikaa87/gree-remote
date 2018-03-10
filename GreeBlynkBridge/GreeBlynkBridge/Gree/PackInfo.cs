using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;

namespace GreeBlynkBridge.Gree
{
    class PackInfo
    {
        [JsonProperty("t")]
        public string Type { get; set; }

        [JsonProperty("cid")]
        public string ClientId { get; set; }
    }
}
