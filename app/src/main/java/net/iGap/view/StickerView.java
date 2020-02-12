package net.iGap.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.adapter.items.cells.AnimatedStickerCell;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.downloadFile.IGDownloadFile;
import net.iGap.helper.downloadFile.IGDownloadFileStruct;

import java.io.File;

public class StickerView extends FrameLayout implements EventListener {
    private AppCompatImageView stickerIv;
    private AppCompatImageView stickerEmojiView;

    private String viewToken;
    private String viewPath;
    private int viewType;
    private boolean needToShowEmoji;

    public void loadSticker(StructIGSticker structIGSticker, boolean needToShowEmoji) {
        this.needToShowEmoji = needToShowEmoji;
        load(structIGSticker.getToken(), structIGSticker.getPath(), structIGSticker.getType(), structIGSticker.getName(), structIGSticker.getId(), structIGSticker.getFileSize());
    }

    public void loadSticker(StructIGSticker structIGSticker) {
        this.needToShowEmoji = false;
        load(structIGSticker.getToken(), structIGSticker.getPath(), structIGSticker.getType(), structIGSticker.getName(), structIGSticker.getId(), structIGSticker.getFileSize());
    }

    public void loadStickerGroup(StructIGStickerGroup stickerGroup, boolean needToShowEmoji) {
        this.needToShowEmoji = needToShowEmoji;
        load(stickerGroup.getAvatarToken(), stickerGroup.getAvatarPath(), stickerGroup.getAvatarType(), stickerGroup.getAvatarName(), stickerGroup.getGroupId(), stickerGroup.getAvatarSize());
    }

    public void loadStickerGroup(StructIGStickerGroup stickerGroup) {
        this.needToShowEmoji = false;
        load(stickerGroup.getAvatarToken(), stickerGroup.getAvatarPath(), stickerGroup.getAvatarType(), stickerGroup.getAvatarName(), stickerGroup.getGroupId(), stickerGroup.getAvatarSize());
    }


    private void load(String token, String path, int type, String emoji, String id, long fileSize) {
        viewPath = path;
        viewToken = token;
        viewType = type;

        if (viewPath == null || viewToken == null)
            return;

        if (stickerIv == null && type == StructIGSticker.ANIMATED_STICKER) {
            stickerIv = new AnimatedStickerCell(getContext());
            addView(stickerIv, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 0, 0, 0, 0));
        } else if (stickerIv == null) {
            stickerIv = new AppCompatImageView(getContext());
            addView(stickerIv, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 0, 0, 0, 0));
        }

        if (hasFileOnLocal()) {
            if (type == StructIGSticker.ANIMATED_STICKER && stickerIv instanceof AnimatedStickerCell) {
                ((AnimatedStickerCell) stickerIv).playAnimation(viewPath);
            } else {
                Glide.with(getContext()).load(viewPath).into(stickerIv);
            }
        } else {
            if (type == StructIGSticker.NORMAL_STICKER) {
                stickerIv.setBackgroundResource(0);
            }
            IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(id, viewToken, fileSize, viewPath));
        }

        if (needToShowEmoji && stickerEmojiView == null) {
            stickerEmojiView = new AppCompatImageView(getContext());
            stickerEmojiView.setScaleType(ImageView.ScaleType.CENTER);
            stickerEmojiView.setImageDrawable(EmojiManager.getInstance().getEmojiDrawable(emoji));
            addView(stickerEmojiView, LayoutCreator.createFrame(24, 24, Gravity.RIGHT | Gravity.BOTTOM, 0, 0, 2, 2));
        }
    }

    public StickerView(@NonNull Context context) {
        super(context);
    }

    public StickerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventManager.getInstance().addEventListener(EventManager.STICKER_DOWNLOAD, this);
        EventManager.getInstance().addEventListener(EventManager.EMOJI_LOADED, this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventManager.getInstance().removeEventListener(EventManager.STICKER_DOWNLOAD, this);
        EventManager.getInstance().removeEventListener(EventManager.EMOJI_LOADED, this);
    }

    @Override
    public void receivedMessage(int id, Object... message) {
        G.runOnUiThread(() -> {
            if (id == EventManager.STICKER_DOWNLOAD) {
                String filePath = (String) message[0];
                String fileToken = (String) message[1];

                if (viewToken != null && viewToken.equals(fileToken)) {
                    if (viewType == StructIGSticker.ANIMATED_STICKER) {
                        ((AnimatedStickerCell) stickerIv).playAnimation(filePath);
                    } else if (filePath.equals(viewPath)) {
                        Glide.with(getContext()).load(filePath).into(stickerIv);
                    }
                }
            } else if (id == EventManager.EMOJI_LOADED) {
                if (stickerEmojiView != null) {
                    stickerEmojiView.invalidate();
                }
            }
        });
    }


    public boolean hasFileOnLocal() {
        return new File(viewPath).exists() && new File(viewPath).canRead();
    }

}
