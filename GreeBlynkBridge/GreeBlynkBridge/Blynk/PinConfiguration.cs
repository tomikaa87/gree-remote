namespace GreeBlynkBridge.Blynk
{
    internal class PinConfiguration
    {
        public int DeviceSelector { get; set; }

        [Pin(Gree.DeviceParameterKeys.Mode, ValueOffset = 1)]
        public int SetMode { get; set; }

        [Pin(Gree.DeviceParameterKeys.SetTemperature, IsReadOnly = true)]
        public int GetTemperature { get; set; }

        [Pin(Gree.DeviceParameterKeys.SetTemperature)]
        public int SetTemperature { get; set; }

        [Pin(Gree.DeviceParameterKeys.FanSpeed)]
        public int SetFanSpeed { get; set; }

        [Pin(Gree.DeviceParameterKeys.VerticalSwing, ValueOffset = 1)]
        public int SetVerticalSwing { get; set; }

        [Pin(Gree.DeviceParameterKeys.Power, IsBinary = true)]
        public int SetPower { get; set; }

        [Pin(Gree.DeviceParameterKeys.TurboMode, IsBinary = true)]
        public int SetTurbo { get; set; }

        [Pin(Gree.DeviceParameterKeys.QuietMode, IsBinary = true)]
        public int SetQuiet { get; set; }

        [Pin(Gree.DeviceParameterKeys.SleepMode, IsBinary = true)]
        public int SetSleep { get; set; }

        [Pin(Gree.DeviceParameterKeys.XfanMode, IsBinary = true)]
        public int SetXfan { get; set; }

        [Pin(Gree.DeviceParameterKeys.HealthMode, IsBinary = true)]
        public int SetHealth { get; set; }

        [Pin(Gree.DeviceParameterKeys.EnergySavingMode, IsBinary = true)]
        public int SetEnergySaving { get; set; }

        [Pin(Gree.DeviceParameterKeys.Light, IsBinary = true)]
        public int SetLight { get; set; }

        [Pin(Gree.DeviceParameterKeys.AirMode, IsBinary = true)]
        public int SetAir { get; set; }

        public int SwitchDevice { get; set; }

        public override string ToString()
        {
            string s = $"\n\nPin configuration:\n";

            foreach (var f in typeof(PinConfiguration).GetProperties())
            {
                s += $"  {f.Name} = {f.GetValue(this)}\n";
            }

            return s;
        }
    }
}
