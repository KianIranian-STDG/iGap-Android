package net.iGap.kuknos.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentKuknosTradePagerBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.view.adapter.TabAdapter;
import net.iGap.libs.bottomNavigation.Util.Utils;

public class KuknosTradePagerFrag extends BaseFragment {

    private FragmentKuknosTradePagerBinding binding;
    private HelperToolbar mHelperToolbar;
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    public static KuknosTradePagerFrag newInstance() {
        KuknosTradePagerFrag kuknosLoginFrag = new KuknosTradePagerFrag();
        return kuknosLoginFrag;
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

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.kuknosTradePagerToolbar;
        Utils.darkModeHandler(toolbarLayout);
        toolbarLayout.addView(mHelperToolbar.getView());

        viewPager = binding.kuknosTradePager;
        tabLayout = binding.kuknosTradePagerTabLayout;
        adapter = new TabAdapter(getFragmentManager());
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

}
