package net.iGap.messenger.ui.toolBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import net.iGap.helper.LayoutCreator;

@SuppressLint("ViewConstructor")
public class ToolbarItems extends LinearLayout {

    public Toolbar parentToolbar;
    public boolean isActionMode;

    public ToolbarItems(@NonNull Context context, Toolbar parentToolbar) {
        super(context);
        setOrientation(HORIZONTAL);
        setScrollContainer(true);
        setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        this.parentToolbar = parentToolbar;
    }

    public ToolbarItem addItem(int tag, int icon, @ColorInt int color) {
        return addItem(tag, null, icon, 52, color);
    }

    public ToolbarItem addItem(int tag, String text, int icon, int width, @ColorInt int color) {

        ToolbarItem item = new ToolbarItem(getContext(), this, tag, color, text, icon);
        addView(item, LayoutCreator.createFrame(width, LayoutCreator.MATCH_PARENT));

        item.setOnClickListener(v -> {
            ToolbarItem toolbarItem = (ToolbarItem) v;
            if (toolbarItem.hasSubMenu()) {
                toolbarItem.togglePopup();
            } else if (toolbarItem.isSearchBox()) {
                parentToolbar.onSearchVisibilityChanged(toolbarItem.toggleSearch(true));
            }
        });
        return item;
    }

    public ToolbarItem addItemWithWidth(int tag, int icon, int width) {
        return addItem(tag, null, icon, width, Color.WHITE);
    }

    public void closeSearchBox(boolean closeKeyboard) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view instanceof ToolbarItem) {
                ToolbarItem item = (ToolbarItem) view;
                if (item.isSearchBox() && item.isSearchBoxVisible()) {
                    parentToolbar.onSearchVisibilityChanged(false);
                    item.toggleSearch(closeKeyboard);
                }
            }
        }
    }
}
