namespace GreeBlynkBridge.Blynk
{
    class PinConfiguration
    {
        public int deviceSelector;

        [Pin(Gree.DeviceParameterKeys.Mode, ValueOffset = 1)]
        public int setMode;

        [Pin(Gree.DeviceParameterKeys.SetTemperature, IsReadOnly = true)]
        public int getTemperature;

        [Pin(Gree.DeviceParameterKeys.SetTemperature)]
        public int setTemperature;

        [Pin(Gree.DeviceParameterKeys.FanSpeed)]
        public int setFanSpeed;

        [Pin(Gree.DeviceParameterKeys.VerticalSwing, ValueOffset = 1)]
        public int setVerticalSwing;

        [Pin(Gree.DeviceParameterKeys.Power, IsBinary = true)]
        public int setPower;

        [Pin(Gree.DeviceParameterKeys.TurboMode, IsBinary = true)]
        public int setTurbo;

        [Pin(Gree.DeviceParameterKeys.QuietMode, IsBinary = true)]
        public int setQuiet;

        [Pin(Gree.DeviceParameterKeys.SleepMode, IsBinary = true)]
        public int setSleep;

        [Pin(Gree.DeviceParameterKeys.XfanMode, IsBinary = true)]
        public int setXfan;

        [Pin(Gree.DeviceParameterKeys.HealthMode, IsBinary = true)]
        public int setHealth;

        [Pin(Gree.DeviceParameterKeys.EnergySavingMode, IsBinary = true)]
        public int setEnergySaving;

        [Pin(Gree.DeviceParameterKeys.Light, IsBinary = true)]
        public int setLight;

        [Pin(Gree.DeviceParameterKeys.AirMode, IsBinary = true)]
        public int setAir;

        public override string ToString()
        {
            string s = $"\n\nPin configuration:\n";

            foreach (var f in typeof(PinConfiguration).GetFields())
                s += $"  {f.Name} = {f.GetValue(this)}\n";

            return s;
        }
    }
}
