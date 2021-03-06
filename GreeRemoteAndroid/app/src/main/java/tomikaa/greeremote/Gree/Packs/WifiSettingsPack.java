package tomikaa.greeremote.Gree.Packs;

import com.google.gson.annotations.SerializedName;

import tomikaa.greeremote.Gree.Packets.Packet;

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

//{"psw": "<wifi pass>","ssid": "<wifi ap>","t": "wlan"}

public class WifiSettingsPack extends Packet {
    public static String TYPE = "wlan";

    @SerializedName("psw")
    public String psw;

    @SerializedName("ssid")
    public String ssid;

    public WifiSettingsPack() {
        type = TYPE;
    }
}