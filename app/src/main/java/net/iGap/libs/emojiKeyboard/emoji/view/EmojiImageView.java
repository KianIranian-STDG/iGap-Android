package net.iGap.libs.emojiKeyboard.emoji.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.observers.eventbus.EventManager;

@SuppressLint("ViewConstructor")
public class EmojiImageView extends AppCompatImageView implements EventListener {
    private boolean needToDrawBack;

    @Override
    public void receivedMessage(int id, Object... message) {
        if (id == EventManager.EMOJI_LOADED)
            G.runOnUiThread(this::invalidate);
    }

    public EmojiImageView(Context context) {
        super(context);
        setScaleType(ImageView.ScaleType.CENTER);
        setPadding(LayoutCreator.dp(2), LayoutCreator.dp(2), LayoutCreator.dp(2), LayoutCreator.dp(2));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventManager.getInstance().addEventListener(EventManager.EMOJI_LOADED, this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventManager.getInstance().removeEventListener(EventManager.EMOJI_LOADED, this);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(widthMeasureSpec));
    }

    public void addEmojiToRecent(String code) {
        if (!EmojiManager.getInstance().isValidEmoji(code)) {
            return;
        }
        EmojiManager.getInstance().addRecentEmoji(code);
        EmojiManager.getInstance().saveRecentEmoji();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            needToDrawBack = false;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            needToDrawBack = true;
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (needToDrawBack) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(getResources().getColor(R.color.black_translucent));
            canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()), LayoutCreator.dp(2), LayoutCreator.dp(2), paint);
        }
        super.onDraw(canvas);

    }
}
