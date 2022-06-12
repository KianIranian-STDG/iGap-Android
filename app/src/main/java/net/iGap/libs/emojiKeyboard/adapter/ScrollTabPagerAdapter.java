package net.iGap.libs.emojiKeyboard.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import net.iGap.G;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScrollTabPagerAdapter extends PagerAdapter {
    private List<View> views = new ArrayList<>();

    public ScrollTabPagerAdapter(List<View> views) {
        if (G.isAppRtl)
            Collections.reverse(views);
        this.views.addAll(views);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        View view = views.get(position);
        collection.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, @NonNull Object view) {
        collection.removeView((View) view);
    }


    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }
}