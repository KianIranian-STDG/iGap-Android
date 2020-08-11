package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperString;
import net.iGap.observers.interfaces.OnChannelCheckUsername;
import net.iGap.observers.interfaces.OnChannelUpdateUsername;
import net.iGap.proto.ProtoChannelCheckUsername;
import net.iGap.realm.RealmRoom;
import net.iGap.request.RequestChannelCheckUsername;
import net.iGap.request.RequestChannelUpdateUsername;

public class FragmentCreateChannelViewModel extends ViewModel implements OnChannelCheckUsername {

    private ObservableInt showLoading = new ObservableInt(View.GONE);
    private ObservableInt channelLinkErrorMessage = new ObservableInt();
    private ObservableBoolean isLinkEnable = new ObservableBoolean(true);
    private ObservableField<String> channelLink = new ObservableField<>(Config.IGAP_LINK_PREFIX);
    private MutableLiveData<Integer> showErrorMessage = new MutableLiveData<>();
    private MutableLiveData<String> copyChannelLink = new MutableLiveData<>();

    private int channelType = R.id.privateChannel;


    private Spannable wordtoSpan;

    public MutableLiveData<Long> getRoom = new MutableLiveData<>();
    private long roomId;
    private String inviteLink;
    private String token;

    public FragmentCreateChannelViewModel(long roomId, String inviteLink, String token) {
        G.onChannelCheckUsername = this;

        this.roomId = roomId;
        this.inviteLink = inviteLink;
        this.token = token;

        wordtoSpan = new SpannableString(channelLink.get());
        Selection.setSelection(wordtoSpan, channelLink.get().length());

        setInviteLink();
    }

    public ObservableInt getShowLoading() {
        return showLoading;
    }

    public ObservableInt getChannelLinkErrorMessage() {
        return channelLinkErrorMessage;
    }

    public ObservableBoolean getIsLinkEnable() {
        return isLinkEnable;
    }

    public ObservableField<String> getChannelLink() {
        return channelLink;
    }

    public MutableLiveData<Integer> getShowErrorMessage() {
        return showErrorMessage;
    }

    public MutableLiveData<String> getCopyChannelLink() {
        return copyChannelLink;
    }

    public void isRadioGroup(int id) {
        channelType = id;
        setInviteLink();
    }

    public void onClickTxtInputLink() {
        Log.wtf(this.getClass().getName(), "onClickTxtInputLink");
        if (channelType == R.id.privateChannel) {
            copyChannelLink.setValue(channelLink.get());
        }
    }

    public void onClickFinish() {
        G.onChannelUpdateUsername = new OnChannelUpdateUsername() {
            @Override
            public void onChannelUpdateUsername(final long roomId, final String username) {
                RealmRoom.updateUsername(roomId, username);
                showLoading.set(View.GONE);
                getRoom.postValue(roomId);
            }

            @Override
            public void onError(int majorCode, int minorCode, int time) {
                showLoading.set(View.GONE);
            }

            @Override
            public void onTimeOut() {
                showLoading.set(View.GONE);
                showErrorMessage.postValue(R.string.time_out);
            }
        };

        if ((channelType == R.id.privateChannel || channelLink.get().length() > 0) && roomId > 0) {
            showLoading.set(View.VISIBLE);
            if (channelType == R.id.privateChannel) {
                RealmRoom.setPrivate(roomId, () -> {
                    showLoading.set(View.GONE);
                    getRoom.setValue(roomId);
                });
            } else {
                new RequestChannelUpdateUsername().channelUpdateUsername(roomId, channelLink.get().replace(Config.IGAP_LINK_PREFIX, ""));
            }
        }
    }

    public void afterTextChanged(String s) {
        if (channelType == R.id.publicChannel) {
            if (!s.startsWith(Config.IGAP_LINK_PREFIX)) {
                channelLink.set(Config.IGAP_LINK_PREFIX);
            }

            if (HelperString.regexCheckUsername(s.replace(Config.IGAP_LINK_PREFIX, ""))) {
                new RequestChannelCheckUsername().channelCheckUsername(roomId, channelLink.get().replace(Config.IGAP_LINK_PREFIX, ""));
            } else {
                channelLinkErrorMessage.set(R.string.INVALID);
            }
        }
    }

    private void setInviteLink() {
        if (channelType == R.id.privateChannel) {
            channelLink.set(inviteLink);
            isLinkEnable.set(false);
            channelLinkErrorMessage.set(0);
        } else if (channelType == R.id.publicChannel) {
            isLinkEnable.set(true);
            channelLink.set(Config.IGAP_LINK_PREFIX);
        }
    }


    @Override
    public void onChannelCheckUsername(final ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status status) {

        if (G.fragmentActivity != null) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.AVAILABLE) {
                        channelLinkErrorMessage.set(0);
                    } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.INVALID) {
                        channelLinkErrorMessage.set(R.string.INVALID);
                    } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.TAKEN) {
                        channelLinkErrorMessage.set(R.string.TAKEN);
                    } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.OCCUPYING_LIMIT_EXCEEDED) {
                        channelLinkErrorMessage.set(R.string.OCCUPYING_LIMIT_EXCEEDED);
                    }
                }
            });
        }
    }

    @Override
    public void onError(int majorCode, int minorCode) {
        showErrorMessage.postValue(R.string.error);
    }

    @Override
    public void onTimeOut() {
        showErrorMessage.postValue(R.string.time_out);
    }
}
