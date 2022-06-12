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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import net.iGap.messenger.theme.Theme;

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
import java.util.Date;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * This is new fragment for showing and sending map based on osmdroid (open street map android api)
 * The reason for this replacement is Google sanctions on the map service for Iranian app
 */

public class FragmentMap extends BaseFragment implements MapEventsReceiver {

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    private MapView mMapView;
    private MapController mMapController;
    private LocationManager mLocationManager;
    private ShowMapType mShowMapType;
    private GeoPoint mCurrentGeoPoint;
    private GeoPoint mLastGeoPoint;
    private Location mGpsLocation;
    private Location mNetworkLocation;
    private Marker marker;
    private View mSendLocationButton;
    private View mCurrentLocationButton;
    private View mDriveButton;
    private TextView mAddressStrip;
    private MaterialProgressBar mProgressBar;
    private TextView mLocatingText;
    private TextView mInternetNotConnectedText;
    private Group driveGroupIcon;
    private Group sendGroupIcon;
    private TextView send_location_icon;

    public static FragmentMap newInstance() {
        FragmentMap fragment = new FragmentMap();
        return fragment;
    }

    public static FragmentMap newInstance(Double latitude, Double longitude) {
        FragmentMap fragment = new FragmentMap();
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
        View view = inflater.inflate(R.layout.map_fragment, container, false);
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
        send_location_icon = view.findViewById(R.id.send_location_icon);
        send_location_icon.setTextColor(Theme.getColor(Theme.key_white));
        mInternetNotConnectedText = view.findViewById(R.id.new_map_fragment_internet_not_connected);
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

        mProgressBar.setVisibility(View.VISIBLE);
        if (isInternetConnected()) {
            mLocatingText.setVisibility(View.VISIBLE);
            mInternetNotConnectedText.setVisibility(View.GONE);

            /**Here we request to get location with both Gps and Network provider to compare its later*/
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    mGpsLocation = location;
                }
            });

            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    mNetworkLocation = location;
                }
            });

            mGpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mNetworkLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            compareGpsAndNetworkLocationAndSet();

        } else {
            mInternetNotConnectedText.setVisibility(View.VISIBLE);
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
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        }
        return LocationManager.NETWORK_PROVIDER;
    }

    /**
     * This method compares locations which obtain from Gps and Network providers and set whichever is more accurate
     */
    private void compareGpsAndNetworkLocationAndSet() {
        if (mGpsLocation == null) {
            mProgressBar.setVisibility(View.GONE);
            mLocatingText.setVisibility(View.GONE);
            mCurrentGeoPoint = new GeoPoint(mNetworkLocation.getLatitude(), mNetworkLocation.getLongitude());
            mLastGeoPoint = mCurrentGeoPoint;
            adjustmentAccordingToMapType();
            addMarker(mLastGeoPoint);
            mMapController.animateTo(mLastGeoPoint);

        } else if (mNetworkLocation == null) {
            mProgressBar.setVisibility(View.GONE);
            mLocatingText.setVisibility(View.GONE);
            mCurrentGeoPoint = new GeoPoint(mGpsLocation.getLatitude(), mGpsLocation.getLongitude());
            mLastGeoPoint = mCurrentGeoPoint;
            adjustmentAccordingToMapType();
            addMarker(mLastGeoPoint);
            mMapController.animateTo(mLastGeoPoint);
        } else {
            if (mGpsLocation.getAccuracy() < mNetworkLocation.getAccuracy()) {
                mProgressBar.setVisibility(View.GONE);
                mLocatingText.setVisibility(View.GONE);
                mCurrentGeoPoint = new GeoPoint(mGpsLocation.getLatitude(), mGpsLocation.getLongitude());
                mLastGeoPoint = mCurrentGeoPoint;
                adjustmentAccordingToMapType();
                addMarker(mLastGeoPoint);
                mMapController.animateTo(mLastGeoPoint);
            } else {
                mProgressBar.setVisibility(View.GONE);
                mLocatingText.setVisibility(View.GONE);
                mCurrentGeoPoint = new GeoPoint(mNetworkLocation.getLatitude(), mNetworkLocation.getLongitude());
                mLastGeoPoint = mCurrentGeoPoint;
                adjustmentAccordingToMapType();
                addMarker(mLastGeoPoint);
                mMapController.animateTo(mLastGeoPoint);
            }
        }
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

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else
            return false;
    }

    public static void takeScreenshot(OnGetPicture listener) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View rootView = G.currentActivity.getWindow().getDecorView().getRootView();
            rootView.setDrawingCacheEnabled(true);
            Bitmap screenshot = Bitmap.createBitmap(rootView.getDrawingCache());
            rootView.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            screenshot.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            listener.getBitmap(screenshot);

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    public interface OnGetPicture {
        void getBitmap(Bitmap bitmap);
    }

    enum ShowMapType {
        SEND_LOCATION, READ_LOCATION
    }

}