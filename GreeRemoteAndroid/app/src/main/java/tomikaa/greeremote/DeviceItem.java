package tomikaa.greeremote;

import java.io.Serializable;

import tomikaa.greeremote.Gree.Device.Device;

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
 * Created by tomikaa87 <https://github.com/tomikaa87> on 2017-10-22.
 */

public class DeviceItem implements Serializable {

    public String mId = "ID";
    public String mName = "Name";
    public Device.Mode mMode = Device.Mode.AUTO;
    public int mTemperature = 0;
    public RoomType mRoomType = RoomType.NONE;

    public enum RoomType {
        NONE,
        LIVING_ROOM,
        BEDROOM,
        KITCHEN,
        DINING_ROOM,
        BATHROOM,
        OFFICE
    }

    public DeviceItem() {}

    public DeviceItem(Device device) {
        updateWithDevice(device);
    }

    public void updateWithDevice(Device device) {
        mId = device.getId();
        mName = device.getName();
        mMode = device.getMode();
        mTemperature = device.getTemperature();
    }
}
