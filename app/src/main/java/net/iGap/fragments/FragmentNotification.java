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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import net.iGap.R;
import net.iGap.databinding.FragmentNotificationBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.messenger.theme.Theme;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentNotificationViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotification extends BaseFragment {

    private long roomId;
    private FragmentNotificationBinding fragmentNotificationBinding;
    private FragmentNotificationViewModel fragmentNotificationViewModel;

    public FragmentNotification() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentNotificationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
        return attachToSwipeBack(fragmentNotificationBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView ntg_txt_desc_notifications = view.findViewById(R.id.ntg_txt_desc_notifications);
        TextView ntg_txt_desc_vibrate = view.findViewById(R.id.ntg_txt_desc_notifications);
        TextView ntg_txt_desc_sound = view.findViewById(R.id.ntg_txt_desc_notifications);
        ntg_txt_desc_notifications.setTextColor(Theme.getColor(Theme.key_theme_color));
        ntg_txt_desc_vibrate.setTextColor(Theme.getColor(Theme.key_theme_color));
        ntg_txt_desc_sound.setTextColor(Theme.getColor(Theme.key_theme_color));
        roomId = getArguments().getLong("ID");
        initDataBinding();

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.icon_back)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.ntg_title_toolbar))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        try {
                            popBackStackFragment();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                });
        fragmentNotificationBinding.ntgLayoutToolbar.addView(toolbar.getView());

    }

    private void initDataBinding() {
        fragmentNotificationViewModel = new FragmentNotificationViewModel(fragmentNotificationBinding, roomId);
        fragmentNotificationBinding.setFragmentNotificationViewModel(fragmentNotificationViewModel);
    }
}
