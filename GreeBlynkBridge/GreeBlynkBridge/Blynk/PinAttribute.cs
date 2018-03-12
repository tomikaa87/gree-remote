using System;

namespace GreeBlynkBridge.Blynk
{
    internal class PinAttribute : Attribute
    {
        public string DeviceParamName { get; private set; }
        public bool IsBinary { get; set; }
        public int ValueOffset { get; set; }
        public bool IsReadOnly { get; set; }

        public PinAttribute(string deviceParamName)
        {
            DeviceParamName = deviceParamName;
        }
    }
}
