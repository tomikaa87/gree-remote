package tomikaa.greeremote.device;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import tomikaa.greeremote.AppConfig;
import tomikaa.greeremote.device.packets.DeviceDetailsPacket;
import tomikaa.greeremote.device.packets.DeviceResponsePacket;

/**
 * Created by tomikaa on 2017. 10. 23..
 */

public class DeviceManager {
    private static DeviceManager sInstance;

    public static final int MODE_AUTO = 0;
    public static final int MODE_COOL = 1;
    public static final int MODE_DRY = 2;
    public static final int MODE_FAN = 3;
    public static final int MODE_HEAT = 4;

    public static final int MIN_TEMP = 16;
    public static final int MAX_TEMP = 30;

    public static void createInstance(Context context) {
        Log.d(LOG_TAG, "creating instance");
        sInstance = new DeviceManager(context);
    }

    public static DeviceManager getInstance() {
        return sInstance;
    }

    private DeviceManager(Context context) {
        Log.i(LOG_TAG, "created");

        mContext = context;
        mTimer = new Timer();

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                onTimerTimeout();
            }
        };
    }

    public boolean scanDevicesOnLocalNetwork() {
        if (mState != State.IDLE) {
            Log.w(LOG_TAG, "Cannot initiate scan while other operation is in progress");
            return false;
        }

        //mState = State.SCANNING;

        Log.i(LOG_TAG, "starting device scan on local network");

        if (mSocket == null) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            int networkType = networkInfo.getType();

            if (!AppConfig.DEBUG) {
                if (networkType != ConnectivityManager.TYPE_WIFI && networkType != ConnectivityManager.TYPE_ETHERNET) {
                    Log.w(LOG_TAG, "scanning can only be used on local network");
                    mState = State.IDLE;
                    return false;
                }
            }

            Log.d(LOG_TAG, String.format("creating DatagramSocket on port %d", DATAGRAM_PORT));

            try {
                mSocket = new DatagramSocket(new InetSocketAddress(DATAGRAM_PORT));
            } catch (SocketException e) {
                Log.e(LOG_TAG, "failed to create socket. Error: " + e.getMessage());
                mState = State.IDLE;
                return false;
            }
        }

        new AsyncScanner().execute(mSocket);

        //mTimer.schedule(mTimerTask, 500, 500);

        Log.d(LOG_TAG, "local host address: " + mSocket.getLocalSocketAddress().toString());

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
    private Timer mTimer;
    private TimerTask mTimerTask;

    private void onTimerTimeout() {
        Log.d(LOG_TAG, "timer tick");

        if (mSocket == null) {
            Log.w(LOG_TAG, "socket is null, canceling timer task");
            mTimerTask.cancel();
            return;
        }

//        mSocket.receive();
    }

    private class AsyncScanner extends AsyncTask<DatagramSocket, Void, DeviceDetailsPacket[]> {
        private static final String LOG_TAG = "AsyncScanner";
        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected DeviceDetailsPacket[] doInBackground(DatagramSocket... params) {
            DatagramSocket socket = params[0];
            ArrayList<DeviceResponsePacket> responses = new ArrayList<>();

            try {
                Log.d(LOG_TAG, "sending scan packet");
                String scanPacketContent = ProtocolUtils.createScanPacket();
                DatagramPacket scanPacket = new DatagramPacket(
                        scanPacketContent.getBytes(),
                        scanPacketContent.length(),
                        InetAddress.getByName("192.168.1.255"),
                        7000);
                socket.send(scanPacket);

                Log.d(LOG_TAG, "setting receive timeout");
                socket.setSoTimeout(5000);

                while (true) {

                    Log.d(LOG_TAG, "waiting for response");
                    byte[] buf = new byte[65536];
                    DatagramPacket packet = new DatagramPacket(buf, 65536);
                    socket.receive(packet);

                    Log.d(LOG_TAG, "response received");
                    InetAddress address = packet.getAddress();
                    byte[] data = packet.getData();
                    String json = new String(data, 0, packet.getLength());
                    Log.d(LOG_TAG, String.format("response from %s: %s", address.getHostAddress(), json));

                    Gson gson = new Gson();
                    responses.add(gson.fromJson(json, DeviceResponsePacket.class));
                }
            } catch (SocketTimeoutException e) {
                Log.d(LOG_TAG, "socket timeout");
            } catch (SocketException e) {
                Log.w(LOG_TAG, "socket error: " + e.getMessage());
                return null;
            } catch (IOException e) {
                Log.w(LOG_TAG, "io error: " + e.getMessage());
                return null;
            }

            Log.d(LOG_TAG, String.format("got %d response(s)", responses.size()));

            ArrayList<DeviceDetailsPacket> deviceDetails = new ArrayList<>();

            for (DeviceResponsePacket packet: responses) {
                if (packet.pack == null)
                    continue;

                String packJson = ProtocolUtils.decryptPack(packet.pack, ProtocolUtils.GENERIC_AES_KEY);
                Log.d(LOG_TAG, "packJson: " + packJson);

                Gson gson = new Gson();
                DeviceDetailsPacket details = gson.fromJson(packJson, DeviceDetailsPacket.class);
                if ("gree".equalsIgnoreCase(details.brand))
                    deviceDetails.add(details);
            }

            Log.d(LOG_TAG, String.format("got %d device(s)", deviceDetails.size()));
            Log.d(LOG_TAG, "finished");

            return deviceDetails.toArray(new DeviceDetailsPacket[0]);
        }

        @Override
        public void onPostExecute(DeviceDetailsPacket[] result) {
            super.onPostExecute(result);
            // TODO start binding
        }
    }
}
