package tomikaa.greeremote.device;

/**
 * Created by tomikaa on 2017. 10. 27..
 */

public class DeviceDescriptor {
    public final String id;
    public final String name;
    public final String key;

    DeviceDescriptor(String id, String name, String key) {
        this.id = id;
        this.name = name;
        this.key = key;
    }
}
