package net.iGap.libs.emojiKeyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.Theme;

@SuppressLint("ViewConstructor")
public class KeyboardView extends FrameLayout {

    public static final int MODE_EMOJI = 0;
    public static final int MODE_KEYBOARD = 1;
    public static final int MODE_BOT_UNDER_BUTTON = 2;
    public static final int MODE_ATTACHMENT = 3;

    private EmojiView emojiView;
    private int currentMode;
    private Listener listener;
    private boolean needSticker;

    private int keyboardHeight;
    private int keyboardHeightLand;

    public KeyboardView(@NonNull Context context, Listener listener, int mode, boolean needSticker) {
        super(context);
        this.listener = listener;
        currentMode = mode;
        this.needSticker = needSticker;

        if (emojiView == null)
            createEmojiView();
    }

    public void setCurrentMode(int mode, int contentView) {
        currentMode = mode;
        if (mode == MODE_EMOJI) {

            if (emojiView == null)
                createEmojiView();

            emojiView.setVisibility(VISIBLE);

            emojiView.setContentView(EmojiView.EMOJI);

        } else if (mode == MODE_KEYBOARD) {
            if (emojiView != null)
                emojiView.setVisibility(INVISIBLE);
        } else if (mode == MODE_ATTACHMENT) {

        } else if (mode == MODE_BOT_UNDER_BUTTON) {

        }

        if (listener != null)
            listener.onViewCreated(currentMode);
    }

    private void createEmojiView() {
        if (emojiView != null)
            return;

        emojiView = new EmojiView(getContext(), needSticker, true);
        emojiView.setListener(new EmojiView.Listener() {
            @Override
            public void onBackSpace() {
                listener.onBackSpace();
            }

            @Override
            public void onStickerClick(StructIGSticker structIGSticker) {
                listener.onSendStickerAsMessage(structIGSticker);
            }

            @Override
            public void onStickerSettingClick() {
                listener.onStickerSettingClicked();
            }

            @Override
            public void onAddStickerClicked() {
                listener.onAddStickerClicked();
            }

            @Override
            public void onEmojiSelected(String unicode) {
                listener.onEmojiSelected(unicode);
            }

        });

        addView(emojiView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setBackgroundColor(Theme.getInstance().getDividerColor(getContext()));
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY));
    }

    public void setStickerPermission(boolean stickerPermission) {
        if (emojiView != null) {
            emojiView.setStickerPermission(stickerPermission);
        }
    }

    public void onDestroyParentFragment() {
        if (emojiView != null)
            emojiView.onDestroyParentFragment();
    }

    public interface Listener {
        void onViewCreated(int mode);

        void onStickerSettingClicked();

        void onBackSpace();

        void onSendStickerAsMessage(StructIGSticker structIGSticker);

        void onAddStickerClicked();

        void onEmojiSelected(String unicode);
    }

    public void setKeyboardHeight(int keyboardHeightLand, int keyboardHeight) {
        this.keyboardHeightLand = keyboardHeightLand;
        this.keyboardHeight = keyboardHeight;
    }

    public int getCurrentMode() {
        return currentMode;
    }
}
