package net.iGap.kuknos.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.module.AndroidUtils;
import net.iGap.observers.interfaces.ToolbarListener;


public class KuknosBuyAgainFrag extends BaseFragment {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private String assetType;

    public static KuknosBuyAgainFrag newInstance() {
        KuknosBuyAgainFrag fragment = new KuknosBuyAgainFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        AndroidUtils.requestAdjustResize(getActivity(), getClass().getSimpleName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_kuknos_buy_again, container, false);
        if (getArguments() != null) {

            assetType = getArguments().getString("assetCode");

        }
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
        LinearLayout toolbarLayout = view.findViewById(R.id.fragKuknosBuyAgainToolbar);
        toolbarLayout.addView(mHelperToolbar.getView());
        mViewPager = view.findViewById(R.id.kuknos_buy_again_pager);

        mViewPager.setAdapter(new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    return KuknosGetSupportFrag.newInstance();
                } else {
                    KuknosRefundRialFrag refundRialFrag = KuknosRefundRialFrag.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putString("assetType", assetType);
                    refundRialFrag.setArguments(bundle);
                    return refundRialFrag;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0)
                    return getString(R.string.kuknos_buy_again_tab_Get_PMN_physical_support, assetType);
                else
                    return getString(R.string.kuknos_buy_again_tab_Rial_equivalent, assetType);
            }
        });
        mViewPager.setCurrentItem(1);
        mTabLayout = view.findViewById(R.id.kuknos_buy_again_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AndroidUtils.removeAdjustResize(getActivity(), getClass().getSimpleName());
    }
}