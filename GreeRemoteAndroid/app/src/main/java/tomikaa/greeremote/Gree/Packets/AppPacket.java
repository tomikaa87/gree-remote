package tomikaa.greeremote.Gree.Packets;

/**
 * Created by tomikaa on 2017. 11. 26..
 */

public class AppPacket extends Packet {
    public static String CID = "app";
    public static String TYPE = "pack";

    public AppPacket() {
        cid = CID;
        type = TYPE;
        uid = 0;
    }
}
