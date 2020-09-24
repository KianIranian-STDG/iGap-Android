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
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperSetAction;
import net.iGap.helper.HelperString;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.DbManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static net.iGap.G.isLocationFromBot;
import static net.iGap.R.id.mf_fragment_map_view;

public class FragmentMap extends BaseFragment implements OnMapReadyCallback, View.OnClickListener, LocationListener {

    public static String Latitude = "latitude";
    public static String Longitude = "longitude";
    public static String PosoitionMode = "positionMode";


    public static String flagFragmentMap = "FragmentMap";
    Marker marker;
    private GoogleMap mMap;
    private Double latitude;
    private Double longitude;
    private Mode mode;
    private TextView accuracy, txtTitle, txtUserName, txtDistance;
    private ImageView imgProfile;

    private boolean showGPS = false;

    private RelativeLayout rvSendPosition, rvSeePosition;
    private FloatingActionButton fabOpenMap;
    private Bundle bundle;
    private RelativeLayout rvIcon;
    private net.iGap.module.MaterialDesignTextView itemIcon;
    private Location location;


    public static FragmentMap getInctance(Double latitude, Double longitude, Mode mode, int type, long roomId, String senderID) {

        FragmentMap fragmentMap = new FragmentMap();

        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putLong("roomId", roomId);
        bundle.putString("senderId", senderID);

        bundle.putDouble(FragmentMap.Latitude, latitude);
        bundle.putDouble(FragmentMap.Longitude, longitude);
        bundle.putSerializable(PosoitionMode, mode);

        fragmentMap.setArguments(bundle);
        return fragmentMap;
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

    public static String saveBitmapToFile(Bitmap bitmap) {

        String result = "";

        try {
            if (bitmap == null) return result;

            String fileName = "/location_" + HelperString.getRandomFileName(3) + ".png";
            File file = new File(G.DIR_TEMP, fileName);

            OutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

            result = file.getPath();
        } catch (FileNotFoundException e) {

        }

        return result;
    }

    public String saveMapToFile(Bitmap bitmap) {

        String result = "";

        try {
            if (bitmap == null) return result;

            String fileName = "/location_" + latitude.toString().replace(".", "") + "_" + longitude.toString().replace(".", "") + ".png";
            File file = new File(G.DIR_TEMP, fileName);

            OutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

            result = file.getPath();
        } catch (FileNotFoundException e) {

        }

        return result;
    }

    public static void loadImageFromPosition(double latiude, double longitude, OnGetPicture listener) {

        String urlstr = "https://maps.googleapis.com/maps/api/staticmap?center=" + latiude + "," + longitude + "&zoom=16&size=480x240" + "&markers=color:red%7Clabel:S%7C" + latiude + "," + longitude + "&maptype=roadmap&key=" + G.context.getString(R.string.google_maps_key);

        new DownloadImageTask(listener).execute(urlstr);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.map_fragment, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle saveInctanceState) {
        super.onViewCreated(view, saveInctanceState);


        /* *//**//* itemIcon = (MaterialDesignTextView) view.findViewById(R.id.mf_icon);*/


        rvIcon = view.findViewById(R.id.rv_icon);

        Drawable mDrawableSkip = ContextCompat.getDrawable(getContext(), R.drawable.ic_circle_shape);
        if (mDrawableSkip != null) {
            mDrawableSkip.setColorFilter(new PorterDuffColorFilter(new Theme().getPrimaryColor(getContext()), PorterDuff.Mode.SRC_IN));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                rvIcon.setBackground(mDrawableSkip);
            }
        }


        imgProfile = view.findViewById(R.id.mf_imgProfile);

        rvSendPosition = view.findViewById(R.id.mf_rv_send_position);
        rvSeePosition = view.findViewById(R.id.mf_rv_see_position);

        rvSendPosition.setEnabled(false);

        accuracy = view.findViewById(R.id.mf_txt_accuracy);
        accuracy.setText(getResources().getString(R.string.get_location_data));


        txtTitle = view.findViewById(R.id.mf_txt_message);

        txtUserName = view.findViewById(R.id.mf_txt_userName);
        txtDistance = view.findViewById(R.id.mf_txt_distance);

        txtDistance.setText(getResources().getString(R.string.calculation));

        txtUserName.setTextColor(new Theme().getPrimaryColor(getContext()));

        accuracy.setTextColor(new Theme().getPrimaryColor(getContext()));
        txtDistance.setTextColor(new Theme().getPrimaryColor(getContext()));


        rvSendPosition.setBackgroundColor(new Theme().getPrimaryColor(getContext()));
        txtTitle.setTextColor(new Theme().getPrimaryColor(getContext()));

        fabOpenMap = view.findViewById(R.id.mf_fab_openMap);
        fabOpenMap.setBackgroundTintList(ColorStateList.valueOf(new Theme().getButtonColor(getContext())));
        fabOpenMap.setColorFilter(Color.WHITE);

        bundle = getArguments();

        if (bundle != null) {

            latitude = bundle.getDouble(FragmentMap.Latitude);
            longitude = bundle.getDouble(FragmentMap.Longitude);

            mode = (Mode) bundle.getSerializable(PosoitionMode);

            if (mode == Mode.sendPosition) {
                if (G.onHelperSetAction != null) {
                    G.onHelperSetAction.onAction(ProtoGlobal.ClientAction.SENDING_LOCATION);
                }
            }

            initComponent(view, bundle.getInt("type", 0), bundle.getLong("roomId", 00), bundle.getString("senderId", null));
        } else {
            close();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isLocationFromBot = false;
        HelperSetAction.sendCancel(FragmentChat.messageId);
    }

    private void close() {
        //  mActivity.getSupportFragmentManager().beginTransaction().remove(FragmentMap.this).commit();

        popBackStackFragment();
    }

    private void initComponent(View view, int type, long roomId, String senderId) {

        SupportMapFragment mapFragment = new SupportMapFragment();
        if (getActivity() != null) {
            new HelperFragment(getActivity().getSupportFragmentManager(), mapFragment).setReplace(false).setAddToBackStack(false).setResourceContainer(mf_fragment_map_view).load();
        }

        mapFragment.getMapAsync(FragmentMap.this);


        rvSendPosition = view.findViewById(R.id.mf_rv_send_position);


        rvSendPosition.setBackgroundColor(new Theme().getPrimaryColor(getContext()));

        if (mode == Mode.sendPosition) {
            fabOpenMap.hide();
            rvSendPosition.setVisibility(View.VISIBLE);
            rvSeePosition.setVisibility(View.GONE);
            rvSendPosition.setOnClickListener(this);


        } else if (mode == Mode.seePosition) {
            rvSeePosition.setVisibility(View.VISIBLE);
            fabOpenMap.show();
            rvSendPosition.setVisibility(View.GONE);
            fabOpenMap.setOnClickListener(this);

            DbManager.getInstance().doRealmTask(realm -> {
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fabOpenMap.getLayoutParams();

                if (HelperCalander.isPersianUnicode) {
                    //  params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params.anchorGravity = Gravity.LEFT | Gravity.BOTTOM;

                    txtUserName.setGravity(Gravity.RIGHT);
                    ((RelativeLayout.LayoutParams) txtUserName.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                } else {
                    //    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.anchorGravity = Gravity.RIGHT | Gravity.BOTTOM;


                    txtUserName.setGravity(Gravity.LEFT);
                    ((RelativeLayout.LayoutParams) txtUserName.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                }

                if (type == ProtoGlobal.Room.Type.CHAT.getNumber() || type == ProtoGlobal.Room.Type.GROUP.getNumber()) {

                    RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo("id", Long.parseLong(senderId)).findFirst();
                    txtUserName.setText(realmRegisteredInfo.getDisplayName());

                    setAvatar(Long.parseLong(senderId));


                } else {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
                    txtUserName.setText(realmRoom.getTitle());

                    setAvatar(roomId);

                }
            });
        }
    }

    private void setAvatar(long id) {
        avatarHandler.getAvatar(new ParamWithAvatarType(imgProfile, id).avatarType(AvatarHandler.AvatarType.ROOM).showMain());
    }

    //****************************************************************************************************

    /**
     * This callback is triggered when the map is ready to be used.
     * Manipulates the map once available.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        final boolean[] updatePosition = {true};

        //if device has not gps permision in androi 6+ return form map
        if (ActivityCompat.checkSelfPermission(G.fragmentActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(G.fragmentActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (mode == Mode.seePosition || isLocationFromBot) {
            mMap.setMyLocationEnabled(true);

            mMap.getUiSettings().setZoomGesturesEnabled(true);
        } else {
            mMap.getUiSettings().setZoomGesturesEnabled(false);
            mMap.setMyLocationEnabled(false);
        }


        LatLng latLng = new LatLng(latitude, longitude);

        marker = mMap.addMarker(new MarkerOptions().position(latLng).title("position"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));


        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();

        String provider;
        try {
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            provider = locationManager.getBestProvider(criteria, true);
        } catch (Exception e) {
            provider = locationManager.getBestProvider(criteria, false);
        }

        location = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(provider, 60, 10, this);
        onLocationChanged(location);


        if (mode == Mode.sendPosition) {


            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                @SuppressLint("MissingPermission")
                public void onMyLocationChange(Location location) {


                    updatePosition[0] = false;
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    if (marker != null) {
                        marker.remove();
                    }

                    LatLng la = new LatLng(latitude, longitude);

                    marker = mMap.addMarker(new MarkerOptions().position(la).title("position"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(la, 16));

                    try {
                        accuracy.setText("( " + String.format("%.6f", latitude) + " , " + String.format("%.6f", longitude) + " )");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mMap.setOnMyLocationChangeListener(null);
                }
            });


            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {

                    if (updatePosition[0]) {
                        Display display = G.currentActivity.getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);

                        LatLng mapCenter = mMap.getProjection().fromScreenLocation(new Point(size.x / 2, size.y / 2));
                        latitude = mapCenter.latitude;
                        longitude = mapCenter.longitude;


                        mMap.getUiSettings().setCompassEnabled(true);


                        accuracy.setText("( " + String.format("%.6f", latitude) + " , " + String.format("%.6f", longitude) + " )");

                        if (marker != null) {
                            marker.remove();

                        }

                        marker = mMap.addMarker(new MarkerOptions().position(mapCenter).title("position"));
           /*             if (marker.getPosition().latitude == fixLat && marker.getPosition().longitude == fixLong) {
                            mMap.setMyLocationEnabled(false);
                        } else {
                            mMap.setMyLocationEnabled(true);
                        }*/

                    } else {
                        updatePosition[0] = true;
                    }
                }
            });

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                public void onMapClick(LatLng latLng) {


                    updatePosition[0] = false;
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;

                    if (marker != null) {
                        marker.remove();
                    }
                    marker = mMap.addMarker(new MarkerOptions().position(latLng).title("position"));


                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                }
            });

            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @SuppressLint("MissingPermission")
                @Override
                public boolean onMyLocationButtonClick() {

                    Location location = mMap.getMyLocation();

                    if (location == null) return false;

                    updatePosition[0] = false;
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    if (marker != null) {
                        marker.remove();
                    }

                    LatLng la = new LatLng(latitude, longitude);

                    marker = mMap.addMarker(new MarkerOptions().position(la).title("position"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(la, 16));


                    return false;


                }
            });

            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onCameraIdle() {
                    try {
                        if (mode == Mode.sendPosition) {

                            Location loc1 = new Location("");
                            loc1.setLatitude(marker.getPosition().latitude);
                            loc1.setLongitude(marker.getPosition().longitude);

                            Location loc2 = new Location("");
                            loc2.setLatitude(location.getLatitude());
                            loc2.setLongitude(location.getLongitude());

                            if (loc1.distanceTo(loc2) > 35) {
                                mMap.setMyLocationEnabled(true);
                            } else {
                                mMap.setMyLocationEnabled(false);
                            }
                        }
                    } catch (Exception e) {
                        mMap.setMyLocationEnabled(true);
                    }

                }
            });

            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    rvSendPosition.setEnabled(true);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        if (latitude == null || longitude == null) {

            G.currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.set_position), false);

                }
            });
        } else {

            switch (view.getId()) {
                case R.id.mf_rv_send_position:
                    try {

                        mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                            @Override
                            public void onSnapshotReady(Bitmap bitmap) {

                                String path = saveMapToFile(bitmap);

                                close();

                                if (path.length() > 0) {
                                    //ActivityChat activity = (ActivityChat) mActivity;
                                    //activity.sendPosition(latitude, longitude, path);

                                    if (G.iSendPositionChat != null) {
                                        G.iSendPositionChat.send(latitude, longitude, path);
                                    }

                                }
                            }
                        });
                    } catch (Exception e) {
                        close();
                        //ActivityChat activity = (ActivityChat) mActivity;
                        //activity.sendPosition(latitude, longitude, null);

                        if (G.iSendPositionChat != null) {
                            G.iSendPositionChat.send(latitude, longitude, null);
                        }
                    }

                    break;

                case R.id.mf_fab_openMap:
                    try {
                        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(im here)");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    } catch (ActivityNotFoundException e) {
                    }
                    break;


            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {

        try {
            if (mode == Mode.seePosition) {
                Location loc1 = new Location("");
                loc1.setLatitude(marker.getPosition().latitude);
                loc1.setLongitude(marker.getPosition().longitude);

                Location loc2 = new Location("");
                loc2.setLatitude(location.getLatitude());
                loc2.setLongitude(location.getLongitude());


                txtDistance.setText(String.format("%.1f", loc1.distanceTo(loc2)) + " " + getResources().getString(R.string.map_distance) + " ");


            }

        } catch (Exception e) {
        }


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

    public enum Mode {
        sendPosition, seePosition
    }

    public interface OnGetPicture {

        void getBitmap(Bitmap bitmap);
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
