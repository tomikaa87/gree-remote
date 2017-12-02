package tomikaa.greeremote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import tomikaa.greeremote.Gree.Device.Device;
import tomikaa.greeremote.Gree.Device.DeviceManager;
import tomikaa.greeremote.Gree.Device.DeviceManagerEventListener;

public class DeviceActivity extends AppCompatActivity {
    private DeviceItem mDeviceItem;
    private TextView mTemperatureTextView;
    private Device mDevice;
    private DeviceManagerEventListener mDeviceManagerEventListener;

    public static String EXTRA_FEATURE_HELP = "tomikaa.greeremote.FEATURE_HELP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        mTemperatureTextView = (TextView) findViewById(R.id.temperatureTextView);

        Intent intent = getIntent();
        mDeviceItem = (DeviceItem) intent.getSerializableExtra(MainActivity.EXTRA_DEVICE_ITEM);

        setTitle(mDeviceItem.mName);

        mDevice = DeviceManager.getInstance().getDevice(mDeviceItem.mId);

        mDeviceManagerEventListener = new DeviceManagerEventListener() {
            @Override
            public void onEvent(Event event) {
                if (event == Event.DEVICE_STATUS_UPDATED)
                    update();
            }
        };

        DeviceManager.getInstance().registerEventListener(mDeviceManagerEventListener);

        update();
        setupFanSpeedSeekBarChangeListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DeviceManager.getInstance().unregisterEventListener(mDeviceManagerEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device, menu);
        return true;
    }

    public void update() {
        mTemperatureTextView.setText(String.format("%d", mDevice.getTemperature()));

        int activeColor = ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null);
        int inactiveColor = ResourcesCompat.getColor(getResources(), R.color.colorSecondaryText, null);

        final Device.Mode mode = mDevice.getMode();

        setImageButtonColorFilter(R.id.autoModeButton, mode == Device.Mode.AUTO ? activeColor : inactiveColor);
        setImageButtonColorFilter(R.id.coolModeButton, mode == Device.Mode.COOL ? activeColor : inactiveColor);
        setImageButtonColorFilter(R.id.dryModeButton, mode == Device.Mode.DRY ? activeColor : inactiveColor);
        setImageButtonColorFilter(R.id.fanModeButton, mode == Device.Mode.FAN ? activeColor : inactiveColor);
        setImageButtonColorFilter(R.id.heatModeButton, mode == Device.Mode.HEAT ? activeColor : inactiveColor);
        setImageButtonColorFilter(R.id.powerButton, mDevice.isPoweredOn() ? activeColor : inactiveColor);

        setSwitchChecked(R.id.airSwitch, mDevice.isAirModeEnabled());
        setSwitchChecked(R.id.healthSwitch, mDevice.isHealthModeEnabled());
        setSwitchChecked(R.id.xfanSwitch, mDevice.isXfanModeEnabled());
        setSwitchChecked(R.id.sleepSwitch, mDevice.isSleepModeEnabled());
        setSwitchChecked(R.id.quietSwitch, mDevice.isQuietModeEnabled());
        setSwitchChecked(R.id.turboSwitch, mDevice.isTurboModeEnabled());
        setSwitchChecked(R.id.energySavingSwitch, mDevice.isSavingModeEnabled());
        setSwitchChecked(R.id.lightSwitch, mDevice.isLightEnabled());

        ((SeekBar) findViewById(R.id.fanSpeedSeekBar)).setProgress(mDevice.getFanSpeed().ordinal());
    }

    public void onAirHelpButtonClicked(View view) {
        startHelpActivity(DeviceHelpActivity.Feature.AIR);
    }

    public void onHealthHelpButtonClicked(View view) {
        startHelpActivity(DeviceHelpActivity.Feature.HEALTH);
    }

    public void onDryHelpButtonClicked(View view) {
        startHelpActivity(DeviceHelpActivity.Feature.DRY);
    }

    public void onSleepHelpButtonClicked(View view) {
        startHelpActivity(DeviceHelpActivity.Feature.SLEEP);
    }

    public void onQuietHelpButtonClicked(View view) {
        startHelpActivity(DeviceHelpActivity.Feature.QUIET);
    }

    public void onTurboHelpButtonClicked(View view) {
        startHelpActivity(DeviceHelpActivity.Feature.TURBO);
    }

    public void onSavingHelpButtonClicked(View view) {
        startHelpActivity(DeviceHelpActivity.Feature.SAVING);
    }

    public void onLightHelpButtonClicked(View view) {
        startHelpActivity(DeviceHelpActivity.Feature.LIGHT);
    }

    public void onAutoModeButtonClicked(View view) {
        mDevice.setMode(Device.Mode.AUTO);
    }

    public void onCoolModeButtonClicked(View view) {
        mDevice.setMode(Device.Mode.COOL);
    }

    public void onDryModeButtonClicked(View view) {
        mDevice.setMode(Device.Mode.DRY);
    }

    public void onFanModeButtonClicked(View view) {
        mDevice.setMode(Device.Mode.FAN);
    }

    public void onHeatModeButtonClicked(View view) {
        mDevice.setMode(Device.Mode.HEAT);
    }

    public void onPlusButtonClicked(View view) {
        mDevice.setTemperature(mDevice.getTemperature() + 1, Device.TemperatureUnit.CELSIUS);
    }

    public void onMinusButtonClicked(View view) {
        mDevice.setTemperature(mDevice.getTemperature() - 1, Device.TemperatureUnit.CELSIUS);
    }

    public void onPowerButtonClicked(View view) {
        mDevice.setPoweredOn(!mDevice.isPoweredOn());
    }

    public void onAirSwitchClicked(View view) {
        mDevice.setAirModeEnabled(isSwitchChecked(R.id.airSwitch));
    }

    public void onHealthSwitchClicked(View view) {
        mDevice.setHealthModeEnabled(isSwitchChecked(R.id.healthSwitch));
    }

    public void onXfanSwitchClicked(View view) {
        mDevice.setXfanModeEnabled(isSwitchChecked(R.id.xfanSwitch));
    }

    public void onSleepSwitchClicked(View view) {
        mDevice.setSleepModeEnabled(isSwitchChecked(R.id.sleepSwitch));
    }

    public void onQuietSwitchClicked(View view) {
        mDevice.setQuietModeEnabled(isSwitchChecked(R.id.quietSwitch));
    }

    public void onTurboSwitchClicked(View view) {
        mDevice.setTurboModeEnabled(isSwitchChecked(R.id.turboSwitch));
    }

    public void onSavingSwitchClicked(View view) {
        mDevice.setSavingModeEnabled(isSwitchChecked(R.id.energySavingSwitch));
    }

    public void onLightSwitchClicked(View view) {
        mDevice.setLightEnabled(isSwitchChecked(R.id.lightSwitch));
    }

    private void startHelpActivity(DeviceHelpActivity.Feature feature) {
        Intent intent = new Intent(this, DeviceHelpActivity.class);
        intent.putExtra(EXTRA_FEATURE_HELP, feature);
        startActivity(intent);
    }

    private void setImageButtonColorFilter(int id, int color) {
        View view = findViewById(id);
        if (view instanceof ImageButton) {
            ((ImageButton) view).setColorFilter(color);
        }
    }

    private void setSwitchChecked(int id, boolean checked) {
        View view = findViewById(id);
        if (view instanceof Switch) {
            ((Switch) view).setChecked(checked);
        }
    }

    private boolean isSwitchChecked(int id) {
        View view = findViewById(id);
        if (view instanceof Switch) {
            return ((Switch) view).isChecked();
        }

        return false;
    }

    private void setupFanSpeedSeekBarChangeListener() {
        ((SeekBar) findViewById(R.id.fanSpeedSeekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // FIXME: int might be used for fan speed instead of enum
                mDevice.setFanSpeed(Device.FanSpeed.values()[seekBar.getProgress()]);
            }
        });
    }
}
