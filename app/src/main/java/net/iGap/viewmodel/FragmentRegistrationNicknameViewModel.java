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

import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentRegistrationNicknameBinding;
import net.iGap.fragments.ReagentFragment;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserProfileSetNickNameResponse;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserInfo;
import net.iGap.request.RequestUserProfileSetNickname;

import io.realm.Realm;

public class FragmentRegistrationNicknameViewModel {

    public final static String ARG_USER_ID = "arg_user_id";
    public long userId = 0;
    public ObservableField<String> callBackEdtNikeName = new ObservableField<>("");
    public ObservableField<String> edtNikeNameHint = new ObservableField<>(G.context.getResources().getString(R.string.pu_nikname_profileUser));
    public ObservableInt prgVisibility = new ObservableInt(View.GONE);
    public ObservableInt edtNikeNameColor = new ObservableInt(G.context.getResources().getColor(R.color.black_register));
    public ObservableInt lineBelowEditTextColor = new ObservableInt(G.context.getResources().getColor(R.color.border_editText));
    private FragmentRegistrationNicknameBinding fragmentRegistrationNicknameBinding;
    private TextInputLayout txtInputNickName;


    public FragmentRegistrationNicknameViewModel(Bundle arguments, FragmentRegistrationNicknameBinding fragmentRegistrationNicknameBinding) {

        this.fragmentRegistrationNicknameBinding = fragmentRegistrationNicknameBinding;
        getInfo(arguments);

    }

    public void watcher(CharSequence s) {

        txtInputNickName.setErrorEnabled(true);
        txtInputNickName.setError("");
        txtInputNickName.setHintTextAppearance(R.style.remove_error_appearance);
        edtNikeNameColor.set(G.context.getResources().getColor(R.color.border_editText));
        lineBelowEditTextColor.set(G.context.getResources().getColor(android.R.color.black));


    }

    public void OnClickBtnLetsGo(View v) {

        Realm realm = Realm.getDefaultInstance();
        final String nickName = callBackEdtNikeName.get();
        if (!nickName.equals("")) {
//            //showProgressBar();
            G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    setNickName();
                }
            });

            // TODO: 4/14/19 add Representer fragment


        } else {
            G.handler.post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void run() {
                    txtInputNickName.setErrorEnabled(true);
                    txtInputNickName.setError(G.fragmentActivity.getResources().getString(R.string.Toast_Write_NickName));
                    txtInputNickName.setHintTextAppearance(R.style.error_appearance);
                    edtNikeNameColor.set(G.context.getResources().getColor(R.color.red));
                    lineBelowEditTextColor.set(G.context.getResources().getColor(R.color.red));
                }
            });
        }

        realm.close();

    }


    private void getInfo(Bundle arguments) {
        if (arguments != null) {
            userId = (int) arguments.getLong(ARG_USER_ID, -1);
            delete();
        }
        txtInputNickName = fragmentRegistrationNicknameBinding.puTxtInputNikeName;
    }


    private void setNickName() {
        G.onUserProfileSetNickNameResponse = new OnUserProfileSetNickNameResponse() {
            @Override
            public void onUserProfileNickNameResponse(final String nickName, String initials) {
                getUserInfo();
            }

            @Override
            public void onUserProfileNickNameError(int majorCode, int minorCode) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });
            }

            @Override
            public void onUserProfileNickNameTimeOut() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });
            }
        };
        new RequestUserProfileSetNickname().userProfileNickName(callBackEdtNikeName.get());
    }

    private void getUserInfo() {
        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                G.displayName = user.getDisplayName();

                                RealmUserInfo.putOrUpdate(realm, user);

                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
//                                        G.onUserInfoResponse = null;
                                        hideProgressBar();
//                                        Intent intent = new Intent(context, ActivityMain.class);
//                                        intent.putExtra(ARG_USER_ID, user.getId());
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        G.context.startActivity(intent);
//                                        G.fragmentActivity.finish();

                                        ReagentFragment reagentFragment = ReagentFragment.newInstance(true);
                                        FragmentManager fragmentManager = G.fragmentActivity.getSupportFragmentManager();
                                        reagentFragment.userId = user.getId();
                                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                                        transaction.replace(R.id.ar_layout_root, reagentFragment);
                                        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left);
                                        transaction.commitAllowingStateLoss();
                                    }
                                });
                            }
                        });
                        realm.close();
                    }
                });
            }

            @Override
            public void onUserInfoTimeOut() {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });
            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });
            }
        };
        new RequestUserInfo().userInfo(G.userId);
    }

    private void delete() {
        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        if (realmUserInfo != null) {
            RealmAvatar.deleteAvatarWithOwnerId(G.userId);
        }
        realm.close();
    }

    public void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                prgVisibility.set(View.VISIBLE);
                G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    public void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                prgVisibility.set(View.GONE);
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
        });
    }

}
