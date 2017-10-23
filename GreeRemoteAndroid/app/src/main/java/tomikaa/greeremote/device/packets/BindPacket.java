package tomikaa.greeremote.device.packets;

/**
 * Created by tomikaa on 2017. 10. 23..
 */

public class BindPacket {
    String t = "bind";
    String mac;
    int uid = 0;

    public BindPacket(String mac) {
        this.mac = mac;
    }
}
