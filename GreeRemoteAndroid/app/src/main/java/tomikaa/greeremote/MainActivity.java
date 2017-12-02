package tomikaa.greeremote;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import tomikaa.greeremote.Gree.Device.Device;
import tomikaa.greeremote.Gree.Device.DeviceManager;

public class MainActivity extends AppCompatActivity
    implements DeviceItemFragment.OnListFragmentInteractionListener {

    public static String EXTRA_DEVICE_ITEM = "tomikaa.greeremote.DEVICEITEM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            DeviceManager.getInstance().discoverDevices();
            Snackbar.make(view, getString(R.string.device_scan_start_notification), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            }
        });

        DeviceManager.getInstance().discoverDevices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(DeviceItem item) {
        Intent intent = new Intent(this, DeviceActivity.class);
        intent.putExtra(EXTRA_DEVICE_ITEM, item);
        startActivity(intent);
    }
}
