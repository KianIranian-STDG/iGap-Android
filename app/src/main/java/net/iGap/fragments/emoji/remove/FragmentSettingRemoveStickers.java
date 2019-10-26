package net.iGap.fragments.emoji.remove;


import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.tabs.TabLayout;
import com.vanniktech.emoji.sticker.struct.StructGroupSticker;
import com.vanniktech.emoji.sticker.struct.StructItemSticker;

import net.iGap.R;
import net.iGap.Theme;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.FragmentToolBarBack;
import net.iGap.helper.HelperError;
import net.iGap.realm.RealmStickers;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSettingRemoveStickers extends FragmentToolBarBack {

    private List<StructGroupSticker> stickerList = new ArrayList<>();
    private ArrayList<StructItemSticker> recentStickerList;

    public FragmentSettingRemoveStickers() {
        // Required empty public constructor
    }

    public static FragmentSettingRemoveStickers newInstance(ArrayList<StructItemSticker> recentStickerList) {

        FragmentSettingRemoveStickers fragmentDetailStickers = new FragmentSettingRemoveStickers();
        Bundle bundle = new Bundle();
        bundle.putSerializable("RECENT", recentStickerList);
        fragmentDetailStickers.setArguments(bundle);
        return fragmentDetailStickers;
    }

    @Override
    public void onCreateViewBody(LayoutInflater inflater, LinearLayout root, @Nullable Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_setting_stickers, root, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleTextView.setText(R.string.remove_sticker);

        stickerList = RealmStickers.getAllStickers(true);
        recentStickerList = (ArrayList<StructItemSticker>) getArguments().getSerializable("RECENT");

        menu_item1.setText(R.string.delete_icon);
        menu_item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FragmentRemoveRecentSticker.removeStickerList != null && FragmentRemoveRecentSticker.removeStickerList.size() > 0) {
                    new MaterialDialog.Builder(getActivity())
                            .title(getResources().getString(R.string.remove_sticker))
                            .content(getResources().getString(R.string.add_sticker_text))
                            .positiveText(getString(R.string.yes))
                            .negativeText(getString(R.string.no))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    FragmentChat.onUpdateSticker.updateRecentlySticker(FragmentRemoveRecentSticker.removeStickerList);
                                    FragmentRemoveRecentSticker.removeStickerList.clear();
                                    popBackStackFragment();
                                }
                            })
                            .show();
                } else {
                    HelperError.showSnackMessage(getResources().getString(R.string.Please_select_an_option), false);
                }
            }
        });

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager viewPager = view.findViewById(R.id.pager);

        viewPager.setAdapter(new SectionPagerAdapter(getActivity().getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                if (i == 1) {
                    menu_item1.setVisibility(View.VISIBLE);
                } else {
                    menu_item1.setVisibility(View.GONE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        updateFontTabLayout(tabLayout);

    }

    private void updateFontTabLayout(TabLayout tabLayout) {
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

    public class SectionPagerAdapter extends FragmentStatePagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FragmentRemoveSticker.newInstance(stickerList);
                case 1:
                default:
                    return FragmentRemoveRecentSticker.newInstance(recentStickerList);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.Favorite_Sticker);
                case 1:
                default:
                    return getString(R.string.Recently_Sticker);
            }
        }
    }
}
