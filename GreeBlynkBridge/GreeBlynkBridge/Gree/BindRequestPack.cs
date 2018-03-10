using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;

namespace GreeBlynkBridge.Gree
{
    class BindRequestPack
    {
        [JsonProperty("t")]
        public string Type { get => "bind"; private set {} }

        [JsonProperty("uid")]
        public int UID { get => 0; private set {} }

        [JsonProperty("mac")]
        public string MAC { get; set; }
    }
}
