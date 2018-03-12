using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

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

    class BlynkController
    {
        private readonly BlynkLibrary.Blynk m_blynk;
        private readonly ILogger m_log = Logging.Logger.CreateLogger<BlynkController>();
        private readonly List<string> m_deviceIDs;
        private List<Gree.Controller> m_deviceControllers;
        private PinConfiguration m_pinConfig;
        private Gree.Controller m_selectedDevice;

        public BlynkController(IConfiguration config)
        {
            m_deviceIDs = PopulateDevicesFromConfig(config);

            SetupPinMappingFromConfig(config);

            var token = config["blynk:token"];
            if (token == null)
                throw new ArgumentException("Blynk API token is not configured");

            m_blynk = new BlynkLibrary.Blynk(token, "blynk-cloud.com", 8442);
            m_blynk.VirtualPinReceived += BlynkVirtualPinReceived;

            m_blynk.Connect();
        }

        public void SetDeviceControllers(List<Gree.Controller> controllers)
        {
            if (controllers.Count() == 0)
            {
                m_log.LogWarning("Controller list is empty");
                return;
            }

            m_log.LogDebug("Updating device controllers");

            m_deviceControllers = controllers;

            UpdateSelectedDevice();
        }

        private async void BlynkVirtualPinReceived(BlynkLibrary.Blynk b, BlynkLibrary.VirtualPinEventArgs e)
        {
            m_log.LogDebug($"Virtual pin received: {e.Data.Pin}={e.Data.Value[0].ToString()}");

            var pin = e.Data.Pin;

            if (!int.TryParse(e.Data.Value[0].ToString(), out int value))
            {
                m_log.LogWarning("Non-integer value received, ignoring");
                return;
            }

            int binaryValue = value > 0 ? 1 : 0;

            if (pin == m_pinConfig.deviceSelector)
            {
                SelectDevice(value - 1);
            }
            else
            {
                foreach (var field in typeof(PinConfiguration).GetFields())
                {
                    if (int.TryParse(field.GetValue(m_pinConfig).ToString(), out int result))
                    {
                        if (result == pin)
                        {
                            var ca = field.GetCustomAttributes(typeof(PinAttribute), false);
                            if (ca.Count() > 0)
                            {
                                var attribute = (ca.First() as PinAttribute);

                                if (attribute.IsReadOnly)
                                    break;

                                await SetDeviceParameter(attribute.DeviceParamName,
                                    attribute.IsBinary ? binaryValue : (value + attribute.ValueOffset));

                                break;
                            }
                        }
                    }
                }
            }
        }

        private async Task SetDeviceParameter(string parameter, int value)
        {
            var logPrefix = $"SetDeviceParameter(parameter={parameter}, value={value})";

            if (m_selectedDevice == null)
            {
                m_log.LogWarning($"{logPrefix} failed, no selected device found");
                return;
            }

            await m_selectedDevice.SetDeviceParameter(parameter, value);
        }

        private List<string> PopulateDevicesFromConfig(IConfiguration config)
        {
            var ids = config.AsEnumerable()
                .Where(p => p.Key.StartsWith("blynk:devices:"))
                .Select(p => p.Value)
                .ToList();

            if (ids.Count() == 0)
                throw new ArgumentException("No devices IDs configured");

            m_log.LogInformation("Blynk will use the following devices:");
            for (int i = 0; i < ids.Count(); ++i)
                m_log.LogInformation($"  {i}={ids[i]}");

            return ids;
        }

        private void SetupPinMappingFromConfig(IConfiguration config)
        {
            m_pinConfig = new PinConfiguration()
            {
                deviceSelector = ReadPinMappingValue("device-selector", config),
                setMode = ReadPinMappingValue("set-mode", config),
                getTemperature = ReadPinMappingValue("get-temperature", config),
                setTemperature = ReadPinMappingValue("set-temperature", config),
                setFanSpeed = ReadPinMappingValue("set-fan-speed", config),
                setVerticalSwing = ReadPinMappingValue("set-vertical-swing", config),
                setPower = ReadPinMappingValue("set-power", config),
                setTurbo = ReadPinMappingValue("set-turbo", config),
                setQuiet = ReadPinMappingValue("set-quiet", config),
                setSleep = ReadPinMappingValue("set-sleep", config),
                setXfan = ReadPinMappingValue("set-xfan", config),
                setHealth = ReadPinMappingValue("set-health", config),
                setEnergySaving = ReadPinMappingValue("set-energy-saving", config),
                setLight = ReadPinMappingValue("set-light", config),
                setAir = ReadPinMappingValue("set-air", config)
            };

            m_log.LogInformation(m_pinConfig.ToString());
        }

        private int ReadPinMappingValue(string name, IConfiguration config)
        {
            var pin = config[$"blynk:pins:{name}"];
            if (pin == null)
                throw new ArgumentException($"{name} pin is undefined");

            if (int.TryParse(pin, out int number))
                return number;
            else
                throw new ArgumentException($"Value of {name} is not a valid integer");
        }

        private void UpdateSelectedDevice()
        {
            m_blynk.ReadVirtualPin(new BlynkLibrary.VirtualPin() { Pin = m_pinConfig.deviceSelector });
        }

        private async void UpdateBlynkValues()
        {
            await m_selectedDevice.UpdateDeviceStatus();

            foreach (var field in typeof(PinConfiguration).GetFields())
            {
                if (field.CustomAttributes.Count() == 0)
                    continue;

                var pinAttributes = field.GetCustomAttributes(typeof(PinAttribute), false);

                if (pinAttributes.Count() == 0)
                    continue;

                var pinAttribute = pinAttributes[0] as PinAttribute;
                var paramName = pinAttribute.DeviceParamName;
                var pin = int.Parse(field.GetValue(m_pinConfig).ToString());

                if (!m_selectedDevice.Parameters.ContainsKey(paramName))
                    continue;

                var value = m_selectedDevice.Parameters[paramName] + pinAttribute.ValueOffset;

                m_log.LogDebug($"Updating Blynk pin: {pin}={value}");

                m_blynk.SendVirtualPin(new BlynkLibrary.VirtualPin()
                {
                    Pin = pin,
                    Value = new List<object>() { value }
                });
            }
        }

        private void SelectDevice(int index)
        {
            if (index >= m_deviceIDs.Count())
            {
                m_log.LogWarning($"Cannot select device with invalid index: {index}");
                return;
            }

            m_log.LogInformation($"Selecting device: {index}={m_deviceIDs[index]}");

            if (m_selectedDevice != null)
                m_selectedDevice.DeviceStatusChanged -= SelectedDeviceStatusChanged;

            try
            {
                m_selectedDevice = m_deviceControllers
                    .Where(c => c.DeviceID.Equals(m_deviceIDs[index], StringComparison.OrdinalIgnoreCase))
                    .First();
            }
            catch (Exception)
            {
                m_log.LogWarning("Cannot select device, no controller found");
                return;
            }

            m_selectedDevice.DeviceStatusChanged += SelectedDeviceStatusChanged;

            UpdateBlynkValues();
        }

        private void SelectedDeviceStatusChanged(object sender, Gree.DeviceStatusChangedEventArgs e)
        {
            UpdateBlynkValues();
        }
    }
}
