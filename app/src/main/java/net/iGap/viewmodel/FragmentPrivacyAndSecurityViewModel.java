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

import android.content.SharedPreferences;
import android.databinding.ObservableField;
import android.util.Log;
import android.view.View;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import java.util.ArrayList;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentActiveSessions;
import net.iGap.fragments.FragmentBlockedUser;
import net.iGap.fragments.FragmentPassCode;
import net.iGap.fragments.FragmentSecurity;
import net.iGap.helper.HelperFragment;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.structs.StructSessions;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmPrivacy;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserProfileSetSelfRemove;

import static android.content.Context.MODE_PRIVATE;

public class FragmentPrivacyAndSecurityViewModel {

    private Realm realm;
    private RealmUserInfo realmUserInfo;
    private RealmPrivacy realmPrivacy;
    private RealmChangeListener<RealmModel> userInfoListener;
    private RealmChangeListener<RealmModel> privacyListener;
    private int poWhoCan;

    int poSelfRemove;
    private SharedPreferences sharedPreferences;
    private int poRbDialogSelfDestruction = 0;
    private int selfRemove;
    private ArrayList<StructSessions> itemSessionsgetActivelist = new ArrayList<StructSessions>();

    public ObservableField<String> callbackSeeMyAvatar = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.everybody));
    public ObservableField<String> callbackInviteChannel = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.everybody));
    public ObservableField<String> callbackInviteGroup = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.everybody));
    public ObservableField<String> callbackVoiceCall = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.everybody));
    public ObservableField<String> callbackSeeLastSeen = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.everybody));
    public ObservableField<String> callbackSelfDestruction = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.everybody));

    public FragmentPrivacyAndSecurityViewModel() {

        realm = Realm.getDefaultInstance();
        getInfo();

    }

    public void onClickBlocked(View view) {
        new HelperFragment(new FragmentBlockedUser()).setReplace(false).load();
    }

    public void onClickSeeMyAvatar(View view) {
        openDialogWhoCan(ProtoGlobal.PrivacyType.AVATAR, poWhoCan, R.string.title_who_can_see_my_avatar);
    }

    public void onClickInviteChannel(View view) {
        openDialogWhoCan(ProtoGlobal.PrivacyType.CHANNEL_INVITE, poWhoCan, R.string.title_who_can_invite_you_to_channel_s);
    }

    public void onClickInviteGroup(View view) {
        openDialogWhoCan(ProtoGlobal.PrivacyType.GROUP_INVITE, poWhoCan, R.string.title_who_can_invite_you_to_group_s);
    }

    public void onClickVoiceCall(View view) {
        openDialogWhoCan(ProtoGlobal.PrivacyType.VOICE_CALLING, poWhoCan, R.string.title_who_is_allowed_to_call);
    }

    public void onClickSeeLastSeen(View view) {
        openDialogWhoCan(ProtoGlobal.PrivacyType.USER_STATUS, poWhoCan, R.string.title_Last_Seen);
    }

    public void onClickPassCode(View view) {
        new HelperFragment(new FragmentPassCode()).setReplace(false).load();
    }

    public void onClickTwoStepVerification(View view) {
        new HelperFragment(new FragmentSecurity()).setReplace(false).load();
    }

    public void onClickActivitySessions(View view) {
        new HelperFragment(new FragmentActiveSessions()).setReplace(false).load();
    }

    public void onClickSelfDestruction(View view) {
        selfDestructs();
    }
    //===============================================================================
    //====================================Methods====================================
    //===============================================================================


    private void getInfo() {
        realmPrivacy = realm.where(RealmPrivacy.class).findFirst();
        realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        RealmPrivacy.getUpdatePrivacyFromServer();
        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        poSelfRemove = sharedPreferences.getInt(SHP_SETTING.KEY_POSITION_SELF_REMOVE, 2);

        updatePrivacyUI(realmPrivacy);

        userInfoListener = new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel element) {

                Log.i("CCCCCCCCCCCCC", "0 onChange: ");
                if (((RealmUserInfo) element).isValid()) {
                    selfRemove = ((RealmUserInfo) element).getSelfRemove();
                    setTextSelfDestructs();
                }
            }
        };

        privacyListener = new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel element) {
                Log.i("CCCCCCCCCCCCC", "1 onChange: ");
                updatePrivacyUI((RealmPrivacy) element);
            }
        };
    }

    private void openDialogWhoCan(final ProtoGlobal.PrivacyType privacyType, int position, int title) {

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(title)).titleGravity(GravityEnum.START).titleColor(G.context.getResources().getColor(android.R.color.black)).items(R.array.privacy_setting_array).itemsCallbackSingleChoice(position, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                switch (which) {
                    case 0: {
                        RealmPrivacy.sendUpdatePrivacyToServer(privacyType, ProtoGlobal.PrivacyLevel.ALLOW_ALL);

                        break;
                    }
                    case 1: {
                        RealmPrivacy.sendUpdatePrivacyToServer(privacyType, ProtoGlobal.PrivacyLevel.ALLOW_CONTACTS);
                        break;
                    }
                    case 2: {
                        RealmPrivacy.sendUpdatePrivacyToServer(privacyType, ProtoGlobal.PrivacyLevel.DENY_ALL);
                        break;
                    }
                }
                return false;
            }
        }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).show();
    }

    private String getStringFromEnumString(String str) {

        if (str == null || str.length() == 0) {
            poWhoCan = 0;
            return G.fragmentActivity.getResources().getString(R.string.everybody);
        }

        int resString = 0;

        if (str.equals(ProtoGlobal.PrivacyLevel.ALLOW_ALL.toString())) {
            poWhoCan = 0;
            resString = R.string.everybody;
        } else if (str.equals(ProtoGlobal.PrivacyLevel.ALLOW_CONTACTS.toString())) {
            poWhoCan = 1;
            resString = R.string.my_contacts;
        } else {
            poWhoCan = 2;
            resString = R.string.no_body;
        }

        return G.fragmentActivity.getResources().getString(resString);
    }

    private void updatePrivacyUI(RealmPrivacy realmPrivacy) {
        if (realmPrivacy.isValid()) {

            callbackSeeMyAvatar.set(getStringFromEnumString(realmPrivacy.getWhoCanSeeMyAvatar()));
            callbackInviteChannel.set(getStringFromEnumString(realmPrivacy.getWhoCanInviteMeToChannel()));
            callbackInviteGroup.set(getStringFromEnumString(realmPrivacy.getWhoCanInviteMeToGroup()));
            callbackSeeLastSeen.set(getStringFromEnumString(realmPrivacy.getWhoCanSeeMyLastSeen()));
            callbackVoiceCall.set(getStringFromEnumString(realmPrivacy.getWhoCanVoiceCallToMe()));
        }
    }

    private void selfDestructs() {

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.self_destructs)).titleGravity(GravityEnum.START).titleColor(G.context.getResources().getColor(android.R.color.black)).items(R.array.account_self_destruct).itemsCallbackSingleChoice(poSelfRemove, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                switch (which) {
                    case 0: {
                        callbackSelfDestruction.set(G.fragmentActivity.getResources().getString(R.string.month_1));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(1);
                        break;
                    }
                    case 1: {
                        callbackSelfDestruction.set(G.fragmentActivity.getResources().getString(R.string.month_3));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(3);
                        break;
                    }
                    case 2: {

                        callbackSelfDestruction.set(G.fragmentActivity.getResources().getString(R.string.month_6));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(6);
                        break;
                    }
                    case 3: {

                        callbackSelfDestruction.set(G.fragmentActivity.getResources().getString(R.string.year_1));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(12);
                        break;
                    }
                }
                return false;
            }
        }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).show();
    }

    private void setTextSelfDestructs() throws IllegalStateException {
        if (selfRemove != 0) {
            switch (selfRemove) {
                case 1:
                    callbackSelfDestruction.set(G.fragmentActivity.getResources().getString(R.string.month_1));
                    poSelfRemove = 0;
                    break;
                case 3:
                    callbackSelfDestruction.set(G.fragmentActivity.getResources().getString(R.string.month_3));
                    poSelfRemove = 1;
                    break;
                case 6:
                    callbackSelfDestruction.set(G.fragmentActivity.getResources().getString(R.string.month_6));
                    poSelfRemove = 2;
                    break;
                case 12:
                    callbackSelfDestruction.set(G.fragmentActivity.getResources().getString(R.string.year_1));
                    poSelfRemove = 3;
                    break;
            }
        } else {
            callbackSelfDestruction.set(G.fragmentActivity.getResources().getString(R.string.month_6));
        }
    }


    public void onPause() {

        if (realmUserInfo != null) {
            realmUserInfo.removeAllChangeListeners();
        }

        if (realmPrivacy != null) {
            realmPrivacy.removeAllChangeListeners();
        }

    }


    public void onResume() {

        if (realmUserInfo != null) {
            if (userInfoListener != null) {
                Log.i("CCCCCCCCCCCCC", "2 onChange: ");
                realmUserInfo.addChangeListener(userInfoListener);
            }
            selfRemove = realmUserInfo.getSelfRemove();
            setTextSelfDestructs();
        }

        if (realmPrivacy != null) {
            if (privacyListener != null) {
                realmPrivacy.addChangeListener(privacyListener);
            }
        }
        updatePrivacyUI(realmPrivacy);
    }


    public void onDetach() {
        realm.close();
    }


}
