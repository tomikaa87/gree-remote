package tomikaa.greeremote.Gree.Device;

/**
 * Created by tomikaa on 2017. 12. 02..
 */

public interface DeviceManagerEventListener {
    enum Event {
        DEVICE_LIST_UPDATED,
        DEVICE_STATUS_UPDATED
    }

    void onEvent(Event event);
}
