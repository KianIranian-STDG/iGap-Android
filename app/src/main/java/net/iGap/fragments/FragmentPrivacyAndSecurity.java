/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentPrivacyAndSecurityBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.realm.RealmPrivacy;
import net.iGap.request.RequestUserContactsGetBlockedList;
import net.iGap.request.RequestUserProfileGetSelfRemove;
import net.iGap.viewmodel.FragmentPrivacyAndSecurityViewModel;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPrivacyAndSecurity extends BaseFragment {


    private FragmentPrivacyAndSecurityViewModel fragmentPrivacyAndSecurityViewModel;
    private FragmentPrivacyAndSecurityBinding fragmentPrivacyAndSecurityBinding;
    private HelperToolbar mHelperToolbar;


    public FragmentPrivacyAndSecurity() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentPrivacyAndSecurityBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy_and_security, container, false);
        return attachToSwipeBack(fragmentPrivacyAndSecurityBinding.getRoot());
    }

    @Override
    public void onPause() {
        super.onPause();
        fragmentPrivacyAndSecurityViewModel.onPause();
    }


    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();
        setupToolbar();

        new RequestUserContactsGetBlockedList().userContactsGetBlockedList();

        RealmPrivacy.getUpdatePrivacyFromServer();

        fragmentPrivacyAndSecurityBinding.parentPrivacySecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fragmentPrivacyAndSecurityBinding.stpsRippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                popBackStackFragment();
            }
        });

        new RequestUserProfileGetSelfRemove().userProfileGetSelfRemove();

        fragmentPrivacyAndSecurityViewModel.goToBlockedUserPage.observe(this, go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentBlockedUser()).setReplace(false).load();
            }
        });

        fragmentPrivacyAndSecurityViewModel.goToPassCodePage.observe(this, go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentPassCode()).setReplace(false).load();
            }
        });

        fragmentPrivacyAndSecurityViewModel.goToSecurityPage.observe(this, go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentSecurity()).setReplace(false).load();
            }
        });

        fragmentPrivacyAndSecurityViewModel.goToActiveSessionsPage.observe(this, go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentActiveSessions()).setReplace(false).load();
            }
        });

    }

    private void setupToolbar() {

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setDefaultTitle(G.context.getResources().getString(R.string.st_title_Privacy_Security))
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });

        fragmentPrivacyAndSecurityBinding.fpsLayoutToolbar.addView(mHelperToolbar.getView());
    }

    private void initDataBinding() {

        fragmentPrivacyAndSecurityViewModel = new FragmentPrivacyAndSecurityViewModel();
        fragmentPrivacyAndSecurityBinding.setFragmentPrivacyAndSecurityViewModel(fragmentPrivacyAndSecurityViewModel);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentPrivacyAndSecurityViewModel.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentPrivacyAndSecurityViewModel.onResume();
    }
}
