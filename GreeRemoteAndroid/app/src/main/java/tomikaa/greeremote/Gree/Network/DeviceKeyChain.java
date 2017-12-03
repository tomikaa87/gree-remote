package tomikaa.greeremote.Gree.Network;

import java.util.HashMap;

/*
 * This file is part of GreeRemoteAndroid.
 *
 * GreeRemoteAndroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GreeRemoteAndroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GreeRemoteAndroid. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Created by tomikaa87 <https://github.com/tomikaa87> on 2017-12-01.
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
