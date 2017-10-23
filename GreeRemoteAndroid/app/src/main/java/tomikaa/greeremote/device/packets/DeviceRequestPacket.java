package tomikaa.greeremote.device.packets;

/**
 * Created by tomikaa on 2017. 10. 23..
 */

public class DeviceRequestPacket {
    String t = "pack";
    String cid = "app";
    int i;
    int uid = 0;
    String pack;

    public DeviceRequestPacket(String encPackBase64, int i) {
        this.i = i;
        this.pack = encPackBase64;
    }
}
