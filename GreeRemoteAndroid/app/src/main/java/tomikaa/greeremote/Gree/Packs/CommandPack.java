package tomikaa.greeremote.Gree.Packs;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tomikaa on 2017. 11. 26..
 */

public class CommandPack extends Pack {
    public static String TYPE = "cmd";

    @SerializedName("opt")
    public String[] keys;

    @SerializedName("p")
    public Integer[] values;

    public CommandPack() {
        type = TYPE;
    }
}