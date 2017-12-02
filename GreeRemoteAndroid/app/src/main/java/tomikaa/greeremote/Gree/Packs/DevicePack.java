package tomikaa.greeremote.Gree.Packs;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tomikaa on 2017. 11. 26..
 */

public class DevicePack extends Pack {
    public static String TYPE = "dev";

    public String cid;
    public String bc;
    public String brand;
    public String catalog;
    public String mid;
    public String model;
    public String name;
    public String series;
    public String ver;
    public Integer lock;

    @SerializedName("vender")
    public String vendor;

    public DevicePack() {
        type = TYPE;
    }
}
