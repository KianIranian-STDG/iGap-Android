// Copyright (c) 2016, iGap - www.iGap.im
// iGap is a Hybrid instant messaging service .
// RooyeKhat Media Co . - www.RooyeKhat.co
// All rights reserved.

package com.iGap.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iGap.R;
import com.iGap.module.SlidingTabLayout;

import java.util.ArrayList;


public class SlidingTabsColorsFragment extends Fragment {

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private ArrayList<Fragment> pages = new ArrayList<Fragment>();


    private static final String TAG = "SlidingTabsColorsFragment";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pages.add(FragmentTransactionPaymentHistory.newInstance(FragmentTransactionPaymentHistory.PaymentTaype.weekly));
        pages.add(FragmentTransactionPaymentHistory.newInstance(FragmentTransactionPaymentHistory.PaymentTaype.monthly));
        pages.add(FragmentTransactionPaymentHistory.newInstance(FragmentTransactionPaymentHistory.PaymentTaype.yearly));

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_sliding_tabs, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);

        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(new SampleFragmentPagerAdapter(getChildFragmentManager()));


        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);


        mSlidingTabLayout.setViewPager(mViewPager);


    }


    class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int i) {

            return pages.get(i);
        }


        @Override
        public int getCount() {
            return pages.size();
        }

    }


}