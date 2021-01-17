package tomikaa.greeremote.Gree.Device;

import android.util.Log;

import java.util.Map;

import tomikaa.greeremote.Gree.Packs.DatPack;
import tomikaa.greeremote.Gree.Packs.DevicePack;
import tomikaa.greeremote.Gree.Packs.ResultPack;
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
 * Created by tomikaa87 <https://github.com/tomikaa87> on 2017-11-27.
 */

class DeviceImpl implements Device {
    private final String mDeviceId;
    private final DeviceManager mDeviceManager;
    private final String mLogTag;

    public enum Parameter {
        POWER("Pow"),
        MODE("Mod"),
        TEMPERATURE("SetTem"),
        TEMPERATURE_UNIT("TemUn"),
        FAN_SPEED("WdSpd"),
        AIR_MODE("Air"),
        XFAN_MODE("Blo"),
        HEALTH_MODE("Health"),
        SLEEP_MODE("SwhSlp"),
        QUIET_MODE("Quiet"),
        TURBO_MODE("Tur"),
        SAVING_MODE("SvSt"),
        LIGHT("Lig"),
        HORIZONTAL_SWING("SwingLfRig"),
        VERTICAL_SWING("SwUpDn"),
        STHT_MODE("StHt"),
        HEAT_COOL_TYPE("HeatCoolType"),
        TEM_REC_MODE("TemRec");

        private final String mParam;

        Parameter(final String param) {
            mParam = param;
        }

        @Override
        public String toString() {
            return mParam;
        }
    }

    private String mName = "";
    private Mode mMode = Mode.AUTO;
    private FanSpeed mFanSpeed = FanSpeed.AUTO;
    private int mTemperature = 0;
    private TemperatureUnit mTemperatureUnit = TemperatureUnit.CELSIUS;
    private boolean mPoweredOn;
    private boolean mLightEnabled;
    private boolean mQuietModeEnabled;
    private boolean mTurboModeEnabled;
    private boolean mHealthModeEnabled;
    private boolean mAirModeEnabled;
    private boolean mXfanModeEnabled;
    private boolean mSavingModeEnabled;
    private boolean mSleepModeEnabled;
    private VerticalSwingMode mVerticalSwingMode = VerticalSwingMode.DEFAULT;

    public DeviceImpl(String deviceId, DeviceManager deviceManager) {
        mDeviceId = deviceId;
        mDeviceManager = deviceManager;
        mLogTag = String.format("DeviceImpl(%s)", deviceId);

        Log.i(mLogTag, "Created");
    }

    public void updateWithDatPack(DatPack pack) {
        updateParameters(Utils.zip(pack.keys, pack.values));
    }

    public void updateWithResultPack(ResultPack pack) {
        updateParameters(Utils.zip(pack.keys, pack.values));
    }

    public void updateWithDevicePack(DevicePack pack) {
        Log.d(mLogTag, "Updating name: " + pack.name);
        mName = pack.name;
    }

    @Override
    public String getId() {
        return mDeviceId;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void setName(String name) {
    }

    @Override
    public Mode getMode() {
        return mMode;
    }

    @Override
    public void setMode(Mode mode) {
        setParameter(Parameter.MODE, mode.ordinal());
    }

    @Override
    public FanSpeed getFanSpeed() {
        return mFanSpeed;
    }

    @Override
    public void setFanSpeed(FanSpeed fanSpeed) {
        setParameter(Parameter.FAN_SPEED, fanSpeed.ordinal());
    }

    @Override
    public int getTemperature() {
        return mTemperature;
    }

    @Override
    public void setTemperature(int value, TemperatureUnit unit) {
        setParameters(
                new Parameter[]{Parameter.TEMPERATURE, Parameter.TEMPERATURE_UNIT},
                new Integer[]{value, unit.ordinal()}
        );
    }

    @Override
    public boolean isPoweredOn() {
        return mPoweredOn;
    }

    @Override
    public void setPoweredOn(boolean poweredOn) {
        setParameter(Parameter.POWER, poweredOn ? 1 : 0);
    }

    @Override
    public boolean isLightEnabled() {
        return mLightEnabled;
    }

    @Override
    public void setLightEnabled(boolean enabled) {
        setParameter(Parameter.LIGHT, enabled ? 1 : 0);
    }

    @Override
    public boolean isQuietModeEnabled() {
        return mQuietModeEnabled;
    }

    @Override
    public void setQuietModeEnabled(boolean enabled) {
        setParameter(Parameter.QUIET_MODE, enabled ? 1 : 0);
    }

    @Override
    public boolean isTurboModeEnabled() {
        return mTurboModeEnabled;
    }

    @Override
    public void setTurboModeEnabled(boolean enabled) {
        setParameter(Parameter.TURBO_MODE, enabled ? 1 : 0);
    }

    @Override
    public boolean isHealthModeEnabled() {
        return mHealthModeEnabled;
    }

    @Override
    public void setHealthModeEnabled(boolean enabled) {
        setParameter(Parameter.HEALTH_MODE, enabled ? 1 : 0);
    }

    @Override
    public boolean isAirModeEnabled() {
        return mAirModeEnabled;
    }

    @Override
    public void setAirModeEnabled(boolean enabled) {
        setParameter(Parameter.AIR_MODE, enabled ? 1 : 0);
    }

    @Override
    public boolean isXfanModeEnabled() {
        return mXfanModeEnabled;
    }

    @Override
    public void setXfanModeEnabled(boolean enabled) {
        setParameter(Parameter.XFAN_MODE, enabled ? 1 : 0);
    }

    @Override
    public boolean isSavingModeEnabled() {
        return mSavingModeEnabled;
    }

    @Override
    public void setSavingModeEnabled(boolean enabled) {
        setParameter(Parameter.SAVING_MODE, enabled ? 1 : 0);
    }

    @Override
    public boolean isSleepModeEnabled() {
        return mSleepModeEnabled;
    }

    @Override
    public void setSleepModeEnabled(boolean enabled) {
        setParameter(Parameter.SLEEP_MODE, enabled ? 1 : 0);
    }

    @Override
    public VerticalSwingMode getVerticalSwingMode() {
        return mVerticalSwingMode;
    }

    @Override
    public void setVerticalSwingMode(VerticalSwingMode mode) {
        setParameter(Parameter.VERTICAL_SWING, mode.ordinal());
    }

    @Override
    public int getParameter(String name) {
        return 0;
    }

    @Override
    public void setParameter(String name, int value) {
        mDeviceManager.setParameter(this, name, value);
    }

    @Override
    public void setWifiSsidPassword(String ssid, String psw) {
        mDeviceManager.setWifi(ssid, psw);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceImpl device = (DeviceImpl) o;

        return mDeviceId.equals(device.mDeviceId);
    }

    @Override
    public int hashCode() {
        return mDeviceId.hashCode();
    }

    private void setParameter(Parameter parameter, int value) {
        setParameter(parameter.toString(), value);
    }

    private void setParameters(Parameter[] parameters, Integer[] values) {
        String[] names = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            names[i] = parameters[i].toString();
        }

        mDeviceManager.setParameters(this, Utils.zip(names, values));
    }

    private void updateParameters(Map<String, Integer> p) {
        Log.d(mLogTag, "Updating parameters: " + p);

        mMode = getEnumParameter(p, Parameter.MODE, Mode.values(), mMode);
        mFanSpeed = getEnumParameter(p, Parameter.FAN_SPEED, FanSpeed.values(), mFanSpeed);
        mTemperature = getOrdinalParameter(p, Parameter.TEMPERATURE, mTemperature);
        mTemperatureUnit = getEnumParameter(p, Parameter.TEMPERATURE_UNIT, TemperatureUnit.values(), mTemperatureUnit);
        mPoweredOn = getBooleanParameter(p, Parameter.POWER, mPoweredOn);
        mLightEnabled = getBooleanParameter(p, Parameter.LIGHT, mLightEnabled);
        mQuietModeEnabled = getBooleanParameter(p, Parameter.QUIET_MODE, mQuietModeEnabled);
        mTurboModeEnabled = getBooleanParameter(p, Parameter.TURBO_MODE, mTurboModeEnabled);
        mHealthModeEnabled = getBooleanParameter(p, Parameter.HEALTH_MODE, mHealthModeEnabled);
        mAirModeEnabled = getBooleanParameter(p, Parameter.AIR_MODE, mAirModeEnabled);
        mXfanModeEnabled = getBooleanParameter(p, Parameter.XFAN_MODE, mXfanModeEnabled);
        mSavingModeEnabled = getBooleanParameter(p, Parameter.SAVING_MODE, mSavingModeEnabled);
        mSleepModeEnabled = getBooleanParameter(p, Parameter.SLEEP_MODE, mSleepModeEnabled);
        mVerticalSwingMode = getEnumParameter(p, Parameter.VERTICAL_SWING, VerticalSwingMode.values(), mVerticalSwingMode);
    }

    private static <E> E getEnumParameter(Map<String, Integer> m, Parameter p, E[] values, E def) {
        if (m.containsKey(p.toString())) {
            int ordinal = m.get(p.toString());
            if (ordinal >= 0 && ordinal < values.length) {
                return values[ordinal];
            }
        }

        return def;
    }

    private static int getOrdinalParameter(Map<String, Integer> m, Parameter p, int def) {
        if (m.containsKey(p.toString()))
            return m.get(p.toString());
        return def;
    }

    private static boolean getBooleanParameter(Map<String, Integer> m, Parameter p, boolean def) {
        return getOrdinalParameter(m, p, def ? 1 : 0) == 1;
    }
}
