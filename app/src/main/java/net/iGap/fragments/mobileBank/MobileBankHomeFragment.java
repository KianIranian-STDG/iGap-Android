package net.iGap.fragments.mobileBank;


import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.tabs.TabLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.Theme;
import net.iGap.databinding.FragmentMobileBankHomeBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.adapter.mobileBank.MobileBankHomeAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MobileBankHomeFragment extends BaseFragment {

    private FragmentMobileBankHomeBinding binding;

    public MobileBankHomeFragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mobile_bank_home, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        setupViewPager();
    }

    private void setupViewPager() {
        if (getActivity() == null) return;
        binding.viewPager.setOffscreenPageLimit(2);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.viewPager.setAdapter(new MobileBankHomeAdapter(getActivity().getSupportFragmentManager(), getModes(), getTitles()));
        binding.viewPager.setCurrentItem(G.isAppRtl ? 2 : 0);
        updateFontTabLayout(binding.tabLayout);
    }

    private List<MobileBankHomeTabFragment.HomeTabMode> getModes() {
        List<MobileBankHomeTabFragment.HomeTabMode> mode = new ArrayList<>();
        if (G.isAppRtl) {
            mode.add(MobileBankHomeTabFragment.HomeTabMode.SERVICE);
            mode.add(MobileBankHomeTabFragment.HomeTabMode.DEPOSIT);
            mode.add(MobileBankHomeTabFragment.HomeTabMode.CARD);
        } else {
            mode.add(MobileBankHomeTabFragment.HomeTabMode.CARD);
            mode.add(MobileBankHomeTabFragment.HomeTabMode.DEPOSIT);
            mode.add(MobileBankHomeTabFragment.HomeTabMode.SERVICE);
        }
        return mode;
    }

    private List<String> getTitles() {
        List<String> title = new ArrayList<>();
        if (G.isAppRtl) {
            title.add(getString(R.string.services));
            title.add(getString(R.string.accounts));
            title.add(getString(R.string.cards));
        } else {
            title.add(getString(R.string.cards));
            title.add(getString(R.string.accounts));
            title.add(getString(R.string.services));
        }
        return title;
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

    private void setupToolbar() {

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setRoundBackground(false)
                .setLeftIcon(R.string.icon_back)
                .setLifecycleOwner(getViewLifecycleOwner())
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });

        binding.toolbar.addView(toolbar.getView());
    }

}
