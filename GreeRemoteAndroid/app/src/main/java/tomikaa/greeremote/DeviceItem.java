package tomikaa.greeremote;

import java.io.Serializable;

/**
 * Created by tomikaa on 2017. 10. 22..
 */

public class DeviceItem implements Serializable {
    public String mId = "ID";
    public String mName = "Name";
    public Mode mMode = Mode.AUTO;
    public int mTemperature = 16;
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

    public enum Mode {
        AUTO,
        COOL,
        DRY,
        FAN,
        HEAT
    }
}
