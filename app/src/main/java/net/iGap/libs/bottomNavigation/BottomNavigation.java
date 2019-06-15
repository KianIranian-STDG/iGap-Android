package net.iGap.libs.bottomNavigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.libs.bottomNavigation.Event.OnBottomNavigationBadge;
import net.iGap.libs.bottomNavigation.Event.OnItemChangeListener;
import net.iGap.libs.bottomNavigation.Event.OnItemSelected;
import net.iGap.libs.bottomNavigation.Util.Utils;

import java.util.ArrayList;
import java.util.List;

public class BottomNavigation extends LinearLayout implements OnItemSelected {

    public static final String TAG = "aabolfazlNavigation";
    private OnItemChangeListener onItemChangeListener;
    private List<TabItem> tabItems = new ArrayList<>();
    private int defaultItem;
    private int selectedItemPosition = defaultItem;
    private float cornerRadius;
    private int backgroundColor;

    public BottomNavigation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attributeSet) {
        parseAttr(attributeSet);
        setMinimumHeight(Utils.dpToPx(60));
        setOrientation(HORIZONTAL);
        setWeightSum(5);
    }

    private void parseAttr(AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.BottomNavigation);

            try {
                if (G.isDarkTheme){
                    backgroundColor = getContext().getResources().getColor(R.color.navigation_dark_mode_bg);
                }else{
                    backgroundColor = typedArray.getColor(R.styleable.BottomNavigation_background_color,
                            getResources().getColor(R.color.background_color));
                }
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
        for (int i = 0; i < getChildCount(); i++) {
            final TabItem tabItem = (TabItem) getChildAt(i);
            tabItem.setPosition(i);
            tabItems.add(tabItem);
            tabItem.setOnTabItemSelected(this);
        }

    }


    @Override
    public void selectedTabItem(final int position) {
        if (position != selectedItemPosition) {
            selectedItemPosition = position;
            onSelectedItemChanged();
            if (onItemChangeListener != null) {
                onItemChangeListener.onSelectedItemChanged(tabItems.get(position).getPosition());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void dispatchDraw(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(backgroundColor);
        canvas.drawPath(roundedRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius, true), paint);
        super.dispatchDraw(canvas);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Path roundedRect(float left, float top, float right, float bottom, float rx, float ry, boolean justTop) {
        Path path = new Path();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
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
        for (int i = 0; i < tabItems.size(); i++) {
            if (tabItems.get(i).getPosition() == position) {
                if (position != selectedItemPosition) {
                    selectedItemPosition = position;
                    onSelectedItemChanged();
                    if (onItemChangeListener != null) {
                        onItemChangeListener.onSelectedItemChanged(tabItems.get(position).getPosition());
                    }
                }
            }
        }
    }

    private void onSelectedItemChanged() {
        for (int i = 0; i < tabItems.size(); i++) {
            if (tabItems.get(i).getPosition() == selectedItemPosition) {
                tabItems.get(i).setSelectedItem(true);
            } else {
                tabItems.get(i).setSelectedItem(false);
            }
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

    public void setOnBottomNavigationBadge(OnBottomNavigationBadge callBack) {
        for (int i = 0; i < tabItems.size(); i++) {
            TabItem tabItem = tabItems.get(i);
            tabItem.setBadgeColor(callBack.badgeColor());

            switch (i) {
                case 0:
                    tabItem.setBadgeCount(0);
                    break;
                case 1:
                    tabItem.setBadgeCount(callBack.callCount());
                    break;
                case 2:
                    tabItem.setBadgeCount(callBack.messageCount());
                    break;
                case 3:
                    tabItem.setBadgeCount(0);
                    break;
                case 4:
                    tabItem.setBadgeCount(0);
                    break;
            }
        }
    }
}
