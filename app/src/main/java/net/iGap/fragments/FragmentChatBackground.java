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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.AdapterChatBackground;
import net.iGap.adapter.AdapterSolidChatBackground;
import net.iGap.databinding.ActivityChatBackgroundBinding;
import net.iGap.dialog.topsheet.TopSheetDialog;
import net.iGap.helper.HelperSaveFile;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.AttachFile;
import net.iGap.module.SHP_SETTING;
import net.iGap.proto.ProtoInfoWallpaper;
import net.iGap.realm.RealmWallpaper;
import net.iGap.realm.RealmWallpaperProto;
import net.iGap.viewmodel.ChatBackgroundViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FragmentChatBackground extends BaseFragment implements ToolbarListener {

    private ActivityChatBackgroundBinding binding;
    private ChatBackgroundViewModel viewModel;

    private AdapterChatBackground adapterChatBackgroundSetting;
    private AdapterSolidChatBackground adapterSolidChatbackground;
    private HelperToolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ChatBackgroundViewModel(getContext().getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE));
            }
        }).get(ChatBackgroundViewModel.class);

        adapterChatBackgroundSetting = new AdapterChatBackground(new ArrayList<>(), viewModel.getOnImageWallpaperListClick());
        adapterSolidChatbackground = new AdapterSolidChatBackground(new ArrayList<>(), viewModel.getOnImageClick());
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_chat_background, container, false);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.init();

        toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.more_icon, R.string.check_icon, R.string.retry_icon)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.st_title_Background))
                .setListener(this);

        binding.fcbLayoutToolbar.addView(toolbar.getView());

        toolbar.getSecondRightButton().setVisibility(View.GONE);

        viewModel.getLoadSelectedImage().observe(getViewLifecycleOwner(), wallpaper -> {
            if (wallpaper != null) {
                Picasso.get().load(wallpaper.getImagePath()).fit().into(binding.stchfFullImage);
                if (wallpaper.isNew()) {
                    toolbar.getSecondRightButton().setVisibility(View.VISIBLE);
                    toolbar.getThirdRightButton().setVisibility(View.GONE);
                }
            }
        });

        viewModel.getShowAddImage().observe(getViewLifecycleOwner(),isShow->{
            if (getActivity() != null&&isShow != null && isShow){
                new MaterialDialog.Builder(getActivity()).title(R.string.choose_picture).negativeText(R.string.cancel).items(R.array.profile).itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        AttachFile attachFile = new AttachFile(getActivity());
                        if (text.toString().equals(G.context.getString(R.string.from_camera))) {
                            try {
                                attachFile.requestTakePicture(FragmentChatBackground.this);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                attachFile.requestOpenGalleryForImageSingleSelect(FragmentChatBackground.this);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        viewModel.getLoadSelectedColor().observe(getViewLifecycleOwner(), colorValue -> {
            if (colorValue != null) {
                binding.stchfFullImage.setBackgroundColor(Color.parseColor(colorValue));
            }
        });

        viewModel.getMenuList().observe(getViewLifecycleOwner(), menuList -> {
            if (getContext() != null && menuList != null) {
                new TopSheetDialog(getContext()).setListDataWithResourceId(menuList, -1, position -> viewModel.onMenuItemClicked(position)).show();
            }
        });

        viewModel.getLoadChatBackgroundImage().observe(getViewLifecycleOwner(), isLoad -> {

        });

        viewModel.getLoadChatBackgroundSolidColor().observe(getViewLifecycleOwner(), isLoad -> {

        });

        viewModel.getGoBack().observe(getViewLifecycleOwner(), isGoBack -> {
            if (getActivity() != null && isGoBack != null && isGoBack) {
                getActivity().onBackPressed();
            }
        });

        viewModel.getRemoveChatBackgroundFileSelectedFromMemory().observe(getViewLifecycleOwner(), isClear -> {
            if (getActivity() != null && isClear != null && isClear) {
                HelperSaveFile.removeFromPrivateDirectory(getActivity());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
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
    }

    @Override
    public void onLeftIconClickListener(View view) {
        viewModel.onBackMenuItemClick();
    }

    @Override
    public void onRightIconClickListener(View view) {
        viewModel.onMenuClick();
    }

    @Override
    public void onSecondRightIconClickListener(View view) {
        viewModel.onAcceptMenuItemClick();
    }

    @Override
    public void onThirdRightIconClickListener(View view) {
        viewModel.onMenuResetItemClick();
    }

    public enum WallpaperType {
        addNew, local, proto
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
