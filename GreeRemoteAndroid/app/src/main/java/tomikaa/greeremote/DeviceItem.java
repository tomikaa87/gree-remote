package tomikaa.greeremote;

import java.io.Serializable;

import tomikaa.greeremote.Gree.Device.Device;

/**
 * Created by tomikaa on 2017. 10. 22..
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
