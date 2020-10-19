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
import net.iGap.observers.interfaces.ToolbarListener;


public class KuknosBuyAgainFrag extends BaseFragment {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    public static KuknosBuyAgainFrag newInstance() {
        KuknosBuyAgainFrag fragment = new KuknosBuyAgainFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
                    return KuknosEquivalentRialFrag.newInstance();
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
                    return "دریافت پشتوانه \n فیزیکی PMN";
                else
                    return "معادل ریالی \n پرداخت PMN";
            }
        });
        mTabLayout = view.findViewById(R.id.kuknos_buy_again_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        return view;
    }

}