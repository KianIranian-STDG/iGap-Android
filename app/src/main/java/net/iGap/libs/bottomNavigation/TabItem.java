package net.iGap.libs.bottomNavigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.chat.BadgeView;
import net.iGap.libs.bottomNavigation.Event.OnItemSelected;

import static net.iGap.G.context;

public class TabItem extends RelativeLayout implements View.OnClickListener {

    private final String TAG = "TabItem";

    private BottomNavigation bottomNavigation;
    private OnItemSelected onTabItemSelected;

    private Drawable selectedIcon;
    private Drawable unSelectedIcon;
    private ImageView imageView;
    private BadgeView badgeView;
    private int position;
    private boolean active = false;
    private boolean isRtl = G.isAppRtl;


    public TabItem(Context context) {
        super(context);
        init(null);
    }

    public TabItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TabItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attributeSet) {
        parseAttr(attributeSet);

        if (imageView == null)
            imageView = new ImageView(getContext());
        if (badgeView == null)
            badgeView = new BadgeView(getContext());

        imageView.setId(R.id.bottomIcon);
        RelativeLayout.LayoutParams badgeParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams iconParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        iconParams.addRule(CENTER_IN_PARENT);
        imageView.setLayoutParams(iconParams);
        if (isRtl) {
            badgeParams.addRule(RelativeLayout.LEFT_OF, imageView.getId());
            badgeParams.setMargins(0, i_Dp(R.dimen.dp4), -16, i_Dp(R.dimen.dp4));
        } else {
            badgeParams.addRule(RelativeLayout.RIGHT_OF, imageView.getId());
            badgeParams.setMargins(-16, i_Dp(R.dimen.dp4), 0, i_Dp(R.dimen.dp4));
        }

        badgeView.setLayoutParams(badgeParams);

        addView(imageView);
        postDelayed(() -> addView(badgeView), 150);
        setOnClickListener(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        checkParent();
    }

    private void checkParent() {
        post(() -> {
            if (getParent() instanceof BottomNavigation) {
                bottomNavigation = (BottomNavigation) getParent();
                setupViews();
                Log.i(TAG, "checkParent: parent loaded");
            } else {
                throw new RuntimeException(TAG + "BottomNavigation");
            }
        });
    }

    private void setupViews() {
        imageView.setImageDrawable(selectedIcon);
        if (position == bottomNavigation.getDefaultItem())
            active = true;
        setSelected(active);
    }

    private void parseAttr(AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.TabItem);

            try {
                selectedIcon = typedArray.getDrawable(R.styleable.TabItem_selected_icon);
                unSelectedIcon = typedArray.getDrawable(R.styleable.TabItem_unselected_icon);
            } finally {
                typedArray.recycle();
            }
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void onClick(View v) {
        if (onTabItemSelected != null)
            onTabItemSelected.selectedTabItem(position);
        Log.i(TAG, "onClick: ");
    }

    public void setSelected(boolean isActive) {
        if (active != isActive) {
            active = isActive;
        }

        //todo:// add dark mode svg for bottom nav icons
        if (G.isDarkTheme){

        }else {

        }

        if (active) {
            imageView.setImageDrawable(selectedIcon);
        } else {
            imageView.setImageDrawable(unSelectedIcon);
        }
        Log.i(TAG, "setSelected: ");
    }

    public void setOnTabItemSelected(OnItemSelected onItemSelected) {
        this.onTabItemSelected = onItemSelected;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setBadgeCount(int count) {
        badgeView.getTextView().setText(String.valueOf(count));
        badgeView.getTextView().setTextSize(9);
        badgeView.getTextView().setSingleLine(true);
        if (count == 0) {
            badgeView.setVisibility(GONE);
        } else if (count > 99) {
            badgeView.setVisibility(VISIBLE);
            badgeView.getTextView().setText("+99");
        }else
            badgeView.setVisibility(VISIBLE);
        Log.i(TAG, "setBadgeCount: " + count);

    }

    public void setBadgeColor(int color) {
        badgeView.setBadgeColor(color);
    }

    public boolean isActive() {
        return active;
    }

    public int i_Dp(int dpSrc) {
        return (int) context.getResources().getDimension(dpSrc);
    }
}
