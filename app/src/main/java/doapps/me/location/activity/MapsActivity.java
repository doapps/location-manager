package doapps.me.location.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
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

    boolean isFirst;

    private Handler handler = new Handler();

    private static final int PETICION_PERMISO_LOCALIZACION = 100;

    private GoogleMap mMap;
    private Marker marker;

    private GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gpsTracker = new GPSTracker(this);
        Log.e("COORDINATES", gpsTracker.getLatitude() + ", " + gpsTracker.getLongitude());

        //  startService(new Intent(this, GPSMonitor.class));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        mMap = googleMap;

        myLocation();
    }

    public void myLocation() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (true) {
                        try {
                            Thread.sleep(1000);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    //  GPSTracker gpsTracker = new GPSTracker(MapsActivity.this);
                                    addMarket(gpsTracker.getLatitude(), gpsTracker.getLongitude());

                                    latitude.setText("" + gpsTracker.getLatitude());
                                    longitude.setText("" + gpsTracker.getLongitude());

                                    Log.e("LA", "" + gpsTracker.getLatitude() + " LO: " + gpsTracker.getLongitude());
                                }
                            });

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();
        }
    }

    public void addMarket(double lat, double lng) {
        if (!isFirst) {
            isFirst = true;
            LatLng coordenada = new LatLng(lat, lng);

            CameraUpdate ubication = CameraUpdateFactory.newLatLngZoom(coordenada, 16);

            if (mMap == null) {
            } else {
                marker = mMap.addMarker(new MarkerOptions().position(coordenada)
                        .draggable(false)
                );
                mMap.animateCamera(ubication);
            }
        } else {
            marker.setPosition(new LatLng(lat, lng));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                myLocation();
            }
        }
    }
}
