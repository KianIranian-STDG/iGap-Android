package net.iGap.libs.bottomNavigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import net.iGap.R;
import net.iGap.fragments.BottomNavigationFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.bottomNavigation.Event.OnItemChangeListener;
import net.iGap.libs.bottomNavigation.Event.OnItemSelected;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.module.AppUtils;

public class BottomNavigation extends LinearLayout implements OnItemSelected, View.OnClickListener {

    public static final String TAG = "aabolfazlNavigation";
    private OnItemChangeListener onItemChangeListener;
    private int defaultItem;
    private int selectedItemPosition = defaultItem;
    private float cornerRadius;
    private int backgroundColor;
    private OnLongClickListener onLongClickListener;

    public BottomNavigation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attributeSet) {
        parseAttr(attributeSet);
        setMinimumHeight(Utils.dpToPx(50));
        setOrientation(HORIZONTAL);
    }

    private void parseAttr(AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.BottomNavigation);
            try {
                backgroundColor = typedArray.getColor(R.styleable.BottomNavigation_background_color, getResources().getColor(R.color.background_color));
                cornerRadius = typedArray.getInt(R.styleable.BottomNavigation_corner_radius, 0);
            } finally {
                typedArray.recycle();
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupChildren();
    }

    private void setupChildren() {
        try {
            for (int i = 0; i < getChildCount(); i++) {
                TabItem tabItem = (TabItem) getChildAt(i);
                tabItem.setPosition(i);
                tabItem.setOnTabItemSelected(this);
                if (tabItem.haveAvatarImage) {
                    tabItem.setOnLongClickListener(this::onLongClick);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void selectedTabItem(final int position) {
        if (position != selectedItemPosition) {
            selectedItemPosition = position;
            onSelectedItemChanged();
            if (onItemChangeListener != null) {
                onItemChangeListener.onSelectedItemChanged(((TabItem) getChildAt(position)).getPosition());
            }
        } else {
            onItemChangeListener.onSelectAgain(((TabItem) getChildAt(position)).getPosition());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(backgroundColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawPath(roundedRect(0, getWidth(), getHeight(), LayoutCreator.dpToPx((int) cornerRadius), LayoutCreator.dpToPx((int) cornerRadius), true), paint);
            super.dispatchDraw(canvas);
        } else {
            super.dispatchDraw(canvas);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Path roundedRect(float top, float right, float bottom, float rx, float ry, boolean justTop) {
        Path path = new Path();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - (float) 0;
        float height = bottom - top;
        if (rx > width / 2) rx = width / 2;
        if (ry > height / 2) ry = height / 2;
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));
        path.moveTo(right, top + ry);

        path.rQuadTo(0, -ry, -rx, -ry);
        path.rLineTo(-widthMinusCorners, 0);

        path.rQuadTo(-rx, 0, -rx, ry);
        path.rLineTo(0, heightMinusCorners);

        if (justTop) {
            path.rLineTo(0, ry);
            path.rLineTo(width, 0);
            path.rLineTo(0, -ry);
        } else {
            path.rQuadTo(0, ry, rx, ry);
            path.rLineTo(widthMinusCorners, 0);
            path.rQuadTo(rx, 0, rx, -ry);
        }

        path.rLineTo(0, -heightMinusCorners);
        path.close();

        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setConvexPath(path);
            }
        });


        return path;
    }

    public void setCurrentItem(int position) {
        defaultItem = position;
        for (int i = 0; i < getChildCount(); i++) {
            if (((TabItem) getChildAt(i)).getPosition() == position) {
                if (position != selectedItemPosition) {
                    selectedItemPosition = position;
                    onSelectedItemChanged();
                    if (onItemChangeListener != null) {
                        onItemChangeListener.onSelectedItemChanged(((TabItem) getChildAt(i)).getPosition());
                    }
                }
            }
        }
    }

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    private void onSelectedItemChanged() {
        try {
            for (int i = 0; i < getChildCount(); i++) {
                ((TabItem) getChildAt(i)).setSelectedItem(((TabItem) getChildAt(i)).getPosition() == selectedItemPosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getDefaultItem() {
        return defaultItem;
    }

    public void setDefaultItem(int defaultItem) {
        this.defaultItem = defaultItem;
        this.selectedItemPosition = defaultItem;
    }

    public void setOnItemChangeListener(OnItemChangeListener onItemChangeListener) {
        onItemChangeListener.onSelectedItemChanged(defaultItem);
        this.onItemChangeListener = onItemChangeListener;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setOnBottomNavigationBadge(int unreadCount) {
        for (int i = 0; i < getChildCount(); i++) {
            TabItem tabItem = (TabItem) getChildAt(i);
            tabItem.updateBadge(unreadCount);
        }
    }

    public int getCurrentTab() {
        return selectedItemPosition;
    }

    @Override
    public void onClick(View v) {
        if (selectedItemPosition != BottomNavigationFragment.PROFILE_FRAGMENT) {
            v.setSelected(!v.isSelected());
        }
        selectedTabItem(BottomNavigationFragment.PROFILE_FRAGMENT);
    }

    public boolean onLongClick(View v) {
        if (onItemChangeListener != null) {
            onLongClickListener.onLongClick(v);
            AppUtils.setVibrator(15);
        }
        return true;
    }

    public void setProfileOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

}
