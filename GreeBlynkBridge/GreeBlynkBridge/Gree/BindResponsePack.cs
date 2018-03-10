using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;

namespace GreeBlynkBridge
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
