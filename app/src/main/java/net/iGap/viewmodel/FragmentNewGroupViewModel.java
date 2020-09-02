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

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperTracker;
import net.iGap.module.AppUtils;
import net.iGap.observers.interfaces.OnChatConvertToGroup;
import net.iGap.observers.interfaces.OnClientGetRoomResponse;
import net.iGap.proto.ProtoClientGetRoom;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.IG_Objects;
import net.iGap.request.RequestChannelAvatarAdd;
import net.iGap.request.RequestChatConvertToGroup;
import net.iGap.request.RequestClientGetRoom;
import net.iGap.request.RequestGroupAvatarAdd;

import static net.iGap.module.MusicPlayer.roomId;

public class FragmentNewGroupViewModel extends BaseViewModel {

    public static String prefix = "NewGroup";
    public static long avatarId = 0;
    public static ProtoGlobal.Room.Type type;
    public static String mCurrentPhotoPath;
    public Uri uriIntent;
    public String token;
    public boolean existAvatar = false;
    public String mInviteLink;
    public boolean isChannel = false;
    public MutableLiveData<String> titleToolbar = new MutableLiveData<>();
    public ObservableField<String> txtInputName = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.group_name) + " " + G.fragmentActivity.getResources().getString(R.string.mandatory));
    public ObservableField<String> edtSetNewGroup = new ObservableField<>("");
    public ObservableField<String> edtDescription = new ObservableField<>("");
    public ObservableField<String> txtDescriptionHint = new ObservableField<>("");
    public ObservableField<Integer> prgWaiting = new ObservableField<>(View.GONE);
    public ObservableField<Integer> edtDescriptionLines = new ObservableField<>(1);
    public ObservableField<Integer> edtDescriptionMaxLines = new ObservableField<>(2);
    public ObservableField<Integer> edtDescriptionImeOptions = new ObservableField<>(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
    public ObservableField<Integer> edtDescriptionInputType = new ObservableField<>(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    public ObservableField<Boolean> nextStepEnable = new ObservableField<>(true);
    private long groomId = 0;
    public MutableLiveData<Long> createdRoomId = new MutableLiveData<>();
    public MutableLiveData<ContactGroupFragmentModel> goToContactGroupPage = new MutableLiveData<>();
    public MutableLiveData<CreateChannelModel> goToCreateChannelPage = new MutableLiveData<>();


    public FragmentNewGroupViewModel(Bundle arguments) {
        getInfo(arguments);
    }

    public void onClickNextStep(View view) {

        if (edtSetNewGroup.get().length() > 0) {
            prgWaiting.set(View.VISIBLE);
            G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            if (prefix.equals("NewChanel")) {
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                isChannel = true;

                IG_Objects.Req_CreateChannel req = new IG_Objects.Req_CreateChannel();
                req.name = edtSetNewGroup.get();
                req.description = edtDescription.get();

                getRequestManager().sendRequest(req, (response, error) -> {
                    if (error == null) {
                        IG_Objects.Res_CreateChannel res = (IG_Objects.Res_CreateChannel) response;
                        getChannelRoom(res.roomId);

                        HelperTracker.sendTracker(HelperTracker.TRACKER_CREATE_CHANNEL);
                    } else {
                        IG_Objects.Error err = (IG_Objects.Error) error;

                        hideProgressBar();
                        if (err.major == 479) {
                            G.runOnUiThread(this::ShowDialogLimitCreate);
                        }
                    }
                });

            } else if (prefix.equals("ConvertToGroup")) {
                isChannel = false;
                chatToGroup();
            } else {
                isChannel = false;

                IG_Objects.Req_CreateGroup req = new IG_Objects.Req_CreateGroup();
                req.name = edtSetNewGroup.get();
                req.description = edtDescription.get();

                getRequestManager().sendRequest(req, (response, error) -> {
                    if (error == null) {
                        IG_Objects.Res_CreateGroup res = (IG_Objects.Res_CreateGroup) response;

                        hideProgressBar();

                        roomId = res.roomId;
                        getRoom(res.roomId, ProtoGlobal.Room.Type.GROUP, true);
                        HelperTracker.sendTracker(HelperTracker.TRACKER_CREATE_GROUP);
                    } else {
                        IG_Objects.Error err = (IG_Objects.Error) error;

                        G.runOnUiThread(() -> {
                            hideProgressBar();
                            if (err.major == 380) {
                                ShowDialogLimitCreate();
                            }
                        });
                    }
                });
            }
        } else {
            if (prefix.equals("NewChanel")) {
                Toast.makeText(G.context, R.string.please_enter_channel_name, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(G.context, R.string.please_enter_group_name, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void onClickCancel(View view) {
        AppUtils.closeKeyboard(view);
        if (G.IMAGE_NEW_GROUP != null && G.IMAGE_NEW_GROUP.exists()) {
            G.IMAGE_NEW_GROUP.delete();
        } else if (G.IMAGE_NEW_CHANEL != null) {
            G.IMAGE_NEW_CHANEL.delete();
        }
        try {
            G.fragmentActivity.onBackPressed();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void getInfo(Bundle bundle) {
        if (bundle != null) { // get a list of image
            prefix = bundle.getString("TYPE");
            if (bundle.getLong("ROOMID") != 0) {
                groomId = bundle.getLong("ROOMID");
            }
        }

        if (prefix.equals("NewChanel")) {
            titleToolbar.setValue(G.fragmentActivity.getResources().getString(R.string.New_Chanel));
        } else if (prefix.equals("ConvertToGroup")) {
            titleToolbar.setValue(G.fragmentActivity.getResources().getString(R.string.chat_to_group));
        } else {
            titleToolbar.setValue(G.fragmentActivity.getResources().getString(R.string.new_group));
        }

        switch (prefix) {
            case "NewChanel":
                txtInputName.set(G.fragmentActivity.getResources().getString(R.string.channel_name) + " " + G.fragmentActivity.getResources().getString(R.string.mandatory));
                txtDescriptionHint.set(G.fragmentActivity.getResources().getString(R.string.new_channel_hint));
                break;
            case "ConvertToGroup":
                txtInputName.set(G.fragmentActivity.getResources().getString(R.string.group_name) + " " + G.fragmentActivity.getResources().getString(R.string.mandatory));
                txtDescriptionHint.set(G.fragmentActivity.getResources().getString(R.string.new_group_hint));
                break;
            default:
                txtInputName.set(G.fragmentActivity.getResources().getString(R.string.group_name) + " " + G.fragmentActivity.getResources().getString(R.string.mandatory));
                txtDescriptionHint.set(G.fragmentActivity.getResources().getString(R.string.new_group_hint));
                break;
        }


        edtDescriptionImeOptions.set(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        edtDescriptionInputType.set(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        edtDescriptionLines.set(1);
        edtDescriptionMaxLines.set(2);


    }

    private void getChannelRoom(final long roomId) {
        G.onClientGetRoomResponse = new OnClientGetRoomResponse() {
            @Override
            public void onClientGetRoomResponse(final ProtoGlobal.Room room, ProtoClientGetRoom.ClientGetRoomResponse.Builder builder, RequestClientGetRoom.IdentityClientGetRoom identity) {
                if (identity.createRoomMode != RequestClientGetRoom.CreateRoomMode.requestFromOwner) {
                    return;
                }
                G.onClientGetRoomResponse = null;

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mInviteLink = room.getChannelRoomExtra().getPrivateExtra().getInviteLink();
                        if (existAvatar) {
                            new RequestChannelAvatarAdd().channelAvatarAdd(room.getId(), token);
                        } else {
                            hideProgressBar();
                            goToCreateChannelPage.setValue(new CreateChannelModel(room.getId(), mInviteLink, token));
                        }
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                G.onClientGetRoomResponse = null;
            }

            @Override
            public void onTimeOut() {
                G.onClientGetRoomResponse = null;
            }
        };
        new RequestClientGetRoom().clientGetRoom(roomId, RequestClientGetRoom.CreateRoomMode.requestFromOwner);
    }

    private void ShowDialogLimitCreate() {
        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.title_limit_Create_Group).content(R.string.text_limit_Create_Group).positiveText(R.string.B_ok).show();
    }

    private void chatToGroup() {
        G.onChatConvertToGroup = new OnChatConvertToGroup() {
            @Override
            public void onChatConvertToGroup(long roomId, final String name, final String description, ProtoGlobal.GroupRoom.Role role) {
                getRoom(roomId, ProtoGlobal.Room.Type.GROUP, false);
            }

            @Override
            public void Error(int majorCode, int minorCode) {

                hideProgressBar();
            }

            @Override
            public void timeOut() {
                hideProgressBar();
            }
        };

        new RequestChatConvertToGroup().chatConvertToGroup(groomId, edtSetNewGroup.get(), edtDescription.get());
    }


    private void getRoom(final long roomId, final ProtoGlobal.Room.Type typeCreate, boolean isGroup) {

        G.onClientGetRoomResponse = new OnClientGetRoomResponse() {
            @Override
            public void onClientGetRoomResponse(final ProtoGlobal.Room room, ProtoClientGetRoom.ClientGetRoomResponse.Builder builder, RequestClientGetRoom.IdentityClientGetRoom identity) {

                if (identity.createRoomMode != RequestClientGetRoom.CreateRoomMode.requestFromOwner) {
                    return;
                }

                try {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (existAvatar) {
                                showProgressBar();
                                if (room.getType() == ProtoGlobal.Room.Type.GROUP) {
                                    new RequestGroupAvatarAdd().groupAvatarAdd(roomId, token);
                                } else {
                                    new RequestChannelAvatarAdd().channelAvatarAdd(roomId, token);
                                }
                            } else {
                                hideProgressBar();
                                if (isGroup) {
                                    createdRoomId.postValue(roomId);
                                } else {
                                    goToContactGroupPage.setValue(new ContactGroupFragmentModel(roomId, room.getGroupRoomExtra().getParticipantsCountLimitLabel(), typeCreate.toString(), true));
                                }
                            }
                        }
                    });
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

                hideProgressBar();
            }
        };

        new RequestClientGetRoom().clientGetRoom(roomId, RequestClientGetRoom.CreateRoomMode.requestFromOwner);
    }

    //=======================result for picture

    public void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                nextStepEnable.set(false);
                prgWaiting.set(View.VISIBLE);
                if (G.fragmentActivity != null)
                    G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    public void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                nextStepEnable.set(true);
                prgWaiting.set(View.GONE);
                if (G.fragmentActivity != null)
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });


    }

    public class ContactGroupFragmentModel {
        private Long roomId;
        private String limit;
        private String type;
        private boolean newRoom;

        ContactGroupFragmentModel(Long roomId, String limit, String type, boolean newRoom) {
            this.roomId = roomId;
            this.limit = limit;
            this.type = type;
            this.newRoom = newRoom;
        }

        public Long getRoomId() {
            return roomId;
        }

        public String getLimit() {
            return limit;
        }

        public String getType() {
            return type;
        }

        public boolean isNewRoom() {
            return newRoom;
        }
    }

    public class CreateChannelModel {
        private long roomId;
        private String inviteLink;
        private String token;

        public CreateChannelModel(long roomId, String inviteLink, String token) {
            this.roomId = roomId;
            this.inviteLink = inviteLink;
            this.token = token;
        }

        public long getRoomId() {
            return roomId;
        }

        public String getInviteLink() {
            return inviteLink;
        }

        public String getToken() {
            return token;
        }
    }
}
