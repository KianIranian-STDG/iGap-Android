package com.iGap.fragments;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.iGap.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by android3 on 1/5/2017.
 */

public class MapFragment extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button btnSendPosition;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mf_fragment_map_view);
        mapFragment.getMapAsync(this);

        btnSendPosition = (Button) findViewById(R.id.mf_btn_send_position);

        btnSendPosition.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override public void onSnapshotReady(Bitmap bitmap) {

                        saveBitmapToFile(bitmap);
                    }
                });
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }

        mMap.setMyLocationEnabled(true);

        //CameraPosition cameraPosition = new CameraPosition.Builder()
        //    .zoom(10)
        //    .bearing(70)
        //    .tilt(25)
        //    .build();
        //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        Location lo = mMap.getMyLocation();
        LatLng la;
        if (lo == null) {
            la = new LatLng(35.7288711, 51.4243625);
        } else {
            la = new LatLng(lo.getLatitude(), lo.getLongitude());
        }

        mMap.addMarker(new MarkerOptions().position(la).title("how"));
        //  mMap.moveCamera(CameraUpdateFactory.newLatLng(la));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(la, 16));
    }

    public static void saveBitmapToFile(Bitmap bitmap) {
        try {
            if (bitmap == null) return;

            Log.e("ddd", bitmap.getByteCount() + "  " + bitmap.getWidth() + "   " + bitmap.getHeight());

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            Calendar calendar = Calendar.getInstance();
            java.util.Date now = calendar.getTime();
            Timestamp tsTemp = new Timestamp(now.getTime());
            String ts = tsTemp.toString();

            File file = new File(path, ts + ".jpg");

            OutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        } catch (FileNotFoundException e) {

        }
    }
}

