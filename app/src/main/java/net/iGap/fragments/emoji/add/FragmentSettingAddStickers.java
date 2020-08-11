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

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentToolBarBack;
import net.iGap.fragments.emoji.struct.StructIGStickerCategory;
import net.iGap.module.Theme;
import net.iGap.observers.rx.IGSingleObserver;
import net.iGap.repository.StickerRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;


public class FragmentSettingAddStickers extends FragmentToolBarBack {

    private ProgressBar pBar;
    private TextView emptyRecycle;
    private SectionPagerAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CompositeDisposable compositeDisposable;

    public static FragmentSettingAddStickers newInstance() {
        return new FragmentSettingAddStickers();
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

        compositeDisposable = new CompositeDisposable();

        emptyRecycle.setOnClickListener(v -> getCategories());

        titleTextView.setText(R.string.add_sticker);

        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setSelectedTabIndicatorColor(new Theme().getAccentColor(getContext()));
        viewPager = view.findViewById(R.id.pager);

        adapter = new SectionPagerAdapter(getActivity().getSupportFragmentManager());

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        getCategories();

    }

    private void getCategories() {
        emptyRecycle.setVisibility(View.GONE);
        pBar.setVisibility(View.VISIBLE);

        StickerRepository.getInstance().getStickerCategory()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new IGSingleObserver<List<StructIGStickerCategory>>(compositeDisposable) {
                    @Override
                    public void onSuccess(List<StructIGStickerCategory> categories) {

                        if (categories.size() > 3) {
                            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                        }

                        if (G.selectedLanguage.equals("fa")) {
                            Collections.reverse(categories);
                        }

                        adapter.setData(categories);
                        adapter.notifyDataSetChanged();
                        updateFontTabLayout();

                        if (G.selectedLanguage.equals("fa")) {
                            viewPager.setCurrentItem(categories.size() - 1);
                        }

                        pBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
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
            tv.setTypeface(ResourcesCompat.getFont(tv.getContext(), R.font.main_font));
            tv.setTextColor(new Theme().getTitleTextColor(tv.getContext()));
            tabLayout.getTabAt(i).setCustomView(tv);
        }
    }

    public class SectionPagerAdapter extends FragmentStatePagerAdapter {
        private List<StructIGStickerCategory> categories = new ArrayList<>();

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setData(List<StructIGStickerCategory> categories) {
            this.categories = categories;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return AddStickersFragment.newInstance(categories.get(position));
        }

        @Override
        public int getCount() {
            return categories.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return categories.get(position).getName();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
    }
}
