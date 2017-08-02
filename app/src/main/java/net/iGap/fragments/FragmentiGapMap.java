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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ToggleButton;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import io.realm.Sort;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.interfaces.OnGeoCommentResponse;
import net.iGap.interfaces.OnGeoGetComment;
import net.iGap.interfaces.OnGetNearbyCoordinate;
import net.iGap.interfaces.OnInfo;
import net.iGap.interfaces.OnLocationChanged;
import net.iGap.interfaces.OnMapClose;
import net.iGap.interfaces.OnMapRegisterState;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.DialogAnimation;
import net.iGap.module.FileUtils;
import net.iGap.module.GPSTracker;
import net.iGap.module.MyInfoWindow;
import net.iGap.proto.ProtoGeoGetNearbyCoordinate;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.request.RequestGeoGetComment;
import net.iGap.request.RequestGeoGetNearbyCoordinate;
import net.iGap.request.RequestGeoRegister;
import net.iGap.request.RequestGeoUpdateComment;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
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
    public static ArrayList<String> mapUrls = new ArrayList<>();
    private ScrollView rootTurnOnGps;
    private ViewGroup vgMessageGps;
    public static Location location;
    public static RippleView btnBack;
    public static RippleView rippleMoreMap;
    public static boolean isBackPress = false;
    private ToggleButton toggleGps;
    private ToggleButton btnMapChangeRegistration;
    private TextView txtTextTurnOnOffGps;
    private TextView txtSendMessageGps;
    private EditText edtMessageGps;
    private FloatingActionButton fabGps;
    private ProgressBar prgWaitingSendMessage;
    private FragmentActivity mActivity;
    private ItemizedIconOverlay<OverlayItem> itemizedIconOverlay = null;
    private GestureDetector mGestureDetector;

    private String specialRequests;

    private boolean firstEnter = true;
    private boolean canUpdate = true;
    private boolean isGpsOn = false;
    private boolean first = true;
    public static boolean mapRegistrationStatus;

    private final double LONGITUDE_LIMIT = 0.011; // 0.0552 (5 KiloMeters) , 0.011 (1 KiloMeters)
    private final double LATITUDE_LIMIT = 0.009; // 0.0451 (5 KiloMeters) , 0.009 (1 KiloMeters)
    private double northLimitation;
    private double eastLimitation;
    private double southLimitation;
    private double westLimitation;
    private double lastLatitude;
    private double lastLongitude;
    private double lat1;
    private double lon1;

    public static int page;
    public static final int pageiGapMap = 1;
    public static final int pageUserList = 2;
    private final int DEFAULT_LOOP_TIME = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    private final int ZOOM_LEVEL_MIN = 13;
    private final int ZOOM_LEVEL_NORMAL = 16;
    private final int ZOOM_LEVEL_MAX = 19;
    private final int BOUND_LIMIT_METERS = 5000;
    private int lastSpecialRequestsCursorPosition = 0;

    private long latestUpdateTime = 0;
    long firstTap = 0;
    private boolean isEndLine = true;
    private String txtComment;

    public static FragmentiGapMap getInstance() {
        return new FragmentiGapMap();
    }

    public enum MarkerColor {
        GRAY, GREEN
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
        //clickDrawMarkActive();

        page = 1;
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
        String url = mapUrls.get(new Random().nextInt(mapUrls.size()));
        map.setTileSource(new OnlineTileSourceBase("USGS Topo", ZOOM_LEVEL_MIN, ZOOM_LEVEL_MAX, 256, ".png", new String[]{url}) {
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

        prgWaitingSendMessage = (ProgressBar) view.findViewById(R.id.prgWaitSendMessage);
        txtSendMessageGps = (TextView) view.findViewById(R.id.txtSendMessageGps);
        txtSendMessageGps.setText(G.context.getString(R.string.md_close_button));
        txtSendMessageGps.setTextColor(getResources().getColor(R.color.gray_4c));

        G.onGeoCommentResponse = new OnGeoCommentResponse() {
            @Override
            public void commentResponse() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {


                        txtComment = edtMessageGps.getText().toString();
                        if (edtMessageGps.length() > 0) {
                            txtSendMessageGps.setVisibility(View.VISIBLE);
                        } else {
                            txtSendMessageGps.setVisibility(View.GONE);
                        }
                        prgWaitingSendMessage.setVisibility(View.GONE);
                        txtSendMessageGps.setText(getString(R.string.md_close_button));
                        txtSendMessageGps.setTextColor(getResources().getColor(R.color.gray_4c));
                        edtMessageGps.setEnabled(true);
                    }
                });
            }

            @Override
            public void errorCommentResponse() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        txtSendMessageGps.setVisibility(View.VISIBLE);
                        prgWaitingSendMessage.setVisibility(View.GONE);
                        txtSendMessageGps.setText(getString(R.string.md_close_button));
                        txtSendMessageGps.setTextColor(getResources().getColor(R.color.gray_4c));
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
                        txtSendMessageGps.setText(G.context.getString(R.string.md_close_button));
                        txtSendMessageGps.setTextColor(getResources().getColor(R.color.gray_4c));
                        prgWaitingSendMessage.setVisibility(View.GONE);
                        edtMessageGps.setEnabled(true);
                    }
                });
            }
        };

        txtSendMessageGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (txtSendMessageGps.getText().toString().contains(getResources().getString(R.string.md_close_button))) {
                    new RequestGeoUpdateComment().updateComment("");
                    edtMessageGps.setText("");
                    txtSendMessageGps.setVisibility(View.GONE);
                    txtSendMessageGps.setText(getString(R.string.md_close_button));
                    txtSendMessageGps.setTextColor(getResources().getColor(R.color.gray_4c));
                } else {
                    txtSendMessageGps.setVisibility(View.GONE);
                    prgWaitingSendMessage.setVisibility(View.VISIBLE);
                    edtMessageGps.setEnabled(false);
                    new RequestGeoUpdateComment().updateComment(edtMessageGps.getText().toString());
                }


                //edtMessageGps.setText("");
            }
        });

        final String beforChangeComment = edtMessageGps.getText().toString();

        edtMessageGps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (s.length() > 0) {

                    if (!txtComment.equals(s.toString())) {
                        txtSendMessageGps.setVisibility(View.VISIBLE);
                        txtSendMessageGps.setText(G.context.getString(R.string.md_igap_check));
                        txtSendMessageGps.setTextColor(Color.parseColor(G.appBarColor));
                    } else {
                        txtSendMessageGps.setVisibility(View.VISIBLE);
                        txtSendMessageGps.setText(getString(R.string.md_close_button));
                        txtSendMessageGps.setTextColor(getResources().getColor(R.color.gray_4c));
                    }
                } else {
                    txtSendMessageGps.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                edtMessageGps.removeTextChangedListener(this);
                if (edtMessageGps.getLineCount() > 4) {
                    edtMessageGps.setText(specialRequests);
                    //edtMessageGps.setSelection(lastSpecialRequestsCursorPosition);

                    if (isEndLine) {
                        Log.i("CCCCCCCCCC", "afterTextChanged: " + isEndLine);
                        isEndLine = false;
                        shoSnackBar(getResources().getString(R.string.please_try_again));
                    }
                } else {
                    isEndLine = true;
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
                    GPSTracker.getGpsTrackerInstance().detectLocation();
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
                page = pageiGapMap;
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
                icon1.setText(getResources().getString(R.string.md_nearby));

                TextView txtItem2 = (TextView) v.findViewById(R.id.dialog_text_item2_notification);
                TextView icon2 = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);
                txtItem2.setText(getResources().getString(R.string.nearby));
                icon2.setText(getResources().getString(R.string.md_igap_map_marker_multiple));


                TextView txtItem3 = (TextView) v.findViewById(R.id.dialog_text_item3_notification);
                TextView icon3 = (TextView) v.findViewById(R.id.dialog_icon_item3_notification);
                txtItem3.setText(getResources().getString(R.string.map_registration));
                icon3.setText(getResources().getString(R.string.md_igap_map_marker_off));


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

    private void shoSnackBar(final String message) {
        G.currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Snackbar snack = Snackbar.make(mActivity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);

                snack.setAction(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snack.dismiss();
                    }
                });
                snack.show();
            }
        });
    }

    /**
     * ****************************** methods ******************************
     */

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
        double[] bound = getBoundingBox(location.getLatitude(), location.getLongitude(), BOUND_LIMIT_METERS);
        northLimitation = bound[2];
        eastLimitation = bound[3];
        southLimitation = bound[0];
        westLimitation = bound[1];
        BoundingBoxE6 bBox = new BoundingBoxE6(bound[2] + extraBounding, bound[3] + extraBounding, bound[0] - extraBounding, bound[1] - extraBounding);
        map.setScrollableAreaLimit(bBox);
    }

    private double[] getBoundingBox(final double pLatitude, final double pLongitude, final int pDistanceInMeters) {
        final double[] boundingBox = new double[4];
        final double latRadian = Math.toRadians(pLatitude);
        final double degLatKm = 110.574235;
        final double degLongKm = 110.572833 * Math.cos(latRadian);
        final double deltaLat = pDistanceInMeters / 1000.0 / degLatKm;
        final double deltaLong = pDistanceInMeters / 1000.0 / degLongKm;

        final double minLat = pLatitude - deltaLat;
        final double minLong = pLongitude - deltaLong;
        final double maxLat = pLatitude + deltaLat;
        final double maxLong = pLongitude + deltaLong;

        boundingBox[0] = minLat; // south
        boundingBox[1] = minLong; // west
        boundingBox[2] = maxLat; // north
        boundingBox[3] = maxLong; // east

        return boundingBox;
    }

    public static void deleteMapFileCash() {
        try {

            IConfigurationProvider configurationProvider = Configuration.getInstance();
            FileUtils.deleteRecursive((configurationProvider.getOsmdroidBasePath()));
        } catch (Exception e) {
            Log.e("debug", "FragmentiGapMap     deleteMapFileCash()    " + e.toString());
        }
    }

    /**
     * ****************************** draw in map ******************************
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

    private void drawMark(final OverlayItem mapItem, final boolean hasComment, final long userIdR) {

        if (userIdR == 0) {
            return;
        }

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                Marker marker = new Marker(map);
                marker.setPosition(new GeoPoint(mapItem.getPoint().getLatitude(), mapItem.getPoint().getLongitude()));
                InfoWindow infoWindow;
                marker.setIcon(avatarMark(userIdR, MarkerColor.GRAY));
                infoWindow = new MyInfoWindow(map, marker, userIdR, hasComment, FragmentiGapMap.this, mActivity);
                marker.setInfoWindow(infoWindow);

                markers.add(marker);
                map.getOverlays().add(marker);
                map.invalidate();
            }
        });
    }

    public static Drawable avatarMark(long userId, MarkerColor markerColor) {
        String pathName = "";
        String initials = "";
        String color = "";
        Bitmap bitmap = null;
        Realm realm = Realm.getDefaultInstance();
        for (RealmAvatar avatar : realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).findAllSorted(RealmAvatarFields.ID, Sort.DESCENDING)) {
            if (avatar.getFile() != null) {
                pathName = avatar.getFile().getLocalFilePath();
            }
        }
        if (pathName == null || pathName.isEmpty()) {
            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();
            if (realmRegisteredInfo != null) {
                initials = realmRegisteredInfo.getInitials();
                color = realmRegisteredInfo.getColor();
            }
            bitmap = HelperImageBackColor.drawAlphabetOnPicture((int) G.context.getResources().getDimension(R.dimen.dp60), initials, color);
        } else {
            bitmap = BitmapFactory.decodeFile(pathName);
        }
        realm.close();

        boolean mineAvatar = false;
        if (userId == G.userId) {
            mineAvatar = true;
        }

        return new BitmapDrawable(context.getResources(), drawAvatar(bitmap, markerColor, mineAvatar));
    }

    private static Bitmap drawAvatar(Bitmap bm, MarkerColor markerColor, boolean mineAvatar) {
        Bitmap bitmap = getCircleBitmap(bm, mineAvatar);
        int firstBorderColor;
        int firstBorderSize;
        int secondBoarderColor;
        int secondBoarderSize;
        int thirdBoarderColor;
        int thirdBoarderSize;
        if (mineAvatar) {
            firstBorderColor = Color.parseColor("#f23131");
            secondBoarderColor = Color.parseColor("#55f23131");
            thirdBoarderColor = Color.parseColor("#f23131");

            firstBorderSize = 2;
            secondBoarderSize = (int) G.context.getResources().getDimension(R.dimen.dp6);
            thirdBoarderSize = 2;
        } else {
            if (markerColor == MarkerColor.GREEN) {
                firstBorderColor = Color.WHITE;
                secondBoarderColor = Color.parseColor("#553dbcb3");
                thirdBoarderColor = G.context.getResources().getColor(R.color.primary);

                firstBorderSize = 2;
                secondBoarderSize = (int) G.context.getResources().getDimension(R.dimen.dp18);
                thirdBoarderSize = 2;
            } else {
                firstBorderColor = Color.WHITE;
                secondBoarderColor = Color.parseColor("#554f4f4f");
                thirdBoarderColor = G.context.getResources().getColor(R.color.colorOldBlack);

                firstBorderSize = 2;
                secondBoarderSize = (int) G.context.getResources().getDimension(R.dimen.dp10);
                thirdBoarderSize = 2;
            }
        }

        bitmap = addBorderToCircularBitmap(bitmap, firstBorderSize, firstBorderColor);
        bitmap = addBorderToCircularBitmap(bitmap, secondBoarderSize, secondBoarderColor);
        bitmap = addBorderToCircularBitmap(bitmap, thirdBoarderSize, thirdBoarderColor);
        return bitmap;
    }

    protected static Bitmap getCircleBitmap(Bitmap bm, boolean mineAvatar) {
        int sice;
        if (mineAvatar) {
            sice = Math.min((int) G.context.getResources().getDimension(R.dimen.dp10), (int) G.context.getResources().getDimension(R.dimen.dp10));
        } else {
            sice = Math.min((int) G.context.getResources().getDimension(R.dimen.dp24), (int) G.context.getResources().getDimension(R.dimen.dp24));
        }
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(bm, sice, sice);
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(G.context.getResources().getColor(R.color.primary));
        canvas.drawOval(rectF, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) 4);
        if (mineAvatar) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        } else {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        }
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    protected static Bitmap addBorderToCircularBitmap(Bitmap srcBitmap, int borderWidth, int borderColor) {
        int dstBitmapWidth = srcBitmap.getWidth() + borderWidth * 2;
        Bitmap dstBitmap = Bitmap.createBitmap(dstBitmapWidth, dstBitmapWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dstBitmap);
        canvas.drawBitmap(srcBitmap, borderWidth, borderWidth, null);
        Paint paint = new Paint();
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        paint.setAntiAlias(true);
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getWidth() / 2, canvas.getWidth() / 2 - borderWidth / 2, paint);
        srcBitmap.recycle();
        return dstBitmap;
    }

    /**
     * hint : call this method after fill location
     */
    private void getCoordinateLoop(final int delay, boolean loop) {
        if (loop && page == pageiGapMap) {
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
            getCoordinateLoop(0, false);
        }
        mapBounding(location);
        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        OverlayItem overlayItem = new OverlayItem("title", "City", geoPoint);

        //Drawable drawable = context.getResources().getDrawable(R.drawable.location_current);
        overlayItem.setMarker(avatarMark(G.userId, MarkerColor.GRAY)); // marker color is not important in this line because for mineAvatar will be unused.

        ArrayList<OverlayItem> overlayItemArrayList = new ArrayList<>();
        overlayItemArrayList.add(overlayItem);
        ItemizedOverlay<OverlayItem> locationOverlay = new ItemizedIconOverlay<>(context, overlayItemArrayList, null);

        if (latestLocation != null) {
            map.getOverlays().remove(latestLocation);
        }

        latestLocation = locationOverlay;
        map.getOverlays().add(locationOverlay);

        //if (BuildConfig.DEBUG) {
        //    G.handler.post(new Runnable() {
        //        @Override
        //        public void run() {
        //            Toast.makeText(context, "Update Position", Toast.LENGTH_SHORT).show();
        //        }
        //    });
        //}
    }

    @Override
    public void onNearbyCoordinate(List<ProtoGeoGetNearbyCoordinate.GeoGetNearbyCoordinateResponse.Result> results) {
        map.getOverlays().removeAll(markers);

        for (final ProtoGeoGetNearbyCoordinate.GeoGetNearbyCoordinateResponse.Result result : results) {
            if (G.userId != result.getUserId()) { // don't show mine
                RealmRegisteredInfo.getRegistrationInfo(result.getUserId(), new OnInfo() {
                    @Override
                    public void onInfo(RealmRegisteredInfo registeredInfo) {
                        drawMark(result.getLat(), result.getLon(), result.getHasComment(), result.getUserId());
                    }
                });
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
            if (mapRegistrationStatus) {
                rootTurnOnGps.setVisibility(View.GONE);
                fabGps.setVisibility(View.VISIBLE);
                vgMessageGps.setVisibility(View.VISIBLE);
                rippleMoreMap.setVisibility(View.VISIBLE);
                GPSTracker.getGpsTrackerInstance().detectLocation();
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
        FragmentiGapMap.page = FragmentiGapMap.pageiGapMap;
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
                txtComment = comment;
                if (G.userId == userIdR && comment.length() > 0) {
                    edtMessageGps.setText(comment);
                    txtSendMessageGps.setText(getString(R.string.md_close_button));
                    txtSendMessageGps.setTextColor(getResources().getColor(R.color.gray_4c));
                } else {
                    txtSendMessageGps.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        if (map.getZoomLevel() == ZOOM_LEVEL_MAX) {
            map.getController().zoomTo(ZOOM_LEVEL_MAX - 1);
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
