package net.iGap.module.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import net.iGap.G;
import net.iGap.adapter.items.cells.AnimatedStickerCell;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.downloadFile.IGDownloadFile;
import net.iGap.helper.downloadFile.IGDownloadFileStruct;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.imageLoaderService.ImageLoadingServiceInjector;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.repository.StickerRepository;

import java.io.File;

public class StickerView extends FrameLayout implements EventManager.EventDelegate {
    private AppCompatImageView stickerIv;
    private AppCompatImageView stickerEmojiView;

    private String viewToken;
    private String viewPath;
    private int viewType;
    private boolean needToShowEmoji;

    public void loadSticker(String token, String path, String fileName, String id, long fileSize) {
        int type;
        this.needToShowEmoji = false;

        if (path == null || path.equals(""))
            type = 100;
        else if (path.endsWith(".json")) {
            type = StructIGSticker.ANIMATED_STICKER;
        } else {
            type = StructIGSticker.NORMAL_STICKER;
        }

        load(token, path, fileName, type, "", id, fileSize);
    }

    public void loadSticker(StructIGSticker structIGSticker, boolean needToShowEmoji) {
        this.needToShowEmoji = needToShowEmoji;
        load(structIGSticker.getToken(), structIGSticker.getPath(), structIGSticker.getFileName(), structIGSticker.getType(), structIGSticker.getName(), structIGSticker.getId(), structIGSticker.getFileSize());
    }

    public void loadSticker(StructIGSticker structIGSticker) {
        this.needToShowEmoji = false;
        load(structIGSticker.getToken(), structIGSticker.getPath(), structIGSticker.getFileName(), structIGSticker.getType(), structIGSticker.getName(), structIGSticker.getId(), structIGSticker.getFileSize());
    }

    public void loadStickerGroup(StructIGStickerGroup stickerGroup, boolean needToShowEmoji) {
        this.needToShowEmoji = needToShowEmoji;
        load(stickerGroup.getAvatarToken(), stickerGroup.getAvatarPath(), stickerGroup.getAvatarName(), stickerGroup.getAvatarType(), stickerGroup.getAvatarName(), stickerGroup.getGroupId(), stickerGroup.getAvatarSize());
    }

    public void loadStickerGroup(StructIGStickerGroup stickerGroup) {
        this.needToShowEmoji = false;
        load(stickerGroup.getAvatarToken(), stickerGroup.getAvatarPath(), stickerGroup.getAvatarName(), stickerGroup.getAvatarType(), stickerGroup.getAvatarName(), stickerGroup.getGroupId(), stickerGroup.getAvatarSize());
    }

    private void load(String token, String path, String fileName, int type, String emoji, String id, long fileSize) {
        viewPath = path == null ? StickerRepository.getInstance().getStickerPath(token, fileName) : path;
        viewToken = token;
        viewType = type;

        if (viewPath == null || viewToken == null || viewType == -1)
            return;

        if (stickerIv == null) {
            if (type == StructIGSticker.ANIMATED_STICKER) {
                stickerIv = new AnimatedStickerCell(getContext());
                ((AnimatedStickerCell) stickerIv).setFailureListener(Throwable::printStackTrace);
                addView(stickerIv, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 0, 0, 0, 0));
            } else {
                stickerIv = new AppCompatImageView(getContext());
                addView(stickerIv, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 0, 0, 0, 0));
            }
        }

        if (hasFileOnLocal()) {
            if (type == StructIGSticker.ANIMATED_STICKER) {
                if (stickerIv instanceof AnimatedStickerCell)
                    ((AnimatedStickerCell) stickerIv).playAnimation(viewPath);
                else {
                    removeView(stickerIv);
                    stickerIv = null;

                    stickerIv = new AnimatedStickerCell(getContext());
                    ((AnimatedStickerCell) stickerIv).setFailureListener(Throwable::printStackTrace);
                    addView(stickerIv, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 0, 0, 0, 0));
                    ((AnimatedStickerCell) stickerIv).playAnimation(viewPath);
                }
            } else if (type == StructIGSticker.NORMAL_STICKER) {
                if (stickerIv instanceof AnimatedStickerCell) {
                    removeView(stickerIv);
                    stickerIv = null;

                    stickerIv = new AppCompatImageView(getContext());
                    addView(stickerIv, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 0, 0, 0, 0));
                    ImageLoadingServiceInjector.inject().loadImage(stickerIv, viewPath, true);
                } else {
                    ImageLoadingServiceInjector.inject().loadImage(stickerIv, viewPath, true);
                }
            }
        } else {
            if (type == StructIGSticker.NORMAL_STICKER) {
                ImageLoadingServiceInjector.inject().clear(stickerIv);
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
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.STICKER_DOWNLOAD, this);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.EMOJI_LOADED, this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.STICKER_DOWNLOAD, this);
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.EMOJI_LOADED, this);
    }

    public boolean hasFileOnLocal() {
        return new File(viewPath).exists() && new File(viewPath).canRead();
    }

    @Override
    public void receivedEvent(int id, int account, Object... args) {
        G.runOnUiThread(() -> {
            if (id == EventManager.STICKER_DOWNLOAD) {
                String filePath = (String) args[0];
                String fileToken = (String) args[1];

                if (viewToken != null && viewToken.equals(fileToken)) {
                    if (viewType == StructIGSticker.ANIMATED_STICKER && stickerIv instanceof AnimatedStickerCell) {
                        ((AnimatedStickerCell) stickerIv).playAnimation(filePath);
                    } else if (filePath.equals(viewPath)) {
                        ImageLoadingServiceInjector.inject().loadImage(stickerIv, viewPath, true);
                    }
                }
            } else if (id == EventManager.EMOJI_LOADED) {
                if (stickerEmojiView != null) {
                    stickerEmojiView.invalidate();
                }
            }
        });
    }
}
