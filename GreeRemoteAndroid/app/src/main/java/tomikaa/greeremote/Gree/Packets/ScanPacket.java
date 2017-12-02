package tomikaa.greeremote.Gree.Packets;

/**
 * Created by tomikaa on 2017. 11. 26..
 */

public class ScanPacket extends Packet {
    public static String TYPE = "scan";

    public ScanPacket() {
        type = TYPE;
    }
}
