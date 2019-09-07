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

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentCreateChannelBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.AppUtils;
import net.iGap.proto.ProtoGlobal;
import net.iGap.viewmodel.FragmentCreateChannelViewModel;

public class FragmentCreateChannel extends BaseFragment implements ToolbarListener {

    public static OnRemoveFragment onRemoveFragment;

    private FragmentCreateChannelViewModel fragmentCreateChannelViewModel;
    private FragmentCreateChannelBinding fragmentCreateChannelBinding;
    private HelperToolbar mHelperToolbar;

    public FragmentCreateChannel() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentCreateChannelBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_channel, container, false);
        return attachToSwipeBack(fragmentCreateChannelBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();
        initComponents(view);
        ProgressBar prgWaiting = fragmentCreateChannelBinding.fchPrgWaitingAddContact;
        AppUtils.setProgresColler(prgWaiting);

        onRemoveFragment = new OnRemoveFragment() {
            @Override
            public void remove() {
                popBackStackFragment();
            }
        };

        fragmentCreateChannelViewModel.getRoom.observe(this, roomId -> {
            if (getActivity() != null && roomId != null) {
                Fragment fragment = ContactGroupFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putLong("RoomId", roomId);
                bundle.putString("TYPE", ProtoGlobal.Room.Type.CHANNEL.toString());
                bundle.putBoolean("NewRoom", true);
                fragment.setArguments(bundle);

                /*if (FragmentCreateChannel.onRemoveFragment != null) {
                    FragmentCreateChannel.onRemoveFragment.remove();
                }*/
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });
    }

    private void initComponents(View view) {

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setRightIcons(R.string.check_icon)
                .setLeftIcon(R.string.back_icon)
                .setDefaultTitle(G.context.getResources().getString(R.string.new_channel))
                .setListener(this);

        LinearLayout layoutToolbar = fragmentCreateChannelBinding.fchLayoutToolbar;
        layoutToolbar.addView(mHelperToolbar.getView());

        //mHelperToolbar.getTextViewLogo().setText(G.context.getString(R.string.new_channel));

    }

    private void initDataBinding() {

        fragmentCreateChannelViewModel = new FragmentCreateChannelViewModel(getArguments(), fragmentCreateChannelBinding);
        fragmentCreateChannelBinding.setFragmentCreateChannelViewModel(fragmentCreateChannelViewModel);
    }

    @Override
    public void onDetach() {

        fragmentCreateChannelViewModel.onDetach();
        super.onDetach();
    }

    public interface OnRemoveFragment {
        void remove();
    }

    @Override
    public void onRightIconClickListener(View view) {
        fragmentCreateChannelViewModel.onClickFinish(view);
    }

    @Override
    public void onLeftIconClickListener(View view) {
        fragmentCreateChannelViewModel.onClickCancel(view);
    }
}
