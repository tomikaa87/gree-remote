package tomikaa.greeremote.Gree.Packs;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tomikaa on 2017. 11. 26..
 */

public class BindOkPack extends Pack {
    public static String TYPE = "bindok";

    public String key;

    @SerializedName("r")
    public int resultCode;

    public BindOkPack() {
        type = TYPE;
    }
}
