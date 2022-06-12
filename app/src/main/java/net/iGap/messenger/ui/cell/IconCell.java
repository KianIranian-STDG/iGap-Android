package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;

import net.iGap.G;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.components.IconView;

public class IconCell extends FrameLayout {

    private final IconView iconView;
    private boolean isMenuOpen = false;
    private IconCellListener iconCellListener;

    public IconCell(Context context, IconCellListener iconCellListener) {
        super(context);

        this.iconCellListener = iconCellListener;

        iconView = new IconView(context);
        iconView.setTextColor(Theme.getColor(Theme.key_white));
        iconView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
        iconView.setGravity(Gravity.CENTER);
        iconView.setSingleLine();
        iconView.setOnClickListener(v -> {
            if (!isMenuOpen()) {
                showMenu();
            } else {
                closeMenu();
            }
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    iconCellListener.refresh();
                }
            }, 100);
        });
        addView(iconView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER,(getMeasuredWidth() - iconView.getMeasuredWidth()) / 2 , 0 , 0 , 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }

    public void setTextColor(int color) {
        iconView.setIconColor(color);
        invalidate();
    }

    private void showMenu() {
        isMenuOpen = true;
        iconView.animate().rotationBy(180);
    }

    public void closeMenu() {
        isMenuOpen = false;
        iconView.animate().rotation(0);
    }

    private boolean isMenuOpen() {
        return isMenuOpen;
    }

    public void setIcon(String icon) {
        iconView.setText(icon);
    }

    public  interface IconCellListener{
        void refresh();
    }
}
