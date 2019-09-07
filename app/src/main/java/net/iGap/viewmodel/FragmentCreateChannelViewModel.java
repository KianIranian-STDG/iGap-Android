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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;

import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentCreateChannelBinding;
import net.iGap.fragments.FragmentCreateChannel;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperString;
import net.iGap.interfaces.OnChannelCheckUsername;
import net.iGap.interfaces.OnChannelUpdateUsername;
import net.iGap.proto.ProtoChannelCheckUsername;
import net.iGap.realm.RealmRoom;
import net.iGap.request.RequestChannelCheckUsername;
import net.iGap.request.RequestChannelUpdateUsername;

import static android.content.Context.CLIPBOARD_SERVICE;

public class FragmentCreateChannelViewModel implements OnChannelCheckUsername {

    public static final int PRIVATE = 0;
    public static final int PUBLIC = 1;
    public Spannable wordtoSpan;
    public ObservableField<String> edtSetLink = new ObservableField<>(Config.IGAP_LINK_PREFIX);
    public ObservableInt prgWaiting = new ObservableInt(View.GONE);
    public ObservableInt txtFinishColor = new ObservableInt(Color.parseColor(G.appBarColor));
    public ObservableBoolean txtInputLinkEnable = new ObservableBoolean(false);
    public ObservableBoolean txtFinishEnable = new ObservableBoolean(true);
    public ObservableBoolean edtSetLinkEnable = new ObservableBoolean(true);
    public ObservableBoolean isRadioButtonPrivate = new ObservableBoolean(true);
    public ObservableField<Boolean> isRadioButtonPublic = new ObservableField<>(false);
    public MutableLiveData<Long> getRoom = new MutableLiveData<>();
    private Long roomId;
    private String inviteLink;
    private String token;
    private boolean existAvatar;
    private String pathSaveImage;
    private FragmentCreateChannelBinding fragmentCreateChannelBinding;


    public FragmentCreateChannelViewModel(Bundle arguments, FragmentCreateChannelBinding fragmentCreateChannelBinding) {
        this.fragmentCreateChannelBinding = fragmentCreateChannelBinding;
        getInfo(arguments);
    }

    private void getInfo(Bundle arguments) {

        G.onChannelCheckUsername = this;

        if (arguments != null) {
            roomId = arguments.getLong("ROOMID");
            inviteLink = "https://" + arguments.getString("INVITE_LINK");
            token = arguments.getString("TOKEN");
        }

        edtSetLink.set(Config.IGAP_LINK_PREFIX);
        wordtoSpan = new SpannableString(edtSetLink.get());
        Selection.setSelection(wordtoSpan, edtSetLink.get().length());

        setInviteLink();
    }

    public void isRadioGroup(RadioGroup radioGroup, int id) {
        setInviteLink();
    }


    public void txtBack(View view) {
        if (FragmentCreateChannel.onRemoveFragment != null) {
            FragmentCreateChannel.onRemoveFragment.remove();
        }
    }

    public void onClickRadioButtonPublic(View view) {

    }

    public void onClickRadioButtonPrivate(View view) {

    }

    public void onClickTxtInputLink(View view) {


        if (isRadioButtonPrivate.get()) {
            final PopupMenu popup = new PopupMenu(G.fragmentActivity, view);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.menu_item_copy, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_link_copy:
                            String copy;
                            copy = edtSetLink.get();
                            ClipboardManager clipboard = (ClipboardManager) G.context.getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("LINK_GROUP", copy);
                            clipboard.setPrimaryClip(clip);

                            break;
                    }
                    return true;
                }
            });

            popup.show(); //
        }
    }

    public void onClickFinish(View view) {
        G.onChannelUpdateUsername = new OnChannelUpdateUsername() {
            @Override
            public void onChannelUpdateUsername(final long roomId, final String username) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        RealmRoom.updateUsername(roomId, username);
                        hideProgressBar();
                        getRoom.setValue(roomId);
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode, int time) {
                hideProgressBar();
            }

            @Override
            public void onTimeOut() {
                hideProgressBar();
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.time_out), false);
                    }
                });
            }
        };

        if ((isRadioButtonPrivate.get() || edtSetLink.get().length() > 0) && roomId > 0) {

            showProgressBar();

            if (isRadioButtonPrivate.get()) {
                RealmRoom.setPrivate(roomId);
                hideProgressBar();
                getRoom.setValue(roomId);
            } else {

                String userName = edtSetLink.get().replace(Config.IGAP_LINK_PREFIX, "");
                new RequestChannelUpdateUsername().channelUpdateUsername(roomId, userName);
            }
        }
    }

    public void onClickCancel(View view) {
        if (FragmentCreateChannel.onRemoveFragment != null) {
            FragmentCreateChannel.onRemoveFragment.remove();
        }
    }

    public void afterTextChanged(Editable s) {

        if (!isRadioButtonPrivate.get()) {

            if (!s.toString().startsWith(Config.IGAP_LINK_PREFIX)) {
                edtSetLink.set(Config.IGAP_LINK_PREFIX);

                //Selection.setSelection(new SpannableString(edtSetLink.get()), edtSetLink.get().length());
                //} else {
                //Selection.setSelection(new SpannableString(edtSetLink.get()), edtSetLink.get().length());
                //}
            }

            if (HelperString.regexCheckUsername(s.toString().replace(Config.IGAP_LINK_PREFIX, ""))) {
                String userName = edtSetLink.get().replace(Config.IGAP_LINK_PREFIX, "");
                new RequestChannelCheckUsername().channelCheckUsername(roomId, userName);
            } else {
                txtFinishEnable.set(false);
                txtFinishColor.set(G.context.getResources().getColor(R.color.gray_6c));
                txtInputLinkEnable.set(true);
                fragmentCreateChannelBinding.fchTxtInputNikeName.setError("" + G.fragmentActivity.getResources().getString(R.string.INVALID));
            }
        }
    }


    public void onDetach() {
        hideProgressBar();
    }

    private void hideProgressBar() {

        if (G.fragmentActivity != null) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {

                    prgWaiting.set(View.GONE);
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });
        }
    }

    private void setInviteLink() {

        if (isRadioButtonPrivate.get()) {
            edtSetLink.set(inviteLink);
            edtSetLinkEnable.set(false);
            txtFinishEnable.set(true);
            txtFinishColor.set(Color.parseColor(G.appBarColor));
            txtInputLinkEnable.set(true);
            fragmentCreateChannelBinding.fchTxtInputNikeName.setError("");
        } else if (isRadioButtonPublic.get()) {
            edtSetLinkEnable.set(true);
            edtSetLink.set(Config.IGAP_LINK_PREFIX);
        }
    }


    @Override
    public void onChannelCheckUsername(final ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status status) {

        if (G.fragmentActivity != null) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.AVAILABLE) {

                        txtFinishEnable.set(true);
                        txtFinishColor.set(Color.parseColor(G.appBarColor));
                        txtInputLinkEnable.set(true);
                        fragmentCreateChannelBinding.fchTxtInputNikeName.setError("");
                    } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.INVALID) {
                        txtFinishEnable.set(false);
                        txtFinishColor.set(G.context.getResources().getColor(R.color.gray_6c));
                        txtInputLinkEnable.set(true);
                        fragmentCreateChannelBinding.fchTxtInputNikeName.setError("" + G.fragmentActivity.getResources().getString(R.string.INVALID));
                    } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.TAKEN) {
                        txtFinishEnable.set(false);
                        txtFinishColor.set(G.context.getResources().getColor(R.color.gray_6c));
                        txtInputLinkEnable.set(true);
                        fragmentCreateChannelBinding.fchTxtInputNikeName.setError("" + G.fragmentActivity.getResources().getString(R.string.TAKEN));
                    } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.OCCUPYING_LIMIT_EXCEEDED) {
                        txtFinishEnable.set(false);
                        txtFinishColor.set(G.context.getResources().getColor(R.color.gray_6c));
                        txtInputLinkEnable.set(true);
                        fragmentCreateChannelBinding.fchTxtInputNikeName.setError("" + G.fragmentActivity.getResources().getString(R.string.OCCUPYING_LIMIT_EXCEEDED));
                    }
                }
            });
        }
    }

    @Override
    public void onError(int majorCode, int minorCode) {
    }

    @Override
    public void onTimeOut() {

        if (G.fragmentActivity != null) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {

                    HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.time_out), false);

                }
            });
        }
    }

    private void showProgressBar() {

        if (G.fragmentActivity != null) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {

                    prgWaiting.set(View.VISIBLE);
                    G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });
        }
    }
}
