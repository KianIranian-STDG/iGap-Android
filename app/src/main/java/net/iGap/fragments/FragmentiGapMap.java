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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.ArrayList;
import java.util.List;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.OnGetNearbyCoordinate;
import net.iGap.interfaces.OnLocationChanged;
import net.iGap.libs.floatingAddButton.ArcMenu;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.GPSTracker;
import net.iGap.module.MyInfoWindow;
import net.iGap.proto.ProtoGeoGetNearbyCoordinate;
import net.iGap.request.RequestGeoGetNearbyCoordinate;
import net.iGap.request.RequestGeoUpdateComment;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
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

import static net.iGap.G.context;

public class FragmentiGapMap extends Fragment implements OnLocationChanged, OnGetNearbyCoordinate {

    private MapView map;
    private ItemizedIconOverlay<OverlayItem> itemizedIconOverlay = null;
    private ItemizedOverlay<OverlayItem> latestLocation;
    private ArrayList<Marker> markers = new ArrayList<>();
    private GestureDetector mGestureDetector;
    private ViewGroup rootTurnOnGps;

    private boolean first = true;
    private boolean firstEnter = true;
    private double lat1;
    private double lon1;
    public static Location location;

    private FragmentActivity mActivity;

    //0.011 longitude, 0.009 latitude
    private final double LONGITUDE_LIMIT = 0.011;
    private final double LATITUDE_LIMIT = 0.009;

    double northLimitation;
    double eastLimitation;
    double southLimitation;
    double westLimitation;

    private double lastLatitude;
    private double lastLongitude;

    private boolean canUpdate = true;

    private long latestUpdateTime = 0;

    public static FragmentiGapMap getInstance() {
        return new FragmentiGapMap();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        return inflater.inflate(R.layout.fragment_igap_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        G.onLocationChanged = this;
        G.onGetNearbyCoordinate = this;
        startMap(view);
        statusCheck();
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
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(false);
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
        map.setTileSource(new OnlineTileSourceBase("USGS Topo", 16, 16, 256, ".png", new String[]{Config.URL_MAP}) {
            @Override
            public String getTileURLString(MapTile aTile) {
                return getBaseUrl() + aTile.getZoomLevel() + "/" + aTile.getX() + "/" + aTile.getY() + mImageFilenameEnding;
            }
        });

        rootTurnOnGps = (ViewGroup) view.findViewById(R.id.rootTurnOnGps);
        rootTurnOnGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //have to empty
            }
        });

        ToggleButton toggleGps = (ToggleButton) view.findViewById(R.id.toggleGps);
        toggleGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        final ArcMenu arcMap = (ArcMenu) view.findViewById(R.id.arc_map);
        FloatingActionButton btnLocation = (FloatingActionButton) view.findViewById(R.id.fab_map_location);
        FloatingActionButton btnOthers = (FloatingActionButton) view.findViewById(R.id.fab_map_others);
        FloatingActionButton btnList = (FloatingActionButton) view.findViewById(R.id.fab_map_list);
        RippleView rippleMoreMap = (RippleView) view.findViewById(R.id.ripple_more_map);

        btnOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arcMap.isMenuOpened()) {
                    arcMap.toggleMenu();
                }
                if (location != null) {
                    new RequestGeoGetNearbyCoordinate().getNearbyCoordinate(location.getLatitude(), location.getLongitude());
                }
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arcMap.isMenuOpened()) {
                    arcMap.toggleMenu();
                }
                if (location != null) {
                    currentLocation(location, false);
                } else {
                    new GPSTracker().detectLocation();
                }
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arcMap.isMenuOpened()) {
                    arcMap.toggleMenu();
                }
                //TODO [Saeed Mozaffari] [2017-06-19 5:49 PM] - Go To Nearby Distance Page
            }
        });

        rippleMoreMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentMapUsers fragmentMapUsers = FragmentMapUsers.newInstance();
                try {
                    mActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.mapContainer, fragmentMapUsers, "fragment_map").commitAllowingStateLoss();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        });

        new RequestGeoUpdateComment().updateComment("I am " + G.displayName);
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
                        marker.setIcon(context.getResources().getDrawable(R.drawable.location_mark_comment_yes));
                    } else {
                        marker.setIcon(context.getResources().getDrawable(R.drawable.location_mark_comment_no));
                    }

                    InfoWindow infoWindow = new MyInfoWindow(R.layout.info_map_window, map, userId, hasComment, FragmentiGapMap.this, mActivity);
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
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initMapListener();
            }
        }, 2000);
    }

    private void mapBounding(Location location) {
        double extraBounding = 0.01;

        northLimitation = location.getLatitude() + LATITUDE_LIMIT;
        eastLimitation = location.getLongitude() + LONGITUDE_LIMIT;
        southLimitation = location.getLatitude() - LATITUDE_LIMIT;
        westLimitation = location.getLongitude() - LONGITUDE_LIMIT;
        BoundingBoxE6 bBox = new BoundingBoxE6(northLimitation + extraBounding, eastLimitation + extraBounding, southLimitation - extraBounding, westLimitation - extraBounding);
        map.setScrollableAreaLimit(bBox);
    }

    private void initMapListener() {
        map.setMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {

                final GeoPoint geoPoint = event.getSource().getBoundingBox().getCenter();
                if ((geoPoint.getLatitude() < northLimitation) && (geoPoint.getLatitude() > southLimitation) && (geoPoint.getLongitude() < eastLimitation) && geoPoint.getLongitude() > westLimitation) {
                    lastLatitude = geoPoint.getLatitude();
                    lastLongitude = geoPoint.getLongitude();
                    canUpdate = true;
                } else if (canUpdate) {
                    canUpdate = false;
                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            map.getController().animateTo(new GeoPoint(lastLatitude, lastLongitude));
                            G.handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    canUpdate = true;
                                }
                            }, 2000);
                        }
                    }, 100);
                }

                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                return false;
            }
        });
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
            mapBounding(location);
        }

        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        OverlayItem overlayItem = new OverlayItem("title", "City", geoPoint);

        Drawable drawable = context.getResources().getDrawable(R.drawable.location_current);
        overlayItem.setMarker(drawable);

        ArrayList<OverlayItem> overlayItemArrayList = new ArrayList<OverlayItem>();
        overlayItemArrayList.add(overlayItem);
        ItemizedOverlay<OverlayItem> locationOverlay = new ItemizedIconOverlay<>(context, overlayItemArrayList, null);

        if (latestLocation != null) {
            map.getOverlays().remove(latestLocation);
        }

        latestLocation = locationOverlay;
        map.getOverlays().add(locationOverlay);

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "Update Position", Toast.LENGTH_SHORT).show();
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            rootTurnOnGps.setVisibility(View.VISIBLE);

        } else {
            rootTurnOnGps.setVisibility(View.GONE);
            new GPSTracker().detectLocation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        statusCheck();
    }
}
