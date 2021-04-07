package org.paygear.fragment;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;

import org.paygear.RaadApp;
import org.paygear.WalletActivity;
import org.paygear.model.Card;

import ir.radsense.raadcore.OnFragmentInteraction;


public class CashOutFragment extends Fragment {

    ViewPager mPager;
    WalletPagerAdapter mPagerAdapter;

    Card mCard;
    boolean isCashOut;

    public CashOutFragment() {
    }

    public static CashOutFragment newInstance(Card card, boolean isCashOut) {
        CashOutFragment fragment = new CashOutFragment();
        Bundle args = new Bundle();
        args.putSerializable("Card", card);
        args.putBoolean("IsCashOut", isCashOut);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCard = (Card) getArguments().getSerializable("Card");
            isCashOut = getArguments().getBoolean("IsCashOut");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_cash_out, container, false);

        ViewGroup rootView = view.findViewById(R.id.rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rootView.setBackgroundColor(Color.parseColor(WalletActivity.primaryColor));
        }

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setLeftIcon(R.string.back_icon)
                .setDefaultTitle(getString(isCashOut ? R.string.cashout_taxi : R.string.charge_paygear))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null)
                            getActivity().onBackPressed();
                    }
                });

        LinearLayout lytToolbar = view.findViewById(R.id.toolbarLayout);
        lytToolbar.addView(toolbar.getView());

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        mPager = view.findViewById(R.id.view_pager);
        mPagerAdapter = new WalletPagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(mPager);

        if (isCashOut) {
            mPager.setCurrentItem(1);
        }

        /*for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                TextView textView = new AppCompatTextView(getContext());
                textView.setId(android.R.id.text1);
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                textView.setTypeface(Typefaces.get(getContext(), Typefaces.IRAN_MEDIUM));

                tab.setCustomView(textView);
            }
        }*/

        return view;
    }


    class WalletPagerAdapter extends FragmentPagerAdapter {
        Fragment[] fragments;

        public WalletPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new Fragment[getCount()];
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return CashOutRequestFragment.newInstance(mCard,
                            isCashOut ? CashOutRequestFragment.REQUEST_CASH_OUT_NORMAL : CashOutRequestFragment.REQUEST_CASH_IN);
                case 1:
                    return CashOutRequestFragment.newInstance(mCard, CashOutRequestFragment.REQUEST_CASH_OUT_IMMEDIATE);
                case 2:
                    return CashOutRequestFragment.newInstance(mCard,CashOutRequestFragment.REQUEST_CASH_OUT_TO_WALLET);
                default:
                    return null;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            fragments[position] = (Fragment) super.instantiateItem(container, position);
            return fragments[position];
        }

        public Fragment getFragment(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return isCashOut ? (RaadApp.selectedMerchant!=null?3:2) : 1;
        }

        public void notifyFragments(Fragment fragment, Bundle bundle) {
            //((OnFragmentInteraction) fragments[0]).onFragmentResult(fragment, bundle);
            if (fragments[2] != null)
                ((OnFragmentInteraction) fragments[2]).onFragmentResult(fragment, bundle);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(isCashOut ? R.string.normal_cash_out : R.string.charge_paygear);
                case 1:
                    return getString(R.string.immediate_cash_out);
                case 2:
                    return getString(R.string.cash_to_wallet);
                default:
                    return "";
            }
        }
    }

}
