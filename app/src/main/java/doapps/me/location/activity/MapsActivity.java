package doapps.me.location.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import doapps.me.location.R;
import doapps.me.location.util.GPSMonitor;
import doapps.me.location.util.GPSTracker;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    @BindView(R.id.latitude_text)
    TextView latitude;

    @BindView(R.id.longitude_text)
    TextView longitude;

    private GoogleMap mMap;
    private Marker marker;

    private GPSTracker gpsTracker;
    private static final int LOCATION_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        mMap = googleMap;

        showLocation();
    }

    public void showLocation() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.e("showLocation", "1");
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        } else {
            Log.e("showLocation", "2");
            stopService(new Intent(this, GPSMonitor.class));
            startService(new Intent(this, GPSMonitor.class));
            gpsTracker = new GPSTracker(this);
            if (gpsTracker.canGetLocation) {
                addMarket(gpsTracker.getLatitude(), gpsTracker.getLongitude());

                latitude.setText("" + gpsTracker.getLatitude());
                longitude.setText("" + gpsTracker.getLongitude());
            }
        }
    }

    public void addMarket(double lat, double lng) {
        if (mMap != null) {
            LatLng coordinate = new LatLng(lat, lng);
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(coordinate, 16);

            if (marker == null) {
                marker = mMap.addMarker(new MarkerOptions().position(coordinate).draggable(false));
                mMap.animateCamera(location);
            } else {
                marker.setPosition(new LatLng(lat, lng));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission", "granded");
                showLocation();
            }
        }
    }
}
