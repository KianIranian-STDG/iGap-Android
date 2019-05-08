package net.iGap.libs.bottomNavigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import net.iGap.G;
import net.iGap.R;
import net.iGap.libs.bottomNavigation.Event.OnItemSelected;

public class TabItem extends android.support.v7.widget.AppCompatTextView implements View.OnClickListener {

    private final String TAG = "TabItem";

    private BottomNavigation bottomNavigation;
    private OnItemSelected onItemSelected;

    private String tabIcon;
    private int position;
    private boolean active = false;

    private int selectedColor;
    private int unSelectedColor;
    private float selectedSize;
    private float unSelectedSize;


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
        setTypeface(G.typeface_Fontico);
        setOnClickListener(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        checkParent();
    }

    private void checkParent() {
        post(new Runnable() {
            @Override
            public void run() {
                if (getParent() instanceof BottomNavigation) {
                    bottomNavigation = (BottomNavigation) getParent();
                    setupViews();
                } else {
                    throw new RuntimeException(TAG + "parent must be BottomNavigation!");
                }
            }
        });
    }

    private void setupViews() {
        setText(tabIcon);
        setGravity(Gravity.CENTER);
        if (position == bottomNavigation.getDefaultItem())
            active = true;
        setSelected(active);

    }

    private void parseAttr(AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.TabItem);

            try {
                selectedColor = typedArray.getColor(R.styleable.TabItem_select_color, getResources().getColor(R.color.selected_color));
                unSelectedColor = typedArray.getColor(R.styleable.TabItem_unselect_color, getResources().getColor(R.color.unSelected_color));
                tabIcon = typedArray.getString(R.styleable.TabItem_tab_icon);
                selectedSize = typedArray.getDimension(R.styleable.TabItem_select_size, 20);
                unSelectedSize = typedArray.getDimension(R.styleable.TabItem_unselect_size, 14);
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
            setTextColor(selectedColor);
            setTextSize(TypedValue.COMPLEX_UNIT_SP, selectedSize);
        } else {
            setTextColor(unSelectedColor);
            setTextSize(TypedValue.COMPLEX_UNIT_SP, unSelectedSize);
        }
    }

    public void setOnItemSelected(OnItemSelected onItemSelected) {
        this.onItemSelected = onItemSelected;
    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }

    public void setUnSelectedColor(int unSelectedColor) {
        this.unSelectedColor = unSelectedColor;
    }

    public void setSelectedSize(float selectedSize) {
        this.selectedSize = selectedSize;
    }

    public void setUnSelectedSize(float unSelectedSize) {
        this.unSelectedSize = unSelectedSize;
    }
}
