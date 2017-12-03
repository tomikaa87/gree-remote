package tomikaa.greeremote.Gree;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import tomikaa.greeremote.Gree.Deserializers.PackDeserializer;
import tomikaa.greeremote.Gree.Network.DeviceKeyChain;
import tomikaa.greeremote.Gree.Packets.Packet;
import tomikaa.greeremote.Gree.Packs.DatPack;
import tomikaa.greeremote.Gree.Packs.Pack;

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

public class Utils {

    public static class Unzipped {
        public final String[] keys;
        public final Integer[] values;

        public Unzipped(String[] keys, Integer[] values) {
            this.keys = keys;
            this.values = values;
        }
    }

    public static Map<String, Integer> zip(String[] keys, Integer[] values) throws IllegalArgumentException {
        if (keys.length != values.length)
            throw new IllegalArgumentException("Length of keys and values must match");

        Map<String, Integer> zipped = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            zipped.put(keys[i], values[i]);
        }

        return zipped;
    }

    public static Unzipped unzip(Map<String, Integer> map) {
        return new Unzipped(map.keySet().toArray(new String[0]), map.values().toArray(new Integer[0]));
    }

    public static Map<String, Integer> getValues(DatPack pack) {
        return zip(pack.keys, pack.values);
    }

    public static String serializePacket(Packet packet, DeviceKeyChain deviceKeyChain) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        if (packet.pack != null) {
            String key = getKey(deviceKeyChain, packet);
            String plainPack = gson.toJson(packet.pack);
            packet.encryptedPack = tomikaa.greeremote.Gree.Crypto.encrypt(plainPack, key);
        }

        return gson.toJson(packet);
    }

    public static Packet deserializePacket(String jsonString, DeviceKeyChain deviceKeyChain) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Pack.class, new PackDeserializer());

        Gson gson = gsonBuilder.create();

        Packet packet = gson.fromJson(jsonString, Packet.class);

        if (packet.encryptedPack != null) {
            String key = getKey(deviceKeyChain, packet);
            String plainPack = Crypto.decrypt(packet.encryptedPack, key);
            packet.pack = gson.fromJson(plainPack, Pack.class);
        }

        return packet;
    }

    private static String getKey(DeviceKeyChain keyChain, Packet packet) {
        String key = Crypto.GENERIC_KEY;

        Log.i("getKey", String.format("packet.cid: %s, packet.tcid: %s", packet.cid, packet.tcid));

        if (keyChain != null) {
            if (keyChain.containsKey(packet.cid)) {
                key = keyChain.getKey(packet.cid);
            } else if (keyChain.containsKey(packet.tcid)) {
                key = keyChain.getKey(packet.tcid);
            }
        }

        return key;
    }
}