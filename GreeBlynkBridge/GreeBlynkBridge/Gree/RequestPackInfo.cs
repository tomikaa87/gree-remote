using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;

namespace GreeBlynkBridge.Gree
{
    class RequestPackInfo
    {
        [JsonProperty("t")]
        public string Type { get; set; }

        [JsonProperty("uid")]
        public string UID { get; set; }

        [JsonProperty("mac")]
        public string MAC { get; set; }

    }
}
