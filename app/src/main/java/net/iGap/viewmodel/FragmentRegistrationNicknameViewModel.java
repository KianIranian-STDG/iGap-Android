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

import android.content.SharedPreferences;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.AccountManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.BindingAdapter;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.interfaces.OnInfoCountryResponse;
import net.iGap.interfaces.OnUserAvatarResponse;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserProfileSetNickNameResponse;
import net.iGap.interfaces.OnUserProfileSetRepresentative;
import net.iGap.model.AccountUser;
import net.iGap.module.CountryListComparator;
import net.iGap.module.CountryReader;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.structs.StructCountry;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestInfoCountry;
import net.iGap.request.RequestUserAvatarAdd;
import net.iGap.request.RequestUserInfo;
import net.iGap.request.RequestUserProfileSetNickname;
import net.iGap.request.RequestUserProfileSetRepresentative;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;

public class FragmentRegistrationNicknameViewModel extends ViewModel implements OnUserAvatarResponse {

    public long userId;
    private boolean existAvatar = false;
    private String pathImageUser;
    private int idAvatar;
    private String regex;
    private AvatarHandler avatarHandler;
    private ArrayList<StructCountry> structCountryArrayList = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    public MutableLiveData<Boolean> hideKeyboard = new MutableLiveData<>();
    public MutableLiveData<Integer> progressValue = new MutableLiveData<>();
    public MutableLiveData<BindingAdapter.AvatarImage> avatarImagePath = new MutableLiveData<>();
    public MutableLiveData<Boolean> showErrorName = new MutableLiveData<>();
    public MutableLiveData<Boolean> showErrorLastName = new MutableLiveData<>();
    public MutableLiveData<Boolean> showReagentPhoneNumberError = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogSelectCountry = new MutableLiveData<>();
    public MutableLiveData<Boolean> showReagentPhoneNumberStartWithZeroError = new MutableLiveData<>();
    public MutableLiveData<Long> goToMain = new MutableLiveData<>();
    public MutableLiveData<Integer> showRequestError = new MutableLiveData<>();
    public ObservableInt prgVisibility = new ObservableInt(View.GONE);
    public ObservableInt showCameraImage = new ObservableInt(View.VISIBLE);
    public ObservableField<String> reagentCountryCode = new ObservableField<>("+98");
    public ObservableInt reagentPhoneNumberMaskMaxCount = new ObservableInt(11);
    public ObservableField<String> reagentPhoneNumberMask = new ObservableField<>("###-###-####");
    public ObservableField<String> reagentPhoneNumber = new ObservableField<>("");

    public FragmentRegistrationNicknameViewModel(Long userId, AvatarHandler avatarHandler, SharedPreferences sharedPreferences) {
        this.userId = userId;
        this.avatarHandler = avatarHandler;
        this.sharedPreferences = sharedPreferences;
        //ToDo: create repository and move this to that
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
            if (realmUserInfo != null) {
                RealmAvatar.deleteAvatarWithOwnerId(G.userId);
            }
        }

        G.onUserAvatarResponse = this;

        CountryReader countryReade = new CountryReader();
        StringBuilder fileListBuilder = countryReade.readFromAssetsTextFile("country.txt", G.context);

        String list = fileListBuilder.toString();
        // Split line by line Into array
        String[] listArray = list.split("\\r?\\n");
        //Convert array
        for (String s : listArray) {
            StructCountry structCountry = new StructCountry();
            String[] listItem = s.split(";");
            structCountry.setCountryCode(listItem[0]);
            structCountry.setAbbreviation(listItem[1]);
            structCountry.setName(listItem[2]);
            if (listItem.length > 3) {
                structCountry.setPhonePattern(listItem[3]);
            } else {
                structCountry.setPhonePattern(" ");
            }
            structCountryArrayList.add(structCountry);
        }

        Collections.sort(structCountryArrayList, new CountryListComparator());

        reagentCountryCode.set("+" + sharedPreferences.getInt("callingCode", 98));
        String pattern = sharedPreferences.getString("pattern", "");
        reagentPhoneNumberMask.set((pattern != null && !pattern.equals("")) ? pattern.replace("X", "#").replace(" ", "-") : "##################");
        regex = sharedPreferences.getString("regex", "");
    }

    public ArrayList<StructCountry> getStructCountryArrayList() {
        return structCountryArrayList;
    }

    public void selectAvatarOnClick() {
        if (!existAvatar) {
            showDialog.setValue(true);
        }
    }

    public void onCountryCodeClick() {
        showDialogSelectCountry.setValue(true);
    }

    public void setCountry(StructCountry country) {
        prgVisibility.set(View.VISIBLE);
        new RequestInfoCountry().infoCountry(country.getAbbreviation(), new OnInfoCountryResponse() {
            @Override
            public void onInfoCountryResponse(final int callingCode, final String name, final String pattern, final String regexR) {
                G.handler.post(() -> {
                    prgVisibility.set(View.GONE);
                    reagentCountryCode.set("+" + callingCode);
                    if (pattern.equals("")) {
                        reagentPhoneNumberMask.set("##################");
                    } else {
                        reagentPhoneNumberMask.set(pattern.replace("X", "#").replace(" ", "-"));
                    }
                    regex = regexR;
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                //empty
                prgVisibility.set(View.GONE);
            }
        });
        reagentPhoneNumber.set("");
    }

    public void onTextChanged(String reagentPhoneNumber) {
        if (reagentPhoneNumber.startsWith("0")) {
            this.reagentPhoneNumber.set("");
            showReagentPhoneNumberStartWithZeroError.setValue(true);
        }
    }

    public void OnClickBtnLetsGo(String name, String lastName) {
        try (Realm realm = Realm.getDefaultInstance()) {
            if (name.length() > 0) {
                showErrorName.setValue(false);
                if (lastName.length() > 0) {
                    showErrorLastName.setValue(false);
                    if (reagentPhoneNumber.get().isEmpty() || isValidReagentPhoneNumber()) {
                        showReagentPhoneNumberError.setValue(false);
                        hideKeyboard.setValue(true);
                        prgVisibility.set(View.VISIBLE);
                        setNickName(name, lastName, reagentPhoneNumber.get().isEmpty());
                    } else {
                        showReagentPhoneNumberError.setValue(true);
                    }
                } else {
                    showErrorLastName.setValue(true);
                }
            } else {
                showErrorName.setValue(true);
            }
        }
    }

    private boolean isValidReagentPhoneNumber() {
        return reagentPhoneNumber.get().length() > 0 && regex.equals("") || (!regex.equals("") && reagentPhoneNumber.get().replace("-", "").matches(regex));
    }

    private void setNickName(String name, String lastName, boolean isReagentIsEmpty) {
        new RequestUserProfileSetNickname().userProfileNickName(name + " " + lastName, new OnUserProfileSetNickNameResponse() {
            @Override
            public void onUserProfileNickNameResponse(final String nickName, String initials) {
                if (isReagentIsEmpty) {
                    getUserInfo();
                } else {
                    setReagent();
                }
            }

            @Override
            public void onUserProfileNickNameError(int majorCode, int minorCode) {
                G.handler.post(() -> prgVisibility.set(View.GONE));
            }

            @Override
            public void onUserProfileNickNameTimeOut() {
                G.handler.post(() -> prgVisibility.set(View.GONE));
            }
        });
    }

    private void setReagent() {
        new RequestUserProfileSetRepresentative().userProfileSetRepresentative(
                reagentCountryCode.get().replace("+", "") + reagentPhoneNumber.get().replace("-", ""),
                new OnUserProfileSetRepresentative() {
                    @Override
                    public void onSetRepresentative(String phone) {
                        try (Realm realm = Realm.getDefaultInstance()) {
                            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                            RealmUserInfo.setRepresentPhoneNumber(realm, realmUserInfo, phone);
                        }
                        getUserInfo();
                    }

                    @Override
                    public void onErrorSetRepresentative(int majorCode, int minorCode) {
                        G.handler.post(() -> {
                            prgVisibility.set(View.GONE);
                            if (majorCode == 10177  && minorCode == 2) {
                                HelperError.showSnackMessage(G.context.getString(R.string.referral_error_yourself), false);
                            }
                        });
                    }
                });
    }

    private void getUserInfo() {
        //ToDo:change it and user register repository
        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {
                G.handler.post(() -> {
                    try (Realm realm = Realm.getDefaultInstance()) {
                        realm.executeTransaction(realm1 -> RealmUserInfo.putOrUpdate(realm1, user));
                    }
                    AccountManager.getInstance().addAccount(new AccountUser(
                            user.getId(),
                            null,
                            user.getDisplayName(),
                            null,
                            user.getInitials(),
                            user.getColor(),
                            0
                    ));
                    G.displayName = user.getDisplayName();
                    prgVisibility.set(View.GONE);
                    goToMain.setValue(userId);
                });
            }

            @Override
            public void onUserInfoTimeOut() {
                prgVisibility.set(View.GONE);
                showRequestError.postValue(R.string.error);
            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {
                prgVisibility.set(View.GONE);
                showRequestError.setValue(R.string.error);
            }
        };
        new RequestUserInfo().userInfo(G.userId);
    }

    @Override
    public void onAvatarAdd(final ProtoGlobal.Avatar avatar) {
        avatarHandler.avatarAdd(G.userId, pathImageUser, avatar, avatarPath -> G.handler.post(() -> {
            existAvatar = true;
            prgVisibility.set(View.GONE);
            showCameraImage.set(View.GONE);
            avatarImagePath.setValue(new BindingAdapter.AvatarImage(avatarPath, false, null));
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

    public void uploadAvatar(String path) {
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
    }
}
