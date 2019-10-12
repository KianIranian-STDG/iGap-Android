package net.iGap.libs.bottomNavigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.libs.bottomNavigation.Event.OnItemChangeListener;
import net.iGap.libs.bottomNavigation.Event.OnItemSelected;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class BottomNavigation extends LinearLayout implements OnItemSelected, View.OnClickListener, View.OnLongClickListener {

    public static final String TAG = "aabolfazlNavigation";
    private OnItemChangeListener onItemChangeListener;
    private List<TabItem> tabItems = new ArrayList<>();
    private int defaultItem;
    private int selectedItemPosition = defaultItem;
    private float cornerRadius;
    private int backgroundColor;
    private int badgeColor;
    private OnLongClickListener onLongClickListener;
    private CircleImageView avatarImageView;

    private AvatarHandler avatarHandler;

    public BottomNavigation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        badgeColor = context.getResources().getColor(R.color.red);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attributeSet) {
        parseAttr(attributeSet);
        setMinimumHeight(Utils.dpToPx(50));
        setOrientation(HORIZONTAL);
        setWeightSum(5);
    }

    private void parseAttr(AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.BottomNavigation);

            try {
                if (G.isDarkTheme) {
                    backgroundColor = getContext().getResources().getColor(R.color.navigation_dark_mode_bg);
                } else {
                    backgroundColor = typedArray.getColor(R.styleable.BottomNavigation_background_color, getResources().getColor(R.color.background_color));
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
        try {
            for (int i = 0; i < getChildCount(); i++) {
                if (i != 4) {
                    final TabItem tabItem = (TabItem) getChildAt(i);
                    tabItem.setPosition(i);
                    tabItems.add(tabItem);
                    tabItem.setOnTabItemSelected(this);
                } else {
                    avatarImageView = (CircleImageView) getChildAt(4);
                    avatarImageView.setOnLongClickListener(this);
                    avatarImageView.setOnClickListener(this);
                    avatarImageView.setBorderColor(Color.parseColor(G.isDarkTheme ? "#868686" : "#FF696969"));
                }
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "setupChildren: ", e);
        }
    }


    @Override
    public void selectedTabItem(final int position) {
        if (position != selectedItemPosition) {
            selectedItemPosition = position;
            onSelectedItemChanged();
            if (onItemChangeListener != null) {
                if (position != 4) {
                    onItemChangeListener.onSelectedItemChanged(tabItems.get(position).getPosition());
                } else {
                    onItemChangeListener.onSelectedItemChanged(position);
                }
            }
        } else {
            if (onItemChangeListener != null) {
                onItemChangeListener.onSelectAgain(tabItems.get(position).getPosition());
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(backgroundColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawPath(roundedRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius, true), paint);
            super.dispatchDraw(canvas);
        } else {
            super.dispatchDraw(canvas);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        avatarHandler = new AvatarHandler();
        avatarHandler.registerChangeFromOtherAvatarHandler();
        avatarHandler.getAvatar(new ParamWithAvatarType(avatarImageView, G.userId).avatarType(AvatarHandler.AvatarType.USER).showMain());
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

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    private void onSelectedItemChanged() {
        try {
            for (int i = 0; i < tabItems.size(); i++) {
                if (tabItems.get(i).getPosition() == selectedItemPosition) {
                    tabItems.get(i).setSelectedItem(true);
                } else {
                    tabItems.get(i).setSelectedItem(false);
                }
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "onSelectedItemChanged: ", e);
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

    public void setOnBottomNavigationBadge(int unreadCount, int callCount) {
        TabItem tabItem = tabItems.get(2);
        tabItem.setBadgeColor(badgeColor);
        tabItem.setBadgeCount(unreadCount);
    }

    public int getCurrentTab() {
        return selectedItemPosition;
    }

    @Override
    public void onClick(View v) {
        selectedTabItem(4);
    }

    @Override
    public boolean onLongClick(View v) {
        if (onItemChangeListener != null) {
            onLongClickListener.onLongClick(v);
            AppUtils.setVibrator(15);
        }
        return false;
    }

    public void setProfileOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        avatarHandler.unregisterChangeFromOtherAvatarHandler();
    }

    public AvatarHandler getAvatarHandler() {
        return avatarHandler;
    }
}
