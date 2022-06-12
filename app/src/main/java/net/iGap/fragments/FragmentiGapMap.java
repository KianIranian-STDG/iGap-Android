/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.fragments;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.KeyboardUtils;
import net.iGap.libs.floatingAddButton.ArcMenu;
import net.iGap.libs.floatingAddButton.StateChangeListener;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.messenger.ui.toolBar.ToolbarItems;
import net.iGap.module.FileUtils;
import net.iGap.module.GPSTracker;
import net.iGap.module.MyInfoWindow;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.dialog.BottomSheetItemClickCallback;
import net.iGap.module.dialog.topsheet.TopSheetDialog;
import net.iGap.observers.interfaces.OnGeoCommentResponse;
import net.iGap.observers.interfaces.OnGeoGetComment;
import net.iGap.observers.interfaces.OnGetNearbyCoordinate;
import net.iGap.observers.interfaces.OnInfo;
import net.iGap.observers.interfaces.OnLocationChanged;
import net.iGap.observers.interfaces.OnMapClose;
import net.iGap.observers.interfaces.OnMapRegisterState;
import net.iGap.observers.interfaces.OnMapUsersGet;
import net.iGap.proto.ProtoGeoGetNearbyCoordinate;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmGeoNearbyDistance;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.request.RequestGeoGetComment;
import net.iGap.request.RequestGeoGetNearbyCoordinate;
import net.iGap.request.RequestGeoRegister;
import net.iGap.request.RequestGeoUpdateComment;
import net.iGap.request.RequestGeoUpdatePosition;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
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
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.Sort;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.Config.URL_MAP;
import static net.iGap.R.id.st_fab_gps;

public class FragmentiGapMap extends BaseFragment implements OnLocationChanged, OnGetNearbyCoordinate, OnMapRegisterState, OnMapClose, OnMapUsersGet, OnGeoGetComment, GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener {

    private final static int done_button = 1;
    private View doneButton;

    public static final int pageiGapMap = 1;
    public static final int pageUserList = 2;
    public static ArrayList<String> mapUrls = new ArrayList<>();
    public static Location location;
    public static RippleView btnBack;
    public static RippleView rippleMoreMap;
    public static boolean isBackPress = false;
    public static FloatingActionButton fabGps, btnSatelliteView, btnOrginView;
    public static ArcMenu fabStateSwitcher;
    public static Location mineStaticLocation;
    public static boolean mapRegistrationStatus;
    private ViewPager mViewPager;
    public static int page;
    private final double LONGITUDE_LIMIT = 0.011;
    private final double LATITUDE_LIMIT = 0.009;
    private final int DEFAULT_LOOP_TIME = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    private final int ZOOM_LEVEL_MIN = 16;
    private final int ZOOM_LEVEL_NORMAL = 16;
    private final int ZOOM_LEVEL_MAX = 19;
    private final int BOUND_LIMIT_METERS = 5000;
    public static boolean isMenuButtonAddShown = false;
    private MapView map;
    private ItemizedOverlay<OverlayItem> latestLocation;
    private ArrayList<Marker> markers = new ArrayList<>();
    private ScrollView rootTurnOnGps;
    private ViewGroup vgMessageGps;
    private ToggleButton toggleGps;
    private ToggleButton btnMapChangeRegistration;
    private TextView txtTextTurnOnOffGps;
    private TextView txtDescriptionMap;
    private TextView txtSendMessageGps;
    private EditText edtMessageGps;
    private ProgressBar prgWaitingSendMessage;
    private ProgressBar prgWaitingGetUser;
    private String specialRequests;
    private boolean firstEnter = true;
    private boolean canUpdate = true;
    private boolean isGpsOn = false;
    private boolean first = true;
    private double northLimitation;
    private double eastLimitation;
    private double southLimitation;
    private double westLimitation;
    private double lastLatitude;
    private double lastLongitude;
    private boolean isEndLine = true;
    private String txtComment = "";
    private boolean isSendRequestGeoCoordinate = false;
    private String url;
    static boolean changeState = false;
    private int orientation = G.rotationState;
    private TopSheetDialog dialog;

    private Toolbar mHelperToolbar;
    private final int toolbarDotsTag = 1;


    private FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

    public static FragmentiGapMap getInstance() {
        return new FragmentiGapMap();
    }

    public static void deleteMapFileCash() {
        try {
            IConfigurationProvider configurationProvider = Configuration.getInstance();
            FileUtils.deleteRecursive((configurationProvider.getOsmdroidBasePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Drawable avatarMark(long userId, MarkerColor markerColor) {
        Bitmap bitmap = DbManager.getInstance().doRealmTask(realm -> {
            String pathName = "";
            Bitmap bitmap1 = null;
            for (RealmAvatar avatar : realm.where(RealmAvatar.class).equalTo("ownerId", userId).findAll().sort("id", Sort.DESCENDING)) {
                if (avatar.getFile() != null) {
                    pathName = avatar.getFile().getLocalFilePath();
                    if (pathName == null) {
                        pathName = avatar.getFile().getLocalThumbnailPath();
                    }
                    break;
                }
            }
            if (pathName == null || pathName.isEmpty()) {
                bitmap1 = getInitials(realm, userId);
            } else {
                try {
                    File imgFile = new File(pathName);
                    bitmap1 = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                } catch (OutOfMemoryError e) {
                    try {
                        File imgFile = new File(pathName);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        bitmap1 = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
                    } catch (OutOfMemoryError e1) {
                        try {
                            File imgFile = new File(pathName);
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 4;
                            bitmap1 = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
                        } catch (OutOfMemoryError e2) {
                            e2.printStackTrace();
                        }
                    }
                }

                if (bitmap1 == null) {
                    bitmap1 = getInitials(realm, userId);
                }
            }
            return bitmap1;
        });
        return new BitmapDrawable(G.context.getResources(), drawAvatar(bitmap, markerColor, userId == AccountManager.getInstance().getCurrentUser().getId()));
    }

    private static Bitmap getInitials(Realm realm, long userId) {
        String initials = "";
        String color = "";
        RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);
        if (realmRegisteredInfo != null) {
            initials = realmRegisteredInfo.getInitials();
            color = realmRegisteredInfo.getColor();
        }
        return HelperImageBackColor.drawAlphabetOnPicture((int) G.context.getResources().getDimension(R.dimen.dp60), initials, color);
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
            thirdBoarderColor = Color.parseColor("#00f23131");

            firstBorderSize = (int) G.context.getResources().getDimension(R.dimen.dp2);
            secondBoarderSize = (int) G.context.getResources().getDimension(R.dimen.dp32);
            thirdBoarderSize = (int) G.context.getResources().getDimension(R.dimen.dp2);
        } else {
            if (markerColor == MarkerColor.GREEN) {
                firstBorderColor = Color.WHITE;
                secondBoarderColor = Color.parseColor("#553dbcb3");
                thirdBoarderColor = Theme.getColor(Theme.key_light_theme_color);

                firstBorderSize = (int) G.context.getResources().getDimension(R.dimen.dp2);
                secondBoarderSize = (int) G.context.getResources().getDimension(R.dimen.dp18);
                thirdBoarderSize = (int) G.context.getResources().getDimension(R.dimen.dp2);
            } else {
                firstBorderColor = Color.WHITE;
                secondBoarderColor = Color.parseColor("#554f4f4f");
                //thirdBoarderColor = G.context.getResources().getColor(R.color.colorOldBlack);
                thirdBoarderColor = Color.parseColor("#004f4f4f");

                firstBorderSize = (int) G.context.getResources().getDimension(R.dimen.dp2);
                secondBoarderSize = (int) G.context.getResources().getDimension(R.dimen.dp10);
                thirdBoarderSize = (int) G.context.getResources().getDimension(R.dimen.dp2);
            }
        }

        bitmap = addBorderToCircularBitmap(bitmap, firstBorderSize, firstBorderColor);
        if (mineAvatar) {
            //bitmap = addBorderToCircularBitmap(bitmap, secondBoarderSize, secondBoarderColor);
        } else {
            bitmap = addBorderToCircularBitmapSharp(bitmap, secondBoarderSize, secondBoarderColor);
        }
        bitmap = addBorderToCircularBitmap(bitmap, thirdBoarderSize, thirdBoarderColor);
        return bitmap;
    }

    protected static Bitmap getCircleBitmap(Bitmap bm, boolean mineAvatar) {
        int sice;
        if (mineAvatar) {
            sice = Math.min((int) G.context.getResources().getDimension(R.dimen.dp10), (int) G.context.getResources().getDimension(R.dimen.dp10));
        } else {
            sice = Math.min((int) G.context.getResources().getDimension(R.dimen.dp32), (int) G.context.getResources().getDimension(R.dimen.dp32));
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
        paint.setColor(Color.parseColor("#f23131"));
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
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getWidth() / 2, canvas.getWidth() / 2 - (borderWidth / 2 + G.context.getResources().getDimension(R.dimen.dp1)), paint);
        if (!srcBitmap.isRecycled()) {
            srcBitmap.recycle();
            srcBitmap = null;
        }
        return dstBitmap;
    }

    protected static Bitmap addBorderToCircularBitmapSharp(Bitmap srcBitmap, int borderWidth, int borderColor) {
        int dstBitmapWidth = srcBitmap.getWidth() + borderWidth * 2;
        Bitmap dstBitmap = Bitmap.createBitmap(dstBitmapWidth, dstBitmapWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dstBitmap);
        canvas.drawBitmap(srcBitmap, borderWidth, borderWidth, null);
        Paint paint = new Paint();
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));//DST_OUT

        Paint paintSharp = new Paint();
        paintSharp.setColor(Color.WHITE);
        paintSharp.setStyle(Paint.Style.FILL);
        paintSharp.setStrokeWidth(borderWidth);
        paintSharp.setAntiAlias(true);

        Path path1 = new Path();
        path1.moveTo(borderWidth + G.context.getResources().getDimension(R.dimen.dp1), canvas.getWidth() / 2);// first point
        path1.lineTo(canvas.getWidth() - borderWidth - G.context.getResources().getDimension(R.dimen.dp1), canvas.getWidth() / 2);
        path1.lineTo((canvas.getWidth() / 2), srcBitmap.getWidth() + borderWidth + (srcBitmap.getWidth() / 8));
        path1.lineTo(borderWidth + G.context.getResources().getDimension(R.dimen.dp1), canvas.getWidth() / 2);// last point
        path1.close();
        paintSharp.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));//DST_OVER
        canvas.drawPath(path1, paintSharp);

        canvas.drawCircle(canvas.getWidth() / 2, canvas.getWidth() / 2, canvas.getWidth() / 2 - (borderWidth / 2 + G.context.getResources().getDimension(R.dimen.dp1)), paint);

        if (!srcBitmap.isRecycled()) {
            srcBitmap.recycle();
            srcBitmap = null;
        }
        return dstBitmap;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));

        KeyboardUtils.addKeyboardToggleListener(getActivity(), new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                if (isVisible && fabStateSwitcher.isMenuOpened()) {
                    fabStateSwitcher.toggleMenu();
                }

                if (isVisible && orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
                    btnSatelliteView.hide();
                    btnOrginView.hide();
                    fabGps.hide();
                } else {
                    btnSatelliteView.show();
                    btnOrginView.show();
                    fabGps.show();
                }
            }
        });

        return inflater.inflate(R.layout.fragment_igap_map, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HelperTracker.sendTracker(HelperTracker.TRACKER_NEARBY_PAGE);
        G.onLocationChanged = this;
        G.onGetNearbyCoordinate = this;
        G.onMapRegisterState = this;
        G.onMapClose = this;
        G.onGeoGetComment = this;
        G.onMapUsersGet = this;
        attentionDialog();
        map = view.findViewById(R.id.map);

        startMap(view);
        //clickDrawMarkActive();


        fabStateSwitcher = view.findViewById(R.id.st_fab_state);


      /*  fabStateSwitcher.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.fabBottom)));
        fabStateSwitcher.setColorFilter(Color.WHITE);*/
        fabStateSwitcher.setBackgroundTintColor();
        fabStateSwitcher.setFabSize();

        fabStateSwitcher.setStateChangeListener(new StateChangeListener() {
            @Override
            public void onMenuOpened() {

            }

            @Override
            public void onMenuClosed() {
                isMenuButtonAddShown = false;
            }
        });


        btnOrginView = view.findViewById(R.id.ac_fab_orgin);
        btnOrginView.setBackgroundTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_default_text)));

        btnOrginView.setOnClickListener(view12 -> {


            deleteMapFileCash();
            if (getActivity() != null && isAdded()) {
                changeState = getActivity().getSharedPreferences("KEY_SWITCH_MAP_STATE", Context.MODE_PRIVATE)
                        .getBoolean("state", false);


                if (!changeState) {
                    deleteMapFileCash();
                    getActivity().getSharedPreferences("KEY_SWITCH_MAP_STATE", Context.MODE_PRIVATE).edit().putBoolean("state", true).apply();

                    new HelperFragment(G.currentActivity.getSupportFragmentManager(), FragmentiGapMap.getInstance()).setImmediateRemove(true).remove();

                    new HelperFragment(G.currentActivity.getSupportFragmentManager(), FragmentiGapMap.getInstance()).load();
                }

            }

            if (fabStateSwitcher.isMenuOpened()) {
                fabStateSwitcher.toggleMenu();
            }
        });

        btnSatelliteView = view.findViewById(R.id.ac_fab_satellite);
        btnSatelliteView.setBackgroundTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_theme_color)));
        btnSatelliteView.setOnClickListener(view1 -> {
            if (getActivity() != null && isAdded()) {
                changeState = getActivity().getSharedPreferences("KEY_SWITCH_MAP_STATE", Context.MODE_PRIVATE)
                        .getBoolean("state", false);


                if (changeState) {
                    deleteMapFileCash();
                    getActivity().getSharedPreferences("KEY_SWITCH_MAP_STATE", Context.MODE_PRIVATE).edit().putBoolean("state", false).apply();
                    new HelperFragment(getActivity().getSupportFragmentManager(), FragmentiGapMap.getInstance()).setImmediateRemove(true).remove();
                    if (getActivity() != null) {
                        new HelperFragment(getActivity().getSupportFragmentManager(), FragmentiGapMap.getInstance()).load();
                    }
                }
            }
            if (fabStateSwitcher.isMenuOpened()) {
                fabStateSwitcher.toggleMenu();
            }

        });

        fabStateSwitcher.fabMenu.setOnClickListener(v -> fabStateSwitcher.toggleMenu());

       /* if (HelperCalander.isPersianUnicode)
            fabStateSwitcher.chanegMenuItem(false);
        else
            fabStateSwitcher.chanegMenuItem(true);*/

       /* fabStateSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
                View v = dialog.getCustomView();
                *//* DialogAnimation.animationUp(dialog);*//*
                dialog.getWindow().setLayout(ViewMaker.dpToPx(220), WindowManager.LayoutParams.WRAP_CONTENT);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;

                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();


                if (G.selectedLanguage.equals("en")) {
                    wmlp.gravity = Gravity.TOP | Gravity.LEFT;

                    wmlp.x = ViewMaker.dpToPx(10);   //x position
                } else {
                    wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
                    wmlp.x = ViewMaker.dpToPx(10);   //x position
                }


                int s = getActivity().getChangingConfigurations();


              *//*  if (orientation==0){
                   if (G.isLandscape)
                       wmlp.y = ViewMaker.dpToPx(160);
                       else
                       wmlp.y = ViewMaker.dpToPx(400);

                }else *//*
                if (orientation == 1 || orientation == 0) {
                    //Do some stuff
                    wmlp.y = ViewMaker.dpToPx(400);   //y

                } else if (orientation == 2) {
                    //Do some stuff
                    wmlp.y = ViewMaker.dpToPx(160);   //y
                }

                dialog.show();


                ViewGroup root1 = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);
                ViewGroup root2 = (ViewGroup) v.findViewById(R.id.dialog_root_item2_notification);
                ViewGroup root3 = (ViewGroup) v.findViewById(R.id.dialog_root_item3_notification);

                root1.setVisibility(View.GONE);
                root2.setVisibility(View.VISIBLE);
                root3.setVisibility(View.VISIBLE);

                TextView txtItem1 = (TextView) v.findViewById(R.id.dialog_text_item1_notification);
                TextView icon1 = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);
                txtItem1.setText(G.fragmentActivity.getResources().getString(R.string.satellite_view));
                icon1.setText(G.fragmentActivity.getResources().getString(R.string.md_nearby));

                TextView txtItem2 = (TextView) v.findViewById(R.id.dialog_text_item2_notification);
                TextView icon2 = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);
                txtItem2.setText(G.fragmentActivity.getResources().getString(R.string.default_view));
                icon2.setText(G.fragmentActivity.getResources().getString(R.string.md_map));


                TextView txtItem3 = (TextView) v.findViewById(R.id.dialog_text_item3_notification);
                TextView icon3 = (TextView) v.findViewById(R.id.dialog_icon_item3_notification);
                txtItem3.setText(G.fragmentActivity.getResources().getString(R.string.satellite_view));
                icon3.setText(G.fragmentActivity.getResources().getString(R.string.md_satellite_variant));

                root1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        fabGps.setVisibility(View.GONE);
                        fabStateSwitcher.setVisibility(View.GONE);
                        rippleMoreMap.setVisibility(View.GONE);
                        page = pageUserList;
                        try {
                            new HelperFragment(FragmentMapUsers.newInstance()).setResourceContainer(R.id.mapContainer_main).setReplace(false).load();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                    }
                });

                root2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();


                        if (isAdded()) {
                            changeState = getActivity().getSharedPreferences("KEY_SWITCH_MAP_STATE", Context.MODE_PRIVATE)
                                    .getBoolean("state", false);


                            if (changeState) {
                                deleteMapFileCash();
                                getActivity().getSharedPreferences("KEY_SWITCH_MAP_STATE", Context.MODE_PRIVATE).edit().putBoolean("state", false).apply();

                                new HelperFragment(FragmentiGapMap.getInstance()).remove();

                                new HelperFragment(FragmentiGapMap.getInstance()).load();
                            }

                        }

                    }
                });

                root3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();

                        dialog.dismiss();

                        deleteMapFileCash();
                        if (isAdded()) {
                            changeState = getActivity().getSharedPreferences("KEY_SWITCH_MAP_STATE", Context.MODE_PRIVATE)
                                    .getBoolean("state", false);


                            if (!changeState) {
                                deleteMapFileCash();
                                getActivity().getSharedPreferences("KEY_SWITCH_MAP_STATE", Context.MODE_PRIVATE).edit().putBoolean("state", true).apply();

                                new HelperFragment(FragmentiGapMap.getInstance()).remove();

                                new HelperFragment(FragmentiGapMap.getInstance()).load();
                            }

                        }
                    }
                });







               *//* SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
                Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));*//*

                //   map.onDetach();

          *//*      if (changeState)
                    changeState = false;
                else
                    changeState = true;


                map.invalidate();

                startMap(view);*//*


                // setTile(false);

            }
        });*/


        page = 1;
        new RequestGeoGetComment().getComment(AccountManager.getInstance().getCurrentUser().getId());
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.igap_nearby));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        ToolbarItems toolbarItems = toolbar.createToolbarItems();
        doneButton = toolbarItems.addItem(done_button, R.string.icon_other_vertical_dots, Color.WHITE);
        doneButton.setContentDescription(context.getString(R.string.Done));
        toolbar.setListener(id -> {
            if (id == -1) {
                if (getActivity() != null &&
                        getActivity().getSupportFragmentManager().getFragments().get(getActivity().getSupportFragmentManager().getFragments().size() - 1) != null &&
                        getActivity().getSupportFragmentManager().getFragments().get(getActivity().getSupportFragmentManager().getFragments().size() - 1).getClass().getName().equals(FragmentContactsProfile.class.getName())
                ) {
                    return;
                }

                if (rippleMoreMap.getVisibility() == View.GONE || fabGps.getVisibility() == View.GONE) {
                    rippleMoreMap.setVisibility(View.VISIBLE);
                    fabGps.show();
                    fabStateSwitcher.setVisibility(View.VISIBLE);
                }
                if (!isBackPress) {
                    //getActivity().getSupportFragmentManager().popBackStack();
                    G.fragmentActivity.onBackPressed();
                }
                isBackPress = false;
                page = pageiGapMap;
            } else if (id == done_button) {
                if (getActivity() != null) {
                    List<String> items = new ArrayList<>();
                    items.add(getString(R.string.list_user_map));
                    items.add(getString(R.string.nearby));
                    if (getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE).getBoolean(SHP_SETTING.REGISTER_STATUS, false)) {
                        items.add(getString(R.string.map_registration));
                    } else {
                        items.add(getString(R.string.map_registration_enable));
                    }

                    dialog = new TopSheetDialog(getActivity()).setListData(items, -1, new BottomSheetItemClickCallback() {
                        @Override
                        public void onClick(int position) {
                            if (items.get(position).equals(getString(R.string.list_user_map))) {
                                fabGps.hide();
                                fabStateSwitcher.setVisibility(View.GONE);
                                rippleMoreMap.setVisibility(View.GONE);
                                page = pageUserList;
                                try {
                                    if (getActivity() != null) {
                                        new HelperFragment(getActivity().getSupportFragmentManager(), FragmentMapUsers.newInstance()).setResourceContainer(R.id.mapContainer_main).setReplace(false).load();
                                    }
                                } catch (Exception e) {
                                    e.getStackTrace();
                                }
                            } else if (items.get(position).equals(getString(R.string.nearby))) {
                                if (location != null && !isSendRequestGeoCoordinate) {
                                    new RequestGeoGetNearbyCoordinate().getNearbyCoordinate(location.getLatitude(), location.getLongitude());
                                    showProgress(true);
                                    isSendRequestGeoCoordinate = true;
                                }
                            } else if (items.get(position).equals(getString(R.string.map_registration))) {
                                new MaterialDialog.Builder(getActivity()).title(R.string.Visible_Status_title_dialog_invisible).content(R.string.Visible_Status_text_dialog_invisible).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        new RequestGeoRegister().register(false);

                                    }
                                }).negativeText(R.string.no).onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    }
                                }).show();
                            } else if (items.get(position).equals(getString(R.string.map_registration_enable))) {
                                if (!isGpsOn) {
                                    try {
                                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    } catch (ActivityNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    new MaterialDialog.Builder(getContext()).title(R.string.Visible_Status_title_dialog).content(R.string.Visible_Status_text_dialog).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            if (getRequestManager().isUserLogin()) {
                                                new RequestGeoRegister().register(true);
                                            } else {
                                                toggleGps.setChecked(false);
                                                showSnackBar(getString(R.string.please_check_your_connenction));
                                            }
                                        }
                                    }).negativeText(R.string.no).onNegative((dialog, which) -> toggleGps.setChecked(false)).show();
                                }
                            }
                        }
                    });
                    dialog.show();
                }
            }
        });
        return toolbar;
    }

    private void attentionDialog() {

        SharedPreferences sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();


        if (!sharedPreferences.getBoolean(SHP_SETTING.KEY_MAP_ATTENTION_DIALOG, false)) {

            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.attention).content(R.string.content_attention_dialog).positiveText(R.string.ok).onAny(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(MaterialDialog dialog, DialogAction which) {

                    editor.putBoolean(SHP_SETTING.KEY_MAP_ATTENTION_DIALOG, dialog.isPromptCheckBoxChecked());
                    editor.apply();
                }
            }).checkBoxPromptRes(R.string.dont_ask_again, false, null).show();
        }


    }


    private void startMap(View view) {
        //  map = (MapView) view.findViewById(R.id.map);  // map = new MapView(this); //constructor
        /**
         * Set Type Of Map
         */
        try {


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
            /*GeoPoint startPoint = new GeoPoint(35.689197, 51.388974);
            mapController.setCenter(startPoint);*/

            /**
             * Use From Following Code For Custom Url Tile Server
             */

            if (mapUrls.size() > 0) {
                url = mapUrls.get(new Random().nextInt(mapUrls.size()));
            } else {
                crashlytics.recordException(new Exception("FragmentiGapMap -> mapUrls==0; time:" + System.currentTimeMillis()));
                url = URL_MAP;
            }

            changeState = getActivity().getSharedPreferences("KEY_SWITCH_MAP_STATE", Context.MODE_PRIVATE)
                    .getBoolean("state", false);


            ViewGroup mapContainer = view.findViewById(R.id.mapContainer);
            mapContainer.setOnClickListener(v -> {

            });

            DbManager.getInstance().doRealmTransaction(realm -> {
                realm.where(RealmGeoNearbyDistance.class).findAll().deleteAllFromRealm();
            });

            rootTurnOnGps = view.findViewById(R.id.scrollView);
            rootTurnOnGps.setOnClickListener(v -> {
                //have to empty
            });
            vgMessageGps = view.findViewById(R.id.vgMessageGps);

            txtTextTurnOnOffGps = view.findViewById(R.id.txtTextTurnOnOffGps);
            txtDescriptionMap = view.findViewById(R.id.txtDescriptionMap);
            edtMessageGps = view.findViewById(R.id.edtMessageGps);
            edtMessageGps.setTextColor(Theme.getColor(Theme.key_title_text));

            edtMessageGps.setOnTouchListener((v, event) -> {
                edtMessageGps.setSingleLine(false);
                return false;
            });

            prgWaitingGetUser = view.findViewById(R.id.prgWaitingGetUser);

            toggleGps = view.findViewById(R.id.toggleGps);
            toggleGps.setOnClickListener(v -> {
                if (!isGpsOn) {
                    try {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    new MaterialDialog.Builder(getContext()).title(R.string.Visible_Status_title_dialog).content(R.string.Visible_Status_text_dialog).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (getRequestManager().isUserLogin()) {
                                new RequestGeoRegister().register(true);
                            } else {
                                toggleGps.setChecked(false);
                                showSnackBar(getString(R.string.please_check_your_connenction));
                            }
                        }
                    }).negativeText(R.string.no).onNegative((dialog, which) -> toggleGps.setChecked(false)).show();

                }

            });

            prgWaitingSendMessage = view.findViewById(R.id.prgWaitSendMessage);
            txtSendMessageGps = view.findViewById(R.id.txtSendMessageGps);
            txtSendMessageGps.setText(R.string.icon_close);
            G.onGeoCommentResponse = new OnGeoCommentResponse() {
                @Override
                public void commentResponse() {
                    G.handler.post(() -> {


                        txtComment = edtMessageGps.getText().toString();
                        if (edtMessageGps.length() > 0) {
                            txtSendMessageGps.setVisibility(View.VISIBLE);
                        } else {
                            txtSendMessageGps.setVisibility(View.GONE);
                        }
                        prgWaitingSendMessage.setVisibility(View.GONE);
                        txtSendMessageGps.setText(R.string.icon_close);
                        edtMessageGps.setEnabled(true);
                    });
                }

                @Override
                public void errorCommentResponse() {
                    G.handler.post(() -> {

                        txtSendMessageGps.setVisibility(View.VISIBLE);
                        prgWaitingSendMessage.setVisibility(View.GONE);
                        txtSendMessageGps.setText(R.string.icon_close);
                        edtMessageGps.setEnabled(true);
                    });
                }

                @Override
                public void timeOutCommentResponse() {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {

                            txtSendMessageGps.setVisibility(View.VISIBLE);
                            txtSendMessageGps.setText(R.string.icon_close);
                            prgWaitingSendMessage.setVisibility(View.GONE);
                            edtMessageGps.setEnabled(true);
                        }
                    });
                }
            };

            txtSendMessageGps.setOnClickListener(v -> {


                if (txtSendMessageGps.getText().toString().contains(G.fragmentActivity.getResources().getString(R.string.icon_close))) {
                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.Clear_Status).content(R.string.Clear_Status_desc).positiveText(R.string.st_dialog_reset_all_notification_yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            new RequestGeoUpdateComment().updateComment("");
                            edtMessageGps.setText("");
                            txtSendMessageGps.setVisibility(View.GONE);
                            txtSendMessageGps.setText(R.string.icon_close);

                        }
                    }).negativeText(R.string.st_dialog_reset_all_notification_no).onNegative((dialog, which) -> {
                    }).show();

                } else {
                    txtSendMessageGps.setVisibility(View.GONE);
                    prgWaitingSendMessage.setVisibility(View.VISIBLE);
                    edtMessageGps.setEnabled(false);
                    new RequestGeoUpdateComment().updateComment(edtMessageGps.getText().toString());
                }


                //edtMessageGps.setText("");
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
                            txtSendMessageGps.setText(R.string.icon_sent);
                        } else {
                            txtSendMessageGps.setVisibility(View.VISIBLE);
                            txtSendMessageGps.setText(R.string.icon_close);
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
                            isEndLine = false;
                            showSnackBar(G.fragmentActivity.getResources().getString(R.string.exceed_4_line));
                        }
                    } else {
                        isEndLine = true;
                        specialRequests = edtMessageGps.getText().toString();
                    }

                    edtMessageGps.addTextChangedListener(this);

                }
            });

            fabGps = view.findViewById(st_fab_gps);


            fabGps.setBackgroundTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)));
            fabGps.setColorFilter(Color.WHITE);


            fabGps.setOnClickListener(v -> {
                if (location != null) {
                    currentLocation(location, false);
                    new RequestGeoUpdatePosition().updatePosition(location.getLatitude(), location.getLongitude());
                } else {
                    GPSTracker.getGpsTrackerInstance().detectLocation();
                }
            });

            view.findViewById(R.id.backgroundToolbarMap).setBackgroundColor(Theme.getColor(Theme.key_theme_color));

            btnBack = view.findViewById(R.id.ripple_back_map);

            rippleMoreMap = view.findViewById(R.id.ripple_more_map);

            if (FragmentiGapMap.mineStaticLocation != null) {
                GPSTracker.getGpsTrackerInstance().onLocationChanged(FragmentiGapMap.mineStaticLocation);
            }

        } catch (Exception e) {
        }
    }

    private void showSnackBar(final String message) {
        G.currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                HelperError.showSnackMessage(message, false);

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
                //infoWindow = new MyInfoWindow(map, marker, userIdR, hasComment, FragmentiGapMap.this, G.fragmentActivity, avatarHandler);
                //marker.setInfoWindow(infoWindow);

                markers.add(marker);
                map.getOverlays().add(marker);
                map.invalidate();
            }
        });
    }

    /**
     * hint : call this method after fill location
     */
    private void getCoordinateLoop(final int delay, final boolean loop) {
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (location != null) {
                    if (loop && page == pageiGapMap) {
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isSendRequestGeoCoordinate) {
                                    new RequestGeoGetNearbyCoordinate().getNearbyCoordinate(location.getLatitude(), location.getLongitude());
                                    showProgress(true);
                                    isSendRequestGeoCoordinate = true;
                                }

                                getCoordinateLoop(DEFAULT_LOOP_TIME, true);
                            }
                        }, delay);
                    } else {
                        if (!isSendRequestGeoCoordinate) {
                            new RequestGeoGetNearbyCoordinate().getNearbyCoordinate(location.getLatitude(), location.getLongitude());
                            showProgress(true);
                            isSendRequestGeoCoordinate = true;
                        }

                    }
                }
            }
        }, 0);
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
        FragmentiGapMap.location = location;

        if (firstEnter) {
            lastLatitude = location.getLatitude();
            lastLongitude = location.getLongitude();
            firstEnter = false;
            currentLocation(location, true);
            getCoordinateLoop(0, false);
        }
        mapBounding(location);
        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        OverlayItem overlayItem = new OverlayItem("title", "City", geoPoint);

        //Drawable drawable = context.getResources().getDrawable(R.drawable.location_current);
        overlayItem.setMarker(avatarMark(AccountManager.getInstance().getCurrentUser().getId(), MarkerColor.GRAY)); // marker color is not important in this line because for mineAvatar will be unused.

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

    private void downloadMarkerAvatar(Realm realm, long userId) {
        for (RealmAvatar avatar : realm.where(RealmAvatar.class).equalTo("ownerId", userId).findAll().sort("id", Sort.DESCENDING)) {
            if (avatar.getFile() != null) {
                String pathName = avatar.getFile().getLocalFilePath();
                if (pathName == null) {
                    pathName = avatar.getFile().getLocalThumbnailPath();
                    if (pathName == null) {

                        //todo get avatar?

                    }
                }
                break;
            }
        }

    }

    @Override
    public void onNearbyCoordinate(final List<ProtoGeoGetNearbyCoordinate.GeoGetNearbyCoordinateResponse.Result> results) {
        map.getOverlays().removeAll(markers);
        DbManager.getInstance().doRealmTask(realm -> {
            for (final ProtoGeoGetNearbyCoordinate.GeoGetNearbyCoordinateResponse.Result result : results) {
                downloadMarkerAvatar(realm, result.getUserId());
            }
        });
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (final ProtoGeoGetNearbyCoordinate.GeoGetNearbyCoordinateResponse.Result result : results) {
                    if (AccountManager.getInstance().getCurrentUser().getId() != result.getUserId()) { // don't show mine
                        RealmRegisteredInfo.getRegistrationInfo(result.getUserId(), new OnInfo() {
                            @Override
                            public void onInfo(Long registeredId) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        drawMark(result.getLat(), result.getLon(), result.getHasComment(), result.getUserId());
                                    }
                                });
                            }
                        });
                    }
                }

                showProgress(false);
                isSendRequestGeoCoordinate = false;
            }
        }, 2000);
    }

    private void showProgress(final boolean show) {
        G.currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (prgWaitingGetUser != null) {
                    if (show) {
                        prgWaitingGetUser.setVisibility(View.VISIBLE);
                    } else {
                        prgWaitingGetUser.setVisibility(View.GONE);

                    }
                }
            }
        });
    }

    @Override
    public void onErrorGetNearbyCoordinate() {
        showProgress(false);
        isSendRequestGeoCoordinate = false;
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) G.fragmentActivity.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//GPS is off

            visibleViewAttention(G.fragmentActivity.getString(R.string.turn_on_gps_explain), true);

        } else {// GPS is on
            isGpsOn = true;
            if (mapRegistrationStatus) {
                rootTurnOnGps.setVisibility(View.GONE);
                fabGps.show();
                fabStateSwitcher.setVisibility(View.VISIBLE);
                vgMessageGps.setVisibility(View.VISIBLE);
                rippleMoreMap.setVisibility(View.VISIBLE);
                GPSTracker.getGpsTrackerInstance().detectLocation();
            } else {
                visibleViewAttention(G.fragmentActivity.getString(R.string.Visible_Status_text), false);
            }

        }
    }

    private void visibleViewAttention(String text, boolean b) {
        rootTurnOnGps.setVisibility(View.VISIBLE);
        fabGps.hide();
        fabStateSwitcher.setVisibility(View.GONE);
        toggleGps.setChecked(false);
        vgMessageGps.setVisibility(View.GONE);
        rippleMoreMap.setVisibility(View.GONE);
        txtTextTurnOnOffGps.setText(text);

        if (!b) {
            txtDescriptionMap.setVisibility(View.GONE);
        } else {
            txtDescriptionMap.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        statusCheck();
        FragmentiGapMap.page = FragmentiGapMap.pageiGapMap;
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        try {
            dialog.dismiss();
        } catch (Exception e) {
        }

        orientation = newConfig.orientation;


        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                map.getController().animateTo(new GeoPoint(lastLatitude, lastLongitude));
            }
        }, 1000);
    }

    @Override
    public void onState(final boolean state) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (state) {
                    getCoordinateLoop(0, false);
                    editor.putBoolean(SHP_SETTING.REGISTER_STATUS, true);
                    editor.apply();
                    new RequestGeoGetComment().getComment(AccountManager.getInstance().getCurrentUser().getId());
                } else {
                    editor.putBoolean(SHP_SETTING.REGISTER_STATUS, false);
                    editor.apply();
                }
                if (G.onMapRegisterStateMain != null) {
                    G.onMapRegisterStateMain.onStateMain(state);
                }
                statusCheck();
                if (btnMapChangeRegistration != null) {
                    btnMapChangeRegistration.setChecked(state);
                }
            }
        });

    }

    @Override
    public void onClose() {
        if (getActivity() != null) {
            removeFromBaseFragment(this);
        }
    }

    @Override
    public void onGetComment(final long userIdR, final String comment) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                txtComment = comment;
                if (AccountManager.getInstance().getCurrentUser().getId() == userIdR && comment.length() > 0) {
                    edtMessageGps.setText(comment);
                    txtSendMessageGps.setText(R.string.icon_close);
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

    @Override
    public void onStart() {
        super.onStart();
        G.isFragmentMapActive = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        G.isFragmentMapActive = false;
    }

    @Override
    public void onMapUsersGet() {
    }

    public enum MarkerColor {
        GRAY, GREEN
    }
}
