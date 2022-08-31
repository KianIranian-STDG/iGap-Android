package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentMapUsers;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.cell.TextCheckCell;
import net.iGap.messenger.ui.components.EditTextBoldCursor;
import net.iGap.messenger.ui.components.FloatingMenuButton;
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

public class NearbyFragment extends BaseFragment implements OnLocationChanged, OnGetNearbyCoordinate, OnMapRegisterState, OnMapClose, OnMapUsersGet, OnGeoGetComment, GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener {

    private MapView mapView;
    private FloatingMenuButton floatingMenuButton;
    private EditTextBoldCursor status;
    private TextCheckCell toggleGps;
    private final static int more_button = 1;
    private View moreButton;

    public static final int pageiGapMap = 1;
    public static final int pageUserList = 2;
    public static Location mineStaticLocation;
    public static Location location;
    public static int page;
    public static boolean mapRegistrationStatus;
    public static boolean isBackPress = false;
    public static ArrayList<String> mapUrls = new ArrayList<>();

    private boolean changeState = false;
    private final int ZOOM_LEVEL_MIN = 16;
    private final int ZOOM_LEVEL_MAX = 19;
    private boolean isSendRequestGeoCoordinate = false;
    private boolean canUpdate = true;
    private boolean firstEnter = true;
    private boolean isGpsOn = false;
    private int orientation = G.rotationState;
    private final int DEFAULT_LOOP_TIME = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    private final ArrayList<Marker> markers = new ArrayList<>();
    private final FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
    private double northLimitation;
    private double eastLimitation;
    private double southLimitation;
    private double westLimitation;
    private double lastLatitude;
    private double lastLongitude;
    private String url;
    private ItemizedOverlay<OverlayItem> latestLocation;
    private TopSheetDialog dialog;

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
                thirdBoarderColor = Color.parseColor("#004f4f4f");
                firstBorderSize = (int) G.context.getResources().getDimension(R.dimen.dp2);
                secondBoarderSize = (int) G.context.getResources().getDimension(R.dimen.dp10);
                thirdBoarderSize = (int) G.context.getResources().getDimension(R.dimen.dp2);
            }
        }
        bitmap = addBorderToCircularBitmap(bitmap, firstBorderSize, firstBorderColor);
        bitmap = addBorderToCircularBitmapSharp(bitmap, secondBoarderSize, secondBoarderColor);
        bitmap = addBorderToCircularBitmap(bitmap, thirdBoarderSize, thirdBoarderColor);
        return bitmap;
    }

    protected static Bitmap getCircleBitmap(Bitmap bm, boolean mineAvatar) {
        int width;
        if (mineAvatar) {
            width = Math.min((int) G.context.getResources().getDimension(R.dimen.dp10), (int) G.context.getResources().getDimension(R.dimen.dp10));
        } else {
            width = Math.min((int) G.context.getResources().getDimension(R.dimen.dp32), (int) G.context.getResources().getDimension(R.dimen.dp32));
        }
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(bm, width, width);
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
    @Override
    public View createView(Context context) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        fragmentView = new LinearLayout(context);
        LinearLayout linearLayout = (LinearLayout) fragmentView;
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        fragmentView.setOnTouchListener((v, event) -> true);
        status = new EditTextBoldCursor(context);
        status.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        status.setHintTextColor(Theme.getColor(Theme.key_subtitle_text));
        status.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        status.setTextColor(Theme.getColor(Theme.key_title_text));
        status.setBackground(Theme.createEditTextDrawable(context, false));
        status.setMaxLines(1);
        status.setLines(1);
        status.setPadding(0, 0, 0, 0);
        status.setSingleLine(true);
        status.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
        status.setInputType( InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        status.setImeOptions(EditorInfo.IME_ACTION_DONE);
        status.setHint(context.getString(R.string.hint_gps));
        status.setCursorColor(Theme.getColor(Theme.key_default_text));
        status.setCursorSize(LayoutCreator.dp(20));
        status.setCursorWidth(1.5f);
        status.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                new RequestGeoUpdateComment().updateComment(status.getText().toString());
            }
        });
        linearLayout.addView(status, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 36, 20, 5, 20, 0));
        toggleGps = new TextCheckCell(context);
        toggleGps.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        boolean gpsStatus = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE).getBoolean(SHP_SETTING.REGISTER_STATUS, false);
        toggleGps.setTextAndValueAndCheck(getString(R.string.location_status), context.getString(R.string.turn_on_gps_explain), gpsStatus ? 1 : 0, true, true);
        toggleGps.setTextColor(Theme.getColor(Theme.key_title_text),Theme.getColor(Theme.key_default_text));
        toggleGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggleGps.isChecked()){
                    GpsTurnoff();
                } else {
                    GpsTurnOn();
                }
            }
        });
        linearLayout.addView(toggleGps, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 36, 0, 0, 0, 0));
        FrameLayout frameLayout = new FrameLayout(context);
        mapView = new MapView(context);
        frameLayout.addView(mapView, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, 0, 0, 0, 0));
        floatingMenuButton = new FloatingMenuButton(context, new FloatingMenuButton.FloatingMenuButtonListener() {
            @Override
            public void location() {
                if (location != null) {
                    currentLocation(location, false);
                    new RequestGeoUpdatePosition().updatePosition(location.getLatitude(), location.getLongitude());
                } else {
                    GPSTracker.getGpsTrackerInstance().detectLocation();
                }
            }

            @Override
            public void googleMap() {
                if (getActivity() != null && isAdded()) {
                    changeState = getActivity().getSharedPreferences("KEY_SWITCH_MAP_STATE", Context.MODE_PRIVATE).getBoolean("state", false);
                    if (changeState) {
                        deleteMapFileCash();
                        getActivity().getSharedPreferences("KEY_SWITCH_MAP_STATE", Context.MODE_PRIVATE).edit().putBoolean("state", false).apply();
                        new HelperFragment(getActivity().getSupportFragmentManager(), new NearbyFragment()).setImmediateRemove(true).remove();
                        if (getActivity() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager(), new NearbyFragment()).load();
                        }
                    }
                }
                if (!floatingMenuButton.isFABOpen()) {
                    floatingMenuButton.showFABMenu();
                } else {
                    floatingMenuButton.closeFABMenu();
                }
            }

            @Override
            public void satellite() {
                deleteMapFileCash();
                if (getActivity() != null && isAdded()) {
                    changeState = getActivity().getSharedPreferences("KEY_SWITCH_MAP_STATE", Context.MODE_PRIVATE).getBoolean("state", false);
                    if (!changeState) {
                        deleteMapFileCash();
                        getActivity().getSharedPreferences("KEY_SWITCH_MAP_STATE", Context.MODE_PRIVATE).edit().putBoolean("state", true).apply();
                        new HelperFragment(getActivity().getSupportFragmentManager(), new NearbyFragment()).setImmediateRemove(true).remove();
                        new HelperFragment(getActivity().getSupportFragmentManager(), new NearbyFragment()).load();
                    }
                }
                if (!floatingMenuButton.isFABOpen()) {
                    floatingMenuButton.showFABMenu();
                } else {
                    floatingMenuButton.closeFABMenu();
                }
            }
        });
        frameLayout.addView(floatingMenuButton, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, 0, 0, 0, 0));
        linearLayout.addView(frameLayout, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, 0, 0, 0, 0));
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        return fragmentView;
    }

    private void GpsTurnOn() {
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
                        toggleGps.setChecked(true);
                        new RequestGeoRegister().register(true);
                    } else {
                        toggleGps.setChecked(false);
                        showSnackBar(getString(R.string.please_check_your_connenction));
                    }
                }
            }).negativeText(R.string.no).onNegative((dialog, which) -> toggleGps.setChecked(false)).show();
        }
    }

    private void GpsTurnoff() {
        new MaterialDialog.Builder(getActivity()).title(R.string.Visible_Status_title_dialog_invisible).content(R.string.Visible_Status_text_dialog_invisible).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                toggleGps.setChecked(false);
                new RequestGeoRegister().register(false);
                new HelperFragment(getActivity().getSupportFragmentManager(), new NearbyFragment()).setImmediateRemove(true).remove();
                if (getActivity() != null) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), new NearbyFragment()).load();
                }
            }
        }).negativeText(R.string.no).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

            }
        }).show();
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
        startMap(view);
        page = 1;
        new RequestGeoGetComment().getComment(AccountManager.getInstance().getCurrentUser().getId());
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.iGapNearBy));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        ToolbarItems toolbarItems = toolbar.createToolbarItems();
        moreButton = toolbarItems.addItem(more_button, R.string.icon_other_vertical_dots, Color.WHITE);
        moreButton.setContentDescription(context.getString(R.string.Done));
        toolbar.setListener(id -> {
            if (id == -1) {
                if (!isBackPress) {
                    G.fragmentActivity.onBackPressed();
                }
                isBackPress = false;
                page = pageiGapMap;
                if (floatingMenuButton.isFABOpen()) {
                    floatingMenuButton.closeFABMenu();
                }
                finish();
            } else if (id == more_button) {
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
                                page = pageUserList;
                                try {
                                    if (getActivity() != null) {
                                        new HelperFragment(getActivity().getSupportFragmentManager(), FragmentMapUsers.newInstance()).setReplace(false).load();
                                    }
                                } catch (Exception e) {
                                    e.getStackTrace();
                                }
                            } else if (items.get(position).equals(getString(R.string.nearby))) {
                                if (location != null && !isSendRequestGeoCoordinate) {
                                    new RequestGeoGetNearbyCoordinate().getNearbyCoordinate(location.getLatitude(), location.getLongitude());
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
        try {
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapView.setBuiltInZoomControls(false);
            mapView.setMultiTouchControls(true);
            CompassOverlay mCompassOverlay = new CompassOverlay(getContext(), new InternalCompassOrientationProvider(getContext()), mapView);
            mCompassOverlay.enableCompass();
            mapView.getOverlays().add(mCompassOverlay);
            IMapController mapController = mapView.getController();
            int ZOOM_LEVEL_NORMAL = 16;
            mapController.setZoom(ZOOM_LEVEL_NORMAL);
            final GestureDetector mGestureDetector = new GestureDetector(G.context, this);
            mapView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mGestureDetector.onTouchEvent(event);
                }
            });
            if (mapUrls.size() > 0) {
                url = mapUrls.get(new Random().nextInt(mapUrls.size()));
            } else {
                crashlytics.recordException(new Exception("NearbyFragment -> mapUrls==0; time:" + System.currentTimeMillis()));
                url = URL_MAP;
            }
            changeState = getActivity().getSharedPreferences("KEY_SWITCH_MAP_STATE", Context.MODE_PRIVATE).getBoolean("state", false);
            DbManager.getInstance().doRealmTransaction(realm -> {
                realm.where(RealmGeoNearbyDistance.class).findAll().deleteAllFromRealm();
            });
            G.onGeoCommentResponse = new OnGeoCommentResponse() {
                @Override
                public void commentResponse() {
                    G.handler.post(() -> {
                        status.setEnabled(true);
                    });
                }

                @Override
                public void errorCommentResponse() {
                    G.handler.post(() -> {
                        status.setEnabled(true);
                    });
                }

                @Override
                public void timeOutCommentResponse() {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            status.setEnabled(true);
                        }
                    });
                }
            };
            if (mineStaticLocation != null) {
                GPSTracker.getGpsTrackerInstance().onLocationChanged(mineStaticLocation);
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

    private void currentLocation(Location location, boolean setDefaultZoom) {
        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        if (setDefaultZoom) {
            mapView.getController().setZoom(16);
        }
        mapView.getController().animateTo(startPoint);
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initMapListener();
            }
        }, 2000);
    }

    private void mapBounding(Location location) {
        double extraBounding = 0.01;
        int BOUND_LIMIT_METERS = 5000;
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
        mapView.getOverlayManager().add(line);
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
                Marker marker = new Marker(mapView);
                marker.setPosition(new GeoPoint(mapItem.getPoint().getLatitude(), mapItem.getPoint().getLongitude()));
                InfoWindow infoWindow;
                marker.setIcon(avatarMark(userIdR, MarkerColor.GRAY));
                infoWindow = new MyInfoWindow(mapView, marker, userIdR, hasComment, NearbyFragment.this, G.fragmentActivity, avatarHandler);
                marker.setInfoWindow(infoWindow);
                markers.add(marker);
                mapView.getOverlays().add(marker);
                mapView.invalidate();
            }
        });
    }

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
                                    isSendRequestGeoCoordinate = true;
                                }

                                getCoordinateLoop(DEFAULT_LOOP_TIME, true);
                            }
                        }, delay);
                    } else {
                        if (!isSendRequestGeoCoordinate) {
                            new RequestGeoGetNearbyCoordinate().getNearbyCoordinate(location.getLatitude(), location.getLongitude());
                            isSendRequestGeoCoordinate = true;
                        }

                    }
                }
            }
        }, 0);
    }

    private void initMapListener() {
        mapView.setMapListener(new MapListener() {
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
                            mapView.getController().animateTo(new GeoPoint(lastLatitude, lastLongitude));
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
                return super.onLongPress(e, mapView);
            }
        };
        mapView.getOverlays().add(touchOverlay);
    }

    @Override
    public void onLocationChanged(Location location) {
        NearbyFragment.location = location;

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
        overlayItem.setMarker(avatarMark(AccountManager.getInstance().getCurrentUser().getId(), MarkerColor.GRAY));
        ArrayList<OverlayItem> overlayItemArrayList = new ArrayList<>();
        overlayItemArrayList.add(overlayItem);
        ItemizedOverlay<OverlayItem> locationOverlay = new ItemizedIconOverlay<>(context, overlayItemArrayList, null);
        if (latestLocation != null) {
            mapView.getOverlays().remove(latestLocation);
        }
        latestLocation = locationOverlay;
        mapView.getOverlays().add(locationOverlay);
    }

    private void downloadMarkerAvatar(Realm realm, long userId) {
        for (RealmAvatar avatar : realm.where(RealmAvatar.class).equalTo("ownerId", userId).findAll().sort("id", Sort.DESCENDING)) {
            if (avatar.getFile() != null) {
                String pathName = avatar.getFile().getLocalFilePath();
                if (pathName == null) {
                    pathName = avatar.getFile().getLocalThumbnailPath();
                }
                break;
            }
        }
    }

    @Override
    public void onNearbyCoordinate(final List<ProtoGeoGetNearbyCoordinate.GeoGetNearbyCoordinateResponse.Result> results) {
        mapView.getOverlays().removeAll(markers);
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
                isSendRequestGeoCoordinate = false;
            }
        }, 2000);
    }

    @Override
    public void onErrorGetNearbyCoordinate() {
        isSendRequestGeoCoordinate = false;
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) G.fragmentActivity.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//GPS is off
            visibleViewAttention(G.fragmentActivity.getString(R.string.turn_on_gps_explain), true);
        } else {// GPS is on
            isGpsOn = true;
            if (mapRegistrationStatus) {
                GPSTracker.getGpsTrackerInstance().detectLocation();
            } else {
                visibleViewAttention(G.fragmentActivity.getString(R.string.Visible_Status_text), false);
            }
        }
    }

    private void visibleViewAttention(String text, boolean b) {
        toggleGps.setChecked(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        statusCheck();
        page = pageiGapMap;
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientation = newConfig.orientation;
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mapView.getController().animateTo(new GeoPoint(lastLatitude, lastLongitude));
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
                if (AccountManager.getInstance().getCurrentUser().getId() == userIdR && comment.length() > 0) {
                    status.setText(comment);
                }
            }
        });
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        if (mapView.getZoomLevel() == ZOOM_LEVEL_MAX) {
            mapView.getController().zoomTo(ZOOM_LEVEL_MAX - 1);
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
