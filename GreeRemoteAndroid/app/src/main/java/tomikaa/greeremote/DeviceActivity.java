package tomikaa.greeremote;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.nio.charset.Charset;

public class DeviceActivity extends AppCompatActivity {
    private DeviceItem mDeviceItem;
    private TextView mTemperatureTextView;

    public static String EXTRA_FEATURE_HELP = "tomikaa.greeremote.FEATURE_HELP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        mTemperatureTextView = (TextView) findViewById(R.id.temperatureTextView);

        Intent intent = getIntent();
        mDeviceItem = (DeviceItem) intent.getSerializableExtra(MainActivity.EXTRA_DEVICE_ITEM);

        setTitle(mDeviceItem.mName);

        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device, menu);
        return true;
    }

    public void update() {
        mTemperatureTextView.setText(String.format("%d", mDeviceItem.mTemperature));

        ImageButton autoModeButton = (ImageButton) findViewById(R.id.autoModeButton);
        ImageButton coolModeButton = (ImageButton) findViewById(R.id.coolModeButton);
        ImageButton dryModeButton = (ImageButton) findViewById(R.id.dryModeButton);
        ImageButton fanModeButton = (ImageButton) findViewById(R.id.fanModeButton);
        ImageButton heatModeButton = (ImageButton) findViewById(R.id.heatModeButton);

        int activeColor = ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null);
        int inactiveColor = ResourcesCompat.getColor(getResources(), R.color.colorSecondaryText, null);

        autoModeButton.setColorFilter(mDeviceItem.mMode == DeviceItem.Mode.AUTO ? activeColor : inactiveColor);
        coolModeButton.setColorFilter(mDeviceItem.mMode == DeviceItem.Mode.COOL ? activeColor : inactiveColor);
        dryModeButton.setColorFilter(mDeviceItem.mMode == DeviceItem.Mode.DRY ? activeColor : inactiveColor);
        fanModeButton.setColorFilter(mDeviceItem.mMode == DeviceItem.Mode.FAN ? activeColor : inactiveColor);
        heatModeButton.setColorFilter(mDeviceItem.mMode == DeviceItem.Mode.HEAT ? activeColor : inactiveColor);
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
        DeviceManager dm = DeviceManager.getInstance();
        dm.setMode(mDeviceItem.mId, DeviceManager.MODE_AUTO);
    }

    public void onCoolModeButtonClicked(View view) {
        DeviceManager dm = DeviceManager.getInstance();
        dm.setMode(mDeviceItem.mId, DeviceManager.MODE_COOL);
    }

    public void onDryModeButtonClicked(View view) {
        DeviceManager dm = DeviceManager.getInstance();
        dm.setMode(mDeviceItem.mId, DeviceManager.MODE_DRY);
    }

    public void onFanModeButtonClicked(View view) {
        DeviceManager dm = DeviceManager.getInstance();
        dm.setMode(mDeviceItem.mId, DeviceManager.MODE_FAN);
    }

    public void onHeatModeButtonClicked(View view) {
        DeviceManager dm = DeviceManager.getInstance();
        dm.setMode(mDeviceItem.mId, DeviceManager.MODE_HEAT);
    }

    public void onPlusButtonClicked(View view) {
        DeviceManager dm = DeviceManager.getInstance();
        dm.setTemperature(mDeviceItem.mId, mDeviceItem.mTemperature + 1);
    }

    public void onMinusButtonClicked(View view) {
        DeviceManager dm = DeviceManager.getInstance();
        dm.setTemperature(mDeviceItem.mId, mDeviceItem.mTemperature - 1);
    }

    private void startHelpActivity(DeviceHelpActivity.Feature feature) {
        Intent intent = new Intent(this, DeviceHelpActivity.class);
        intent.putExtra(EXTRA_FEATURE_HELP, feature);
        startActivity(intent);
    }
}
