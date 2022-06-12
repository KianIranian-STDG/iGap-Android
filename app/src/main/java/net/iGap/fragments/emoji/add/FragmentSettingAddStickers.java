package net.iGap.fragments.emoji.add;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import net.iGap.messenger.theme.Theme;
import net.iGap.observers.rx.IGSingleObserver;
import net.iGap.repository.StickerRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;


public class FragmentSettingAddStickers extends FragmentToolBarBack {

    private String type = "ALL";
    private ProgressBar pBar;
    private TextView emptyRecycle;
    private SectionPagerAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CompositeDisposable compositeDisposable;
    private List<String> types = new ArrayList<>();
    private String[] typeSequences = null;
    public FragmentSettingAddStickers(String type) {
        this.type = type;
    }

    public static FragmentSettingAddStickers newInstance(String type) {
        return new FragmentSettingAddStickers(type);
    }

    @Override
    public void onCreateViewBody(LayoutInflater inflater, LinearLayout root, @Nullable Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_setting_stickers, root, true);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pBar = view.findViewById(R.id.pBar);
        emptyRecycle = view.findViewById(R.id.emptyRecycle);

        menu_item1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_filter_list_white_24dp));

        menu_item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeSequences != null) {
                    createDialog().show();
                } else {
                    Toast.makeText(getActivity(), R.string.load_sticker_types_error_message, Toast.LENGTH_SHORT).show();
                }
            }
        });
        compositeDisposable = new CompositeDisposable();

        emptyRecycle.setOnClickListener(v -> getCategories());

        titleTextView.setText(R.string.add_sticker);


        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        tabLayout.setSelectedTabIndicatorColor(Theme.getColor(Theme.key_theme_color));
        viewPager = view.findViewById(R.id.pager);

        adapter = new SectionPagerAdapter(getActivity().getSupportFragmentManager());

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        getTypes();
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

                        adapter.setData(categories, type);
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

    private void getTypes() {
        emptyRecycle.setVisibility(View.GONE);
        pBar.setVisibility(View.VISIBLE);
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
            tv.setTextColor(Theme.getColor(Theme.key_title_text));
            tabLayout.getTabAt(i).setCustomView(tv);
        }
    }

    public class SectionPagerAdapter extends FragmentStatePagerAdapter {
        private List<StructIGStickerCategory> categories = new ArrayList<>();
        private String type;

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setData(List<StructIGStickerCategory> categories, String type) {
            this.categories = categories;
            this.type = type;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return AddStickersFragment.newInstance(categories.get(position).getId(), type, null);
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

    public Dialog createDialog() {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        int[] index = {0};
        if (types.contains(type))
            index[0] = types.indexOf(type);
            builder.setTitle(getResources().getString(R.string.select_sticker_type))
                    .setSingleChoiceItems(typeSequences, index[0],
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    index[0] = which;


                                }
                            })
                    // Set the action buttons
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            type = types.get(index[0]);
                            adapter.type = type;
                            int lastPosition = tabLayout.getSelectedTabPosition();
                            viewPager.setAdapter(adapter);
                            updateFontTabLayout();
                            tabLayout.getTabAt(lastPosition).select();

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            return builder.create();

    }

}
