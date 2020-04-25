package net.iGap.fragments.kuknos;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import net.iGap.R;
import net.iGap.adapter.kuknos.TabAdapter;
import net.iGap.databinding.FragmentKuknosTradePagerBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.module.Theme;
import net.iGap.observers.interfaces.ToolbarListener;

public class KuknosAssetsPagerFrag extends BaseFragment {

    private FragmentKuknosTradePagerBinding binding;
    private TabLayout tabLayout;


    public static KuknosAssetsPagerFrag newInstance() {
        return new KuknosAssetsPagerFrag();
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
                .setLeftIcon(R.string.back_icon)
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
        adapter.addFragment(KuknosCurrentAssetFrag.newInstance(1), getResources().getString(R.string.kuknos_NewAsset_title));
        adapter.addFragment(KuknosCurrentAssetFrag.newInstance(0), getResources().getString(R.string.kuknos_AddAsset_title));
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
