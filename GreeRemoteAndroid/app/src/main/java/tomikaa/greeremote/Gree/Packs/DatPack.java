package tomikaa.greeremote.Gree.Packs;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tomikaa on 2017. 11. 26..
 */

public class DatPack extends Pack {
    public static String TYPE = "dat";

    @SerializedName("r")
    public int resultCode;

    @SerializedName("cols")
    public String[] keys;

    @SerializedName("dat")
    public Integer[] values;

    public DatPack() {
        type = TYPE;
    }
}