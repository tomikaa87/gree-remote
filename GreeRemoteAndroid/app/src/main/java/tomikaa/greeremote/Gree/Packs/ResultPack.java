package tomikaa.greeremote.Gree.Packs;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tomikaa on 2017. 11. 26..
 */

public class ResultPack extends Pack {
    public static String TYPE = "res";

    @SerializedName("r")
    public int resultCode;

    @SerializedName("opt")
    public String[] keys;

    @SerializedName("p")
    public Integer[] values;

    public ResultPack() {
        type = TYPE;
    }
}
