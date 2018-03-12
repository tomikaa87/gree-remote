using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Configuration;
using System.Reflection;
using System.IO;
using System;
using System.Linq;
using System.Collections.Generic;
using System.Timers;
using GreeBlynkBridge.Blynk;

namespace GreeBlynkBridge
{
    class Program
    {
        static ILogger s_log = Logging.Logger.CreateDefaultLogger();

        static async Task Main(string[] args)
        {
            var basePath = Directory.GetParent(new Uri(Assembly.GetExecutingAssembly().CodeBase).AbsolutePath).FullName;

            var configBuilder = new ConfigurationBuilder()
                .SetBasePath(basePath)
                .AddJsonFile("config.json");

            IConfiguration config;

            try
            {
                config = configBuilder.Build();
            } catch (Exception e)
            {
                s_log.LogCritical($"Failed to load configuration: {e.Message}");
                return;
            }

            if (config["skip-initial-scan"] != "True")
                await DiscoverDevices(config);
            else
                s_log.LogInformation("Skipping initial scan");

            var blynk = new BlynkController(config);

            var controllers = Database.AirConditionerManager.LoadAll().Select((m) => new Gree.Controller(m)).ToList();
            blynk.SetDeviceControllers(controllers);

            foreach (var c in controllers)
            {
                c.DeviceStatusChanged += DeviceStatusChanged;
            }

            var deviceUpdateTimer = new Timer(10000)
            {
                Enabled = true,
                AutoReset = true
            };

            deviceUpdateTimer.Elapsed += async (o, e) =>
            {
                s_log.LogDebug("Updating device status");

                foreach (var controller in controllers)
                    await controller.UpdateDeviceStatus();
            };

            while (true)
            {
                await Task.Delay(100);
            }
        }

        private static void DeviceStatusChanged(object sender, Gree.DeviceStatusChangedEventArgs e)
        {
            s_log.LogDebug($"Device ({(sender as Gree.Controller).DeviceName}) changed");
        }

        static async Task DiscoverDevices(IConfiguration config)
        {
            var configEnum = config.AsEnumerable();

            var broadcastAddresses = configEnum.Where((e) => e.Key.StartsWith("network:broadcast:"));
            if (broadcastAddresses.Count() == 0)
            {
                s_log.LogCritical("No network broadcast addresses configured");
                return;
            }

            var foundUnits = new List<Database.AirConditionerModel>();

            foreach (var entry in broadcastAddresses)
            {
                s_log.LogInformation($"Scanning local devices using address {entry.Value}");

                foundUnits.AddRange(await Gree.Scanner.Scan(entry.Value));
            }

            s_log.LogInformation("Updating the database");

            foreach (var unit in foundUnits.Distinct(new Database.AirConditionerModelEqualityComparer()))
            {
                await Database.AirConditionerManager.UpdateAsync(unit);
            }
        }
    }
}
