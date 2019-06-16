package net.iGap.fragments.emoji.add;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import net.iGap.G;
import net.iGap.R;
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
        ViewPager viewPager = view.findViewById(R.id.pager);

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
                            adapter.setData(structCategoryResult.getStickerCategories());
                            adapter.notifyDataSetChanged();
                            updateFontTabLayout();
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

    private void updateFontTabLayout() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            if (tabLayout.getTabAt(i) == null) {
                continue;
            }

            TextView tv = new TextView(getContext());
            tv.setText(tabLayout.getTabAt(i).getText());
            tv.setGravity(Gravity.CENTER);
            tv.setTypeface(G.typeface_IRANSansMobile);

            if (G.isDarkTheme){
                tv.setTextColor(G.context.getResources().getColor(R.color.white));
            }
            else {
                tv.setTextColor(G.context.getResources().getColor(R.color.black));
            }
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
