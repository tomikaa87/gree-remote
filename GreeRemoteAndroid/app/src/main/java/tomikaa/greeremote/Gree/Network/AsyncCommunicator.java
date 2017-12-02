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

/**
 * Created by tomikaa on 2017. 11. 26..
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
