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
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.ArrayList;
import java.util.List;
import net.iGap.BuildConfig;
import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.OnGeoCommentResponse;
import net.iGap.interfaces.OnGeoGetComment;
import net.iGap.interfaces.OnGetNearbyCoordinate;
import net.iGap.interfaces.OnLocationChanged;
import net.iGap.interfaces.OnMapClose;
import net.iGap.interfaces.OnMapRegisterState;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.DialogAnimation;
import net.iGap.module.GPSTracker;
import net.iGap.module.MyInfoWindow;
import net.iGap.proto.ProtoGeoGetNearbyCoordinate;
import net.iGap.request.RequestGeoGetComment;
import net.iGap.request.RequestGeoGetNearbyCoordinate;
import net.iGap.request.RequestGeoGetRegisterStatus;
import net.iGap.request.RequestGeoRegister;
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
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import static net.iGap.G.context;
import static net.iGap.G.userId;
import static net.iGap.R.id.st_fab_gps;

public class FragmentiGapMap extends Fragment implements OnLocationChanged, OnGetNearbyCoordinate, OnMapRegisterState, OnMapClose, OnGeoGetComment, GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener {

    private MapView map;
    private ItemizedOverlay<OverlayItem> latestLocation;
    private ArrayList<Marker> markers = new ArrayList<>();
    private ScrollView rootTurnOnGps;
    private ViewGroup vgMessageGps;
    public static Location location;
    public static RippleView btnBack;
    public static RippleView rippleMoreMap;
    public static boolean isBackPress = false;
    private ToggleButton toggleGps;
    private ToggleButton btnMapChangeRegistration;
    private TextView txtTextTurnOnOffGps;
    private FragmentActivity mActivity;
    private ItemizedIconOverlay<OverlayItem> itemizedIconOverlay = null;
    private GestureDetector mGestureDetector;

    public static ArrayList<String> mapUrls = new ArrayList<>();
    private String specialRequests;

    private boolean firstEnter = true;
    private boolean canUpdate = true;
    private boolean mapRegisterState = true;
    private boolean isGpsOn = false;
    private boolean first = true;
    private EditText edtMessageGps;
    private final double LONGITUDE_LIMIT = 0.011;
    private final double LATITUDE_LIMIT = 0.009;
    private double northLimitation;
    private double eastLimitation;
    private double southLimitation;
    private double westLimitation;
    private double lastLatitude;
    private double lastLongitude;
    private double lat1;
    private double lon1;
    public static int page;
    public static final int pageiGapmap = 1;
    public static final int pageUserList = 2;

    private int lastSpecialRequestsCursorPosition = 0;
    private final int DEFAULT_LOOP_TIME = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    private final int ZOOM_LEVEL_MIN = 15;
    private final int ZOOM_LEVEL_NORMAL = 16;
    private final int ZOOM_LEVEL_MAX = 17;

    private long latestUpdateTime = 0;
    long firstTap = 0;
    private FloatingActionButton fabGps;
    private ProgressBar prgWatingSendMessage;
    private TextView txtSendMessageGps;


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
        G.onMapRegisterState = this;
        G.onMapClose = this;
        G.onGeoGetComment = this;
        startMap(view);
        //statusCheck();
        //clickDrawMarkActive();

        page = 1;
        new RequestGeoGetRegisterStatus().getRegisterStatus();
        new RequestGeoGetComment().getComment(userId);
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
        map.setMultiTouchControls(true);

        /**
         * Compass
         */
        CompassOverlay mCompassOverlay = new CompassOverlay(getContext(), new InternalCompassOrientationProvider(getContext()), map);
        mCompassOverlay.enableCompass();
        map.getOverlays().add(mCompassOverlay);

        /**
         * Set Zoom Value
         */
        IMapController mapController = map.getController();
        mapController.setZoom(ZOOM_LEVEL_NORMAL);


        /**
         * double tap callback enable
         */
        final GestureDetector mGestureDetector = new GestureDetector(G.context, this);
        map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });

        /**
         * Start With This Point
         */
        GeoPoint startPoint = new GeoPoint(35.689197, 51.388974);
        mapController.setCenter(startPoint);

        /**
         * Use From Following Code For Custom Url Tile Server
         */

        String[] mStringArray = new String[mapUrls.size()];
        mStringArray = mapUrls.toArray(mStringArray);

        map.setTileSource(new OnlineTileSourceBase("USGS Topo", ZOOM_LEVEL_MIN, ZOOM_LEVEL_MAX, 256, ".png", mStringArray) {
            @Override
            public String getTileURLString(MapTile aTile) {
                return getBaseUrl() + aTile.getZoomLevel() + "/" + aTile.getX() + "/" + aTile.getY() + mImageFilenameEnding;
            }
        });

        ViewGroup mapContainer = (ViewGroup) view.findViewById(R.id.mapContainer);
        mapContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rootTurnOnGps = (ScrollView) view.findViewById(R.id.scrollView);
        rootTurnOnGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //have to empty
            }
        });
        vgMessageGps = (ViewGroup) view.findViewById(R.id.vgMessageGps);

        txtTextTurnOnOffGps = (TextView) view.findViewById(R.id.txtTextTurnOnOffGps);
        edtMessageGps = (EditText) view.findViewById(R.id.edtMessageGps);

        edtMessageGps.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edtMessageGps.setSingleLine(false);
                return false;
            }
        });
        toggleGps = (ToggleButton) view.findViewById(R.id.toggleGps);
        toggleGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isGpsOn) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                } else {
                    new MaterialDialog.Builder(mActivity).title(R.string.Visible_Status_title_dialog).content(R.string.Visible_Status_text_dialog).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            new RequestGeoRegister().register(true);
                        }
                    }).negativeText(R.string.no).onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            toggleGps.setChecked(false);
                        }
                    }).show();

                }

            }
        });

        prgWatingSendMessage = (ProgressBar) view.findViewById(R.id.prgWaitSendMessage);
        txtSendMessageGps = (TextView) view.findViewById(R.id.txtSendMessageGps);
        txtSendMessageGps.setTextColor(Color.parseColor(G.appBarColor));

        G.onGeoCommentResponse = new OnGeoCommentResponse() {
            @Override
            public void commentResponse() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        txtSendMessageGps.setVisibility(View.VISIBLE);
                        prgWatingSendMessage.setVisibility(View.GONE);
                        edtMessageGps.setEnabled(true);
                        edtMessageGps.setText("");
                    }
                });
            }

            @Override
            public void errorCommentResponse() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        txtSendMessageGps.setVisibility(View.VISIBLE);
                        prgWatingSendMessage.setVisibility(View.GONE);
                        edtMessageGps.setEnabled(true);
                    }
                });
            }

            @Override
            public void timeOutCommentResponse() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        txtSendMessageGps.setVisibility(View.VISIBLE);
                        prgWatingSendMessage.setVisibility(View.GONE);
                        edtMessageGps.setEnabled(true);
                    }
                });
            }
        };

        txtSendMessageGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtSendMessageGps.setVisibility(View.GONE);
                prgWatingSendMessage.setVisibility(View.VISIBLE);
                edtMessageGps.setEnabled(false);
                new RequestGeoUpdateComment().updateComment(edtMessageGps.getText().toString());
                //edtMessageGps.setText("");
            }
        });

        edtMessageGps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0) {
                    txtSendMessageGps.setVisibility(View.VISIBLE);
                } else {
                    txtSendMessageGps.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

                edtMessageGps.removeTextChangedListener(this);

                if (edtMessageGps.getLineCount() > 4) {
                    edtMessageGps.setText(specialRequests);
                    edtMessageGps.setSelection(lastSpecialRequestsCursorPosition);
                } else {
                    specialRequests = edtMessageGps.getText().toString();
                }

                edtMessageGps.addTextChangedListener(this);

            }
        });

        fabGps = (FloatingActionButton) view.findViewById(st_fab_gps);
        fabGps.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.appBarColor)));
        fabGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (location != null) {
                    currentLocation(location, false);
                } else {
                    new GPSTracker().detectLocation();
                }
            }
        });

        view.findViewById(R.id.backgroundToolbarMap).setBackgroundColor(Color.parseColor(G.appBarColor));
        fabGps.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.appBarColor)));

        btnBack = (RippleView) view.findViewById(R.id.ripple_back_map);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rippleMoreMap.getVisibility() == View.GONE || fabGps.getVisibility() == View.GONE) {
                    rippleMoreMap.setVisibility(View.VISIBLE);
                    fabGps.setVisibility(View.VISIBLE);
                }
                if (!isBackPress) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                closeKeyboard(v);
                isBackPress = false;
                page = pageiGapmap;
            }
        });

        rippleMoreMap = (RippleView) view.findViewById(R.id.ripple_more_map);

        rippleMoreMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final MaterialDialog dialog = new MaterialDialog.Builder(mActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
                View v = dialog.getCustomView();
                DialogAnimation.animationUp(dialog);
                dialog.show();


                ViewGroup root1 = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);
                ViewGroup root2 = (ViewGroup) v.findViewById(R.id.dialog_root_item2_notification);
                ViewGroup root3 = (ViewGroup) v.findViewById(R.id.dialog_root_item3_notification);

                root1.setVisibility(View.VISIBLE);
                root2.setVisibility(View.VISIBLE);
                root3.setVisibility(View.VISIBLE);

                TextView txtItem1 = (TextView) v.findViewById(R.id.dialog_text_item1_notification);
                TextView icon1 = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);
                txtItem1.setText(getResources().getString(R.string.list_user_map));
                icon1.setText(getResources().getString(R.string.md_delete_acc));

                TextView txtItem2 = (TextView) v.findViewById(R.id.dialog_text_item2_notification);
                TextView icon2 = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);
                txtItem2.setText(getResources().getString(R.string.nearby));
                icon2.setText(getResources().getString(R.string.md_delete_acc));


                TextView txtItem3 = (TextView) v.findViewById(R.id.dialog_text_item3_notification);
                TextView icon3 = (TextView) v.findViewById(R.id.dialog_icon_item3_notification);
                txtItem3.setText(getResources().getString(R.string.map_registration));
                icon3.setText(getResources().getString(R.string.md_delete_acc));


                root1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        fabGps.setVisibility(View.GONE);
                        rippleMoreMap.setVisibility(View.GONE);
                        page = pageUserList;
                        FragmentMapUsers fragmentMapUsers = FragmentMapUsers.newInstance();
                        try {
                            mActivity.getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.replace, fragmentMapUsers, "map_user_fragment").commitAllowingStateLoss();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                    }
                });

                root2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();

                        if (location != null) {
                            new RequestGeoGetNearbyCoordinate().getNearbyCoordinate(location.getLatitude(), location.getLongitude());
                        }

                    }
                });

                root3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();


                        new MaterialDialog.Builder(mActivity).title(R.string.Visible_Status_title_dialog).content(R.string.Visible_Status_text_dialog_invisible).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                new RequestGeoRegister().register(false);

                            }
                        }).negativeText(R.string.no).onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        }).show();


                        //final MaterialDialog dialog = new MaterialDialog.Builder(mActivity).customView(R.layout.dialog_map_registration, true).build();
                        //View v = dialog.getCustomView();
                        //if (v == null) {
                        //    return;
                        //}
                        //DialogAnimation.animationUp(dialog);
                        //dialog.show();
                        //btnMapChangeRegistration = (ToggleButton) v.findViewById(R.id.btnMapChangeRegistration);
                        //TextView txtMapRegister = (TextView) v.findViewById(R.id.txtMapRegister);
                        //TextView txtIconTurnOnOrOff = (TextView) v.findViewById(R.id.txtIconTurnOnOrOff);
                        //
                        //if (mapRegisterState) {
                        //    txtMapRegister.setText(G.context.getResources().getString(R.string.turn_off_map));
                        //    txtIconTurnOnOrOff.setText(getResources().getString(R.string.md_gap_eye_off));
                        //} else {
                        //    txtMapRegister.setText(G.context.getResources().getString(R.string.turn_on_map));
                        //    txtIconTurnOnOrOff.setText(getResources().getString(R.string.md_visibility));
                        //}
                        //btnMapChangeRegistration.setChecked(mapRegisterState);
                        //
                        //btnMapChangeRegistration.setOnClickListener(new View.OnClickListener() {
                        //    @Override
                        //    public void onClick(View v) {
                        //
                        //        if (mapRegisterState) {
                        //            new RequestGeoRegister().register(false);
                        //        } else {
                        //            new RequestGeoRegister().register(true);
                        //        }
                        //        dialog.dismiss();
                        //    }
                        //});
                    }
                });

            }
        });
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

        //String pathName = "";
        //Realm realm = Realm.getDefaultInstance();
        //for (RealmAvatar avatar : realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, G.userId).findAllSorted(RealmAvatarFields.ID, Sort.DESCENDING)) {
        //    if (avatar.getFile() != null) {
        //        pathName = avatar.getFile().getLocalFilePath();
        //    }
        //}
        //realm.close();
        //
        //Bitmap bitmap = BitmapFactory.decodeFile(pathName);
        //final Drawable drawableFinal = new BitmapDrawable(context.getResources(), getCircleBitmap(bitmap));

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                Marker marker = new Marker(map);
                marker.setPosition(new GeoPoint(mapItem.getPoint().getLatitude(), mapItem.getPoint().getLongitude()));
                if (G.userId != 0) {
                    if (hasComment) {
                        //marker.setIcon(drawableFinal);
                        marker.setIcon(context.getResources().getDrawable(R.drawable.location_mark_comment_yes));
                    } else {
                        //marker.setIcon(drawableFinal);
                        marker.setIcon(context.getResources().getDrawable(R.drawable.location_mark_comment_no));
                    }

                    InfoWindow infoWindow = new MyInfoWindow(map, userId, hasComment, FragmentiGapMap.this, mActivity);
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

    public static Bitmap getCircleBitmap(Bitmap bm) {

        int sice = Math.min((bm.getWidth() / 4), (bm.getHeight()) / 4);

        Bitmap bitmap = ThumbnailUtils.extractThumbnail(bm, sice, sice);

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //Bitmap output = Bitmap.createBitmap(bitmap.getWidth() / 2, bitmap.getHeight() / 2, Bitmap.Config.ARGB_8888);

        int halfWidth = bitmap.getWidth() / 2;
        int halfWidth3 = bitmap.getWidth() / 3;
        int halfHeight = bitmap.getHeight() / 2;
        int halfHeight3 = bitmap.getHeight() / 3;

        Canvas canvas = new Canvas(output);

        final int color = 0xffff0000;
        final Paint paint = new Paint();
        //final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final Rect rect = new Rect(halfWidth - halfWidth3, halfHeight - halfHeight3, halfWidth + halfWidth3, halfHeight + halfHeight3);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) 4);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * hint : call this method after fill location
     */
    private void getCoordinateLoop(final int delay, boolean loop) {
        if (loop && page == pageiGapmap) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    new RequestGeoGetNearbyCoordinate().getNearbyCoordinate(location.getLatitude(), location.getLongitude());
                    getCoordinateLoop(DEFAULT_LOOP_TIME, true);
                }
            }, delay);
        } else {
            new RequestGeoGetNearbyCoordinate().getNearbyCoordinate(location.getLatitude(), location.getLongitude());
        }
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
                drawMark(e, mapView);
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
            getCoordinateLoop(0, true);
        }

        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        OverlayItem overlayItem = new OverlayItem("title", "City", geoPoint);

        Drawable drawable = context.getResources().getDrawable(R.drawable.location_current);
        overlayItem.setMarker(drawable);

        ArrayList<OverlayItem> overlayItemArrayList = new ArrayList<>();
        overlayItemArrayList.add(overlayItem);
        ItemizedOverlay<OverlayItem> locationOverlay = new ItemizedIconOverlay<>(context, overlayItemArrayList, null);

        if (latestLocation != null) {
            map.getOverlays().remove(latestLocation);
        }

        latestLocation = locationOverlay;
        map.getOverlays().add(locationOverlay);

        if (BuildConfig.DEBUG) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Update Position", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onNearbyCoordinate(List<ProtoGeoGetNearbyCoordinate.GeoGetNearbyCoordinateResponse.Result> results) {
        for (Marker marker : markers) {
            map.getOverlays().remove(marker);
        }

        for (ProtoGeoGetNearbyCoordinate.GeoGetNearbyCoordinateResponse.Result result : results) {
            if (userId != result.getUserId()) { // don't show my account
                drawMark(result.getLat(), result.getLon(), result.getHasComment(), result.getUserId());
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//GPS is off

            visibleViewAttention(mActivity.getResources().getString(R.string.turn_on_gps_explain));

        } else {// GPS is on
            isGpsOn = true;
            if (mapRegisterState) {
                rootTurnOnGps.setVisibility(View.GONE);
                fabGps.setVisibility(View.VISIBLE);
                vgMessageGps.setVisibility(View.VISIBLE);
                rippleMoreMap.setVisibility(View.VISIBLE);
                new GPSTracker().detectLocation();
            } else {
                visibleViewAttention(mActivity.getResources().getString(R.string.Visible_Status_text));
            }

        }
    }

    private void visibleViewAttention(String text) {
        rootTurnOnGps.setVisibility(View.VISIBLE);
        fabGps.setVisibility(View.GONE);
        toggleGps.setChecked(false);
        vgMessageGps.setVisibility(View.GONE);
        rippleMoreMap.setVisibility(View.GONE);
        txtTextTurnOnOffGps.setText(text);
    }

    @Override
    public void onResume() {
        super.onResume();
        statusCheck();
        FragmentiGapMap.page = FragmentiGapMap.pageiGapmap;
    }

    private void closeKeyboard(View v) {
        if (isAdded()) {
            try {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    @Override
    public void onState(final boolean state) {
        mapRegisterState = state;

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                statusCheck();
                if (btnMapChangeRegistration != null) {
                    btnMapChangeRegistration.setChecked(state);
                }
            }
        });

    }

    @Override
    public void onClose() {
        if (mActivity != null) {
            mActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    @Override
    public void onGetComment(final long userIdR, final String comment) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (G.userId == userIdR && comment.length() > 0) {
                    edtMessageGps.setText(comment);
                    edtMessageGps.setSingleLine(true);
                }
            }
        });
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        if (map.getZoomLevel() == ZOOM_LEVEL_MAX) {
            map.getController().zoomTo(ZOOM_LEVEL_NORMAL);
        }
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        Log.i("TTT", "onShowPress");
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
