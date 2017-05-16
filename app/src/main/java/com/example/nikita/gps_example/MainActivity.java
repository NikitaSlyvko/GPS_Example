package com.example.nikita.gps_example;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends Activity {
    private TextView textEnabledGPS;
    private TextView textStatusGPS;
    private TextView textLocationGPS;

    private TextView textEnabledNet;
    private TextView textStatusNet;
    private TextView textLocationNet;

    private Button buttonSettings;

    private LocationManager locationManager;

    private StringBuilder sbGPS;
    private StringBuilder sbNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sbGPS = new StringBuilder();
        sbNet = new StringBuilder();

        textEnabledGPS = (TextView) findViewById(R.id.enabled_gps);
        textStatusGPS = (TextView) findViewById(R.id.status_gps);
        textLocationGPS = (TextView) findViewById(R.id.location_gps);

        textEnabledNet = (TextView) findViewById(R.id.enabled_net);
        textStatusNet = (TextView) findViewById(R.id.status_net);
        textLocationNet = (TextView) findViewById(R.id.location_net);

        buttonSettings = (Button) findViewById(R.id.location_button_settings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener
        );
        checkEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if(provider.equals(LocationManager.GPS_PROVIDER)) {
                textStatusGPS.setText("Status: " + String.valueOf(status));
            } else if(provider.equals(LocationManager.NETWORK_PROVIDER)) {
                textStatusNet.setText("Status: " + String.valueOf(status));
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }
    };

    private void showLocation(Location location) {
        if(location == null) return;
        if(location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            textLocationGPS.setText(formatLocation(location));
        } else if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            textLocationNet.setText(formatLocation(location));
        }
    }

    private String formatLocation(Location location) {
        if(location == null) return "";
        return String.format(
                "Coordinates:" + "\n" + " lat = %1$.4f" + "\n" + " lon = %2$.4f" +
                       "\n" + "time = %3$tF %3$tT" + "\n",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()
                )
        );
    }

    private void checkEnabled() {
        textEnabledGPS.setText("Enabled : "
        + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        textLocationNet.setText("Enabled : "
        + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }
}
