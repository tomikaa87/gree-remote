namespace GreeBlynkBridge.Blynk
{
    using System;

    internal class PinAttribute : Attribute
    {
        public PinAttribute(string deviceParamName)
        {
            this.DeviceParamName = deviceParamName;
        }

        public string DeviceParamName { get; private set; }

        public bool IsBinary { get; set; }

        public int ValueOffset { get; set; }

        public bool IsReadOnly { get; set; }
    }
}
