package tomikaa.greeremote.Gree.Network;

import java.util.HashMap;

/**
 * Created by tomikaa on 2017. 12. 01..
 */

public class DeviceKeyChain {
    private final HashMap<String, String> mKeys = new HashMap<>();

    public void addKey(String deviceId, String key) {
        mKeys.put(deviceId.toLowerCase(), key);
    }

    public String getKey(String deviceId) {
        if (deviceId == null)
            return null;

        String id = deviceId.toLowerCase();

        if (!mKeys.containsKey(id))
            return null;

        return mKeys.get(id);
    }

    public boolean containsKey(String deviceId) {
        if (deviceId == null)
            return false;
        
        return mKeys.containsKey(deviceId.toLowerCase());
    }
}
