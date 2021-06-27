package net.iGap.messenger.ui.toolBar;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.components.IconView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.Theme;

@SuppressLint("ViewConstructor")
public class ToolbarItem extends FrameLayout {
    private ActionBarMenuItemSearchListener listener;
    private ToolbarItems parentToolbarItem;
    private ToolbarMenuItemLayout toolbarMenuItemLayout;
    private ToolbarPopup popupWindow;
    private TextView textView;
    private IconView iconView;
    private FrameLayout searchContainer;
    private TextView searchClearButton;
    private EditText searchEditText;
    private boolean isSearchBox;
    private boolean processedPopupClick;
    private int yOffset;
    private int subMenuOpenSide;
    private boolean forceSmoothKeyboard;
    private int additionalXOffset;
    private int additionalYOffset;

    public static class ActionBarMenuItemSearchListener {
        public void onSearchExpand() {
        }

        public boolean canCollapseSearch() {
            return true;
        }

        public void onSearchCollapse() {

        }

        public void onTextChanged(EditText editText) {
        }

        public void onSearchPressed(EditText editText) {
        }

        public void onCaptionCleared() {
        }

        public boolean forceShowClear() {
            return false;
        }

        public Animator getCustomToggleTransition() {
            return null;
        }

        public void onLayout(int l, int t, int r, int b) {

        }

        public boolean canToggleSearch() {
            return true;
        }
    }


    public ToolbarItem(@NonNull Context context, ToolbarItems toolbarItems, int tag, int color, String text, int icon) {
        super(context);
        setTag(tag);

        parentToolbarItem = toolbarItems;
        if (text != null) {
            textView = new TextView(context);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            textView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(LayoutCreator.dp(4), 0, LayoutCreator.dp(4), 0);
            textView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            textView.setText(text);
            if (color != 0) {
                textView.setTextColor(color);
            }
            addView(textView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT));
        } else {
            iconView = new IconView(context);
            iconView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            iconView.setIcon(icon);
            addView(iconView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
            if (color != 0) {
                iconView.setIconColor(color);
            }
        }
    }

    public ToolbarItem setCustomTypeFace(Typeface customTypeFace) {
        iconView.setTypeface(customTypeFace);
        return this;
    }

    public void setIcon(int icon) {
        if (iconView != null) {
            iconView.setIcon(icon);
        }
    }

    public ToolbarItem setIsSearchBox(boolean isSearch) {

        searchContainer = new FrameLayout(getContext()) {

            @Override
            public void setVisibility(int visibility) {
                super.setVisibility(visibility);
                if (searchClearButton != null) {
                    searchClearButton.setVisibility(VISIBLE);
                }
            }

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

                int width = 0;
                int minWidth = MeasureSpec.getSize(widthMeasureSpec);
                measureChildWithMargins(searchClearButton, widthMeasureSpec, 0, heightMeasureSpec, 0);
                if (!G.isAppRtl) {
                    measureChildWithMargins(searchEditText, widthMeasureSpec, width, heightMeasureSpec, 0);
                } else {
                    measureChildWithMargins(searchEditText, MeasureSpec.makeMeasureSpec(minWidth - LayoutCreator.dp(6), MeasureSpec.UNSPECIFIED), width, heightMeasureSpec, 0);
                }
                setMeasuredDimension(Math.max(searchEditText.getMeasuredWidth(), minWidth), MeasureSpec.getSize(heightMeasureSpec));
            }

            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                int x = 0;

                searchEditText.layout(x, searchEditText.getTop(), x + searchEditText.getMeasuredWidth(), searchEditText.getBottom());
            }
        };
        searchContainer.setClipChildren(false);

        parentToolbarItem.addView(searchContainer, 0, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, 6, 0, 0, 0));

        searchContainer.setVisibility(GONE);
        searchEditText = new androidx.appcompat.widget.AppCompatEditText(getContext()) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY), heightMeasureSpec);
            }
        };
        searchEditText.setHint(R.string.search);
        searchEditText.setSingleLine(true);
        searchEditText.setBackground(null);
        searchEditText.setTextColor(Color.WHITE);
        searchEditText.setEllipsize(TextUtils.TruncateAt.END);
        searchEditText.setHintTextColor(Theme.getInstance().getDividerColor(getContext()));

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    searchClearButton.setVisibility(VISIBLE);
                } else {
                    searchClearButton.setVisibility(GONE);
                }
            }
        });

        searchContainer.addView(searchEditText, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER_VERTICAL, 6, 0, 48, 0));
        searchClearButton = new TextView(getContext());
        searchClearButton.setTextSize(22);
        searchClearButton.setTypeface(ResourcesCompat.getFont(getContext(), R.font.font_icons));
        searchClearButton.setTextColor(Theme.getInstance().getPrimaryTextColor(getContext()));
        searchClearButton.setText(R.string.icon_close);
        searchClearButton.setVisibility(GONE);
        searchClearButton.setGravity(Gravity.CENTER);

        searchClearButton.setOnClickListener(view -> {
            if (searchEditText.length() != 0) {
                searchEditText.setText("");
            }
        });
        searchContainer.addView(searchClearButton, LayoutCreator.createFrame(48, LayoutCreator.MATCH_PARENT, Gravity.CENTER_VERTICAL | Gravity.RIGHT));
        isSearchBox = isSearch;
        return this;
    }

    public void openSearchBox() {
        if (searchContainer == null || searchContainer.getVisibility() == VISIBLE || parentToolbarItem == null) {
            return;
        }
        parentToolbarItem.parentToolbar.onSearchVisibilityChanged(toggleSearch(true));
    }

    public ToolBarMenuSubItem addSubItem(int id, int icon, CharSequence text) {
        createPopupLayout();
        ToolBarMenuSubItem item = new ToolBarMenuSubItem(getContext());

        item.setTextAndIcon(text, icon);
        item.setMinimumWidth(LayoutCreator.dp(196));
        item.setTag(id);

        toolbarMenuItemLayout.addView(item);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) item.getLayoutParams();
        if (G.isAppRtl) {
            layoutParams.gravity = Gravity.RIGHT;
        }
        layoutParams.width = LayoutCreator.MATCH_PARENT;
        layoutParams.height = LayoutCreator.dp(48);
        item.setLayoutParams(layoutParams);

        item.setOnClickListener(view -> {
            if (popupWindow != null && popupWindow.isShowing()) {
                if (processedPopupClick) {
                    return;
                }
                processedPopupClick = true;
                popupWindow.dismiss(true);
                if (parentToolbarItem.parentToolbar.listener != null) {
                    parentToolbarItem.parentToolbar.listener.onItemClick((Integer) view.getTag());
                }
            }
        });

        return item;
    }

    private void createPopupLayout() {
        if (toolbarMenuItemLayout != null) {
            return;
        }

        Rect rect = new Rect();
        toolbarMenuItemLayout = new ToolbarMenuItemLayout(getContext());
        toolbarMenuItemLayout.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    view.getHitRect(rect);
                    if (!rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                        popupWindow.dismiss(true);
                    }
                }
            }
            return false;
        });
        toolbarMenuItemLayout.setOnDispatchKeyEventListener(event -> {

            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss(true);
            }
        });
    }

    public void togglePopup() {
        //todo: more conditions should add to this if statement.
        if (toolbarMenuItemLayout == null || parentToolbarItem != null && parentToolbarItem.parentToolbar != null && !parentToolbarItem.isActionMode) {
            return;
        }

        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss(true);
            return;
        }

        if (popupWindow == null) {
            popupWindow = new ToolbarPopup(toolbarMenuItemLayout, LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT);
            popupWindow.setAnimationStyle(1);
        }

        popupWindow.setOutsideTouchable(true);
        popupWindow.setClippingEnabled(true);
        popupWindow.setInputMethodMode(ToolbarPopup.INPUT_METHOD_NOT_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        popupWindow.getContentView().setFocusableInTouchMode(true);
        popupWindow.getContentView().setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_MENU && keyEvent.getRepeatCount() == 0 && keyEvent.getAction() == KeyEvent.ACTION_UP && popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss(true);
                return true;
            }
            return false;
        });
        processedPopupClick = false;
        popupWindow.setFocusable(true);

        if (toolbarMenuItemLayout.getMeasuredWidth() == 0) {
            updateOrShowPopup(true, true);
        } else {
            updateOrShowPopup(true, false);
        }

        popupWindow.startAnimation();

    }

    private void updateOrShowPopup(boolean show, boolean update) {
        int offsetY;

        if (parentToolbarItem != null) {
            offsetY = -parentToolbarItem.parentToolbar.getMeasuredHeight() + parentToolbarItem.getTop() + parentToolbarItem.getPaddingTop()/* - (int) parentMenu.parentActionBar.getTranslationY()*/;
        } else {
            float scaleY = getScaleY();
            offsetY = -(int) (getMeasuredHeight() * scaleY - (subMenuOpenSide != 2 ? getTranslationY() : 0) / scaleY) + additionalYOffset;
        }
        offsetY += yOffset;

        if (show) {
            toolbarMenuItemLayout.scrollToTop();
        }

        if (parentToolbarItem != null) {
            View parent = parentToolbarItem.parentToolbar;
            if (subMenuOpenSide == 0) {
                if (show) {
                    popupWindow.showAsDropDown(parent, getLeft() + parentToolbarItem.getLeft() + getMeasuredWidth() - toolbarMenuItemLayout.getMeasuredWidth() + (int) getTranslationX(), offsetY);
                }
                if (update) {
                    popupWindow.update(parent, getLeft() + parentToolbarItem.getLeft() + getMeasuredWidth() - toolbarMenuItemLayout.getMeasuredWidth() + (int) getTranslationX(), offsetY, -1, -1);
                }
            } else {
                if (show) {
                    if (forceSmoothKeyboard) {
                        popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, getLeft() - LayoutCreator.dp(8) + (int) getTranslationX(), offsetY);
                    } else {
                        popupWindow.showAsDropDown(parent, getLeft() - LayoutCreator.dp(8) + (int) getTranslationX(), offsetY);
                    }
                }
                if (update) {
                    popupWindow.update(parent, getLeft() - LayoutCreator.dp(8) + (int) getTranslationX(), offsetY, -1, -1);
                }
            }
        } else {
            if (subMenuOpenSide == 0) {
                if (getParent() != null) {
                    View parent = (View) getParent();
                    if (show) {
                        popupWindow.showAsDropDown(parent, getLeft() + getMeasuredWidth() - toolbarMenuItemLayout.getMeasuredWidth() + additionalXOffset, offsetY);
                    }
                    if (update) {
                        popupWindow.update(parent, getLeft() + getMeasuredWidth() - toolbarMenuItemLayout.getMeasuredWidth() + additionalXOffset, offsetY, -1, -1);
                    }
                }
            } else if (subMenuOpenSide == 1) {
                if (show) {
                    popupWindow.showAsDropDown(this, -LayoutCreator.dp(8) + additionalXOffset, offsetY);
                }
                if (update) {
                    popupWindow.update(this, -LayoutCreator.dp(8) + additionalXOffset, offsetY, -1, -1);
                }
            } else {
                if (show) {
                    popupWindow.showAsDropDown(this, getMeasuredWidth() - toolbarMenuItemLayout.getMeasuredWidth() + additionalXOffset, offsetY);
                }
                if (update) {
                    popupWindow.update(this, getMeasuredWidth() - toolbarMenuItemLayout.getMeasuredWidth() + additionalXOffset, offsetY, -1, -1);
                }
            }
        }
    }


    public boolean isSearchBox() {
        return isSearchBox;
    }

    public boolean toggleSearch(boolean openKeyboard) {
        if (searchContainer == null) {
            return false;
        }
        if (searchContainer.getVisibility() == VISIBLE) {
            searchContainer.setVisibility(GONE);
//            searchClearButton.setVisibility(GONE);
            searchEditText.clearFocus();
            if (listener != null) {
                listener.onSearchCollapse();
            }
            if (openKeyboard) {
                AndroidUtils.hideKeyboard(searchEditText);
            }
            setVisibility(VISIBLE);
            return false;
        } else {
            searchContainer.setVisibility(VISIBLE);
//            searchClearButton.setVisibility(VISIBLE);
            searchContainer.setAlpha(1f);
            setVisibility(GONE);
            searchEditText.setText("");
            searchEditText.requestFocus();
            if (listener != null) {
                listener.onSearchExpand();
            }
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    if (ignoreOnTextChange) {
//                        ignoreOnTextChange = false;
//                        return;
//                    }
                    if (listener != null) {
                        listener.onTextChanged(searchEditText);
                    }
      /*              checkClearButton();
                    if (!currentSearchFilters.isEmpty()) {
                        if (!TextUtils.isEmpty(searchField.getText()) && selectedFilterIndex >= 0) {
                            selectedFilterIndex = -1;
                            onFiltersChanged();
                        }
                    }*/
                }

                @Override
                public void afterTextChanged(Editable s) {

                }

            });
            if (openKeyboard) {
                AndroidUtils.showKeyboard(searchEditText);
            }
            return true;
        }
    }

    public boolean isSearchBoxVisible() {
        return searchContainer.getVisibility() == VISIBLE;
    }

    public void setAdditionalXOffset(int additionalXOffset) {
        this.additionalXOffset = additionalXOffset;
    }

    public void setMenuYOffset(int offset) {
        yOffset = offset;
    }

    public void setForceSmoothKeyboard(boolean value) {
        forceSmoothKeyboard = value;
    }

    public void setAdditionalYOffset(int additionalYOffset) {
        this.additionalYOffset = additionalYOffset;
    }

    public void setSubMenuOpenSide(int subMenuOpenSide) {
        this.subMenuOpenSide = subMenuOpenSide;
    }

    public boolean hasSubMenu() {
        return toolbarMenuItemLayout != null;
    }

    public ToolbarItem setActionBarMenuItemSearchListener(ActionBarMenuItemSearchListener listener) {
        this.listener = listener;
        return this;
    }
}
