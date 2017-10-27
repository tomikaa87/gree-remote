package tomikaa.greeremote.device;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutionException;

import tomikaa.greeremote.device.packets.BindResponsePacket;
import tomikaa.greeremote.device.packets.DeviceDetailsPacket;

/**
 * Created by tomikaa on 2017. 10. 23..
 */

public class DeviceManager {
    public static final int MODE_AUTO = 0;
    public static final int MODE_COOL = 1;
    public static final int MODE_DRY = 2;
    public static final int MODE_FAN = 3;
    public static final int MODE_HEAT = 4;

    public static final int MIN_TEMP = 16;
    public static final int MAX_TEMP = 30;

    private static DeviceManager sInstance;

    private enum State {
        IDLE,
        SCANNING,
        BINDING,
        REQUESTING
    }

    private State mState = State.IDLE;
    private DatagramSocket mSocket;
    private static final int DATAGRAM_PORT = 7000;
    private Context mContext;
    private static final String LOG_TAG = "DeviceManager";
    private final DeviceDatabase mDeviceStorage;


    public static void createInstance(Context context) {
        Log.d(LOG_TAG, "creating instance");
        sInstance = new DeviceManager(context);
    }

    public static DeviceManager getInstance() {
        return sInstance;
    }


    public boolean scanDevicesOnLocalNetwork() {
        if (mState != State.IDLE) {
            Log.w(LOG_TAG, "Cannot initiate scan while other operation is in progress");
            return false;
        }

        mState = State.SCANNING;

        Log.i(LOG_TAG, "starting device scan on local network");

        if (mSocket == null) {
            if (!createSocket()) {
                mState = State.IDLE;
                return false;
            }
        }

        runScan();

        return true;
    }

    public boolean setMode(String deviceId, int mode) {
        if (mode < MODE_AUTO || mode > MODE_HEAT)
            return false;

        Log.i(LOG_TAG, String.format("setting mode of %s to %d", deviceId, mode));

        return true;
    }

    public boolean setTemperature(String deviceId, int temperature) {
        if (temperature < MIN_TEMP || temperature > MAX_TEMP)
            return false;

        Log.i(LOG_TAG, String.format("setting temperature of %s to %d", deviceId, temperature));

        return true;
    }


    private void runScan() {
        final AsyncScanner scanner = new AsyncScanner(mSocket);
        scanner.setFinishedListener(new AsyncOperationFinishedListener() {
            @Override
            public void onFinished() {
                Log.i(LOG_TAG, "scanning finished");

                DeviceDetailsPacket[] devices = null;

                try {
                    devices = scanner.get();
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, "failed to get scan result. Error: " + e.getMessage());
                } catch (ExecutionException e) {
                    Log.e(LOG_TAG, "failed to get scan result. Error: " + e.getMessage());
                }

                if (devices != null) {
                    Log.i(LOG_TAG, String.format("scan got %d device(s)", devices.length));
                }

                mState = State.IDLE;

                if (devices != null)
                    runBind(devices);
            }
        });
        scanner.execute();
    }

    private void runBind(DeviceDetailsPacket[] devices) {
        mState = State.BINDING;

        final AsyncBinder binder = new AsyncBinder(mSocket);
        binder.setFinishedListener(new AsyncOperationFinishedListener() {
            @Override
            public void onFinished() {
                Log.i(LOG_TAG, "binding finished");

                BindResponsePacket[] responses = null;

                try {
                    responses = binder.get();
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, "failed to get scan result. Error: " + e.getMessage());
                } catch (ExecutionException e) {
                    Log.e(LOG_TAG, "failed to get scan result. Error: " + e.getMessage());
                }

                if (responses != null) {
                    Log.i(LOG_TAG, String.format("bound %d device(s)", responses.length));


                    for (BindResponsePacket packet: responses) {
                        mDeviceStorage.saveDevice(packet.mac, "device", packet.key);
                    }
                }

                mState = State.IDLE;
            }
        });
        binder.execute(devices);
    }

    private DeviceManager(Context context) {
        Log.i(LOG_TAG, "created");

        mContext = context;
        mDeviceStorage = new DeviceDatabase(mContext);

        mDeviceStorage.loadDevices();
    }

    private boolean createSocket() {
        Log.d(LOG_TAG, "creating datagram socket");

        ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        int networkType = networkInfo.getType();

        if (networkType != ConnectivityManager.TYPE_WIFI && networkType != ConnectivityManager.TYPE_ETHERNET) {
            Log.w(LOG_TAG, "scanning can only be used on local network");
            return false;
        }

        Log.d(LOG_TAG, String.format("creating DatagramSocket on port %d", DATAGRAM_PORT));

        try {
            mSocket = new DatagramSocket(new InetSocketAddress(DATAGRAM_PORT));
        } catch (SocketException e) {
            Log.e(LOG_TAG, "failed to create socket. Error: " + e.getMessage());
            return false;
        }

        return true;
    }
}
