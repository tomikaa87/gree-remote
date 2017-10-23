package tomikaa.greeremote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device, menu);
        return true;
    }

    public void update() {
        mTemperatureTextView.setText(String.format("%d", mDeviceItem.mTemperature));
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

    private void startHelpActivity(DeviceHelpActivity.Feature feature) {
        Intent intent = new Intent(this, DeviceHelpActivity.class);
        intent.putExtra(EXTRA_FEATURE_HELP, feature);
        startActivity(intent);
    }
}
