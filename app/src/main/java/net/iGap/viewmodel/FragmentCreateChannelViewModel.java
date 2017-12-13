package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

import android.content.ClipData;
import android.content.ClipboardManager;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;
import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentCreateChannelBinding;
import net.iGap.fragments.ContactGroupFragment;
import net.iGap.fragments.FragmentCreateChannel;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperString;
import net.iGap.interfaces.OnChannelCheckUsername;
import net.iGap.interfaces.OnChannelUpdateUsername;
import net.iGap.interfaces.OnClientGetRoomResponse;
import net.iGap.proto.ProtoChannelCheckUsername;
import net.iGap.proto.ProtoClientGetRoom;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.request.RequestChannelCheckUsername;
import net.iGap.request.RequestChannelUpdateUsername;
import net.iGap.request.RequestClientGetRoom;

import static android.content.Context.CLIPBOARD_SERVICE;

public class FragmentCreateChannelViewModel implements OnChannelCheckUsername {

    private Long roomId;
    private String inviteLink;
    public static final int PRIVATE = 0;
    public static final int PUBLIC = 1;
    private String token;
    private boolean existAvatar;
    private String pathSaveImage;
    private FragmentCreateChannelBinding fragmentCreateChannelBinding;
    public Spannable wordtoSpan;

    public ObservableField<String> edtSetLink = new ObservableField<>("iGap.net/");
    public ObservableField<Integer> prgWaiting = new ObservableField<>(View.GONE);
    public ObservableField<Integer> txtFinishColor = new ObservableField<>(G.context.getResources().getColor(R.color.toolbar_background));
    public ObservableField<Boolean> txtInputLinkEnable = new ObservableField<>(false);
    public ObservableField<Boolean> txtFinishEnable = new ObservableField<>(true);
    public ObservableField<Boolean> edtSetLinkEnable = new ObservableField<>(true);
    public ObservableField<Boolean> isRadioButtonPrivate = new ObservableField<>(true);
    public ObservableField<Boolean> isRadioButtonPublic = new ObservableField<>(false);



    public FragmentCreateChannelViewModel(Bundle arguments, FragmentCreateChannelBinding fragmentCreateChannelBinding) {
        this.fragmentCreateChannelBinding = fragmentCreateChannelBinding;
        getInfo(arguments);
    }

    private void getInfo(Bundle arguments) {

        G.onChannelCheckUsername = this;

        if (arguments != null) {
            roomId = arguments.getLong("ROOMID");
            inviteLink = arguments.getString("INVITE_LINK");
            token = arguments.getString("TOKEN");
        }

        edtSetLink.set("iGap.net/");
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
                        getRoom(roomId, ProtoGlobal.Room.Type.CHANNEL);
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
                getRoom(roomId, ProtoGlobal.Room.Type.CHANNEL);
            } else {

                String userName = edtSetLink.get().replace("iGap.net/", "");
                new RequestChannelUpdateUsername().channelUpdateUsername(roomId, userName);
            }
        }
    }

    public void onClickCancel(View view) {
        if (FragmentCreateChannel.onRemoveFragment != null) {
            FragmentCreateChannel.onRemoveFragment.remove();
        }
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!isRadioButtonPrivate.get()) {

            if (!s.toString().contains("iGap.net/")) {
                edtSetLink.set("iGap.net/");
                Selection.setSelection(wordtoSpan, edtSetLink.get().length());
            }
            if (HelperString.regexCheckUsername(s.toString().replace("iGap.net/", ""))) {
                String userName = edtSetLink.get().replace("iGap.net/", "");
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

    private void getRoom(final Long roomId, final ProtoGlobal.Room.Type type) {
        G.onClientGetRoomResponse = new OnClientGetRoomResponse() {
            @Override
            public void onClientGetRoomResponse(final ProtoGlobal.Room room, ProtoClientGetRoom.ClientGetRoomResponse.Builder builder, RequestClientGetRoom.IdentityClientGetRoom identity) {

                if (identity.createRoomMode != RequestClientGetRoom.CreateRoomMode.requestFromOwner) {
                    return;
                }

                try {
                    if (G.fragmentActivity != null) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {

                                hideProgressBar();
                                Fragment fragment = ContactGroupFragment.newInstance();
                                Bundle bundle = new Bundle();
                                bundle.putLong("RoomId", roomId);
                                bundle.putString("LIMIT", room.getGroupRoomExtra().getParticipantsCountLimitLabel());
                                bundle.putString("TYPE", type.toString());
                                bundle.putBoolean("NewRoom", true);
                                fragment.setArguments(bundle);

                                //popBackStackFragment();
                                if (FragmentCreateChannel.onRemoveFragment != null) {
                                    FragmentCreateChannel.onRemoveFragment.remove();
                                }
                                new HelperFragment(fragment).load();
                            }
                        });
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                hideProgressBar();
            }

            @Override
            public void onTimeOut() {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });
            }
        };

        new RequestClientGetRoom().clientGetRoom(roomId, RequestClientGetRoom.CreateRoomMode.requestFromOwner);
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
            txtFinishColor.set(G.context.getResources().getColor(R.color.toolbar_background));
            txtInputLinkEnable.set(true);
            fragmentCreateChannelBinding.fchTxtInputNikeName.setError("");
        } else if (isRadioButtonPublic.get()) {
            edtSetLinkEnable.set(true);
            edtSetLink.set("");
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
                        txtFinishColor.set(G.context.getResources().getColor(R.color.toolbar_background));
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
