using Newtonsoft.Json;
using System;
using System.Collections.Generic;

namespace GreeBlynkBridge.Gree
{
    class DeviceStatusResponsePack
    {
        [JsonProperty("cols")]
        public List<string> Columns { get; set; }

        [JsonProperty("dat")]
        public List<int> Values { get; set; }

        public int? GetValue(string column)
        {
            var index = Columns.FindIndex((s) => s.Equals(column, StringComparison.OrdinalIgnoreCase));
            
            if (index == -1 || index >= Values.Count)
                return null;

            return Values[index];
        }
    }
}