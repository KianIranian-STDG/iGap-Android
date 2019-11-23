package net.iGap.viewmodel;

import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChatBackground;
import net.iGap.helper.HelperSaveFile;
import net.iGap.interfaces.OnGetWallpaper;
import net.iGap.module.AndroidUtils;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SingleLiveEvent;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoInfoWallpaper;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmWallpaper;
import net.iGap.realm.RealmWallpaperFields;
import net.iGap.realm.RealmWallpaperProto;
import net.iGap.request.RequestInfoWallpaper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatBackgroundViewModel extends ViewModel {

    private MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    private MutableLiveData<WallpaperImage> loadSelectedImage = new MutableLiveData<>();
    private MutableLiveData<String> loadSelectedColor = new MutableLiveData<>();
    private MutableLiveData<List<Integer>> menuList = new MutableLiveData<>();
    private MutableLiveData<Boolean> loadChatBackgroundImage = new MutableLiveData<>();
    private MutableLiveData<Boolean> loadChatBackgroundSolidColor = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> removeChatBackgroundFileSelectedFromMemory = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> showAddImage = new SingleLiveEvent<>();

    private List<Integer> menuItemList;
    private SharedPreferences sharedPreferences;
    private ArrayList<FragmentChatBackground.StructWallpaper> wList;
    private ArrayList<String> solidList;
    private boolean isSolidColor = false;
    private String savePath;
    private OnImageWallpaperListClick onImageWallpaperListClick;
    private OnImageClick onImageClick;

    public ChatBackgroundViewModel(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        String backGroundPath = sharedPreferences.getString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "");
        if (backGroundPath.length() > 0) {
            File f = new File(backGroundPath);
            if (f.exists()) {
                loadSelectedImage.setValue(new WallpaperImage(AndroidUtils.suitablePath(backGroundPath),false));
            } else {
                loadSelectedColor.setValue(backGroundPath);
            }
        }

        menuItemList = new ArrayList<>();
        menuItemList.add(R.string.solid_colors);
        menuItemList.add(R.string.wallpapers);
        onImageWallpaperListClick = new ChatBackgroundViewModel.OnImageWallpaperListClick() {
            @Override
            public void onClick(int position) {
                String bigImagePath;
                if (wList.get(position).getWallpaperType() == FragmentChatBackground.WallpaperType.proto) {
                    RealmAttachment pf = wList.get(position).getProtoWallpaper().getFile();
                    bigImagePath = G.DIR_CHAT_BACKGROUND + "/" + pf.getCacheId() + "_" + pf.getName();
                } else {
                    bigImagePath = wList.get(position).getPath();
                }
                loadSelectedImage.setValue(new WallpaperImage(AndroidUtils.suitablePath(bigImagePath),true));
                savePath = bigImagePath;
                isSolidColor = false;
            }

            @Override
            public void onAddImageClick() {
                showAddImage.setValue(true);
            }
        };

        onImageClick = new OnImageClick() {
            @Override
            public void onClick(int position) {

                //   G.imageLoader.displayImage(AndroidUtils.suitablePath(imagePath), imgFullImage);
                imgFullImage.setImageDrawable(null);
                imgFullImage.setBackgroundColor(Color.parseColor(imagePath));

                savePath = imagePath;

                toolbar.getSecondRightButton().setVisibility(View.VISIBLE);
                toolbar.getThirdRightButton().setVisibility(View.GONE);
                isSolidColor = true;
            }
        };
    }

    public MutableLiveData<Boolean> getGoBack() {
        return goBack;
    }

    public MutableLiveData<WallpaperImage> getLoadSelectedImage() {
        return loadSelectedImage;
    }

    public MutableLiveData<String> getLoadSelectedColor() {
        return loadSelectedColor;
    }

    public MutableLiveData<List<Integer>> getMenuList() {
        return menuList;
    }

    public MutableLiveData<Boolean> getLoadChatBackgroundImage() {
        return loadChatBackgroundImage;
    }

    public MutableLiveData<Boolean> getLoadChatBackgroundSolidColor() {
        return loadChatBackgroundSolidColor;
    }

    public SingleLiveEvent<Boolean> getShowAddImage() {
        return showAddImage;
    }

    public SingleLiveEvent<Boolean> getRemoveChatBackgroundFileSelectedFromMemory() {
        return removeChatBackgroundFileSelectedFromMemory;
    }

    public OnImageWallpaperListClick getOnImageWallpaperListClick() {
        return onImageWallpaperListClick;
    }

    public OnImageClick getOnImageClick() {
        return onImageClick;
    }

    public void init() {
        try {
            new File(G.DIR_CHAT_BACKGROUND).mkdirs();
            new File(G.DIR_CHAT_BACKGROUND + "/.nomedia").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            goBack.setValue(true);
        }

        fillList(true);
    }

    public void onMenuClick() {
        menuList.setValue(menuItemList);
    }

    public void onMenuItemClicked(int position) {
        if (menuItemList.get(position) == R.string.solid_colors) {
            loadChatBackgroundImage.setValue(true);
            loadChatBackgroundSolidColor.setValue(false);
        } else if (menuItemList.get(position) == R.string.wallpapers) {
            loadChatBackgroundSolidColor.setValue(true);
            loadChatBackgroundImage.setValue(false);
        }
    }

    public void onBackMenuItemClick() {
        goBack.setValue(true);
    }

    public void onAcceptMenuItemClick() {
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

            sharedPreferences.edit()
                    .putString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, finalPath)
                    .putBoolean(SHP_SETTING.KEY_CHAT_BACKGROUND_IS_DEFAULT, false)
                    .apply();
            if (G.twoPaneMode && G.onBackgroundChanged != null) {
                G.onBackgroundChanged.onBackgroundChanged(finalPath);
            }

            goBack.setValue(true);
        }
    }

    public void onMenuResetItemClick() {
        removeChatBackgroundFileSelectedFromMemory.setValue(true);
        sharedPreferences.edit()
                .putString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "")
                .putBoolean(SHP_SETTING.KEY_CHAT_BACKGROUND_IS_DEFAULT, true)
                .apply();
        if (G.twoPaneMode && G.onBackgroundChanged != null) {
            G.onBackgroundChanged.onBackgroundChanged("");
        }
        goBack.setValue(true);
    }

    private void fillList(boolean getInfoFromServer) {

        if (wList == null) wList = new ArrayList<>();

        wList.clear();

        DbManager.getInstance().doRealmTask(realm -> {
            RealmWallpaper realmWallpaper = realm.where(RealmWallpaper.class).equalTo(RealmWallpaperFields.TYPE, ProtoInfoWallpaper.InfoWallpaper.Type.CHAT_BACKGROUND_VALUE).findFirst();

            if (realmWallpaper != null) {

                if (realmWallpaper.getLocalList() != null) {
                    for (String localPath : realmWallpaper.getLocalList()) {
                        if (new File(localPath).exists()) {
                            FragmentChatBackground.StructWallpaper _swl = new FragmentChatBackground.StructWallpaper();
                            _swl.setWallpaperType(FragmentChatBackground.WallpaperType.local);
                            _swl.setPath(localPath);
                            wList.add(_swl);

                        }
                    }
                }

                if (realmWallpaper.getWallPaperList() != null) {
                    for (RealmWallpaperProto wallpaper : realmWallpaper.getWallPaperList()) {
                        FragmentChatBackground.StructWallpaper _swp = new StructWallpaper();
                        _swp.setWallpaperType(FragmentChatBackground.WallpaperType.proto);
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
        });
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

    public interface OnImageClick {
        void onClick(int position);
    }

    public interface OnImageWallpaperListClick extends OnImageClick {
        void onAddImageClick();
    }

    public class WallpaperImage{
        private String imagePath;
        private boolean isNew;

        public WallpaperImage(String imagePath, boolean isNew) {
            this.imagePath = imagePath;
            this.isNew = isNew;
        }

        public String getImagePath() {
            return imagePath;
        }

        public boolean isNew() {
            return isNew;
        }
    }
}
