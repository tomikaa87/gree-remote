using System;
using System.Collections.Generic;
using System.Text;
using Newtonsoft.Json;

namespace GreeBlynkBridge.Gree
{
    /*
     {
       "t":"dev",
       "cid":"f4911e1fb84e",
       "bc":"gree",
       "brand":"gree",
       "catalog":"gree",
       "mac":"f4911e1fb84e",
       "mid":"10001",
       "model":"gree",
       "name":"Bedroom",
       "series":"gree",
       "vender":"1",
       "ver":"V1.1.13",
       "lock":0
    }
    */

    class DeviceInfo
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
