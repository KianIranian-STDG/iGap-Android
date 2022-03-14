package net.iGap.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperError;
import net.iGap.module.GPSTracker;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * This is new fragment for showing and sending map based on osmdroid (open street map android api)
 * The reason for this replacement is Google sanctions on the map service for Iranian app
 */

public class NewFragmentMap extends BaseFragment implements MapEventsReceiver, LocationListener {

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    private MapView mMapView;
    private MapController mMapController;
    private LocationManager mLocationManager;
    private ShowMapType mShowMapType;
    private GeoPoint mCurrentGeoPoint;
    private GeoPoint mLastGeoPoint;
    private Marker marker;
    private View mSendLocationButton;
    private View mCurrentLocationButton;
    private View mDriveButton;
    private TextView mAddressStrip;
    private MaterialProgressBar mProgressBar;
    private TextView mLocatingText;
    private Group driveGroupIcon;
    private Group sendGroupIcon;

    public static NewFragmentMap newInstance() {
        NewFragmentMap fragment = new NewFragmentMap();
        return fragment;
    }

    public static NewFragmentMap newInstance(Double latitude, Double longitude) {
        NewFragmentMap fragment = new NewFragmentMap();
        Bundle args = new Bundle();
        args.putDouble(LATITUDE, latitude);
        args.putDouble(LONGITUDE, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && !getArguments().isEmpty()) {
            mShowMapType = ShowMapType.READ_LOCATION;
        } else {
            mShowMapType = ShowMapType.SEND_LOCATION;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_map_fragment, container, false);
        findViews(view);
        try {
            initComponent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void findViews(View view) {
        mMapView = view.findViewById(R.id.open_street_map_view);
        mSendLocationButton = view.findViewById(R.id.send_location);
        mCurrentLocationButton = view.findViewById(R.id.current_location);
        mAddressStrip = view.findViewById(R.id.address_strip);
        driveGroupIcon = view.findViewById(R.id.drive_icon_group);
        sendGroupIcon = view.findViewById(R.id.send_icon_group);
        mDriveButton = view.findViewById(R.id.drive_icon);
        mProgressBar = view.findViewById(R.id.new_map_fragment_progress_bar);
        mLocatingText = view.findViewById(R.id.new_map_fragment_locating_text);
    }

    private void initComponent() throws IOException {
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, android.preference.PreferenceManager.getDefaultSharedPreferences(ctx));
        mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        mMapView.setMultiTouchControls(true);
        mMapView.setDrawingCacheEnabled(true);
        marker = new Marker(mMapView);
        mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(17);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = mLocationManager.getLastKnownLocation(getLocationProvider());
        if(location != null){
            mCurrentGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            adjustmentAccordingToMapType();
            addMarker(mLastGeoPoint);
            mMapController.animateTo(mLastGeoPoint);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            mLocatingText.setVisibility(View.VISIBLE);
            mLocationManager.requestLocationUpdates(getLocationProvider(), 20000, 1000, this);
        }
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getActivity(), this);
        mMapView.getOverlays().add(0, mapEventsOverlay);
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mMapView);
        mMapView.getOverlays().add(scaleBarOverlay);

    }

    private void setListeners() {

        mMapView.setMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                Log.d("osm", "onScroll");
                return true;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                Log.d("osm", "onZoom");
                return true;
            }
        });

        mCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLastGeoPoint = mCurrentGeoPoint;
                mMapController.animateTo(mCurrentGeoPoint);
                setAddressStrip(mLastGeoPoint);
                addMarker(mCurrentGeoPoint);
            }
        });

        mSendLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = mMapView.getDrawingCache();

                if (mLastGeoPoint.getLatitude() == 0 || mLastGeoPoint.getLongitude() == 0) {

                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.set_position), false);

                        }
                    });
                } else {

                    try {

                        String path = saveMapToFile(bitmap);
                        popBackStackFragment();


                        if (path.length() > 0) {
                            //ActivityChat activity = (ActivityChat) mActivity;
                            //activity.sendPosition(latitude, longitude, path);

                            if (G.iSendPositionChat != null) {
                                G.iSendPositionChat.send(mLastGeoPoint.getLatitude(), mLastGeoPoint.getLongitude(), path);
                            }

                        }
                    } catch (Exception e) {
                        popBackStackFragment();                        //ActivityChat activity = (ActivityChat) mActivity;
                        //activity.sendPosition(latitude, longitude, null);

                        if (G.iSendPositionChat != null) {
                            G.iSendPositionChat.send(mLastGeoPoint.getLatitude(), mLastGeoPoint.getLongitude(), null);
                        }
                    }

                }


            }
        });

        mDriveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri location = Uri.parse("geo:" + mLastGeoPoint.getLatitude() + "," + mLastGeoPoint.getLongitude() + "?z=17");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                startActivity(mapIntent);
            }
        });
    }


    public void addMarker(final GeoPoint center) {
        marker.setPosition(center);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getResources().getDrawable(R.drawable.ic_location));
        marker.setDraggable(true);

        /**Following marker attributes use for set an information dialog above of
         * marker that usually show selected address*/
        marker.setTitle("Location:");
        marker.setSubDescription(center.getLatitude() + " , " + center.getLongitude());


//        marker.setInfoWindow(new CustomMarkerInfoWindow(mMapView));
//        marker.setInfoWindowAnchor(marker.ANCHOR_CENTER, marker.ANCHOR_TOP);

//        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker m, MapView mapView) {
//                Log.i("Script","onMarkerClick");
//                m.showInfoWindow();
//                return true;
//            }
//        });

        marker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                Log.i("Script", "onMarkerDragStart()");
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.i("Script", "onMarkerDragEnd()");
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                Log.i("Script", "onMarkerDrag()");
            }
        });

        //osm.getOverlays().clear();
        mMapView.getOverlays().add(new MapOverlay(getActivity()));
        mMapView.getOverlays().add(marker);
        mMapView.invalidate();

    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
//        Toast.makeText(getActivity(), "Coordenadas:\nLatitude: ("+p.getLatitude() +"\nLongitude: " +
//                ""+p.getLongitude()+")" , Toast.LENGTH_SHORT).show();
//
//        InfoWindow.closeAllInfoWindowsOn(mMapView); //Clicando em qualquer canto da tela, fecha o infowindow
        return (true);
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mProgressBar.setVisibility(View.GONE);
        mLocatingText.setVisibility(View.GONE);
        mCurrentGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        mLastGeoPoint = mCurrentGeoPoint;
        mMapController.animateTo(mLastGeoPoint);
        addMarker(mLastGeoPoint);
        adjustmentAccordingToMapType();
        addMarker(mLastGeoPoint);
        mMapController.animateTo(mLastGeoPoint);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }


    /**
     * For adding custom information window above of marker icon
     */
//    public class CustomMarkerInfoWindow extends MarkerInfoWindow {
//
//        public CustomMarkerInfoWindow(MapView mapView) {
//            super(R.layout.bonuspack_bubble, mapView);
//        }
//
//        @Override
//        public void onOpen(Object item) {
//
//            Marker marker = (Marker) item;
//
//        }
//    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates((LocationListener) this);
        }
    }

    class MapOverlay extends Overlay {
        public MapOverlay(Context ctx) {
            super(ctx);
        }

        @Override
        public void draw(Canvas c, MapView osmv, boolean shadow) {

        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent me, MapView mv) {
            Projection p = mMapView.getProjection();
            GeoPoint gp = (GeoPoint) p.fromPixels((int) me.getX(), (int) me.getY());
            mLastGeoPoint = gp;
            setAddressStrip(mLastGeoPoint);
            addMarker(gp);
            return (true);
        }
    }

    public String saveMapToFile(Bitmap bitmap) {

        String result = "";

        try {
            if (bitmap == null) return result;

            String fileName = "/location_" + String.valueOf(mLastGeoPoint.getLatitude()).replace(".", "") + "_" + String.valueOf(mLastGeoPoint.getLongitude()).replace(".", "") + ".png";
            File file = new File(G.DIR_TEMP, fileName);

            OutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

            result = file.getPath();
        } catch (FileNotFoundException e) {

        }

        return result;
    }

    private String getLocationProvider() {
        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return LocationManager.GPS_PROVIDER;
        }
        return LocationManager.NETWORK_PROVIDER;
    }

    private void setAddressStrip(GeoPoint geoPoint) {
        if (mLastGeoPoint.toString().equalsIgnoreCase(mCurrentGeoPoint.toString())) {
            mAddressStrip.setText(getString(R.string.send_current_location) + geoPoint.getLatitude() + " , " + geoPoint.getLongitude());
        } else {
            mAddressStrip.setText(getString(R.string.send_selected_location) + geoPoint.getLatitude() + " , " + geoPoint.getLongitude());
        }
    }

    private void adjustmentAccordingToMapType() {
        if (mShowMapType == ShowMapType.READ_LOCATION) {
            mLastGeoPoint = new GeoPoint(getArguments().getDouble(LATITUDE), getArguments().getDouble(LONGITUDE));
            sendGroupIcon.setVisibility(View.GONE);
            driveGroupIcon.setVisibility(View.VISIBLE);
        } else {
            mLastGeoPoint = mCurrentGeoPoint;
            setAddressStrip(mLastGeoPoint);
        }
    }

    enum ShowMapType {
        SEND_LOCATION, READ_LOCATION
    }

}