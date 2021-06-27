package net.iGap.kuknos.Fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import net.iGap.R;
import net.iGap.adapter.kuknos.TabAdapter;
import net.iGap.databinding.FragmentKuknosTradePagerBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.module.Theme;
import net.iGap.observers.interfaces.ToolbarListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

public class KuknosTradePagerFrag extends BaseFragment {

    private FragmentKuknosTradePagerBinding binding;
    private TabLayout tabLayout;


    public static KuknosTradePagerFrag newInstance() {
        return new KuknosTradePagerFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_trade_pager, container, false);
        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.icon_back)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.kuknosTradePagerToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        ViewPager viewPager = binding.kuknosTradePager;
        tabLayout = binding.kuknosTradePagerTabLayout;
        TabAdapter adapter = new TabAdapter(getChildFragmentManager());
        adapter.addFragment(new KuknosTradeFrag(), getResources().getString(R.string.kuknos_tradePager_trade));
        adapter.addFragment(new KuknosTradeActiveFrag(), getResources().getString(R.string.kuknos_tradePager_active));
        adapter.addFragment(new KuknosTradeHistoryFrag(), getResources().getString(R.string.kuknos_tradePager_history));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        updateFontTabLayout();
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

}
