package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperSaveFile;
import net.iGap.helper.ImageHelper;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.cell.ImageWallpaperCell;
import net.iGap.messenger.ui.cell.SolidWallpaperCell;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.messenger.ui.toolBar.ToolbarItems;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.StructWallpaper;
import net.iGap.module.TimeUtils;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.module.dialog.topsheet.TopSheetDialog;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.OnGetWallpaper;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoInfoWallpaper;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmWallpaper;
import net.iGap.realm.RealmWallpaperProto;
import net.iGap.request.RequestInfoWallpaper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.iGap.helper.HelperSaveFile.getPrivateDirectory;

public class ChatBackgroundFragment extends BaseFragment {

    private final static int more_button = 1;
    private final static int retry_button = 2;
    private final static int done_button = 3;

    private static final int WALLPAPER_IMAGE = 1;
    private static final int SOLID_COLOR = 0;

    public static String wallpaperPath;
    private final List<String> solidColorList;
    private AppCompatImageView imageView;
    private RecyclerListView listView;
    private ProgressBar progressBar;
    private View retryButton;
    private View doneButton;
    private List<StructWallpaper> structWallpapers;
    private ListAdapter listAdapter;
    private int type = WALLPAPER_IMAGE;
    private boolean loading = true;
    private final SharedPreferences sharedPreferences = getSharedManager().getSettingSharedPreferences();
    private File privateDirectory;
    private final MutableLiveData<Boolean> wallpaperLoaded = new MutableLiveData<>(false);

    public ChatBackgroundFragment() {
        try {
            new File(G.DIR_CHAT_BACKGROUND).mkdirs();
            new File(G.DIR_CHAT_BACKGROUND + "/.nomedia").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            if (getActivity() instanceof ActivityMain) {
                getActivity().onBackPressed();
            }
        }
        solidColorList = new ArrayList<>(Arrays.asList(
                "#2962ff", "#00b8d4", "#b71c1c", "#e53935", "#e57373", "#880e4f", "#d81b60", "#f06292",
                "#4a148c", "#8e24aa", "#ba68c8", "#311b92", "#5e35b1", "#9575cd", "#1a237e", "#3949ab",
                "#7986cb", "#0d47a1", "#1e88e5", "#64b5f6", "#01579b", "#039be5", "#4fc3f7", "#006064",
                "#00acc1", "#4dd0e1", "#004d40", "#00897b", "#4db6ac", "#1b5e20", "#43a047", "#81c784",
                "#33691e", "#7cb342", "#aed581", "#827717", "#c0ca33", "#dce775", "#f57f17", "#fdd835",
                "#fff176", "#ff6f00", "#ffb300", "#ffd54f", "#e65100", "#fb8c00", "#fb8c00", "#bf360c",
                "#f4511e", "#ff8a65", "#3e2723", "#6d4c41", "#a1887f", "#212121", "#757575", "#e0e0e0",
                "#263238", "#546e7a", "#90a4ae"));
    }

    @Override
    public View createView(Context context) {
        privateDirectory = getPrivateDirectory(getActivity());

        imageView = new AppCompatImageView(context);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String backGroundPath = sharedPreferences.getString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "");
        if (backGroundPath.length() > 0) {
            File file = new File(backGroundPath);
            if (file.exists()) {
                G.imageLoader.displayImage(AndroidUtils.suitablePath(backGroundPath), imageView);
            } else {
                imageView.setBackgroundColor(Color.parseColor(backGroundPath));
            }
        }
        progressBar = new ProgressBar(context);
        listView = new RecyclerListView(context);
        listView.setItemAnimator(null);
        listView.setLayoutAnimation(null);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(listAdapter = new ListAdapter());
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (position != 0) {
                doneButton.setVisibility(View.VISIBLE);
                retryButton.setVisibility(View.GONE);
            }
            if (view instanceof ImageWallpaperCell) {
                if (position == 0) {
                    showAddImageDialog();
                } else {
                    String bigImagePath;
                    if (structWallpapers.get(position - 1).getWallpaperType() == WallpaperType.proto) {
                        RealmAttachment realmAttachment = structWallpapers.get(position - 1).getProtoWallpaper().getFile();
                        bigImagePath = G.DIR_CHAT_BACKGROUND + "/" + realmAttachment.getCacheId() + "_" + realmAttachment.getName();
                    } else {
                        bigImagePath = structWallpapers.get(position - 1).getPath();
                    }
                    wallpaperPath = bigImagePath;
                    imageView.setBackground(null);
                    imageView.setImageDrawable(Drawable.createFromPath(new File(bigImagePath).getAbsolutePath()));
                }
            } else if (view instanceof SolidWallpaperCell) {
                imageView.setImageDrawable(null);
                imageView.setBackgroundColor(Color.parseColor(solidColorList.get(position - 1)));
            }
        });
        ViewGroup container = new ViewGroup(context) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec);
                imageView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                progressBar.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(30), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(30), MeasureSpec.EXACTLY));
                listView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(100), MeasureSpec.EXACTLY));
                setMeasuredDimension(width, height);
            }

            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int width = r - l;
                int height = b - t;
                imageView.layout(0, 0, width, height);
                progressBar.layout((width / 2) - (progressBar.getMeasuredWidth() / 2), (height / 2) - (progressBar.getMeasuredHeight() / 2), (width / 2) + progressBar.getMeasuredWidth(), (height / 2) + progressBar.getMeasuredHeight());
                listView.layout(0, height - LayoutCreator.dp(120), width, height - LayoutCreator.dp(20));
            }
        };
        container.setOnTouchListener((v, event) -> true);
        container.addView(imageView);
        container.addView(progressBar);
        container.addView(listView);
        fragmentView = container;
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        getStructWallpaperList();
        wallpaperLoaded.observe(getViewLifecycleOwner(), isLoad -> {
            if (listAdapter != null && loading && isLoad) {
                loading = false;
                progressBar.setVisibility(View.GONE);
                listAdapter.notifyDataSetChanged();
            }
        });
        return fragmentView;
    }

    private void showAddImageDialog() {
        new MaterialDialog.Builder(context)
                .title(R.string.choose_picture)
                .negativeText(R.string.cancel)
                .items(R.array.profile)
                .negativeColor(Theme.getColor(Theme.key_button_background))
                .positiveColor(Theme.getColor(Theme.key_button_background))
                .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                .itemsCallback((dialog, view, which, text) -> {
                    doneButton.setVisibility(View.VISIBLE);
                    retryButton.setVisibility(View.GONE);
                    AttachFile attachFile = new AttachFile(getActivity());
                    if (text.toString().equals(getString(R.string.from_camera))) {
                        try {
                            attachFile.requestTakePicture(ChatBackgroundFragment.this);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            attachFile.requestOpenGalleryForImageSingleSelect(ChatBackgroundFragment.this);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    dialog.dismiss();
                }).show();
    }

    private void getStructWallpaperList() {
        structWallpapers = new ArrayList<>();
        structWallpapers.clear();
        DbManager.getInstance().doRealmTask(realm -> {
            RealmWallpaper realmWallpaper = realm.where(RealmWallpaper.class).equalTo("type", ProtoInfoWallpaper.InfoWallpaper.Type.CHAT_BACKGROUND_VALUE).findFirst();
            if (realmWallpaper != null) {
                if (realmWallpaper.getLocalList() != null) {
                    for (String localPath : realmWallpaper.getLocalList()) {
                        if (new File(localPath).exists()) {
                            StructWallpaper structWallpaper = new StructWallpaper();
                            structWallpaper.setWallpaperType(ChatBackgroundFragment.WallpaperType.local);
                            structWallpaper.setPath(localPath);
                            structWallpapers.add(structWallpaper);
                        }
                    }
                }
                if (realmWallpaper.getWallPaperList() != null) {
                    for (RealmWallpaperProto wallpaper : realmWallpaper.getWallPaperList()) {
                        StructWallpaper structWallpaper = new StructWallpaper();
                        structWallpaper.setWallpaperType(ChatBackgroundFragment.WallpaperType.proto);
                        structWallpaper.setProtoWallpaper(realm.copyFromRealm(wallpaper));
                        structWallpapers.add(structWallpaper);
                        if (structWallpapers.size() == realmWallpaper.getWallPaperList().size()) {
                            wallpaperLoaded.postValue(true);
                        }
                    }
                }
            } else {
                getImageListFromServer();
            }
        });
    }

    private void getImageListFromServer() {
        progressBar.setVisibility(View.VISIBLE);
        G.onGetWallpaper = new OnGetWallpaper() {
            @Override
            public void onGetWallpaperList(final List<ProtoGlobal.Wallpaper> list) {
                RealmWallpaper.updateField(list, "", ProtoInfoWallpaper.InfoWallpaper.Type.CHAT_BACKGROUND_VALUE);
                getStructWallpaperList();
            }
        };
        new RequestInfoWallpaper().infoWallpaper(ProtoInfoWallpaper.InfoWallpaper.Type.CHAT_BACKGROUND);
    }

    public void setUserCustomImage(String filePath) {
        if (filePath != null) {
            if (new File(filePath).exists()) {
                RealmWallpaper.updateField(null, filePath, ProtoInfoWallpaper.InfoWallpaper.Type.CHAT_BACKGROUND_VALUE);
                getStructWallpaperList();
            }
        }
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
                        if (getActivity() != null) {
                            AttachFile attachFile = new AttachFile(getActivity());
                            filePath = attachFile.saveGalleryPicToLocal(AttachFile.getFilePathFromUri(data.getData()));
                        }
                    }
                    break;
            }
            setUserCustomImage(filePath);
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.st_title_Background));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        ToolbarItems toolbarItems = toolbar.createToolbarItems();
        View moreButton = toolbarItems.addItem(more_button, R.string.icon_other_vertical_dots, Color.WHITE);
        moreButton.setContentDescription(context.getString(R.string.AccDescrMoreOptions));
        retryButton = toolbarItems.addItem(retry_button, R.string.icon_retry, Color.WHITE);
        retryButton.setContentDescription(context.getString(R.string.retry));
        doneButton = toolbarItems.addItem(done_button, R.string.icon_check_ok, Color.WHITE);
        doneButton.setVisibility(View.GONE);
        doneButton.setContentDescription(context.getString(R.string.Done));
        toolbar.setListener(id -> {
            if (id == -1) {
                finish();
            } else if (id == retry_button) {
                resetWallpaper();
            } else if (id == done_button) {
                applyWallpaper();
            } else if (id == more_button) {
                showMenuDialog();
            }
        });
        return toolbar;
    }

    private void showMenuDialog() {
        if (getContext() != null) {
            List<String> menuItemList = new ArrayList<>();
            menuItemList.add(getString(R.string.solid_colors));
            menuItemList.add(getString(R.string.wallpapers));
            new TopSheetDialog(context).setListData(menuItemList, -1, position -> {
                if (menuItemList.get(position).equals(getString(R.string.solid_colors))) {
                    type = SOLID_COLOR;
                } else if (menuItemList.get(position).equals(getString(R.string.wallpapers))) {
                    type = WALLPAPER_IMAGE;
                }
                doneButton.setVisibility(View.GONE);
                retryButton.setVisibility(View.VISIBLE);
                listAdapter.notifyDataSetChanged();
            }).show();
        }
    }

    private void applyWallpaper() {
        if (wallpaperPath != null && wallpaperPath.length() > 0) {
            String finalPath = "";
            if (type == SOLID_COLOR) {
                finalPath = wallpaperPath;
                HelperSaveFile.removeFromPrivateDirectory(privateDirectory);
            } else {
                try {
                    finalPath = HelperSaveFile.saveInPrivateDirectory(privateDirectory, wallpaperPath);
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
            finish();
        }
    }

    private void resetWallpaper() {
        HelperSaveFile.removeFromPrivateDirectory(privateDirectory);
        sharedPreferences.edit()
                .putString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "")
                .putBoolean(SHP_SETTING.KEY_CHAT_BACKGROUND_IS_DEFAULT, true)
                .apply();
        if (G.twoPaneMode) {
            G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.CHAT_BACKGROUND_CHANGED, ""));
        }
        finish();
    }

    public enum WallpaperType {
        addNew, local, proto
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        @Override
        public int getItemCount() {
            return loading ? 0 : (type == WALLPAPER_IMAGE ? structWallpapers.size() + 1 : solidColorList.size());
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == WALLPAPER_IMAGE) {
                view = new ImageWallpaperCell(context);
            } else if (viewType == SOLID_COLOR) {
                view = new SolidWallpaperCell(context);
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            if (viewType == WALLPAPER_IMAGE) {
                ImageWallpaperCell imageWallpaperCell = (ImageWallpaperCell) holder.itemView;
                imageWallpaperCell.setImageResource(0);
                if (position == 0) {
                    imageWallpaperCell.setImageResource(R.drawable.add_chat_background_setting);
                } else {
                    StructWallpaper wallpaper = structWallpapers.get(position - 1);
                    if (wallpaper.getWallpaperType() == WallpaperType.proto) {
                        RealmAttachment realmAttachment = wallpaper.getProtoWallpaper().getFile();
                        imageWallpaperCell.downloadImage(realmAttachment, "Thumbnail");
                    } else {
                        imageWallpaperCell.displayImage(wallpaper.getPath());
                    }
                    String bigImagePath;
                    if (wallpaper.getWallpaperType() == WallpaperType.proto) {
                        RealmAttachment realmAttachment = wallpaper.getProtoWallpaper().getFile();
                        bigImagePath = G.DIR_CHAT_BACKGROUND + "/" + realmAttachment.getCacheId() + "_" + realmAttachment.getName();
                    } else {
                        bigImagePath = wallpaper.getPath();
                    }
                    if (!new File(bigImagePath).exists()) {
                        RealmAttachment realmAttachment = structWallpapers.get(position - 1).getProtoWallpaper().getFile();
                        imageWallpaperCell.downloadImage(realmAttachment, "File");
                    }
                }
            } else if (viewType == SOLID_COLOR) {
                SolidWallpaperCell solidWallpaperCell = (SolidWallpaperCell) holder.itemView;
                solidWallpaperCell.setCardBackgroundColor(Color.parseColor(solidColorList.get(position)));
            }
        }

        @Override
        public int getItemViewType(int position) {
            return type;
        }
    }
}
