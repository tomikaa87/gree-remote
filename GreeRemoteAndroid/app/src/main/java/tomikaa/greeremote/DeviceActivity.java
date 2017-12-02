package tomikaa.greeremote;

import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
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

        ImageButton autoModeButton = (ImageButton) findViewById(R.id.autoModeButton);
        ImageButton coolModeButton = (ImageButton) findViewById(R.id.coolModeButton);
        ImageButton dryModeButton = (ImageButton) findViewById(R.id.dryModeButton);
        ImageButton fanModeButton = (ImageButton) findViewById(R.id.fanModeButton);
        ImageButton heatModeButton = (ImageButton) findViewById(R.id.heatModeButton);

        int activeColor = ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null);
        int inactiveColor = ResourcesCompat.getColor(getResources(), R.color.colorSecondaryText, null);

        final Device.Mode mode = mDevice.getMode();

        autoModeButton.setColorFilter(mode == Device.Mode.AUTO ? activeColor : inactiveColor);
        coolModeButton.setColorFilter(mode == Device.Mode.COOL ? activeColor : inactiveColor);
        dryModeButton.setColorFilter(mode == Device.Mode.DRY ? activeColor : inactiveColor);
        fanModeButton.setColorFilter(mode == Device.Mode.FAN ? activeColor : inactiveColor);
        heatModeButton.setColorFilter(mode == Device.Mode.HEAT ? activeColor : inactiveColor);
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

    private void startHelpActivity(DeviceHelpActivity.Feature feature) {
        Intent intent = new Intent(this, DeviceHelpActivity.class);
        intent.putExtra(EXTRA_FEATURE_HELP, feature);
        startActivity(intent);
    }
}
