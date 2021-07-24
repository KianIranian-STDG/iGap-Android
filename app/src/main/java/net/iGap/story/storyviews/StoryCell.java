package net.iGap.story.storyviews;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLog;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.messenger.ui.components.IconView;
import net.iGap.module.CircleImageView;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.downloader.DownloadObject;
import net.iGap.module.downloader.Downloader;
import net.iGap.module.downloader.Status;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmStory;
import net.iGap.story.liststories.ImageLoadingView;
import net.iGap.structs.AttachmentObject;

import java.io.File;

import static android.widget.Toast.LENGTH_SHORT;

public class StoryCell extends FrameLayout {

    private CircleImageView circleImage;
    private ImageLoadingView circleImageLoading;
    private TextView topText;
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
    private long userId = 0;
    private long storyId = 0;

    public enum CircleStatus {CIRCLE_IMAGE, LOADING_CIRCLE_IMAGE}

    private CircleStatus status;

    public StoryCell(@NonNull Context context, boolean needDivider, CircleStatus status) {
        this(context, needDivider, status, null);
    }

    public void setStoryId(long id) {
        this.storyId = id;
    }

    public void setData(boolean isFromMyStatus, long userId, long time, String displayName, String color, RealmAttachment attachment, ProtoGlobal.File file) {
        //   avatarHandler.getAvatar(new ParamWithAvatarType(imageView, userId).avatarType(AvatarHandler.AvatarType.USER));
        this.userId = userId;
        topText.setText(displayName);
        bottomText.setText(HelperCalander.getTimeForMainRoom(time));
        if (status == CircleStatus.LOADING_CIRCLE_IMAGE || isFromMyStatus) {
            if (attachment.getLocalThumbnailPath() != null) {
                try{
                    if (!isFromMyStatus) {
                        circleImageLoading.setImageBitmap(BitmapFactory.decodeFile(attachment.getLocalThumbnailPath()));
                    } else {
                        circleImage.setImageBitmap(BitmapFactory.decodeFile(attachment.getLocalThumbnailPath()));
                    }
                }catch (Exception e){
                    if (!isFromMyStatus) {
                        circleImageLoading.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), displayName, color));
                    } else {
                        circleImage.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), displayName, color));
                    }
                }


            } else {
                circleImageLoading.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture(LayoutCreator.dp(64), displayName, color));
                DownloadObject object = DownloadObject.createForStoryAvatar(AttachmentObject.create(attachment), false);
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
                                    if (!isFromMyStatus) {
                                        circleImageLoading.setImageBitmap(BitmapFactory.decodeFile(filepath));
                                    } else {

                                        circleImage.setImageBitmap(BitmapFactory.decodeFile(filepath));
                                    }
                                }
                            });


                        }

                    });
                }
            }
        }
    }

    public StoryCell(@NonNull Context context, boolean needDivider, CircleStatus status, IconClicked iconClicked) {
        super(context);
        if (G.themeColor == Theme.DARK) {
            setBackgroundColor(Theme.getInstance().getToolbarBackgroundColor(context));
        }else {
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
                circleImageLoading.setStatus(ImageLoadingView.Status.LOADING);
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
        topText.setTextColor(Theme.getInstance().getPrimaryTextColor(context));
        topText.setGravity((isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(topText, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, isRtl ? padding : ((padding * 2) + 56), 11.5f, isRtl ? ((padding * 2) + 56) : padding, 0));

        bottomText = new TextView(context);
        bottomText.setSingleLine();
        bottomText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        bottomText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        bottomText.setGravity((isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        bottomText.setTextColor(Theme.getInstance().getTitleTextColor(context));
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
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteStory.deleteStory(storyId);
            }
        });
        addView(deleteIcon, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, isRtl ? (padding + 20) : padding, 0, isRtl ? padding : (20 + padding), 0));
        // this.iconClicked.clickedIcon(icon, icon2);


        if (status == CircleStatus.LOADING_CIRCLE_IMAGE) {
            setOnClickListener(v -> {
                Log.i("nazanin", "StoryCell: ");
                switch (circleImageLoading.getStatus()) {
                    case UNCLICKED:
                        circleImageLoading.setStatus(ImageLoadingView.Status.LOADING);
                        break;
                    case LOADING:
                        circleImageLoading.setStatus(ImageLoadingView.Status.CLICKED);
                        break;
                    case CLICKED:
                        circleImageLoading.setStatus(ImageLoadingView.Status.UNCLICKED);
                }
                Toast.makeText(getContext(), "click !", LENGTH_SHORT).show();
            });
            circleImageLoading.setOnLongClickListener(v -> {
                Toast.makeText(getContext(), "long click !", LENGTH_SHORT).show();
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
            canvas.drawLine(isRtl ? 0 : LayoutCreator.dp(21), getMeasuredHeight() - 1, getMeasuredWidth() - (isRtl ? LayoutCreator.dp(21) : 0), getMeasuredHeight() - 1, Theme.getInstance().getDividerPaint(getContext()));
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

        void openMyStory();
    }
}
