package tomikaa.greeremote.Gree.Device;

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

public interface Device {

    enum Mode {
        AUTO,
        COOL,
        DRY,
        FAN,
        HEAT
    }

    enum FanSpeed {
        AUTO,
        LOW,
        MEDIUM_LOW,
        MEDIUM,
        MEDIUM_HIGH,
        HIGH
    }

    enum TemperatureUnit {
        CELSIUS,
        FAHRENHEIT
    }

    enum VerticalSwingMode {
        DEFAULT,
        FULL,
        FIXED_TOP,
        FIXED_MIDDLE_TOP,
        FIXED_MIDDLE,
        FIXED_MIDDLE_BOTTOM,
        FIXED_BOTTOM,
        SWING_BOTTOM,
        SWING_MIDDLE_BOTTOM,
        SWING_MIDDLE,
        SWING_MIDDLE_TOP,
        SWING_TOP
    }

    String getId();

    String getName();

    void setName(String name);

    Mode getMode();

    void setMode(Mode mode);

    FanSpeed getFanSpeed();

    void setFanSpeed(FanSpeed fanSpeed);

    int getTemperature();

    void setTemperature(int value, TemperatureUnit unit);

    boolean isPoweredOn();

    void setPoweredOn(boolean poweredOn);

    boolean isLightEnabled();

    void setLightEnabled(boolean enabled);

    boolean isQuietModeEnabled();

    void setQuietModeEnabled(boolean enabled);

    boolean isTurboModeEnabled();

    void setTurboModeEnabled(boolean enabled);

    boolean isHealthModeEnabled();

    void setHealthModeEnabled(boolean enabled);

    boolean isAirModeEnabled();

    void setAirModeEnabled(boolean enabled);

    boolean isXfanModeEnabled();

    void setXfanModeEnabled(boolean enabled);

    boolean isSavingModeEnabled();

    void setSavingModeEnabled(boolean enabled);

    boolean isSleepModeEnabled();

    void setSleepModeEnabled(boolean enabled);

    VerticalSwingMode getVerticalSwingMode();

    void setVerticalSwingMode(VerticalSwingMode mode);

    int getParameter(String name);

    void setParameter(String name, int value);

    void setWifiSsidPassword(String ssid, String psw);
}
