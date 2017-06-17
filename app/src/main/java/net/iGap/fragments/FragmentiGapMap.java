/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;

public class FragmentiGapMap extends Fragment implements LocationListener {

    private MapView map;
    private ItemizedIconOverlay<OverlayItem> itemizedIconOverlay = null;
    private ItemizedOverlay<OverlayItem> latestLocation;

    private boolean first = true;
    private boolean firstEnter = true;
    private double lat1;
    private double lon1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Configuration.getInstance().load(G.context, PreferenceManager.getDefaultSharedPreferences(G.context));
        return inflater.inflate(R.layout.fragment_igap_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startMap(view);
    }

    public static FragmentiGapMap getInstance() {
        return new FragmentiGapMap();
    }

    private void startMap(View view) {
        map = (MapView) view.findViewById(R.id.map);
        //map = new MapView(this); //constructor
        /**
         * Set Type Of Map
         */
        map.setTileSource(TileSourceFactory.MAPNIK);

        /**
         * Zoom With MultiTouch And With Two Finger
         */
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        /**
         * Set Zoom Value
         */
        IMapController mapController = map.getController();
        mapController.setZoom(16);

        /**
         * Start With This Point
         */
        GeoPoint startPoint = new GeoPoint(35.689197, 51.388974);
        mapController.setCenter(startPoint);

        /**
         * Use From Following Code For Custom Url Tile Server
         */
        map.setTileSource(new OnlineTileSourceBase("USGS Topo", 0, 18, 256, ".png", new String[]{Config.URL_MAP}) {
            @Override
            public String getTileURLString(MapTile aTile) {
                return getBaseUrl() + aTile.getZoomLevel() + "/" + aTile.getX() + "/" + aTile.getY() + mImageFilenameEnding;
            }
        });

        currentLocation();
        clickDrawMarkActive();
    }

    /**
     * ****************************** methods ******************************
     */

    private void drawLine(ArrayList<Double[]> points) {
        Polyline line = new Polyline();
        line.setWidth(5f);
        line.setColor(Color.BLUE);
        List<GeoPoint> pts = new ArrayList<>();
        for (Double[] point : points) {
            pts.add(new GeoPoint(point[0], point[1]));
        }
        line.setPoints(pts);
        line.setGeodesic(true);
        map.getOverlayManager().add(line);
    }

    private void drawMark(MotionEvent motionEvent, MapView mapView) {
        Projection projection = mapView.getProjection();
        GeoPoint loc = (GeoPoint) projection.fromPixels((int) motionEvent.getX(), (int) motionEvent.getY());
        ArrayList<OverlayItem> overlayArray = new ArrayList<>();
        OverlayItem mapItem = new OverlayItem("", "", new GeoPoint((((double) loc.getLatitudeE6()) / 1000000), (((double) loc.getLongitudeE6()) / 1000000)));
        drawMark(overlayArray, mapItem);
    }

    private void drawMark(double latitude, double longitude) {
        ArrayList<OverlayItem> overlayArray = new ArrayList<>();
        OverlayItem mapItem = new OverlayItem("", "", new GeoPoint(latitude, longitude));
        drawMark(overlayArray, mapItem);
    }

    private void drawMark(ArrayList<OverlayItem> overlayArray, OverlayItem mapItem) {
        Drawable marker = G.context.getResources().getDrawable(R.drawable.location_mark);
        mapItem.setMarker(marker);
        overlayArray.add(mapItem);
        if (itemizedIconOverlay == null) {
            itemizedIconOverlay = new ItemizedIconOverlay<>(G.context, overlayArray, null);
            map.getOverlays().add(itemizedIconOverlay);
            map.invalidate();
        } else {
            //map.getOverlays().remove(anotherItemizedIconOverlay); // remove before mark position
            map.invalidate();
            itemizedIconOverlay = new ItemizedIconOverlay<>(G.context, overlayArray, null);
            map.getOverlays().add(itemizedIconOverlay);
        }
    }

    /**
     * active location callbacks
     */
    private void currentLocation() {
        LocationManager locationManager;
        try {
            locationManager = (LocationManager) G.context.getSystemService(Context.LOCATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (G.context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && G.context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * activation map for show mark after each tap
     */
    private void clickDrawMarkActive() {
        Overlay touchOverlay = new Overlay() {
            ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = null;

            @Override
            public void draw(Canvas arg0, MapView arg1, boolean arg2) {

            }

            @Override
            public boolean onSingleTapConfirmed(final MotionEvent e, final MapView mapView) {
                drawMark(e, mapView);
                return true;
            }

            @Override
            public boolean onLongPress(MotionEvent e, MapView mapView) {
                Projection projection = mapView.getProjection();
                GeoPoint loc = (GeoPoint) projection.fromPixels((int) e.getX(), (int) e.getY());
                double longitude = ((double) loc.getLongitudeE6()) / 1000000;
                double latitude = ((double) loc.getLatitudeE6()) / 1000000;

                if (first) {
                    first = false;
                    lat1 = latitude;
                    lon1 = longitude;

                } else {
                    first = true;

                    Polyline line = new Polyline();
                    line.setWidth(2f);
                    line.setColor(Color.BLUE);
                    List<GeoPoint> pts = new ArrayList<>();
                    pts.add(new GeoPoint(lat1, lon1));
                    pts.add(new GeoPoint(latitude, longitude));
                    line.setPoints(pts);
                    line.setGeodesic(true);
                    map.getOverlayManager().add(line);
                }

                drawMark(e, mapView);
                return super.onLongPress(e, mapView);
            }
        };
        map.getOverlays().add(touchOverlay);
    }

    /**
     * ****************************** callbacks ******************************
     */

    @Override
    public void onLocationChanged(Location location) {
        if (firstEnter) {
            firstEnter = false;
            GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            map.getController().setZoom(16);
            map.getController().animateTo(startPoint);
        }

        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        OverlayItem overlayItem = new OverlayItem("title", "City", geoPoint);

        Drawable drawable = this.getResources().getDrawable(R.drawable.location_current);
        overlayItem.setMarker(drawable);

        ArrayList<OverlayItem> overlayItemArrayList = new ArrayList<OverlayItem>();
        overlayItemArrayList.add(overlayItem);
        ItemizedOverlay<OverlayItem> locationOverlay = new ItemizedIconOverlay<>(G.context, overlayItemArrayList, null);

        if (latestLocation != null) {
            map.getOverlays().remove(latestLocation);
        }

        latestLocation = locationOverlay;
        map.getOverlays().add(locationOverlay);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}
