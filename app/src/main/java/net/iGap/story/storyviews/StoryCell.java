package net.iGap.story.storyviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.controllers.MessageController;
import net.iGap.controllers.MessageDataStorage;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLog;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.helper.upload.ApiBased.HttpUploader;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.components.IconView;
import net.iGap.module.CircleImageView;
import net.iGap.module.FontIconTextView;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.downloader.DownloadObject;
import net.iGap.module.downloader.Downloader;
import net.iGap.module.downloader.Status;
import net.iGap.module.upload.UploadObject;
import net.iGap.module.upload.Uploader;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoStoryUserAddNew;
import net.iGap.realm.RealmStory;
import net.iGap.realm.RealmStoryProto;
import net.iGap.story.MainStoryObject;
import net.iGap.story.StoryObject;
import net.iGap.story.liststories.ImageLoadingView;
import net.iGap.structs.AttachmentObject;
import net.iGap.structs.MessageObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;
import static net.iGap.adapter.items.chat.ViewMaker.setTextSize;

public class StoryCell extends FrameLayout {

    private CircleImageView circleImage;
    private ImageLoadingView circleImageLoading;
    private TextView topText;
    private LinearLayout topViewRootView;
    private TextView middleText;
    private TextView bottomText;
    private IconView icon;
    private IconView icon2;
    private MaterialDesignTextView addIcon;
    private MaterialDesignTextView deleteIcon;
    private MaterialDesignTextView uploadIcon;
    private Context context;
    private int padding = 16;
    private boolean isRtl = G.isAppRtl;
    private boolean needDivider;
    private DeleteStory deleteStory;
    private IconClicked iconClicked;
    private AvatarHandler avatarHandler;
    private boolean isFromMyStatus;
    private boolean isVerified;
    private long userId = 0;
    private long roomId = 0;
    private long storyId = 0;
    private long uploadId;
    private String fileToken;
    private int sendStatus;
    private int storyIndex;
    private boolean isRoom;
    private int mode;
    private String userColorId = "#4aca69";
    private FontIconTextView verifyIconTv;
    private FontIconTextView channelIconTv;
    private boolean isLongClicked = false;
    private static boolean isCreatedView = false;

    public boolean isLongClicked() {
        return isLongClicked;
    }

    public void setLongClicked(boolean longClicked) {
        isLongClicked = longClicked;
    }

    public String getFileToken() {
        return fileToken;
    }

    public void setFileToken(String fileToken) {
        this.fileToken = fileToken;
    }

    public enum CircleStatus {CIRCLE_IMAGE, LOADING_CIRCLE_IMAGE}

    private CircleStatus status;

    public StoryCell(@NonNull Context context) {
        super(context);
    }

    public void setStoryId(long id) {
        this.storyId = id;
    }

    public long getStoryId() {
        return storyId;
    }

    public long getUploadId() {
        return uploadId;
    }

    public void setUploadId(long uploadId) {
        this.uploadId = uploadId;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    public int getStoryIndex() {
        return storyIndex;
    }

    public void setStoryIndex(int storyIndex) {
        this.storyIndex = storyIndex;
    }

    public boolean isRoom() {
        return isRoom;
    }

    public void setRoom(boolean room) {
        isRoom = room;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public void setData(StoryObject storyObject, boolean isRoom, String displayName, String color, Context context, boolean needDivider, CircleStatus status, ImageLoadingView.Status imageLoadingStatus, IconClicked iconClicked) {
        this.isFromMyStatus = true;
        initView(context, needDivider, status, imageLoadingStatus, iconClicked, storyObject.createdAt);
        this.userId = storyObject.userId;
        this.roomId = storyObject.roomId;


        String name = HelperImageBackColor.getFirstAlphabetName(storyObject.displayName != null ? storyObject.displayName : "");

        if (circleImageLoading.getStatus() == ImageLoadingView.Status.FAILED) {
            if (isRoom && mode == 0) {
                deleteIcon.setTextColor(Color.RED);
                topText.setText(storyObject.displayName);
                bottomText.setText(context.getString(R.string.story_could_not_sent));
                bottomText.setTextColor(Color.RED);
            } else {
                uploadIcon.setVisibility(VISIBLE);
                deleteIcon.setVisibility(VISIBLE);
                deleteIcon.setText(R.string.icon_Delete);
                topText.setVisibility(GONE);
                bottomText.setVisibility(GONE);
                middleText.setVisibility(VISIBLE);
                middleText.setText(context.getString(R.string.story_could_not_sent));
                middleText.setTextColor(Theme.getColor(Theme.key_title_text));
            }

        } else if (circleImageLoading.getStatus() == ImageLoadingView.Status.LOADING) {
            if (isRoom && mode == 0) {
                topText.setText(storyObject.displayName);
                bottomText.setText(context.getString(R.string.story_sending));
            } else {
                uploadIcon.setVisibility(GONE);
                deleteIcon.setVisibility(GONE);
                topText.setVisibility(GONE);
                middleText.setVisibility(VISIBLE);
                middleText.setText(context.getString(R.string.story_sending));
                middleText.setTextColor(Theme.getColor(Theme.key_title_text));
            }
        } else {
            topText.setVisibility(VISIBLE);
            bottomText.setVisibility(VISIBLE);
            middleText.setVisibility(GONE);
            uploadIcon.setVisibility(GONE);
            deleteIcon.setVisibility(VISIBLE);
            if (isRoom && mode == 0) {
                topText.setText(storyObject.displayName);
            } else {
                if (G.selectedLanguage.equals("fa")) {
                    topText.setText(HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(storyObject.viewCount)) + " " + context.getString(R.string.story_views));
                } else {
                    topText.setText(storyObject.viewCount + " " + context.getString(R.string.story_views));
                }
            }

            bottomText.setText(LastSeenTimeUtil.computeTime(context, storyObject.userId, storyObject.createdAt / 1000L, false, false));
        }

        AttachmentObject attachment = storyObject.attachmentObject;
        if (attachment != null && (attachment.thumbnailPath != null || attachment.filePath != null)) {
            try {
                Glide.with(G.context).load(attachment.filePath != null ? attachment.filePath : attachment.thumbnailPath).placeholder(new BitmapDrawable(context.getResources(), HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), name, color))).into(circleImageLoading);
            } catch (Exception e) {
                Glide.with(G.context).load(HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), name, color)).into(circleImageLoading);
            }


        } else if (attachment != null) {
            Glide.with(G.context).load(new BitmapDrawable(context.getResources(), HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), name, color))).into(circleImageLoading);
            DownloadObject object = DownloadObject.createForStory(attachment, storyObject.storyId, true);
            Log.e("skfjskjfsd", "setData2: " + storyId + "/" + object.downloadId);
            if (object != null) {
                Downloader.getInstance(AccountManager.selectedAccount).download(object, arg -> {
                    if (arg.status == Status.SUCCESS && arg.data != null) {
                        String filepath = arg.data.getFilePath();
                        String downloadedFileToken = arg.data.getToken();

                        if (!(new File(filepath).exists())) {
                            HelperLog.getInstance().setErrorLog(new Exception("File Dont Exist After Download !!" + filepath));
                        }

                        if (arg.data.getDownloadObject().downloadId == storyId) {
                            DbManager.getInstance().doRealmTransaction(realm1 -> {
                                RealmStoryProto realmStoryProto = realm1.where(RealmStoryProto.class).equalTo("isForReply", false).equalTo("storyId", storyId).findFirst();
                                if (realmStoryProto != null) {
                                    realmStoryProto.getFile().setLocalFilePath(filepath);
                                }
                            });
                            G.runOnUiThread(() -> Glide.with(G.context).load(filepath).placeholder(new BitmapDrawable(context.getResources(), HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), name, color))).into(circleImageLoading));
                        }

                    }

                });
            }
        } else {
            Glide.with(G.context).load(new BitmapDrawable(context.getResources(), HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), name, color))).into(circleImageLoading);
        }

    }

    public void setData(MainStoryObject mainStoryObject, String color, Context context, boolean needDivider, CircleStatus status, ImageLoadingView.Status imageLoadingStatus, IconClicked iconClicked) {
        this.userId = mainStoryObject.userId;
        this.roomId = mainStoryObject.roomId;
        this.isVerified = mainStoryObject.isVerified;
        initView(context, needDivider, status, imageLoadingStatus, iconClicked, mainStoryObject.storyObjects.get(0).createdAt);

        circleImageLoading.setStatus(imageLoadingStatus);
        if (userId == AccountManager.getInstance().getCurrentUser().getId()) {
            topText.setText(context.getString(R.string.my_status));
        } else {
            if (roomId != 0) {
                channelIconTv.setVisibility(VISIBLE);
            }
            if (isVerified) {
                verifyIconTv.setVisibility(VISIBLE);
            }
            topText.setText(mainStoryObject.storyObjects.get(0).displayName != null ? mainStoryObject.storyObjects.get(0).displayName : "");
        }
        String name = HelperImageBackColor.getFirstAlphabetName(mainStoryObject.storyObjects.get(0).displayName != null ? mainStoryObject.storyObjects.get(0).displayName : "");
        if (circleImageLoading.getStatus() == ImageLoadingView.Status.FAILED) {
            bottomText.setText(context.getString(R.string.story_could_not_sent));
            bottomText.setTextColor(Theme.getColor(Theme.key_default_text));
            uploadIcon.setVisibility(GONE);
            deleteIcon.setTextColor(Theme.getColor(Theme.key_theme_color));
            addIcon.setTextColor(Theme.getColor(Theme.key_theme_color));
            addIcon.setText(R.string.icon_error);
        } else if (circleImageLoading.getStatus() == ImageLoadingView.Status.LOADING) {
            bottomText.setText(context.getString(R.string.story_sending));
            uploadIcon.setVisibility(GONE);
            deleteIcon.setTextColor(Theme.getColor(Theme.key_title_text));
        } else {
            bottomText.setText(LastSeenTimeUtil.computeTime(context, mainStoryObject.userId, mainStoryObject.storyObjects.get(0).createdAt / 1000L, false, false));
        }


        AttachmentObject attachment = mainStoryObject.storyObjects.get(0).attachmentObject;
        if (status == CircleStatus.LOADING_CIRCLE_IMAGE) {
            if (attachment != null && (attachment.thumbnailPath != null || attachment.filePath != null)) {
                try {
                    Glide.with(G.context).load(attachment.filePath != null ? attachment.filePath : attachment.thumbnailPath).placeholder(new BitmapDrawable(context.getResources(), HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), name, color))).into(circleImageLoading);

                } catch (Exception e) {

                    Glide.with(G.context).load(new BitmapDrawable(context.getResources(), HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), name, color))).into(circleImageLoading);

                }


            } else if (attachment != null) {
                Glide.with(G.context).load(new BitmapDrawable(context.getResources(), HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), name, color))).into(circleImageLoading);
                DownloadObject object = DownloadObject.createForStory(attachment, storyId, true);
                if (object != null) {
                    Downloader.getInstance(AccountManager.selectedAccount).download(object, arg -> {
                        if (arg.status == Status.SUCCESS && arg.data != null) {
                            String filepath = arg.data.getFilePath();
                            String downloadedFileToken = arg.data.getToken();

                            if (!(new File(filepath).exists())) {
                                HelperLog.getInstance().setErrorLog(new Exception("File Dont Exist After Download !!" + filepath));
                            }


                            if (arg.data.getDownloadObject().downloadId == storyId) {
                                DbManager.getInstance().doRealmTransaction(realm1 -> {
                                    RealmStoryProto realmStoryProto = realm1.where(RealmStoryProto.class).equalTo("isForReply", false).equalTo("storyId", storyId).findFirst();
                                    if (realmStoryProto != null) {
                                        realmStoryProto.getFile().setLocalFilePath(filepath);
                                    }
                                });
                                G.runOnUiThread(() -> Glide.with(G.context).load(filepath).placeholder(new BitmapDrawable(context.getResources(), HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), name, color))).into(circleImageLoading));
                            }

                        }

                    });
                }
            } else {
                Glide.with(G.context).load(new BitmapDrawable(context.getResources(), HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), name, color))).into(circleImageLoading);
            }
        }
    }

    public void initView(Context context, boolean needDivider, CircleStatus status, ImageLoadingView.Status imageLoadingStatus, IconClicked iconClicked, long createTime) {
        removeAllViews();
        if (Theme.isDark() || Theme.isNight()) {
            setBackground(Theme.getSelectorDrawable(Theme.getColor(Theme.key_white)));
        } else {
            setBackgroundColor(Color.WHITE);
        }
        this.status = status;
        this.needDivider = needDivider;
        this.iconClicked = iconClicked;
        this.context = context;
        setWillNotDraw(!needDivider);
        View view;
        switch (this.status) {
            case CIRCLE_IMAGE:
                circleImage = new CircleImageView(getContext());
                circleImage.setLayoutParams(LayoutCreator.createFrame(56, 56, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL, isRtl ? 0 : padding, 8, isRtl ? padding : 0, 8));
                view = circleImage;
                break;
            case LOADING_CIRCLE_IMAGE:
                circleImageLoading = new ImageLoadingView(context);
                circleImageLoading.setStatus(imageLoadingStatus);
                circleImageLoading.setLayoutParams(LayoutCreator.createFrame(72, 72, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL, isRtl ? 0 : 8, 8, isRtl ? 8 : 0, 8));
                view = circleImageLoading;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.status);
        }
        addView(view);

        addIcon = new MaterialDesignTextView(context);
        addIcon.setText(R.string.icon_add_whit_circle);
        addIcon.setTextColor(Theme.getColor(Theme.key_theme_color));
        addIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        addIcon.setGravity(isRtl ? Gravity.LEFT : Gravity.RIGHT);

        addView(addIcon, LayoutCreator.createFrame(18, 18, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.BOTTOM, isRtl ? 0 : padding, 8, isRtl ? padding : 0, 8));

        topViewRootView = new LinearLayout(context);
        topViewRootView.setOrientation(LinearLayout.HORIZONTAL);
        topViewRootView.setGravity(Gravity.CENTER);
        addView(topViewRootView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, isRtl ? padding : ((padding * 2) + 56), 11.5f, isRtl ? ((padding * 2) + 56) : padding, 0));

        channelIconTv = new FontIconTextView(getContext());
        setTextSize(channelIconTv, R.dimen.dp14);
        channelIconTv.setText(R.string.icon_channel);
        channelIconTv.setTextColor(Theme.getColor(Theme.key_icon));
        channelIconTv.setVisibility(GONE);
        topViewRootView.addView(channelIconTv, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));

        topText = new TextView(context);
        topText.setSingleLine();
        topText.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font_bold));
        ViewMaker.setTextSize(topText, R.dimen.dp15);
        topText.setSingleLine(true);
        topText.setEllipsize(TextUtils.TruncateAt.END);
        topText.setTextColor(Theme.getColor(Theme.key_title_text));
        topText.setGravity((isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        topViewRootView.addView(topText, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, !isRtl && roomId != 0 && userId != AccountManager.getInstance().getCurrentUser().getId() ? 4 : 0, 0, isRtl && roomId != 0 && userId != AccountManager.getInstance().getCurrentUser().getId() ? 4 : 0, 0));

        verifyIconTv = new FontIconTextView(getContext());
        verifyIconTv.setTextColor(Theme.getColor(Theme.key_dark_theme_color));
        verifyIconTv.setText(R.string.icon_blue_badge);
        setTextSize(verifyIconTv, R.dimen.standardTextSize);
        verifyIconTv.setVisibility(GONE);
        topViewRootView.addView(verifyIconTv, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, !isRtl && roomId != 0 && userId != AccountManager.getInstance().getCurrentUser().getId() ? 4 : 0, 0, isRtl && roomId != 0 && userId != AccountManager.getInstance().getCurrentUser().getId() ? 4 : 0, 0));


        middleText = new TextView(context);
        middleText.setSingleLine();
        middleText.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font_bold));
        ViewMaker.setTextSize(middleText, R.dimen.dp15);
        middleText.setSingleLine(true);
        middleText.setEllipsize(TextUtils.TruncateAt.END);
        middleText.setTextColor(Theme.getColor(Theme.key_default_text));
        middleText.setGravity((isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        middleText.setVisibility(GONE);
        addView(middleText, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL, isRtl ? padding : ((padding * 2) + 56), 0, isRtl ? ((padding * 2) + 56) : padding, 0));


        bottomText = new TextView(context);
        bottomText.setSingleLine();
        ViewMaker.setTypeFace(bottomText);
        ViewMaker.setTextSize(bottomText, R.dimen.dp13);
        bottomText.setGravity((isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        bottomText.setTextColor(Theme.getColor(Theme.key_default_text));
        addView(bottomText, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, isRtl ? padding : ((padding * 2) + 56), 34.5f, isRtl ? ((padding * 2) + 56) : padding, 0));

        icon = new IconView(getContext());
        icon.setTypeface(ResourcesCompat.getFont(context, R.font.font_icons));
        icon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        addView(icon, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, padding, 0, padding, 0));

        icon2 = new IconView(getContext());
        icon2.setTypeface(ResourcesCompat.getFont(context, R.font.font_icons));
        icon2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        addView(icon2, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, isRtl ? (padding + 30) : padding, 0, isRtl ? padding : (30 + padding), 0));

        LinearLayout iconsRootView = new LinearLayout(getContext());
        iconsRootView.setOrientation(LinearLayout.HORIZONTAL);
        iconsRootView.setGravity(Gravity.CENTER);
        addView(iconsRootView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, isRtl ? 0 : 8, 8, isRtl ? 8 : 0, 8));

        uploadIcon = new MaterialDesignTextView(getContext());
        uploadIcon.setTypeface(ResourcesCompat.getFont(context, R.font.font_icons));
        uploadIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
        uploadIcon.setText(R.string.icon_upload);
        uploadIcon.setVisibility(GONE);
        uploadIcon.setTextColor(Theme.getColor(Theme.key_theme_color));
        uploadIcon.setGravity(Gravity.CENTER);
        uploadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((circleImageLoading.getStatus() == ImageLoadingView.Status.FAILED && isFromMyStatus && !isRoom) ||
                        (circleImageLoading.getStatus() == ImageLoadingView.Status.FAILED && isFromMyStatus && isRoom && mode == 1)) {
                    deleteIcon.setVisibility(GONE);
                    uploadIcon.setVisibility(GONE);
                    deleteIcon.setVisibility(GONE);
                    middleText.setTextColor(Theme.getColor(Theme.key_title_text));
                    middleText.setText(context.getString(R.string.story_sending));
                    circleImageLoading.setStatus(ImageLoadingView.Status.LOADING);
                    StoryObject realmStoryProto;
                    if (fileToken != null) {
                        realmStoryProto = MessageDataStorage.getInstance(AccountManager.selectedAccount).getStoryWithFileToken(fileToken);
                    } else {
                        realmStoryProto = MessageDataStorage.getInstance(AccountManager.selectedAccount).getStoryWithUploadId(uploadId);
                    }

                    if (realmStoryProto != null && realmStoryProto.fileToken != null) {
                        MessageDataStorage.getInstance(AccountManager.selectedAccount).updateStoryStatus(realmStoryProto.id, MessageObject.STATUS_SENDING);
                        List<ProtoStoryUserAddNew.StoryAddRequest> storyAddRequests = new ArrayList<>();
                        ProtoStoryUserAddNew.StoryAddRequest.Builder storyAddRequest = ProtoStoryUserAddNew.StoryAddRequest.newBuilder();
                        storyAddRequest.setToken(realmStoryProto.fileToken);
                        storyAddRequest.setCaption(realmStoryProto.caption);
                        storyAddRequests.add(storyAddRequest.build());
                        if (isRoom) {
                            MessageController.getInstance(AccountManager.selectedAccount).addMyRoomStory(storyAddRequests, roomId);
                        } else {
                            MessageController.getInstance(AccountManager.selectedAccount).addMyStory(storyAddRequests);
                        }

                    } else if (realmStoryProto != null && !Uploader.getInstance().isCompressingOrUploading(String.valueOf(realmStoryProto.id))) {
                        MessageDataStorage.getInstance(AccountManager.selectedAccount).updateStoryStatus(realmStoryProto.id, MessageObject.STATUS_SENDING);
                        HttpUploader.isStoryUploading = true;
                        Uploader.getInstance().upload(UploadObject.createForStory(realmStoryProto.id, realmStoryProto.attachmentObject.filePath, null, realmStoryProto.caption, ProtoGlobal.RoomMessageType.STORY));
                    }
                    EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.STORY_SENDING);

                } else {
                    deleteStory.deleteStory(StoryCell.this, storyId, roomId, isRoom);
                }
            }
        });

        deleteIcon = new MaterialDesignTextView(getContext());
        deleteIcon.setTypeface(ResourcesCompat.getFont(context, R.font.font_icons));
        deleteIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
        deleteIcon.setText(R.string.icon_other_horizontal_dots);
        deleteIcon.setVisibility(GONE);
        deleteIcon.setTextColor(Theme.getColor(Theme.key_theme_color));
        deleteIcon.setGravity(Gravity.CENTER);
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteStory.deleteStory(StoryCell.this, storyId, roomId, isRoom);
            }
        });

        iconsRootView.addView(uploadIcon, LayoutCreator.createLinear(72, 72, Gravity.CENTER_VERTICAL));
        iconsRootView.addView(deleteIcon, LayoutCreator.createLinear(72, 72, Gravity.CENTER_VERTICAL));

        setOnClickListener(v -> {
            deleteStory.onStoryClick(this);
        });

        if (status == CircleStatus.LOADING_CIRCLE_IMAGE) {
            circleImageLoading.setOnClickListener(v -> {
                deleteStory.onStoryClick(this);
            });
        }
        if (isFromMyStatus) {
            setOnLongClickListener(view1 -> {
                if (!isLongClicked) {
                    deleteStory.onStoryLongClick(StoryCell.this);
                    return false;
                }
                return true;
            });

            circleImageLoading.setOnLongClickListener(view1 -> {
                deleteStory.onStoryLongClick(StoryCell.this);
                return true;
            });
        }
    }

    public void setText(String topText, String bottomText) {
        this.topText.setText(topText);
        this.bottomText.setText(bottomText);
    }

    public void setTextColor(int colorLeftText, int colorBottomText) {
        this.topText.setTextColor(colorLeftText);
        this.bottomText.setTextColor(colorBottomText);
    }

    public void setTextSize(int topTextSize, int bottomTextSize) {
        this.topText.setTextSize(topTextSize);
        this.bottomText.setTextSize(bottomTextSize);
    }

    public static void setTextSize(TextView v, int sizeSrc) {

        int mSize = i_Dp(sizeSrc);
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSize);
    }

    public void setTextStyle(int typeFace) {
        topText.setTypeface(ResourcesCompat.getFont(context, typeFace));
    }

    public void setIcons(int icon, int icon2) {
        this.icon.setIcon(icon);
        this.icon2.setIcon(icon2);
    }

    public void setIconsColor(int color, int color2) {
        this.icon.setTextColor(color);
        this.icon2.setTextColor(color2);
    }

    public void setImage(int imageId, AvatarHandler avatarHandler) {
        switch (status) {
            case CIRCLE_IMAGE:
                avatarHandler.getAvatar(new ParamWithAvatarType(this.circleImage, AccountManager.getInstance().getCurrentUser().getId()).avatarType(AvatarHandler.AvatarType.USER));
                break;
            case LOADING_CIRCLE_IMAGE:
                this.circleImageLoading.setImageResource(imageId);
        }
    }

    public void setImage(AvatarHandler avatarHandler, long userId) {
        switch (status) {
            case CIRCLE_IMAGE:
                avatarHandler.getAvatar(new ParamWithAvatarType(this.circleImage, userId).avatarType(AvatarHandler.AvatarType.USER));
                break;
        }
    }

    public void addIconVisibility(boolean visible) {
        addIcon.setVisibility(visible ? VISIBLE : GONE);
        if (visible) {
            if (getStatus() == StoryCell.CircleStatus.CIRCLE_IMAGE) {
                addIcon.setText(R.string.icon_add_whit_circle);
                addIcon.setTextColor(Theme.getColor(Theme.key_theme_color));
            } else {
                addIcon.setTextColor(Theme.getColor(Theme.key_dark_red));
                addIcon.setText(R.string.icon_error);
            }
        }

    }

    public void deleteIconVisibility(boolean visible) {
        deleteIcon.setVisibility(visible ? VISIBLE : GONE);
    }

    public void deleteIconVisibility(boolean visible, int image) {
        deleteIcon.setVisibility(visible ? VISIBLE : GONE);
        deleteIcon.setText(image);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(72) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(G.isAppRtl ? 0 : LayoutCreator.dp(20), getMeasuredHeight() - 1, getMeasuredWidth() - (G.isAppRtl ? LayoutCreator.dp(20) : 0), getMeasuredHeight() - 1, Theme.getDividerPaint());
        }
    }

    public interface IconClicked {
        void clickedIcon(View icon, View icon2);
    }

    public CircleStatus getStatus() {
        return status;
    }

    public void setStatus(CircleStatus status) {
        this.status = status;
        View view;
        switch (this.status) {
            case CIRCLE_IMAGE:
                if (circleImage == null) {
                    circleImage = new CircleImageView(getContext());
                    circleImage.setLayoutParams(LayoutCreator.createFrame(56, 56, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL, isRtl ? 0 : padding, 8, isRtl ? padding : 0, 8));
                    addView(circleImage, 0);
                } else {
                    circleImage.setVisibility(VISIBLE);
                }

                if (circleImageLoading != null) {
                    circleImageLoading.setVisibility(GONE);
                }
                break;
            case LOADING_CIRCLE_IMAGE:
                if (circleImage != null) {
                    circleImage.setVisibility(GONE);
                }
                if (circleImageLoading == null) {
                    circleImageLoading = new ImageLoadingView(context);
                    circleImageLoading.setLayoutParams(LayoutCreator.createFrame(72, 72, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL, isRtl ? 0 : 8, 8, isRtl ? 8 : 0, 8));
                    addView(circleImageLoading, 0);
                } else {
                    circleImageLoading.setVisibility(VISIBLE);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.status);
        }

    }

    public void setImageLoadingStatus(ImageLoadingView.Status status) {
        if (circleImageLoading != null) {
            circleImageLoading.setStatus(status);
        }
    }

    public String getUserColorId() {
        return userColorId;
    }

    public void setUserColorId(String userColorId, String name) {
        this.userColorId = userColorId;
        Log.e("dkfslkdj", ": " + name);
        circleImage.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), HelperImageBackColor.getFirstAlphabetName(name), userColorId));
    }

    public ImageLoadingView getCircleImageLoading() {
        return circleImageLoading;
    }

    public void setDeleteStory(DeleteStory deleteStory) {
        this.deleteStory = deleteStory;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public interface DeleteStory {
        void deleteStory(StoryCell storyCell, long storyId, long roomId, boolean isRoom);

        void onStoryClick(StoryCell storyCell);

        void onStoryLongClick(StoryCell storyCell);
    }
}
