package net.iGap.viewmodel;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.AdapterChatBackground;
import net.iGap.fragments.FragmentChatBackground;
import net.iGap.helper.HelperSaveFile;
import net.iGap.messenger.ui.fragments.ChatBackgroundFragment;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.StructWallpaper;
import net.iGap.module.TimeUtils;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.OnGetWallpaper;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoInfoWallpaper;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmWallpaper;
import net.iGap.realm.RealmWallpaperProto;
import net.iGap.request.RequestInfoWallpaper;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatBackgroundViewModel extends ViewModel {

    private MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    private MutableLiveData<WallpaperImage> loadSelectedImage = new MutableLiveData<>();
    private MutableLiveData<WallpaperSolidColor> loadSelectedColor = new MutableLiveData<>();
    private MutableLiveData<List<String>> menuList = new MutableLiveData<>();
    private MutableLiveData<List<StructWallpaper>> loadChatBackgroundImage = new MutableLiveData<>();
    private MutableLiveData<List<String>> loadChatBackgroundSolidColor = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> showAddImage = new SingleLiveEvent<>();
    private ObservableInt showLoadingView = new ObservableInt(View.GONE);

    private List<String> menuItemList;
    private SharedPreferences sharedPreferences;
    private List<StructWallpaper> wList;
    private List<String> solidList;
    private boolean isSolidColor = false;
    private String savePath;
    private File privateDirectory;
    private OnImageWallpaperListClick onImageWallpaperListClick;

    public ChatBackgroundViewModel(@NotNull SharedPreferences sharedPreferences, File privateDirectory) {
        this.sharedPreferences = sharedPreferences;
        this.privateDirectory = privateDirectory;
        this.solidList = new ArrayList<>(Arrays.asList("#2962ff", "#00b8d4",
                "#b71c1c", "#e53935", "#e57373",
                "#880e4f", "#d81b60", "#f06292",
                "#4a148c", "#8e24aa", "#ba68c8",
                "#311b92", "#5e35b1", "#9575cd",
                "#1a237e", "#3949ab", "#7986cb",
                "#0d47a1", "#1e88e5", "#64b5f6",
                "#01579b", "#039be5", "#4fc3f7",
                "#006064", "#00acc1", "#4dd0e1",
                "#004d40", "#00897b", "#4db6ac",
                "#1b5e20", "#43a047", "#81c784",
                "#33691e", "#7cb342", "#aed581",
                "#827717", "#c0ca33", "#dce775",
                "#f57f17", "#fdd835", "#fff176",
                "#ff6f00", "#ffb300", "#ffd54f",
                "#e65100", "#fb8c00", "#fb8c00",
                "#bf360c", "#f4511e", "#ff8a65",
                "#3e2723", "#6d4c41", "#a1887f",
                "#212121", "#757575", "#e0e0e0",
                "#263238", "#546e7a", "#90a4ae"));
        String backGroundPath = sharedPreferences.getString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "");
        if (backGroundPath.length() > 0) {
            File f = new File(backGroundPath);
            if (f.exists()) {
                loadSelectedImage.setValue(new WallpaperImage(backGroundPath, false));
            } else {
                loadSelectedColor.setValue(new WallpaperSolidColor(Color.parseColor(backGroundPath), false));
            }
        }

        menuItemList = new ArrayList<>();
        menuItemList.add(G.fragmentActivity.getString(R.string.solid_colors));
        menuItemList.add(G.fragmentActivity.getString(R.string.wallpapers));
        onImageWallpaperListClick = new ChatBackgroundViewModel.OnImageWallpaperListClick() {
            @Override
            public void onClick(int type, int position) {
                if (type == AdapterChatBackground.WALLPAPER_IMAGE) {
                    String bigImagePath;
                    if (wList.get(position).getWallpaperType() == ChatBackgroundFragment.WallpaperType.proto) {
                        RealmAttachment pf = wList.get(position).getProtoWallpaper().getFile();
                        bigImagePath = G.DIR_CHAT_BACKGROUND + "/" + pf.getCacheId() + "_" + pf.getName();
                    } else {
                        bigImagePath = wList.get(position).getPath();
                    }
                    loadSelectedImage.setValue(new WallpaperImage(bigImagePath, true));
                    savePath = bigImagePath;
                    isSolidColor = false;
                } else {
                    isSolidColor = true;
                    savePath = solidList.get(position);
                    loadSelectedColor.setValue(new WallpaperSolidColor(Color.parseColor(savePath), true));
                }
            }

            @Override
            public void onAddImageClick() {
                showAddImage.setValue(true);
            }
        };

        fillList(true);
    }

    public MutableLiveData<Boolean> getGoBack() {
        return goBack;
    }

    public MutableLiveData<WallpaperImage> getLoadSelectedImage() {
        return loadSelectedImage;
    }

    public MutableLiveData<WallpaperSolidColor> getLoadSelectedColor() {
        return loadSelectedColor;
    }

    public MutableLiveData<List<String>> getMenuList() {
        return menuList;
    }

    public MutableLiveData<List<StructWallpaper>> getLoadChatBackgroundImage() {
        return loadChatBackgroundImage;
    }

    public MutableLiveData<List<String>> getLoadChatBackgroundSolidColor() {
        return loadChatBackgroundSolidColor;
    }

    public SingleLiveEvent<Boolean> getShowAddImage() {
        return showAddImage;
    }

    public ObservableInt getShowLoadingView() {
        return showLoadingView;
    }

    public OnImageWallpaperListClick getOnImageWallpaperListClick() {
        return onImageWallpaperListClick;
    }

    public void init() {
        try {
            new File(G.DIR_CHAT_BACKGROUND).mkdirs();
            new File(G.DIR_CHAT_BACKGROUND + "/.nomedia").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            goBack.setValue(false);
        }
    }

    public void onMenuClick() {
        menuList.setValue(menuItemList);
    }

    public void onMenuItemClicked(int position) {
        if (menuItemList.get(position).equals(G.fragmentActivity.getString(R.string.solid_colors))) {
            loadChatBackgroundSolidColor.setValue(solidList);
            isSolidColor = true;
        } else if (menuItemList.get(position).equals(G.fragmentActivity.getString(R.string.wallpapers))) {
            loadChatBackgroundImage.setValue(wList);
            isSolidColor = false;
        }
    }

    public void onBackMenuItemClick() {
        goBack.setValue(false);
    }

    public void onAcceptMenuItemClick() {
        if (savePath != null && savePath.length() > 0) {
            String finalPath = "";
            if (isSolidColor) {
                finalPath = savePath;
                HelperSaveFile.removeFromPrivateDirectory(privateDirectory);
            } else {
                try {
                    finalPath = HelperSaveFile.saveInPrivateDirectory(privateDirectory, savePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            sharedPreferences.edit()
                    .putString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, finalPath)
                    .putBoolean(SHP_SETTING.KEY_CHAT_BACKGROUND_IS_DEFAULT, false)
                    .apply();
            if (G.twoPaneMode) {
                String finalPath1 = finalPath;
                G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.CHAT_BACKGROUND_CHANGED, finalPath1));
            }
            goBack.setValue(true);
        }
    }

    public void onMenuResetItemClick() {
        HelperSaveFile.removeFromPrivateDirectory(privateDirectory);
        sharedPreferences.edit()
                .putString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "")
                .putBoolean(SHP_SETTING.KEY_CHAT_BACKGROUND_IS_DEFAULT, true)
                .apply();
        if (G.twoPaneMode) {
            G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.CHAT_BACKGROUND_CHANGED, ""));
        }
        goBack.setValue(true);
    }

    private void fillList(boolean getInfoFromServer) {
        if (wList == null) wList = new ArrayList<>();

        wList.clear();

        DbManager.getInstance().doRealmTask(realm -> {
            RealmWallpaper realmWallpaper = realm.where(RealmWallpaper.class).equalTo("type", ProtoInfoWallpaper.InfoWallpaper.Type.CHAT_BACKGROUND_VALUE).findFirst();

            if (realmWallpaper != null) {
                Log.wtf(this.getClass().getName(), "realmWallpaper != null");
                if (realmWallpaper.getLocalList() != null) {
                    Log.wtf(this.getClass().getName(), "realmWallpaper.getLocalList() != null");
                    for (String localPath : realmWallpaper.getLocalList()) {
                        if (new File(localPath).exists()) {
                            StructWallpaper _swl = new StructWallpaper();
                            _swl.setWallpaperType(ChatBackgroundFragment.WallpaperType.local);
                            _swl.setPath(localPath);
                            wList.add(_swl);
                            loadChatBackgroundImage.postValue(wList);
                        }
                    }
                }

                if (realmWallpaper.getWallPaperList() != null) {
                    Log.wtf(this.getClass().getName(), "realmWallpaper.getWallPaperList() != null");
                    for (RealmWallpaperProto wallpaper : realmWallpaper.getWallPaperList()) {
                        StructWallpaper _swp = new StructWallpaper();
                        _swp.setWallpaperType(ChatBackgroundFragment.WallpaperType.proto);
                        _swp.setProtoWallpaper(realm.copyFromRealm(wallpaper));
                        wList.add(_swp);
                        loadChatBackgroundImage.postValue(wList);
                    }

                } else if (getInfoFromServer) {
                    Log.wtf(this.getClass().getName(), "getInfoFromServer");
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
                Log.wtf(this.getClass().getName(), "realmWallpaper == null");
                if (getInfoFromServer) {
                    getImageListFromServer();
                }
            }
        });
    }

    private void getImageListFromServer() {
        showLoadingView.set(View.VISIBLE);
        G.onGetWallpaper = new OnGetWallpaper() {
            @Override
            public void onGetWallpaperList(final List<ProtoGlobal.Wallpaper> list) {
                RealmWallpaper.updateField(list, "", ProtoInfoWallpaper.InfoWallpaper.Type.CHAT_BACKGROUND_VALUE);
                fillList(false);
                showLoadingView.set(View.GONE);
            }
        };

        new RequestInfoWallpaper().infoWallpaper(ProtoInfoWallpaper.InfoWallpaper.Type.CHAT_BACKGROUND);
    }

    public void setUserCustomImage(String filePath) {
        if (filePath != null) {
            if (new File(filePath).exists()) {
                RealmWallpaper.updateField(null, filePath, ProtoInfoWallpaper.InfoWallpaper.Type.CHAT_BACKGROUND_VALUE);
                fillList(false);
                loadChatBackgroundImage.setValue(wList);

            }
        }
    }

    public interface OnImageWallpaperListClick {
        void onClick(int type, int position);

        void onAddImageClick();
    }

    public class WallpaperImage {
        private String imagePath;
        private boolean isNew;

        WallpaperImage(String imagePath, boolean isNew) {
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

    public class WallpaperSolidColor {
        private int color;
        private boolean isNew;

        WallpaperSolidColor(int color, boolean isNew) {
            this.color = color;
            this.isNew = isNew;
        }

        public int getColor() {
            return color;
        }

        public boolean isNew() {
            return isNew;
        }
    }
}
