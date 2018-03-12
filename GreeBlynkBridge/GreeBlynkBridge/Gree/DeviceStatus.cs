using System;
using System.Collections.Generic;

namespace GreeBlynkBridge.Gree
{
    class DeviceStatus
    {
        public Dictionary<string, int> Parameters { get; set; }

        public static bool operator ==(DeviceStatus a, DeviceStatus b) => a.Equals(b);

        public static bool operator !=(DeviceStatus a, DeviceStatus b) => !a.Equals(b);

        public override int GetHashCode() => Parameters.GetHashCode();

        public override bool Equals(Object obj)
        {
            if (!(obj is DeviceStatus))
                return false;

            return Parameters == (obj as DeviceStatus).Parameters;
        }
    }
}