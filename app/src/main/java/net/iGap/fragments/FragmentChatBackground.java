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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.AdapterChatBackground;
import net.iGap.adapter.AdapterSolidChatBackground;
import net.iGap.dialog.topsheet.TopSheetDialog;
import net.iGap.helper.HelperSaveFile;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.OnGetWallpaper;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.TimeUtils;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoInfoWallpaper;
import net.iGap.realm.RealmWallpaper;
import net.iGap.realm.RealmWallpaperFields;
import net.iGap.realm.RealmWallpaperProto;
import net.iGap.request.RequestInfoWallpaper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.DIR_CHAT_BACKGROUND;

public class FragmentChatBackground extends BaseFragment implements ToolbarListener {

    private String savePath;
    private RecyclerView mRecyclerView, rcvSolidColor;
    private ImageView imgFullImage;
    private AdapterChatBackground adapterChatBackgroundSetting;
    private AdapterSolidChatBackground adapterSolidChatbackground;
    private ArrayList<StructWallpaper> wList;
    private Fragment fragment;
    private RippleView chB_ripple_menu_button;
    private boolean isSolidColor = false;
    private HelperToolbar toolbar;
    ArrayList<String> solidList = new ArrayList<>();


    public static FragmentChatBackground newInstance() {
        return new FragmentChatBackground();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.activity_chat_background, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            new File(DIR_CHAT_BACKGROUND).mkdirs();
            new File(DIR_CHAT_BACKGROUND + "/.nomedia").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fragment = this;

        toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.more_icon, R.string.check_icon, R.string.retry_icon)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.st_title_Background))
                .setListener(this);

        ViewGroup layoutToolbar = view.findViewById(R.id.fcb_layout_toolbar);
        layoutToolbar.addView(toolbar.getView());

        toolbar.getSecondRightButton().setVisibility(View.GONE);
        imgFullImage = view.findViewById(R.id.stchf_fullImage);

        SharedPreferences sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        String backGroundPath = sharedPreferences.getString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "");
        if (backGroundPath.length() > 0) {
            File f = new File(backGroundPath);
            if (f.exists()) {
                G.imageLoader.displayImage(AndroidUtils.suitablePath(backGroundPath), imgFullImage);
            }
        }


        fillList(true);

        mRecyclerView = view.findViewById(R.id.rcvContent);
        rcvSolidColor = view.findViewById(R.id.rcvSolidColor);


        adapterChatBackgroundSetting = new AdapterChatBackground(fragment, wList, new OnImageClick() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(String imagePath) {

                G.imageLoader.displayImage(AndroidUtils.suitablePath(imagePath), imgFullImage);


                savePath = imagePath;

                toolbar.getSecondRightButton().setVisibility(View.VISIBLE);
                toolbar.getThirdRightButton().setVisibility(View.GONE);
                isSolidColor = false;
            }
        });

        adapterSolidChatbackground = new AdapterSolidChatBackground(fragment, solidList, new OnImageClick() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(String imagePath) {

                //   G.imageLoader.displayImage(AndroidUtils.suitablePath(imagePath), imgFullImage);
                imgFullImage.setImageDrawable(null);
                imgFullImage.setBackgroundColor(Color.parseColor(imagePath));

                savePath = imagePath;

                toolbar.getSecondRightButton().setVisibility(View.VISIBLE);
                toolbar.getThirdRightButton().setVisibility(View.GONE);
                isSolidColor = true;
            }
        });


        mRecyclerView.setAdapter(adapterChatBackgroundSetting);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(G.fragmentActivity, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.clearAnimation();


        rcvSolidColor.setAdapter(adapterSolidChatbackground);
        rcvSolidColor.setLayoutManager(new LinearLayoutManager(G.fragmentActivity, LinearLayoutManager.HORIZONTAL, false));
        rcvSolidColor.clearAnimation();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }

        String filePath = null;

        switch (requestCode) {
            case AttachFile.request_code_TAKE_PICTURE:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true);
                    filePath = AttachFile.mCurrentPhotoPath;
                } else {
                    ImageHelper.correctRotateImage(AttachFile.imagePath, true);
                    filePath = AttachFile.imagePath;
                }
                break;
            case AttachFile.request_code_image_from_gallery_single_select:

                if (data != null && data.getData() != null) {

                    if (G.fragmentActivity != null) {
                        AttachFile attachFile = new AttachFile(G.fragmentActivity);
                        filePath = attachFile.saveGalleryPicToLocal(AttachFile.getFilePathFromUri(data.getData()));
                    }
                }

                break;
        }

        if (filePath != null) {

            if (new File(filePath).exists()) {
                RealmWallpaper.updateField(null, filePath, ProtoInfoWallpaper.InfoWallpaper.Type.CHAT_BACKGROUND_VALUE);

                fillList(false);

                adapterChatBackgroundSetting.notifyItemInserted(1);
            }
        }
    }

    private void getImageListFromServer() {
        G.onGetWallpaper = new OnGetWallpaper() {
            @Override
            public void onGetWallpaperList(final List<ProtoGlobal.Wallpaper> list) {
                RealmWallpaper.updateField(list, "", ProtoInfoWallpaper.InfoWallpaper.Type.CHAT_BACKGROUND_VALUE);
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        fillList(false);
                        adapterChatBackgroundSetting.notifyDataSetChanged();
                        adapterSolidChatbackground.notifyDataSetChanged();
                    }
                });
            }
        };

        new RequestInfoWallpaper().infoWallpaper(ProtoInfoWallpaper.InfoWallpaper.Type.CHAT_BACKGROUND);
    }

    private void fillList(boolean getInfoFromServer) {

        if (wList == null) wList = new ArrayList<>();

        wList.clear();


        //add item 0 add new background from local
        StructWallpaper sw = new StructWallpaper();
        sw.setWallpaperType(WallpaperType.addNew);
        wList.add(sw);
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmWallpaper realmWallpaper = realm.where(RealmWallpaper.class).equalTo(RealmWallpaperFields.TYPE, ProtoInfoWallpaper.InfoWallpaper.Type.CHAT_BACKGROUND_VALUE).findFirst();

            if (realmWallpaper != null) {

                if (realmWallpaper.getLocalList() != null) {
                    for (String localPath : realmWallpaper.getLocalList()) {
                        if (new File(localPath).exists()) {
                            StructWallpaper _swl = new StructWallpaper();
                            _swl.setWallpaperType(WallpaperType.local);
                            _swl.setPath(localPath);
                            wList.add(_swl);

                        }
                    }
                }

                if (realmWallpaper.getWallPaperList() != null) {
                    for (RealmWallpaperProto wallpaper : realmWallpaper.getWallPaperList()) {
                        StructWallpaper _swp = new StructWallpaper();
                        _swp.setWallpaperType(WallpaperType.proto);
                        _swp.setProtoWallpaper(wallpaper);
                        wList.add(_swp);
                        solidList.add(_swp.getProtoWallpaper().getColor());
                    }

                } else if (getInfoFromServer) {

                    long time = realmWallpaper.getLastTimeGetList();
                    if (time > 0) {

                        if (time + (2 * 60 * 60 * 1000) < TimeUtils.currentLocalTime()) {
                            getImageListFromServer();
                        }
                    } else {
                        getImageListFromServer();
                    }
                }
            } else {
                if (getInfoFromServer) {
                    getImageListFromServer();
                }
            }
        }
    }

    @Override
    public void onLeftIconClickListener(View view) {
        popBackStackFragment();
    }

    @Override
    public void onRightIconClickListener(View view) {

        List<String> items = new ArrayList<>();
        items.add(getString(R.string.solid_colors));
        items.add(getString(R.string.wallpapers));

        new TopSheetDialog(getContext()).setListData(items, -1, position -> {
            if (items.get(position).equals(getString(R.string.solid_colors))) {
                mRecyclerView.setVisibility(View.GONE);
                rcvSolidColor.setVisibility(View.VISIBLE);
            } else if (items.get(position).equals(getString(R.string.wallpapers))) {
                mRecyclerView.setVisibility(View.VISIBLE);
                rcvSolidColor.setVisibility(View.GONE);
            }
        }).show();
    }

    @Override
    public void onSecondRightIconClickListener(View view) {
        if (getActivity() != null && savePath != null && savePath.length() > 0) {
            String finalPath = "";
            if (isSolidColor) {
                finalPath = savePath;
                HelperSaveFile.removeFromPrivateDirectory(getActivity());
            } else {
                try {
                    finalPath = HelperSaveFile.saveInPrivateDirectory(getActivity(), savePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, finalPath);
            editor.putBoolean(SHP_SETTING.KEY_CHAT_BACKGROUND_IS_DEFAULT, false);
            editor.apply();
            if (G.twoPaneMode && G.onBackgroundChanged != null) {
                G.onBackgroundChanged.onBackgroundChanged(finalPath);
            }

            popBackStackFragment();
        }
    }

    @Override
    public void onThirdRightIconClickListener(View view) {

        if (getActivity() != null) {
            HelperSaveFile.removeFromPrivateDirectory(getActivity());
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "");
            editor.putBoolean(SHP_SETTING.KEY_CHAT_BACKGROUND_IS_DEFAULT, true);
            editor.apply();
            if (G.twoPaneMode && G.onBackgroundChanged != null) {
                G.onBackgroundChanged.onBackgroundChanged("");
            }
            popBackStackFragment();
        }
    }

    public enum WallpaperType {
        addNew, local, proto
    }

    public interface OnImageClick {
        void onClick(String imagePath);
    }

    public class StructWallpaper {

        private WallpaperType wallpaperType;
        private String path;
        private RealmWallpaperProto protoWallpaper;


        public WallpaperType getWallpaperType() {
            return wallpaperType;
        }

        public void setWallpaperType(WallpaperType wallpaperType) {
            this.wallpaperType = wallpaperType;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public RealmWallpaperProto getProtoWallpaper() {
            return protoWallpaper;
        }

        public void setProtoWallpaper(RealmWallpaperProto mProtoWallpaper) {

            this.protoWallpaper = mProtoWallpaper;

        }
    }
}
