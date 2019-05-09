package net.iGap.libs.bottomNavigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import net.iGap.R;
import net.iGap.libs.bottomNavigation.Event.OnItemSelected;

public class TabItem extends android.support.v7.widget.AppCompatImageView implements View.OnClickListener {

    private final String TAG = "TabItem";

    private BottomNavigation bottomNavigation;
    private OnItemSelected onItemSelected;

    private Drawable selectedIcon;
    private Drawable unSelectedIcon;
    private int position;
    private boolean active = false;


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
            } else {
                throw new RuntimeException(TAG + "BottomNavigation");
            }
        });
    }

    private void setupViews() {
        setImageDrawable(selectedIcon);
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
        if (onItemSelected != null)
            onItemSelected.selectedItem(position);
    }

    public void setSelected(boolean isActive) {
        if (active != isActive) {
            active = isActive;
        }
        if (active) {
            setImageDrawable(selectedIcon);
        } else {
            setImageDrawable(unSelectedIcon);
        }
    }

    public void setOnItemSelected(OnItemSelected onItemSelected) {
        this.onItemSelected = onItemSelected;
    }

}
