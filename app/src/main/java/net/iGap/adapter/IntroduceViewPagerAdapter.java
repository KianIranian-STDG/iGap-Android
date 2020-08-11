package net.iGap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager.widget.PagerAdapter;

import net.iGap.G;
import net.iGap.R;

import org.jetbrains.annotations.NotNull;

public class IntroduceViewPagerAdapter extends PagerAdapter {

    private int pageCount;

    public IntroduceViewPagerAdapter(int pageCount) {
        this.pageCount = pageCount;
    }

    @Override
    public int getCount() {
        return pageCount;
    }

    @Override
    public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
        return view.equals(object);
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, int position) {

        int layout;
        if (G.isLandscape)
            layout = R.layout.view_pager_introduce_land;
        else
            layout = R.layout.view_pager_introduce_1;

        View view = LayoutInflater.from(container.getContext()).inflate(layout, container, false);
        AppCompatImageView introImage = view.findViewById(R.id.introImage);
        introImage.setImageResource(getIntroImage(position));
        AppCompatTextView title = view.findViewById(R.id.introTitle);
        title.setText(getTitle(position));
        AppCompatTextView description = view.findViewById(R.id.introDescription);
        description.setText(getDescription(position));
        container.addView(view);
        view.setTag(position);
        return view;
    }

    @Override
    public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    private int getIntroImage(int position) {
        switch (position) {
            case 1:
                return R.drawable.ic_init_nearby;
            case 2:
                return R.drawable.ic_init_iland;
            case 3:
                return R.drawable.ic_init_security;
            default:
                return R.drawable.ic_init_cominucation;
        }
    }

    private int getTitle(int position) {
        switch (position) {
            case 1:
                return R.string.text_line_1_introduce_page7;
            case 2:
                return R.string.text_line_1_introduce_page4;
            case 3:
                return R.string.text_line_1_introduce_page2;
            default:
                return R.string.text_line_1_introduce_page3;
        }
    }

    private int getDescription(int position) {
        switch (position) {
            case 1:
                return R.string.text_line_2_introduce_page7;
            case 2:
                return R.string.text_line_2_introduce_page4;
            case 3:
                return R.string.text_line_2_introduce_page2;
            default:
                return R.string.text_line_2_introduce_page3;
        }
    }
}
