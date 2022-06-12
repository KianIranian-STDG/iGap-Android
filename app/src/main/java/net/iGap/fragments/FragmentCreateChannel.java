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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputLayout;

import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentCreateChannelBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.MEditText;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoGlobal;
import net.iGap.viewmodel.FragmentCreateChannelViewModel;

import org.jetbrains.annotations.NotNull;

import static android.content.Context.CLIPBOARD_SERVICE;

public class FragmentCreateChannel extends BaseFragment {

    private FragmentCreateChannelViewModel viewModel;
    private FragmentCreateChannelBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                long roomId = 0;
                String inviteLink = null;
                String token = null;
                if (getArguments() != null) {
                    roomId = getArguments().getLong("ROOMID");
                    inviteLink = "https://" + getArguments().getString("INVITE_LINK");
                    token = getArguments().getString("TOKEN");
                }
                return (T) new FragmentCreateChannelViewModel(roomId, inviteLink, token);
            }
        }).get(FragmentCreateChannelViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_channel, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RelativeLayout fch_root = view.findViewById(R.id.fch_root);
        fch_root.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        TextInputLayout channel_Link = view.findViewById(R.id.channelLink);
        channel_Link.setDefaultHintTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_theme_color)));
        // to disable swipe in channel creation mode
        getSwipeBackLayout().setEnableGesture(false);

        binding.publicChannel.setTextColor(Theme.getColor(Theme.key_title_text));
        binding.description.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        binding.privateChannel.setTextColor(Theme.getColor(Theme.key_title_text));
        binding.description2.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        binding.permanentLinkTitle.setTextColor(Theme.getColor(Theme.key_title_text));
        binding.description3.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        MEditText fch_edt_link = view.findViewById(R.id.fch_edt_link);
        fch_edt_link.setTextColor(Theme.getColor(Theme.key_title_text));

        binding.fchLayoutToolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setRightIcons(R.string.icon_sent)
                .setLeftIcon(R.string.icon_back)
                .setDefaultTitle(getString(R.string.new_channel))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        viewModel.onClickFinish();
                    }
                }).getView());

        viewModel.getRoom.observe(getViewLifecycleOwner(), roomId -> {
            if (getActivity() != null && roomId != null) {
                Fragment fragment = ContactGroupFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putLong("RoomId", roomId);
                bundle.putString("TYPE", ProtoGlobal.Room.Type.CHANNEL.toString());
                bundle.putBoolean("NewRoom", true);
                fragment.setArguments(bundle);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });

        viewModel.getShowErrorMessage().observe(getViewLifecycleOwner(), errorMessageId -> {
            if (errorMessageId != null) {
                HelperError.showSnackMessage(getString(errorMessageId), false);
            }
        });

        viewModel.getCopyChannelLink().observe(getViewLifecycleOwner(), channelLink -> {
            if (getActivity() != null && channelLink != null) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("LINK_CHANNEL", channelLink);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), R.string.copy_link_title, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        ((ActivityMain) getActivity()).removeAllFragmentFromMain();
        return true;
    }
}
