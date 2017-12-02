package tomikaa.greeremote.Gree.Packs;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tomikaa on 2017. 11. 26..
 */

public class StatusPack extends Pack {
    public static String TYPE = "status";

    @SerializedName("cols")
    public String[] keys;

    public StatusPack() {
        type = TYPE;
    }
}