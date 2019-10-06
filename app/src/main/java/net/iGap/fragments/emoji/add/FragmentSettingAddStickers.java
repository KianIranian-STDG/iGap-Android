package net.iGap.fragments.emoji.add;


import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.fragments.FragmentToolBarBack;
import net.iGap.fragments.emoji.api.APIEmojiService;
import net.iGap.fragments.emoji.api.ApiEmojiUtils;
import net.iGap.fragments.emoji.struct.StickerCategory;
import net.iGap.fragments.emoji.struct.StructCategoryResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentSettingAddStickers extends FragmentToolBarBack {

    private ProgressBar pBar;
    private TextView emptyRecycle;
    private SectionPagerAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public FragmentSettingAddStickers() {

    }

    public static FragmentSettingAddStickers newInstance() {

        FragmentSettingAddStickers fragmentDetailStickers = new FragmentSettingAddStickers();
        Bundle bundle = new Bundle();
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
        pBar = view.findViewById(R.id.pBar);
        emptyRecycle = view.findViewById(R.id.emptyRecycle);

        emptyRecycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCategories();
            }
        });

        titleTextView.setText(R.string.add_sticker);

        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setSelectedTabIndicatorColor(getContext().getResources().getColor(R.color.setting_items_value_color));
        viewPager = view.findViewById(R.id.pager);

        adapter = new SectionPagerAdapter(getActivity().getSupportFragmentManager(), new StickerCategory[0]);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        getCategories();

    }

    private void getCategories() {
        emptyRecycle.setVisibility(View.GONE);
        pBar.setVisibility(View.VISIBLE);

        APIEmojiService mAPIService = ApiEmojiUtils.getAPIService();
        mAPIService.getCategories().enqueue(new Callback<StructCategoryResult>() {
            @Override
            public void onResponse(Call<StructCategoryResult> call, Response<StructCategoryResult> response) {
                StructCategoryResult structCategoryResult = response.body();
                if (structCategoryResult != null && structCategoryResult.isOk() &&
                        structCategoryResult.getStickerCategories() != null) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() == null || getActivity().isFinishing()) {
                                return;
                            }

                            if (structCategoryResult.getStickerCategories().length > 3) {
                                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                            }

                            pBar.setVisibility(View.GONE);


                            StickerCategory[] tabs = structCategoryResult.getStickerCategories();

                            if (G.selectedLanguage.equals("fa")) {
                                tabs = reverseArray(tabs, tabs.length);
                            }

                            adapter.setData(tabs);
                            adapter.notifyDataSetChanged();
                            updateFontTabLayout();

                            if (G.selectedLanguage.equals("fa")) {
                                viewPager.setCurrentItem(tabs.length - 1);
                            }
                        }
                    });
                } else {
                    emptyRecycle.setVisibility(View.VISIBLE);
                    pBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<StructCategoryResult> call, Throwable t) {
                emptyRecycle.setVisibility(View.VISIBLE);
                pBar.setVisibility(View.GONE);
            }
        });
    }

    private StickerCategory[] reverseArray(StickerCategory[] old, int size) {
        StickerCategory[] newArray = new StickerCategory[size];
        int j = size;
        for (int i = 0; i < size; i++) {
            newArray[j - 1] = old[i];
            j = j - 1;
        }

        return newArray;
    }

    private void updateFontTabLayout() {
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
        private StickerCategory[] tabs;

        public SectionPagerAdapter(FragmentManager fm, StickerCategory[] tabs) {
            super(fm);
            this.tabs = tabs;
        }

        public void setData(StickerCategory[] tabs) {
            this.tabs = tabs;
        }

        @Override
        public Fragment getItem(int position) {
            Gson gson = new Gson();
            return FragmentAddStickers.newInstance(gson.toJson(tabs[position]));
        }

        @Override
        public int getCount() {
            return tabs.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position].getName();
        }
    }
}
