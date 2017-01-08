package com.iGap.fragments;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityChat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Calendar;

import static com.iGap.R.id.mf_fragment_map_view;

/**
 * Created by Maryam on 1/6/2017.
 */

public class FragmentMap extends Fragment implements OnMapReadyCallback {

    public static String Latitude = "latitude";
    public static String Longitude = "longitude";
    public static String PosoitionMode = "positionMode";
    public static String flagFragmentMap = "FragmentMap";

    private GoogleMap mMap;

    private Double latitude;
    private Double longitude;
    private Mode mode;
    Marker marker;

    public enum Mode {
        sendPosition,
        seePosition;
    }

    public static FragmentMap getInctance(Double latitude, Double longitude, Mode mode) {

        FragmentMap fragmentMap = new FragmentMap();

        Bundle bundle = new Bundle();
        bundle.putDouble(FragmentMap.Latitude, latitude);
        bundle.putDouble(FragmentMap.Longitude, longitude);
        bundle.putSerializable(PosoitionMode, mode);

        fragmentMap.setArguments(bundle);

        return fragmentMap;
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle saveInctanceState) {
        super.onViewCreated(view, saveInctanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {

            latitude = bundle.getDouble(FragmentMap.Latitude);
            longitude = bundle.getDouble(FragmentMap.Longitude);
            mode = (Mode) bundle.getSerializable(PosoitionMode);

            initComponent(view);
        } else {
            close();
        }
    }

    private void close() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentMap.this).commit();
    }

    private void initComponent(View view) {

        SupportMapFragment mapFragment = new SupportMapFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(mf_fragment_map_view, mapFragment, null).commit();
        mapFragment.getMapAsync(FragmentMap.this);

        Button btnSendPosition = (Button) view.findViewById(R.id.mf_btn_send_position);

        if (mode == Mode.sendPosition) {

            btnSendPosition.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {

                    mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                        @Override public void onSnapshotReady(Bitmap bitmap) {

                            String path = saveBitmapToFile(bitmap);

                            close();

                            if (path.length() > 0) {

                                ActivityChat activity = (ActivityChat) getActivity();
                                activity.sendPosition(latitude, longitude, path);
                            }
                        }
                    });
                }
            });
        } else if (mode == Mode.seePosition) {
            btnSendPosition.setVisibility(View.GONE);
        }
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

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mMap.setMyLocationEnabled(true);

        LatLng latLng = new LatLng(latitude, longitude);

        marker = mMap.addMarker(new MarkerOptions().position(latLng).title("position"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        if (mode == Mode.sendPosition) {

            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override public void onCameraChange(CameraPosition cameraPosition) {

                    Display display = G.currentActivity.getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    LatLng mapCenter = mMap.getProjection().fromScreenLocation(new Point(size.x / 2, size.y / 2));
                    latitude = mapCenter.latitude;
                    longitude = mapCenter.longitude;

                    if (marker != null) {
                        marker.remove();
                    }

                    marker = mMap.addMarker(new MarkerOptions().position(mapCenter).title("position"));
                }
            });

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override public void onMapClick(LatLng latLng) {

                    latitude = latLng.latitude;
                    longitude = latLng.longitude;

                    if (marker != null) {
                        marker.remove();
                    }
                    marker = mMap.addMarker(new MarkerOptions().position(latLng).title("position"));
                }
            });
        }
    }


    //****************************************************************************************************

    public interface OnGetPicture {

        void getBitmap(Bitmap bitmap);
    }

    public static String saveBitmapToFile(Bitmap bitmap) {

        String result = "";

        try {
            if (bitmap == null) return result;

            Calendar calendar = Calendar.getInstance();
            java.util.Date now = calendar.getTime();
            Timestamp tsTemp = new Timestamp(now.getTime());
            String ts = tsTemp.toString();

            File file = new File(G.DIR_TEMP, ts + ".jpg");

            OutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

            result = file.getPath();
        } catch (FileNotFoundException e) {

        }

        return result;
    }

    public static void loadImageFromPosition(double latiude, double longitude, OnGetPicture listener) {

        String urlstr = "https://maps.googleapis.com/maps/api/staticmap?center=" + latiude + "," + longitude + "&zoom=16&size=480x240" +
            "&markers=color:red%7Clabel:S%7C" + latiude + "," + longitude + "&maptype=roadmap&key=" + G.context.getString(R.string.google_maps_key);

        new DownloadImageTask(listener).execute(urlstr);
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        OnGetPicture listener;

        public DownloadImageTask(OnGetPicture listener) {
            this.listener = listener;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            if (listener != null) {
                listener.getBitmap(result);
            }
        }
    }
}
