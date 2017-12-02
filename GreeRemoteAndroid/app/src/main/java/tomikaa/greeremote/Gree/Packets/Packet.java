package tomikaa.greeremote.Gree.Packets;

import tomikaa.greeremote.Gree.Packs.Pack;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tomikaa on 2017. 11. 26..
 */

public class Packet {
    @SerializedName("t")
    public String type;

    public String tcid;
    public Integer i;
    public Integer uid;
    public String cid;

    @SerializedName("pack")
    public String encryptedPack;

    public transient Pack pack;
}
