namespace GreeBlynkBridge.Blynk
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Threading.Tasks;
    using Microsoft.Extensions.Configuration;
    using Microsoft.Extensions.Logging;

    internal class BlynkController
    {
        private BlynkLibrary.Blynk blynk;
        private ILogger log = Logging.Logger.CreateLogger<BlynkController>();
        private List<string> deviceIDs;
        private List<Gree.Controller> deviceControllers;
        private PinConfiguration pinConfig;
        private Gree.Controller selectedDevice;

        public BlynkController(IConfiguration config)
        {
            this.deviceIDs = this.PopulateDevicesFromConfig(config);

            this.SetupPinMappingFromConfig(config);

            var token = config["blynk:token"];
            if (token == null)
            {
                throw new ArgumentException("Blynk API token is not configured");
            }

            var serverAddress = config["blynk:server-address"] ?? "blynk-cloud.com";

            var serverPort = 8442;
            if (int.TryParse(config["blynk:server-port"], out var value))
                serverPort = value;

            this.blynk = new BlynkLibrary.Blynk(token, serverAddress, serverPort);
            this.blynk.VirtualPinReceived += this.BlynkVirtualPinReceived;
            this.blynk.Connect();
        }

        public void SetDeviceControllers(List<Gree.Controller> controllers)
        {
            if (controllers.Count() == 0)
            {
                this.log.LogWarning("Controller list is empty");
                return;
            }

            this.log.LogDebug("Updating device controllers");

            this.deviceControllers = controllers;

            this.UpdateSelectedDevice();
        }

        private async void BlynkVirtualPinReceived(BlynkLibrary.Blynk b, BlynkLibrary.VirtualPinEventArgs e)
        {
            this.log.LogDebug($"Virtual pin received: {e.Data.Pin}={e.Data.Value[0].ToString()}");

            if (!int.TryParse(e.Data.Value[0].ToString(), out int value))
            {
                this.log.LogWarning("Non-integer value received, ignoring");
                return;
            }

            var pin = e.Data.Pin;

            if (pin == this.pinConfig.DeviceSelector)
            {
                this.SelectDevice(value - 1);
            }
            else
            {
                foreach (var p in typeof(PinConfiguration).GetProperties())
                {
                    if (!int.TryParse(p.GetValue(this.pinConfig).ToString(), out int result))
                    {
                        continue;
                    }

                    if (result != pin)
                    {
                        continue;
                    }

                    var pinAttributes = p.GetCustomAttributes(typeof(PinAttribute), false);
                    if (pinAttributes.Count() == 0)
                    {
                        continue;
                    }

                    var attribute = pinAttributes.First() as PinAttribute;

                    // Read-only pin is used for updating gauges and labels
                    if (attribute.IsReadOnly)
                    {
                        break;
                    }

                    int binaryValue = value > 0 ? 1 : 0;

                    await this.SetDeviceParameter(
                        attribute.DeviceParamName,
                        attribute.IsBinary ? binaryValue : (value - attribute.ValueOffset));

                    break;
                }
            }
        }

        private async Task SetDeviceParameter(string parameter, int value)
        {
            var logPrefix = $"SetDeviceParameter(parameter={parameter}, value={value})";

            if (this.selectedDevice == null)
            {
                this.log.LogWarning($"{logPrefix} failed, no selected device found");
                return;
            }

            await this.selectedDevice.SetDeviceParameter(parameter, value);
        }

        private List<string> PopulateDevicesFromConfig(IConfiguration config)
        {
            var ids = config.AsEnumerable()
                .Where(p => p.Key.StartsWith("blynk:devices:"))
                .Select(p => p.Value)
                .ToList();

            if (ids.Count() == 0)
            {
                throw new ArgumentException("No devices IDs configured");
            }

            this.log.LogInformation("Blynk will use the following devices:");
            for (int i = 0; i < ids.Count(); ++i)
            {
                this.log.LogInformation($"  {i}={ids[i]}");
            }

            return ids;
        }

        private void SetupPinMappingFromConfig(IConfiguration config)
        {
            this.pinConfig = new PinConfiguration()
            {
                DeviceSelector = this.ReadPinMappingValue("device-selector", config),
                SetMode = this.ReadPinMappingValue("set-mode", config),
                GetTemperature = this.ReadPinMappingValue("get-temperature", config),
                SetTemperature = this.ReadPinMappingValue("set-temperature", config),
                SetFanSpeed = this.ReadPinMappingValue("set-fan-speed", config),
                SetVerticalSwing = this.ReadPinMappingValue("set-vertical-swing", config),
                SetPower = this.ReadPinMappingValue("set-power", config),
                SetTurbo = this.ReadPinMappingValue("set-turbo", config),
                SetQuiet = this.ReadPinMappingValue("set-quiet", config),
                SetSleep = this.ReadPinMappingValue("set-sleep", config),
                SetXfan = this.ReadPinMappingValue("set-xfan", config),
                SetHealth = this.ReadPinMappingValue("set-health", config),
                SetEnergySaving = this.ReadPinMappingValue("set-energy-saving", config),
                SetLight = this.ReadPinMappingValue("set-light", config),
                SetAir = this.ReadPinMappingValue("set-air", config)
            };

            this.log.LogDebug(this.pinConfig.ToString());
        }

        private int ReadPinMappingValue(string name, IConfiguration config)
        {
            var pin = config[$"blynk:pins:{name}"];

            if (pin == null)
            {
                throw new ArgumentException($"{name} pin is undefined");
            }

            if (int.TryParse(pin, out int number))
            {
                return number;
            }
            else
            {
                throw new ArgumentException($"Value of {name} is not a valid integer");
            }
        }

        private void UpdateSelectedDevice()
        {
            this.blynk.ReadVirtualPin(new BlynkLibrary.VirtualPin()
            {
                Pin = this.pinConfig.DeviceSelector
            });
        }

        private async void UpdateBlynkValues()
        {
            await this.selectedDevice.UpdateDeviceStatus();

            foreach (var field in typeof(PinConfiguration).GetProperties())
            {
                if (field.CustomAttributes.Count() == 0)
                {
                    continue;
                }

                var pinAttributes = field.GetCustomAttributes(typeof(PinAttribute), false);

                if (pinAttributes.Count() == 0)
                {
                    continue;
                }

                var pinAttribute = pinAttributes[0] as PinAttribute;
                var paramName = pinAttribute.DeviceParamName;
                var pin = int.Parse(field.GetValue(this.pinConfig).ToString());

                if (!this.selectedDevice.Parameters.ContainsKey(paramName))
                {
                    continue;
                }

                var value = this.selectedDevice.Parameters[paramName] + pinAttribute.ValueOffset;

                this.log.LogDebug($"Updating Blynk pin: {pin}={value}");

                this.blynk.SendVirtualPin(new BlynkLibrary.VirtualPin()
                {
                    Pin = pin,
                    Value = new List<object>() { value }
                });
            }
        }

        private void SelectDevice(int index)
        {
            if (index >= this.deviceIDs.Count())
            {
                this.log.LogWarning($"Cannot select device with invalid index: {index}");
                return;
            }

            this.log.LogInformation($"Selecting device: {index}={this.deviceIDs[index]}");

            if (this.selectedDevice != null)
            {
                this.selectedDevice.DeviceStatusChanged -= this.SelectedDeviceStatusChanged;
            }

            try
            {
                this.selectedDevice = this.deviceControllers
                    .Where(c => c.DeviceID.Equals(this.deviceIDs[index], StringComparison.OrdinalIgnoreCase))
                    .First();
            }
            catch (Exception)
            {
                this.log.LogWarning("Cannot select device, no controller found");
                return;
            }

            this.selectedDevice.DeviceStatusChanged += this.SelectedDeviceStatusChanged;

            this.UpdateBlynkValues();
        }

        private void SelectedDeviceStatusChanged(object sender, Gree.DeviceStatusChangedEventArgs e)
        {
            this.UpdateBlynkValues();
        }
    }
}
