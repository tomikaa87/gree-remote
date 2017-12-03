package tomikaa.greeremote.Gree.Network;

import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

import tomikaa.greeremote.Gree.Packets.AppPacket;
import tomikaa.greeremote.Gree.Packets.Packet;
import tomikaa.greeremote.Gree.Utils;

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
 * Created by tomikaa87 <https://github.com/tomikaa87> on 2017-11-26.
 */

public class AsyncCommunicator extends AsyncTask<Packet[], Void, Packet[]> {
    private final String LOG_TAG = "AsyncCommunicator";
    private final int DATAGRAM_PORT = 7000;
    private final int TIMEOUT_MS = 500;

    private AsyncCommunicationFinishedListener mCommunicationFinishedListener;
    private DatagramSocket mSocket;
    private final DeviceKeyChain mKeyChain;

    public void setCommunicationFinishedListener(AsyncCommunicationFinishedListener listener) {
        mCommunicationFinishedListener = listener;
    }

    public AsyncCommunicator(DeviceKeyChain deviceKeyChain) {
        mKeyChain = deviceKeyChain;
    }

    public AsyncCommunicator() {
        mKeyChain = null;
    }

    @Override
    protected Packet[] doInBackground(Packet[]... args) {
        Packet[] requests = args[0];
        Packet[] responses = new Packet[0];

        if (requests == null || requests.length == 0)
            return responses;

        if (!createSocket())
            return responses;

        try {
            for (Packet request : requests)
                broadcastPacket(request);
            responses = receivePackets(TIMEOUT_MS);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error: " + e.getMessage());
        } finally {
            closeSocket();
        }

        return responses;
    }

    @Override
    protected void onPostExecute(Packet[] responses) {
        super.onPostExecute(responses);

        if (mCommunicationFinishedListener != null)
            mCommunicationFinishedListener.onFinished();
    }

    private void broadcastPacket(Packet packet) throws IOException {
        String data = Utils.serializePacket(packet, mKeyChain);

        Log.d(LOG_TAG, "Broadcasting: " + data);

        DatagramPacket datagramPacket = new DatagramPacket(
                data.getBytes(), data.length(),
                InetAddress.getByName("255.255.255.255"), DATAGRAM_PORT);

        mSocket.send(datagramPacket);
    }

    private Packet[] receivePackets(int timeout) throws IOException {
        mSocket.setSoTimeout(timeout);

        ArrayList<Packet> responses = new ArrayList<>();
        ArrayList<DatagramPacket> datagramPackets = new ArrayList<>();

        try {
            while (true) {
                byte[] buffer = new byte[65536];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, 65536);

                mSocket.receive(datagramPacket);

                datagramPackets.add(datagramPacket);
            }
        } catch (Exception e) {
            Log.w(LOG_TAG, "Exception: " + e.getMessage());
        }

        for (DatagramPacket p : datagramPackets) {
            String data = new String(p.getData(), 0, p.getLength());
            InetAddress address = p.getAddress();

            Log.d(LOG_TAG, String.format("Received response from %s: %s", address.getHostAddress(), data));

            Packet response = Utils.deserializePacket(data, mKeyChain);

            // Filter out packets sent by us
            if (response.cid != null && response.cid != AppPacket.CID)
                responses.add(response);
        }

        return responses.toArray(new Packet[0]);
    }

    private boolean createSocket() {
        try {
            mSocket = new DatagramSocket(new InetSocketAddress(DATAGRAM_PORT));
        } catch (SocketException e) {
            Log.e(LOG_TAG, "Failed to create socket. Error: " + e.getMessage());
            return false;
        }

        return true;
    }

    private void closeSocket() {
        Log.i(LOG_TAG, "Closing socket");

        mSocket.close();
        mSocket = null;
    }
}
