package net.iGap.story.storyviews;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.controllers.MessageController;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLog;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.helper.upload.ApiBased.HttpUploader;
import net.iGap.messenger.ui.components.IconView;
import net.iGap.module.CircleImageView;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.downloader.DownloadObject;
import net.iGap.module.downloader.Downloader;
import net.iGap.module.downloader.Status;
import net.iGap.module.upload.UploadHttpRequest;
import net.iGap.module.upload.UploadObject;
import net.iGap.module.upload.Uploader;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoStoryUserAddNew;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmStory;
import net.iGap.realm.RealmStoryProto;
import net.iGap.story.liststories.ImageLoadingView;
import net.iGap.structs.AttachmentObject;
import net.iGap.structs.MessageObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import yogesh.firzen.mukkiasevaigal.P;

import static android.widget.Toast.LENGTH_SHORT;

public class StoryCell extends FrameLayout {

    private CircleImageView circleImage;
    private ImageLoadingView circleImageLoading;
    private TextView topText;
    private TextView middleText;
    private TextView bottomText;
    private IconView icon;
    private IconView icon2;
    private MaterialDesignTextView addIcon;
    private MaterialDesignTextView deleteIcon;
    private Context context;
    private int padding = 16;
    private boolean isRtl = G.isAppRtl;
    private boolean needDivider;
    private DeleteStory deleteStory;
    private IconClicked iconClicked;
    private AvatarHandler avatarHandler;
    private boolean isFromMyStatus;
    private long userId = 0;
    private long storyId = 0;
    private long uploadId;
    private String fileToken;
    private int sendStatus;
    private static boolean isCreatedView = false;

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

    public void setData(RealmStoryProto realmStoryProto, String displayName, String color, Context context, boolean needDivider, CircleStatus status, ImageLoadingView.Status imageLoadingStatus, IconClicked iconClicked) {
        initView(context, needDivider, status, imageLoadingStatus, iconClicked, realmStoryProto.getCreatedAt());
        this.userId = realmStoryProto.getUserId();
        this.isFromMyStatus = true;

        String name = HelperImageBackColor.getFirstAlphabetName(displayName);

        if (circleImageLoading.getStatus() == ImageLoadingView.Status.FAILED) {
            deleteIcon.setVisibility(VISIBLE);
            deleteIcon.setText(R.string.upload_ic);
            topText.setVisibility(GONE);
            bottomText.setVisibility(GONE);
            middleText.setVisibility(VISIBLE);
            middleText.setText(context.getString(R.string.story_could_not_sent));
            middleText.setTextColor(Color.RED);

        } else if (circleImageLoading.getStatus() == ImageLoadingView.Status.LOADING) {
            deleteIcon.setVisibility(GONE);
            topText.setVisibility(GONE);
            bottomText.setVisibility(GONE);
            middleText.setVisibility(VISIBLE);
            middleText.setText(context.getString(R.string.story_sending));
            middleText.setTextColor(Theme.getInstance().getTitleTextColor(context));

        } else {
            topText.setVisibility(VISIBLE);
            bottomText.setVisibility(VISIBLE);
            middleText.setVisibility(GONE);
            deleteIcon.setVisibility(VISIBLE);
            if (G.selectedLanguage.equals("en")) {
                topText.setText(realmStoryProto.getViewCount() + " " + context.getString(R.string.story_views));
            } else {
                topText.setText(HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(realmStoryProto.getViewCount())) + " " + context.getString(R.string.story_views));
            }
            bottomText.setText(HelperCalander.getTimeForMainRoom(realmStoryProto.getCreatedAt()));
        }

        RealmAttachment attachment = realmStoryProto.getFile();
        if (attachment != null && (attachment.getLocalThumbnailPath() != null || attachment.getLocalFilePath() != null)) {
            try {
                Glide.with(context).load(attachment.getLocalFilePath() != null ? attachment.getLocalFilePath() : attachment.getLocalThumbnailPath()).placeholder(new BitmapDrawable(context.getResources(), HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), name, color))).into(circleImageLoading);
            } catch (Exception e) {
                Glide.with(context).load(HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), name, color)).into(circleImageLoading);
            }


        } else if (attachment != null) {
            DownloadObject object = DownloadObject.createForStoryAvatar(AttachmentObject.create(attachment), true);
            if (object != null) {
                Downloader.getInstance(AccountManager.selectedAccount).download(object, arg -> {
                    if (arg.status == Status.SUCCESS && arg.data != null) {
                        String filepath = arg.data.getFilePath();
                        String downloadedFileToken = arg.data.getToken();

                        if (!(new File(filepath).exists())) {
                            HelperLog.getInstance().setErrorLog(new Exception("File Dont Exist After Download !!" + filepath));
                        }


                        DbManager.getInstance().doRealmTransaction(realm -> {
                            for (RealmStory realmAvatar1 : realm.where(RealmStory.class).equalTo("id", userId).findAll()) {
                                realmAvatar1.getRealmStoryProtos().get(realmAvatar1.getRealmStoryProtos().size() - 1).getFile().setLocalThumbnailPath(filepath);
                            }
                        });
                        G.runOnUiThread(() -> Glide.with(context).load(filepath).placeholder(new BitmapDrawable(context.getResources(), HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), name, color))).into(circleImageLoading));


                    }

                });
            }
        } else {
            Glide.with(context).load(new BitmapDrawable(context.getResources(), HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), name, color))).into(circleImageLoading);
        }

    }

    public void setData(RealmStory realmStory, String displayName, String color, Context context, boolean needDivider, CircleStatus status, ImageLoadingView.Status imageLoadingStatus, IconClicked iconClicked) {
        initView(context, needDivider, status, imageLoadingStatus, iconClicked, realmStory.getRealmStoryProtos().sort("createdAt").get(realmStory.getRealmStoryProtos().size() - 1).getCreatedAt());
        this.userId = realmStory.getUserId();
        circleImageLoading.setStatus(imageLoadingStatus);
        if (userId == AccountManager.getInstance().getCurrentUser().getId()) {
            topText.setText(context.getString(R.string.my_status));
        } else {
            topText.setText(displayName);
        }
        String name = HelperImageBackColor.getFirstAlphabetName(displayName);
        if (circleImageLoading.getStatus() == ImageLoadingView.Status.FAILED) {
            bottomText.setText(context.getString(R.string.story_could_not_sent));
            bottomText.setTextColor(Color.RED);
            deleteIcon.setTextColor(Color.RED);
            addIcon.setTextColor(Color.RED);
            addIcon.setText(R.string.error_icon);
        } else if (circleImageLoading.getStatus() == ImageLoadingView.Status.LOADING) {
            bottomText.setText(context.getString(R.string.story_sending));
            deleteIcon.setTextColor(Theme.getInstance().getTitleTextColor(context));
        } else {
            bottomText.setText(HelperCalander.getTimeForMainRoom(realmStory.getRealmStoryProtos().sort("createdAt").get(realmStory.getRealmStoryProtos().size() - 1).getCreatedAt()));
        }


        RealmAttachment attachment = realmStory.getRealmStoryProtos().sort("createdAt").get(realmStory.getRealmStoryProtos().size() - 1).getFile();
        if (status == CircleStatus.LOADING_CIRCLE_IMAGE) {
            if (attachment != null && (attachment.getLocalThumbnailPath() != null || attachment.getLocalFilePath() != null)) {
                try {
                    Glide.with(context).load(attachment.getLocalFilePath() != null ? attachment.getLocalFilePath() : attachment.getLocalThumbnailPath()).placeholder(new BitmapDrawable(context.getResources(), HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), name, color))).into(circleImageLoading);

                } catch (Exception e) {

                    Glide.with(context).load(new BitmapDrawable(context.getResources(), HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), name, color))).into(circleImageLoading);

                }


            } else if (attachment != null) {
                DownloadObject object = DownloadObject.createForStoryAvatar(AttachmentObject.create(attachment), true);
                if (object != null) {
                    Downloader.getInstance(AccountManager.selectedAccount).download(object, arg -> {
                        if (arg.status == Status.SUCCESS && arg.data != null) {
                            String filepath = arg.data.getFilePath();
                            String downloadedFileToken = arg.data.getToken();

                            if (!(new File(filepath).exists())) {
                                HelperLog.getInstance().setErrorLog(new Exception("File Dont Exist After Download !!" + filepath));
                            }


                            DbManager.getInstance().doRealmTransaction(realm -> {
                                for (RealmStory realmAvatar1 : realm.where(RealmStory.class).equalTo("id", userId).findAll()) {
                                    realmAvatar1.getRealmStoryProtos().get(realmAvatar1.getRealmStoryProtos().size() - 1).getFile().setLocalThumbnailPath(filepath);
                                }
                            });
                            G.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(context).load(filepath).placeholder(new BitmapDrawable(context.getResources(), HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), name, color))).into(circleImageLoading);
                                }
                            });


                        }

                    });
                }
            } else {
                Glide.with(context).load(new BitmapDrawable(context.getResources(), HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), name, color))).into(circleImageLoading);
            }
        }
    }

    public void initView(Context context, boolean needDivider, CircleStatus status, ImageLoadingView.Status imageLoadingStatus, IconClicked iconClicked, long createTime) {
        if (G.themeColor == Theme.DARK) {
            setBackground(Theme.getSelectorDrawable(Theme.getInstance().getDividerColor(context)));
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
        addIcon.setText(R.string.add_icon_2);
        addIcon.setTextColor(getResources().getColor(R.color.green));
        addIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        addIcon.setGravity(isRtl ? Gravity.LEFT : Gravity.RIGHT);

        addView(addIcon, LayoutCreator.createFrame(18, 18, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.BOTTOM, isRtl ? 0 : padding, 8, isRtl ? padding : 0, 8));

        topText = new TextView(context);
        topText.setSingleLine();
        topText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        topText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        topText.setTextColor(Theme.getInstance().getSendMessageTextColor(topText.getContext()));
        topText.setGravity((isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(topText, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, isRtl ? padding : ((padding * 2) + 56), 11.5f, isRtl ? ((padding * 2) + 56) : padding, 0));

        middleText = new TextView(context);
        middleText.setSingleLine();
        middleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        middleText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        middleText.setTextColor(Theme.getInstance().getPrimaryTextColor(context));
        middleText.setGravity((isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        middleText.setVisibility(GONE);
        addView(middleText, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL, isRtl ? padding : ((padding * 2) + 56), 0, isRtl ? ((padding * 2) + 56) : padding, 0));


        bottomText = new TextView(context);
        bottomText.setSingleLine();
        bottomText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        bottomText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        bottomText.setGravity((isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        bottomText.setTextColor(Color.GRAY);
        addView(bottomText, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, isRtl ? padding : ((padding * 2) + 56), 34.5f, isRtl ? ((padding * 2) + 56) : padding, 0));

        icon = new IconView(getContext());
        icon.setTypeface(ResourcesCompat.getFont(context, R.font.font_icon));
        icon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        addView(icon, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, padding, 0, padding, 0));

        icon2 = new IconView(getContext());
        icon2.setTypeface(ResourcesCompat.getFont(context, R.font.font_icon));
        icon2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        addView(icon2, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, isRtl ? (padding + 30) : padding, 0, isRtl ? padding : (30 + padding), 0));


        deleteIcon = new MaterialDesignTextView(getContext());
        deleteIcon.setTypeface(ResourcesCompat.getFont(context, R.font.font_icon));
        deleteIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
        deleteIcon.setText(R.string.horizontal_more_icon);
        deleteIcon.setTextColor(Theme.getInstance().getSendMessageTextColor(deleteIcon.getContext()));
        deleteIcon.setGravity(Gravity.CENTER);
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (circleImageLoading.getStatus() == ImageLoadingView.Status.FAILED && isFromMyStatus) {
                    deleteIcon.setVisibility(GONE);
                    middleText.setTextColor(Theme.getInstance().getTitleTextColor(context));
                    middleText.setText(context.getString(R.string.story_sending));
                    circleImageLoading.setStatus(ImageLoadingView.Status.LOADING);
                    DbManager.getInstance().doRealmTransaction(realm -> {
                        RealmStoryProto realmStoryProto;
                        if (fileToken != null) {
                            realmStoryProto = realm.where(RealmStoryProto.class).equalTo("fileToken", fileToken).findFirst();
                        } else {
                            realmStoryProto = realm.where(RealmStoryProto.class).equalTo("id", uploadId).findFirst();
                        }

                        if (realmStoryProto != null && realmStoryProto.getFileToken() != null) {
                            realmStoryProto.setStatus(MessageObject.STATUS_SENDING);
                            List<ProtoStoryUserAddNew.StoryAddRequest> storyAddRequests = new ArrayList<>();
                            ProtoStoryUserAddNew.StoryAddRequest.Builder storyAddRequest = ProtoStoryUserAddNew.StoryAddRequest.newBuilder();
                            storyAddRequest.setToken(realmStoryProto.getFileToken());
                            storyAddRequest.setCaption(realmStoryProto.getCaption());
                            storyAddRequests.add(storyAddRequest.build());

                            MessageController.getInstance(AccountManager.selectedAccount).addMyStory(storyAddRequests);
                        } else if (realmStoryProto != null && !Uploader.getInstance().isCompressingOrUploading(String.valueOf(realmStoryProto.getId()))) {
                            realmStoryProto.setStatus(MessageObject.STATUS_SENDING);
                            Uploader.getInstance().upload(UploadObject.createForStory(realmStoryProto.getId(), realmStoryProto.getFile().getLocalFilePath(), null, realmStoryProto.getCaption(), ProtoGlobal.RoomMessageType.IMAGE));
                        }
                        EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.STORY_SENDING);
                    });


                } else {
                    deleteStory.deleteStory(storyId);
                }


            }
        });
        addView(deleteIcon, LayoutCreator.createFrame(72, 72, (isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, isRtl ? 0 : 8, 8, isRtl ? 8 : 0, 8));

        if (status == CircleStatus.LOADING_CIRCLE_IMAGE) {
            setOnClickListener(v -> {
                deleteStory.onStoryClick(this);
            });
            circleImageLoading.setOnClickListener(v -> {
                deleteStory.onStoryClick(this);
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

    public void addIconVisibility(boolean visible) {
        addIcon.setVisibility(visible ? VISIBLE : GONE);
        if (visible) {
            if (getStatus() == StoryCell.CircleStatus.CIRCLE_IMAGE) {
                addIcon.setText(R.string.add_icon_2);
                addIcon.setTextColor(getResources().getColor(R.color.green));
            } else {
                addIcon.setTextColor(Color.RED);
                addIcon.setText(R.string.error_icon);
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
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Theme.getInstance().getDividerColor(getContext()));
            canvas.drawLine(isRtl ? 0 : LayoutCreator.dp(21), getMeasuredHeight() - 1, getMeasuredWidth() - (isRtl ? LayoutCreator.dp(21) : 0), getMeasuredHeight() - 1, paint);
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
        void deleteStory(long storyId);

        void onStoryClick(StoryCell storyCell);
    }
}
