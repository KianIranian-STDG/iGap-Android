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

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableInt;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.fragments.ReagentFragment;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperUploadFile;
import net.iGap.interfaces.OnUserAvatarResponse;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserProfileSetNickNameResponse;
import net.iGap.module.FileUploadStructure;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserAvatarAdd;
import net.iGap.request.RequestUserInfo;
import net.iGap.request.RequestUserProfileSetNickname;

import io.realm.Realm;

public class FragmentRegistrationNicknameViewModel implements OnUserAvatarResponse {

    public long userId;
    private boolean existAvatar = false;
    private String pathImageUser;
    private int idAvatar;
    public MutableLiveData<Integer> progressValue = new MutableLiveData<>();
    public MutableLiveData<String> avatarImagePath = new MutableLiveData<>();
    public MutableLiveData<Boolean> showErrorName = new MutableLiveData<>();
    public MutableLiveData<Boolean> showErrorLastName = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialog = new MutableLiveData<>();
    public ObservableInt prgVisibility = new ObservableInt(View.GONE);

    public FragmentRegistrationNicknameViewModel(Long userId) {
        this.userId = userId;
        //ToDo: create repository and move this to that
        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        if (realmUserInfo != null) {
            RealmAvatar.deleteAvatarWithOwnerId(G.userId);
        }
        realm.close();
        FragmentEditImage.completeEditImage = (path, message, textImageList) -> {
            pathImageUser = path;
            int lastUploadedAvatarId = idAvatar + 1;
            prgVisibility.set(View.VISIBLE);
            HelperUploadFile.startUploadTaskAvatar(pathImageUser, lastUploadedAvatarId, new HelperUploadFile.UpdateListener() {
                @Override
                public void OnProgress(int progress, FileUploadStructure struct) {
                    if (progress < 100) {
                        G.handler.post(() -> progressValue.setValue(progress));
                    } else {
                        new RequestUserAvatarAdd().userAddAvatar(struct.token);
                    }
                }

                @Override
                public void OnError() {
                    prgVisibility.set(View.GONE);
                }
            });
        };
        G.onUserAvatarResponse = this;
    }

    public void selectAvatarOnClick() {
        if (!existAvatar) {
            showDialog.setValue(true);
        }
    }

    public void OnClickBtnLetsGo(String name, String lastName) {
        Realm realm = Realm.getDefaultInstance();
        if (name.length() > 0) {
            showErrorName.setValue(false);
            if (lastName.length() > 0) {
                showErrorLastName.setValue(false);
                G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                realm.executeTransaction(realm1 -> setNickName(name, lastName));
            } else {
                showErrorLastName.setValue(true);
            }
            // TODO: 4/14/19 add Representer fragment
        } else {
            showErrorName.setValue(true);
        }
        realm.close();
    }


    private void setNickName(String name, String lastName) {
        G.onUserProfileSetNickNameResponse = new OnUserProfileSetNickNameResponse() {
            @Override
            public void onUserProfileNickNameResponse(final String nickName, String initials) {
                getUserInfo();
            }

            @Override
            public void onUserProfileNickNameError(int majorCode, int minorCode) {
                G.handler.post(() -> prgVisibility.set(View.GONE));
            }

            @Override
            public void onUserProfileNickNameTimeOut() {
                G.handler.post(() -> prgVisibility.set(View.GONE));
            }
        };
        new RequestUserProfileSetNickname().userProfileNickName(name);
    }

    private void getUserInfo() {
        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {
                G.handler.post(() -> {
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(realm1 -> {
                        G.displayName = user.getDisplayName();
                        RealmUserInfo.putOrUpdate(realm1, user);
                        G.handler.post(() -> {
                            prgVisibility.set(View.GONE);
                            ReagentFragment reagentFragment = ReagentFragment.newInstance(true);
                            FragmentManager fragmentManager = G.fragmentActivity.getSupportFragmentManager();
                            reagentFragment.userId = user.getId();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.replace(R.id.ar_layout_root, reagentFragment);
                            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left);
                            transaction.commitAllowingStateLoss();
                        });
                    });
                    realm.close();
                });
            }

            @Override
            public void onUserInfoTimeOut() {
                G.handler.post(() -> prgVisibility.set(View.GONE));
            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {
                G.handler.post(() -> prgVisibility.set(View.GONE));
            }
        };
        new RequestUserInfo().userInfo(G.userId);
    }

    @Override
    public void onAvatarAdd(final ProtoGlobal.Avatar avatar) {
        HelperAvatar.avatarAdd(G.userId, pathImageUser, avatar, avatarPath -> G.handler.post(() -> {
            existAvatar = true;
            prgVisibility.set(View.GONE);
            avatarImagePath.setValue(avatarPath);
        }));
    }

    @Override
    public void onAvatarAddTimeOut() {
        prgVisibility.set(View.GONE);
    }

    @Override
    public void onAvatarError() {
        prgVisibility.set(View.GONE);
    }

}
