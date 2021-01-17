package tomikaa.greeremote.Gree.Device;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tomikaa.greeremote.Gree.Network.AsyncCommunicationFinishedListener;
import tomikaa.greeremote.Gree.Network.AsyncCommunicator;
import tomikaa.greeremote.Gree.Network.DeviceKeyChain;
import tomikaa.greeremote.Gree.Packets.AppPacket;
import tomikaa.greeremote.Gree.Packets.Packet;
import tomikaa.greeremote.Gree.Packets.ScanPacket;
import tomikaa.greeremote.Gree.Packs.BindOkPack;
import tomikaa.greeremote.Gree.Packs.BindPack;
import tomikaa.greeremote.Gree.Packs.CommandPack;
import tomikaa.greeremote.Gree.Packs.DatPack;
import tomikaa.greeremote.Gree.Packs.DevicePack;
import tomikaa.greeremote.Gree.Packs.ResultPack;
import tomikaa.greeremote.Gree.Packs.StatusPack;
import tomikaa.greeremote.Gree.Packs.WifiSettingsPack;

/*
 * This file is part of GreeRemoteAndroid.
 *
 * GreeRemoteAndroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GreeRemoteAndroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GreeRemoteAndroid. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Created by tomikaa87 <https://github.com/tomikaa87> on 2017-11-27.
 */

public class DeviceManager {
    private final String LOG_TAG = "DeviceManager";
    private final int DATAGRAM_PORT = 7000;

    private static DeviceManager sInstance = null;

    private final HashMap<String, DeviceImpl> mDevices = new HashMap<>();
    private final DeviceKeyChain mKeyChain = new DeviceKeyChain();
    private final ArrayList<DeviceManagerEventListener> mEventListeners = new ArrayList<>();

    public static DeviceManager getInstance() {
        if (sInstance == null)
            sInstance = new DeviceManager();

        return sInstance;
    }

    protected DeviceManager() {
        Log.i(LOG_TAG, "Created");
    }

    public Device[] getDevices() {
        return mDevices.values().toArray(new Device[0]);
    }

    public Device getDevice(String deviceId) {
        if (mDevices.containsKey(deviceId))
            return mDevices.get(deviceId);

        return null;
    }

    public void registerEventListener(DeviceManagerEventListener listener) {
        if (!mEventListeners.contains(listener))
            mEventListeners.add(listener);
    }

    public void unregisterEventListener(DeviceManagerEventListener listener) {
        mEventListeners.remove(listener);
    }

    public void setParameter(Device device, String name, int value) {
        HashMap<String, Integer> p = new HashMap<>();
        p.put(name, value);

        setParameters(device, p);
    }

    public void setParameters(Device device, Map<String, Integer> parameters) {
        Log.d(LOG_TAG, String.format("Setting parameters of %s: %s", device.getId(), parameters));

        AppPacket packet = new AppPacket();
        packet.tcid = device.getId();
        packet.i = 0;

        CommandPack pack = new CommandPack();
        pack.keys = parameters.keySet().toArray(new String[0]);
        pack.values = parameters.values().toArray(new Integer[0]);
        pack.mac = packet.tcid;

        packet.pack = pack;

        final AsyncCommunicator comm = new AsyncCommunicator(mKeyChain);
        comm.setCommunicationFinishedListener(new AsyncCommunicationFinishedListener() {
            @Override
            public void onFinished() {
                try {
                    final Packet[] responses = comm.get();

                    for (Packet response : responses) {
                        if (mDevices.containsKey(response.cid)) {
                            mDevices.get(response.cid).updateWithResultPack((ResultPack) response.pack);
                        }
                    }

                    sendEvent(DeviceManagerEventListener.Event.DEVICE_STATUS_UPDATED);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Failed to get response of command. Error: " + e.getMessage());
                }
            }
        });
        comm.execute(new Packet[] { packet });
    }


    public void setWifi(String ssid, String psw){

        WifiSettingsPack packet = new WifiSettingsPack();
        packet.psw = psw;
        packet.ssid = ssid;

        final AsyncCommunicator comm = new AsyncCommunicator(mKeyChain);
        comm.setCommunicationFinishedListener(new AsyncCommunicationFinishedListener() {
            @Override
            public void onFinished() {
                try {
                    final Packet[] responses = comm.get();

                    for (Packet response : responses) {
                        if (mDevices.containsKey(response.cid)) {
                            mDevices.get(response.cid).updateWithResultPack((ResultPack) response.pack);
                        }
                    }

                    sendEvent(DeviceManagerEventListener.Event.DEVICE_STATUS_UPDATED);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Failed to get response of command. Error: " + e.getMessage());
                }
            }
        });
        comm.execute(new Packet[] { packet });
    }

    public void discoverDevices() {
        Log.i(LOG_TAG, "Device discovery running...");

        final AsyncCommunicator comm = new AsyncCommunicator();
        comm.setCommunicationFinishedListener(new AsyncCommunicationFinishedListener() {
            @Override
            public void onFinished() {
            try {
                Packet[] responses = comm.get();
                Log.i(LOG_TAG, String.format("Got %d response(s)", responses.length));

                bindDevices(responses);
            } catch (Exception e) {

            }
            }
        });

        ScanPacket sp = new ScanPacket();

        comm.execute(new ScanPacket[]{ sp });
    }

    public void updateDevices() {
        if (mDevices.isEmpty()) {
            Log.i(LOG_TAG, "No devices to update");
            return;
        }

        Log.i(LOG_TAG, String.format("Updating %d device(s)", mDevices.size()));

        ArrayList<AppPacket> packets = new ArrayList<>();

        ArrayList<String> keys = new ArrayList<>();
        for (DeviceImpl.Parameter p : DeviceImpl.Parameter.values()) {
            keys.add(p.toString());
        }

        for (DeviceImpl device : mDevices.values()) {
            AppPacket packet = new AppPacket();
            packet.tcid = device.getId();
            packet.i = 0;

            StatusPack pack = new StatusPack();
            pack.keys = keys.toArray(new String[0]);
            pack.mac = device.getId();
            packet.pack = pack;

            packets.add(packet);
        }

        final AsyncCommunicator comm = new AsyncCommunicator(mKeyChain);
        comm.setCommunicationFinishedListener(new AsyncCommunicationFinishedListener() {
            @Override
            public void onFinished() {
                try {
                    Packet[] responses = comm.get();

                    for (Packet response : responses) {
                        if (mDevices.containsKey(response.cid)) {
                            mDevices.get(response.cid).updateWithDatPack((DatPack) response.pack);
                        }
                    }

                    sendEvent(DeviceManagerEventListener.Event.DEVICE_STATUS_UPDATED);

                } catch (Exception e) {
                    Log.w(LOG_TAG, "Failed to get device update result. Error: " + e.getMessage());
                }
            }
        });
        comm.execute(packets.toArray(new Packet[0]));
    }

    private void bindDevices(Packet[] scanResponses) throws IOException {
        ArrayList<AppPacket> requests = new ArrayList<>();

        for (int i = 0; i < scanResponses.length; i++) {
            Packet response = scanResponses[i];

            if (!(response.pack instanceof DevicePack))
                continue;

            DevicePack devicePack = (DevicePack) response.pack;

            DeviceImpl device;

            if (!mDevices.containsKey(response.cid)) {
                device = new DeviceImpl(devicePack.mac, this);
                mDevices.put(devicePack.mac, device);
            } else {
                device = mDevices.get(response.cid);
            }
            device.updateWithDevicePack(devicePack);

            if (!mKeyChain.containsKey(response.cid)) {
                Log.i(LOG_TAG, "Binding device: " + devicePack.name);

                AppPacket request = new AppPacket();
                request.tcid = response.cid;
                request.pack = new BindPack();
                request.pack.mac = request.tcid;
                request.i = 1;

                requests.add(request);
            }
        }

        final AsyncCommunicator comm = new AsyncCommunicator(mKeyChain);
        comm.setCommunicationFinishedListener(new AsyncCommunicationFinishedListener() {
            @Override
            public void onFinished() {
            try {
                Packet[] responses = comm.get();
                storeDevices(responses);
            } catch (Exception e) {

            }
            }
        });
        comm.execute(requests.toArray(new Packet[0]));
    }

    private void storeDevices(Packet[] bindResponses) {
        for (Packet response : bindResponses) {
            if (!(response.pack instanceof BindOkPack))
                continue;

            BindOkPack pack = (BindOkPack) response.pack;

            Log.i(LOG_TAG, "Storing key for device: " + pack.mac);
            mKeyChain.addKey(pack.mac, pack.key);
        }

        sendEvent(DeviceManagerEventListener.Event.DEVICE_LIST_UPDATED);
        updateDevices();
    }

    private void sendEvent(DeviceManagerEventListener.Event event) {
        for (DeviceManagerEventListener listener : mEventListeners)
            listener.onEvent(event);
    }
}
