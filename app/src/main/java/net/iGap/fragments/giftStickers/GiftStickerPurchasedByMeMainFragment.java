package net.iGap.fragments.giftStickers;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import net.iGap.R;
import net.iGap.module.Theme;

public class GiftStickerPurchasedByMeMainFragment extends Fragment {
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_gift_sticker_purchased_by_me, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager = view.findViewById(R.id.vp_purchasedByMe);
        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(1);

        tabLayout = view.findViewById(R.id.tl_purchasedByMe);
        tabLayout.setSelectedTabIndicatorColor(new Theme().getAccentColor(getContext()));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        setTabLayout();
    }


    private void setTabLayout() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {

            if (tabLayout.getTabAt(i) == null) {
                continue;
            }

            TextView tv = new TextView(getContext());
            tv.setText(tabLayout.getTabAt(i).getText());
            tv.setGravity(Gravity.CENTER);
            tv.setTypeface(ResourcesCompat.getFont(tv.getContext(), R.font.main_font));
            tv.setTextColor(new Theme().getTitleTextColor(tv.getContext()));
            tabLayout.getTabAt(i).setCustomView(tv);
        }
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        private GiftStickerPurchasedByMeFragment[] fragments;

        public PagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            fragments = new GiftStickerPurchasedByMeFragment[]{
                    GiftStickerPurchasedByMeFragment.getInstance(GiftStickerPurchasedByMeFragment.ACTIVE),
                    GiftStickerPurchasedByMeFragment.getInstance(GiftStickerPurchasedByMeFragment.NEW),
                    GiftStickerPurchasedByMeFragment.getInstance(GiftStickerPurchasedByMeFragment.FORWARD)
            };
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == fragments[0].getMode()) {
                return getResources().getString(R.string.usable);
            } else if (position == fragments[1].getMode()) {
                return getResources().getString(R.string.activated);
            } else if (position == fragments[2].getMode()) {
                return getResources().getString(R.string.posted);
            } else {
                return "";
            }
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }
}
