package tomikaa.greeremote;

import android.util.Log;

/**
 * Created by tomikaa on 2017. 10. 23..
 */

public class DeviceManager {
    private static DeviceManager sInstance;

    public static int MODE_AUTO = 0;
    public static int MODE_COOL = 1;
    public static int MODE_DRY = 2;
    public static int MODE_FAN = 3;
    public static int MODE_HEAT = 4;

    public static int MIN_TEMP = 16;
    public static int MAX_TEMP = 30;

    public static DeviceManager getInstance() {
        if (sInstance == null)
            sInstance = new DeviceManager();
        return sInstance;
    }

    public boolean scanDevicesOnLocalNetwork() {
        Log.i("DeviceManager", "starting device scan on local network");

        return true;
    }

    public boolean setMode(String deviceId, int mode) {
        if (mode < MODE_AUTO || mode > MODE_HEAT)
            return false;

        Log.i("DeviceManager", String.format("setting mode of %s to %d", deviceId, mode));

        return true;
    }

    public boolean setTemperature(String deviceId, int temperature) {
        if (temperature < MIN_TEMP || temperature > MAX_TEMP)
            return false;

        Log.i("DeviceManager", String.format("setting temperature of %s to %d", deviceId, temperature));

        return true;
    }

    private DeviceManager() {
        Log.i("DeviceManager", "created");
    }
}
