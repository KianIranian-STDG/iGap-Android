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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentPrivacyAndSecurityBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.observers.interfaces.ToolbarListener;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentPrivacyAndSecurityViewModel = ViewModelProviders.of(this).get(FragmentPrivacyAndSecurityViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentPrivacyAndSecurityBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy_and_security, container, false);
        fragmentPrivacyAndSecurityBinding.setFragmentPrivacyAndSecurityViewModel(fragmentPrivacyAndSecurityViewModel);
        fragmentPrivacyAndSecurityBinding.setLifecycleOwner(this);
        return fragmentPrivacyAndSecurityBinding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        fragmentPrivacyAndSecurityViewModel.onPause();
    }


    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentPrivacyAndSecurityBinding.fpsLayoutToolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setDefaultTitle(getString(R.string.st_title_Privacy_Security))
                .setLeftIcon(R.string.icon_back)
                .setLogoShown(true)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                }).getView());

        new RequestUserContactsGetBlockedList().userContactsGetBlockedList();

        RealmPrivacy.getUpdatePrivacyFromServer();

        fragmentPrivacyAndSecurityBinding.parentPrivacySecurity.setOnClickListener(view1 -> {

        });

        new RequestUserProfileGetSelfRemove().userProfileGetSelfRemove();

        fragmentPrivacyAndSecurityViewModel.goToBlockedUserPage.observe(getViewLifecycleOwner(), go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentBlockedUser()).setReplace(false).load();
            }
        });

        fragmentPrivacyAndSecurityViewModel.goToPassCodePage.observe(getViewLifecycleOwner(), go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentPassCode()).setReplace(false).load();
            }
        });

        fragmentPrivacyAndSecurityViewModel.goToSecurityPage.observe(getViewLifecycleOwner(), go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentSecurity()).setReplace(false).load();
            }
        });

        fragmentPrivacyAndSecurityViewModel.goToActiveSessionsPage.observe(getViewLifecycleOwner(), go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentActiveSessions()).setReplace(false).load();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentPrivacyAndSecurityViewModel.onResume();
    }
}
