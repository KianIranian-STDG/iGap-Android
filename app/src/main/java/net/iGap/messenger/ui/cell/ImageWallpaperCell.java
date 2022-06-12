package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.LayoutCreator;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.messenger.ui.fragments.ChatBackgroundFragment;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;

import java.io.File;

public class ImageWallpaperCell extends CardView {
    private final FrameLayout frameLayout;
    private final AppCompatImageView imageView;
    private final MessageProgress messageProgress;

    public ImageWallpaperCell(@NonNull Context context) {
        super(context);
        setCardElevation(4);
        setUseCompatPadding(true);
        frameLayout = new FrameLayout(context);
        addView(frameLayout, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.LEFT | Gravity.TOP));
        imageView = new AppCompatImageView(context);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        frameLayout.addView(imageView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER));
        messageProgress = new MessageProgress(context);
        AppUtils.setProgresColor(messageProgress.progressBar);
        messageProgress.withDrawable(R.drawable.ic_download, true);
        frameLayout.addView(messageProgress, LayoutCreator.createFrame(LayoutCreator.dp(15), LayoutCreator.dp(15), Gravity.CENTER));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(80), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(100), MeasureSpec.EXACTLY));
    }

    public void setImageResource(int background) {
        imageView.setImageResource(background);
        messageProgress.setVisibility(GONE);
    }

    public void displayImage(String path) {
        ChatBackgroundFragment.wallpaperPath = path;
        G.imageLoader.displayImage(AndroidUtils.suitablePath(path), imageView);
        messageProgress.setVisibility(GONE);
    }

    public void downloadImage(RealmAttachment realmAttachment, String type) {
        messageProgress.withDrawable(R.drawable.ic_cancel, true);
        String path = G.DIR_CHAT_BACKGROUND + "/" + (type.equals("Thumbnail") ? "thumb_" : "") + realmAttachment.getCacheId() + "_" + realmAttachment.getName();
        if (!new File(path).exists()) {
            HelperDownloadFile.getInstance().startDownload(ProtoGlobal.RoomMessageType.IMAGE, System.currentTimeMillis() + "", realmAttachment.getToken(), realmAttachment.getUrl(), realmAttachment.getCacheId(), realmAttachment.getName(), realmAttachment.getSmallThumbnail().getSize(), type.equals("Thumbnail") ? ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL : ProtoFileDownload.FileDownload.Selector.FILE, path, type.equals("Thumbnail") ? 4 : 2,
                    new HelperDownloadFile.UpdateListener() {
                        @Override
                        public void OnProgress(String mPath, int progress) {
                            messageProgress.withProgress(progress);
                            if (progress == 100) {
                                displayImage(path);
                            }
                        }

                        @Override
                        public void OnError(String token) {
                            messageProgress.post(new Runnable() {
                                @Override
                                public void run() {
                                    messageProgress.withProgress(0);
                                    messageProgress.withDrawable(R.drawable.ic_download, true);
                                }
                            });
                        }
                    });
        } else {
            displayImage(path);
        }
    }
}
