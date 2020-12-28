package net.iGap.libs.emojiKeyboard;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import net.iGap.module.AndroidUtils;

public class NotifyFrameLayout extends FrameLayout {
    private int keyboardHeight;
    private Listener listener;
    private Rect rect = new Rect();

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onSizeChanged(int keyboardSize, boolean land);
    }

    public NotifyFrameLayout(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        notifyHeightChanged();
    }


    public int getKeyboardHeight() {
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(rect);
        if (rect.bottom == 0 && rect.top == 0) {
            return 0;
        }
        int getViewInset = AndroidUtils.getViewInset(rootView);
        int usableViewHeight = rootView.getHeight() - (rect.top != 0 ? AndroidUtils.statusBarHeight : 0) - getViewInset;
        return Math.max(0, usableViewHeight - (rect.bottom - rect.top));
    }

    public void notifyHeightChanged() {
        if (listener != null) {
            keyboardHeight = getKeyboardHeight();
            final boolean land = AndroidUtils.displaySize.x > AndroidUtils.displaySize.y;
            post(() -> {
                if (listener != null) {
                    listener.onSizeChanged(keyboardHeight, land);
                }
            });
        }
    }
}
