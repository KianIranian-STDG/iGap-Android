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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.OnGetNearbyCoordinate;
import net.iGap.interfaces.OnLocationChanged;
import net.iGap.module.GPSTracker;
import net.iGap.module.MyInfoWindow;
import net.iGap.proto.ProtoGeoGetNearbyCoordinate;
import net.iGap.request.RequestGeoGetNearbyCoordinate;
import net.iGap.request.RequestGeoGetNearbyDistance;
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
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class FragmentiGapMap extends Fragment implements OnLocationChanged, OnGetNearbyCoordinate, GestureDetector.OnGestureListener {

    private MapView map;
    private ItemizedIconOverlay<OverlayItem> itemizedIconOverlay = null;
    private ItemizedOverlay<OverlayItem> latestLocation;
    private ArrayList<Marker> markers = new ArrayList<>();
    private GestureDetector mGestureDetector;

    private boolean first = true;
    private boolean firstEnter = true;
    private double lat1;
    private double lon1;
    private Location location;

    public static FragmentiGapMap getInstance() {
        return new FragmentiGapMap();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Configuration.getInstance().load(G.context, PreferenceManager.getDefaultSharedPreferences(G.context));
        return inflater.inflate(R.layout.fragment_igap_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        G.onLocationChanged = this;
        G.onGetNearbyCoordinate = this;
        startMap(view);
        new GPSTracker().detectLocation();

        //clickDrawMarkActive();
    }

    private void startMap(View view) {
        map = (MapView) view.findViewById(R.id.map); //map = new MapView(this); //constructor
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
        map.setTileSource(new OnlineTileSourceBase("USGS Topo", 0, 20, 256, ".png", new String[]{Config.URL_MAP}) {
            @Override
            public String getTileURLString(MapTile aTile) {
                return getBaseUrl() + aTile.getZoomLevel() + "/" + aTile.getX() + "/" + aTile.getY() + mImageFilenameEnding;
            }
        });


        mGestureDetector = new GestureDetector(G.context, this);
        map.setOnTouchListener(mOnTouchListener);

        Button btnCurrent = (Button) view.findViewById(R.id.btnCurrent);
        Button btnOthers = (Button) view.findViewById(R.id.btnOthers);

        btnCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location != null) {
                    currentLocation(location, false);
                } else {
                    new GPSTracker().detectLocation();
                }
            }
        });

        btnOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location != null) {
                    new RequestGeoGetNearbyCoordinate().getNearbyCoordinate(location.getLatitude(), location.getLongitude());
                    new RequestGeoGetNearbyDistance().getNearbyDistance(location.getLatitude(), location.getLongitude());
                    new RequestGeoGetNearbyCoordinate().getNearbyCoordinate(location.getLatitude(), location.getLongitude());
                }
            }
        });
    }

    /**
     * ****************************** methods ******************************
     */

    public View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mGestureDetector.onTouchEvent(event)) {
                return true;
            } else {
                return false;
            }
        }
    };



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
        OverlayItem mapItem = new OverlayItem("", "", new GeoPoint((((double) loc.getLatitudeE6()) / 1000000), (((double) loc.getLongitudeE6()) / 1000000)));
        drawMark(mapItem, false, 0);
    }

    private void drawMark(double latitude, double longitude, boolean hasComment, long userId) {
        OverlayItem mapItem = new OverlayItem("", "", new GeoPoint(latitude, longitude));
        drawMark(mapItem, hasComment, userId);
    }

    private void drawMark(final OverlayItem mapItem, final boolean hasComment, final long userId) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                Marker marker = new Marker(map);
                marker.setPosition(new GeoPoint(mapItem.getPoint().getLatitude(), mapItem.getPoint().getLongitude()));
                if (userId != 0) {
                    if (hasComment) {
                        marker.setIcon(G.context.getResources().getDrawable(R.drawable.location_mark_comment_yes));
                    } else {
                        marker.setIcon(G.context.getResources().getDrawable(R.drawable.location_mark_comment_no));
                    }

                    InfoWindow infoWindow = new MyInfoWindow(R.layout.info_map_window, map, userId, hasComment);
                    marker.setInfoWindow(infoWindow);
                }

                markers.add(marker);
                map.getOverlays().add(marker);
                map.invalidate();
            }
        });
    }

    private void currentLocation(Location location, boolean setDefaultZoom) {
        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        if (setDefaultZoom) {
            map.getController().setZoom(16);
        }
        map.getController().animateTo(startPoint);
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
                //drawMark(e, mapView);
                return true;
            }

            @Override
            public boolean onLongPress(MotionEvent e, MapView mapView) {
                //Projection projection = mapView.getProjection();
                //GeoPoint loc = (GeoPoint) projection.fromPixels((int) e.getX(), (int) e.getY());
                //double longitude = ((double) loc.getLongitudeE6()) / 1000000;
                //double latitude = ((double) loc.getLatitudeE6()) / 1000000;
                //
                //if (first) {
                //    first = false;
                //    lat1 = latitude;
                //    lon1 = longitude;
                //
                //} else {
                //    first = true;
                //
                //    Polyline line = new Polyline();
                //    line.setWidth(2f);
                //    line.setColor(Color.BLUE);
                //    List<GeoPoint> pts = new ArrayList<>();
                //    pts.add(new GeoPoint(lat1, lon1));
                //    pts.add(new GeoPoint(latitude, longitude));
                //    line.setPoints(pts);
                //    line.setGeodesic(true);
                //    map.getOverlayManager().add(line);
                //}
                //
                //drawMark(e, mapView);
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
        this.location = location;

        if (firstEnter) {
            firstEnter = false;
            currentLocation(location, true);
        }

        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        OverlayItem overlayItem = new OverlayItem("title", "City", geoPoint);

        Drawable drawable = G.context.getResources().getDrawable(R.drawable.location_current);
        overlayItem.setMarker(drawable);

        ArrayList<OverlayItem> overlayItemArrayList = new ArrayList<OverlayItem>();
        overlayItemArrayList.add(overlayItem);
        ItemizedOverlay<OverlayItem> locationOverlay = new ItemizedIconOverlay<>(G.context, overlayItemArrayList, null);

        if (latestLocation != null) {
            map.getOverlays().remove(latestLocation);
        }

        latestLocation = locationOverlay;
        map.getOverlays().add(locationOverlay);

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(G.context, "Update Position", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNearbyCoordinate(List<ProtoGeoGetNearbyCoordinate.GeoGetNearbyCoordinateResponse.Result> results) {

        for (Marker marker : markers) {
            map.getOverlays().remove(marker);
        }

        for (ProtoGeoGetNearbyCoordinate.GeoGetNearbyCoordinateResponse.Result result : results) {
            drawMark(result.getLat(), result.getLon(), result.getHasComment(), result.getUserId());
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}
