package net.iGap.emojiKeyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.helper.LayoutCreator;

@SuppressLint("ViewConstructor")
public class KeyboardView extends FrameLayout {

    public static final int MODE_EMOJI = 0;
    public static final int MODE_KEYBOARD = 1;
    public static final int MODE_BOT_UNDER_BUTTON = 2;
    public static final int MODE_ATTACHMENT = 3;

    private EmojiView emojiView;
    private int currentMode;
    private Listener listener;

    private int keyboardHeight;
    private int keyboardHeightLand;

    public KeyboardView(@NonNull Context context, Listener listener, int mode) {
        super(context);
        this.listener = listener;
        currentMode = mode;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (listener != null)
            listener.onViewCreated(currentMode);
    }

    public void setCurrentMode(int mode, int contentView) {
        if (mode == MODE_EMOJI) {
            if (emojiView == null)
                createEmojiView();

            emojiView.setContentView(contentView);

        } else if (mode == MODE_KEYBOARD) {

        } else if (mode == MODE_ATTACHMENT) {

        } else if (mode == MODE_BOT_UNDER_BUTTON) {

        }
    }

    private void createEmojiView() {
        if (emojiView != null) return;

        emojiView = new EmojiView(getContext(), true, true);
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

        });

        addView(emojiView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setBackgroundColor(Color.parseColor("#FFFFFF"));
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY));
    }

    public interface Listener {
        void onViewCreated(int mode);

        void onStickerSettingClicked();

        void onBackSpace();

        void onSendStickerAsMessage(StructIGSticker structIGSticker);

        void onAddStickerClicked();

    }

    public void setKeyboardHeight(int keyboardHeightLand, int keyboardHeight) {
        this.keyboardHeightLand = keyboardHeightLand;
        this.keyboardHeight = keyboardHeight;
    }
}
