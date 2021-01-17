package tomikaa.greeremote;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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
 * Created by tomikaa87 <https://github.com/tomikaa87> on 2017-10-23.
 */

public class DeviceHelpActivity extends AppCompatActivity {
    private Feature mFeature;

    public enum Feature {
        UNKNOWN,
        AIR,
        HEALTH,
        DRY,
        SLEEP,
        QUIET,
        TURBO,
        SAVING,
        LIGHT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_help);

        mFeature = (Feature) getIntent().getSerializableExtra(DeviceActivity.EXTRA_FEATURE_HELP);

        updateTitle();
    }

    private void updateTitle() {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.device_help_title));
        sb.append(" ");

        switch (mFeature)
        {
            case AIR:
                sb.append(getString(R.string.device_help_air));
                break;

            case HEALTH:
                sb.append(getString(R.string.device_help_health));
                break;

            case DRY:
                sb.append(getString(R.string.device_help_dry));
                break;

            case SLEEP:
                sb.append(getString(R.string.device_help_sleep));
                break;

            case QUIET:
                sb.append(getString(R.string.device_help_quiet));
                break;

            case TURBO:
                sb.append(getString(R.string.device_help_turbo));
                break;

            case SAVING:
                sb.append(getString(R.string.device_help_saving));
                break;

            case LIGHT:
                sb.append(getString(R.string.device_help_light));
                break;

            case UNKNOWN:
                break;
        }

        setTitle(sb.toString());
    }
}
