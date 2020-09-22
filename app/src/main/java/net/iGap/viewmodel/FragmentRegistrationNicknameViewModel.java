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

import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.BindingAdapter;
import net.iGap.helper.HelperError;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.upload.OnUploadListener;
import net.iGap.model.LocationModel;
import net.iGap.model.repository.ErrorWithWaitTime;
import net.iGap.model.repository.RegisterRepository;
import net.iGap.module.CountryListComparator;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.structs.StructCountry;
import net.iGap.module.upload.Uploader;
import net.iGap.observers.interfaces.OnUserAvatarResponse;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestUserAvatarAdd;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


public class FragmentRegistrationNicknameViewModel extends ViewModel implements OnUserAvatarResponse {

    public long userId;
    private boolean existAvatar = true;
    private String pathImageUser;
    private int idAvatar;

    private AvatarHandler avatarHandler;
    private ArrayList<StructCountry> structCountryArrayList = new ArrayList<>();

    private RegisterRepository repository;

    public MutableLiveData<Boolean> hideKeyboard = new MutableLiveData<>();
    public MutableLiveData<Integer> progressValue = new MutableLiveData<>();
    public ObservableField<BindingAdapter.AvatarImage> avatarImagePath = new ObservableField<>();
    public MutableLiveData<Boolean> showReagentPhoneNumberError = new MutableLiveData<>();
    public SingleLiveEvent<Boolean> showDialog = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> showDialogSelectCountry = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> showReagentPhoneNumberStartWithZeroError = new MutableLiveData<>();
    public ObservableInt prgVisibility = new ObservableInt(View.GONE);
    public ObservableInt showCameraImage = new ObservableInt(View.VISIBLE);
    public ObservableField<String> reagentCountryCode = new ObservableField<>("+98");
    public ObservableInt reagentPhoneNumberMaskMaxCount = new ObservableInt(11);
    public ObservableField<String> reagentPhoneNumberMask = new ObservableField<>("###-###-####");
    public ObservableField<String> reagentPhoneNumber = new ObservableField<>("");
    public ObservableInt showErrorName = new ObservableInt();
    public ObservableInt showErrorLastName = new ObservableInt();

    public FragmentRegistrationNicknameViewModel(AvatarHandler avatarHandler, @NotNull StringBuilder stringBuilder) {
        repository = RegisterRepository.getInstance();
        this.avatarHandler = avatarHandler;

        repository.removeUserAvatar();

        G.onUserAvatarResponse = this;

        String list = stringBuilder.toString();
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

        if (repository.getCallingCode() != 0)
            reagentCountryCode.set("+" + repository.getCallingCode());
        if (repository.getPattern() != "")
            reagentPhoneNumberMask.set(repository.getPattern().replace("X", "#").replace(" ", "-"));
    }

    public ArrayList<StructCountry> getStructCountryArrayList() {
        return structCountryArrayList;
    }

    public void selectAvatarOnClick() {
        if (existAvatar) {
            showDialog.setValue(true);
        } else {
            Log.wtf(this.getClass().getName(), "selectAvatarOnClick: else");
        }
    }

    public void onCountryCodeClick() {
        showDialogSelectCountry.setValue(true);
    }

    public void setCountry(@NotNull StructCountry country) {
        prgVisibility.set(View.VISIBLE);
        repository.getCountryInfo(country.getAbbreviation(), new RegisterRepository.RepositoryCallback<LocationModel>() {
            @Override
            public void onSuccess(LocationModel data) {
                prgVisibility.set(View.GONE);
                reagentCountryCode.set("+" + data.getCountryCode());
                if (data.getPhoneMask().equals("")) {
                    reagentPhoneNumberMask.set("##################");
                } else {
                    reagentPhoneNumberMask.set(data.getPhoneMask().replace("X", "#").replace(" ", "-"));
                }
            }

            @Override
            public void onError() {
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

    public void OnClickBtnLetsGo(@NotNull String name, String lastName) {
        if (name.length() > 0) {
            showErrorName.set(0);
            if (lastName.length() > 0) {
                showErrorLastName.set(0);
                if (reagentPhoneNumber.get().isEmpty() || reagentPhoneNumber.get().length() > 0 && repository.getRegex().equals("") || (!repository.getRegex().equals("") && reagentPhoneNumber.get().replace("-", "").matches(repository.getRegex()))) {
                    showReagentPhoneNumberError.setValue(false);
                    hideKeyboard.setValue(true);
                    prgVisibility.set(View.VISIBLE);
                    repository.setNickName(
                            name,
                            lastName,
                            reagentCountryCode.get().replace("+", ""),
                            reagentPhoneNumber.get().replace("-", ""),
                            new RegisterRepository.RepositoryCallbackWithError<ErrorWithWaitTime>() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(ErrorWithWaitTime error) {// if error is not null set reagent request is have error
                                    if (error != null) {
                                        if (error.getMajorCode() == 10177 && error.getMinorCode() == 2) {
                                            HelperError.showSnackMessage(G.context.getString(R.string.referral_error_yourself), false);
                                        } else {
                                            HelperError.showSnackMessage(G.context.getString(R.string.error), false);
                                        }
                                    } else {
                                        HelperError.showSnackMessage(G.context.getString(R.string.error), false);
                                    }
                                    prgVisibility.set(View.GONE);
                                }
                            });
                } else {
                    showReagentPhoneNumberError.setValue(true);
                }
            } else {
                showErrorLastName.set(R.string.Toast_Write_NickName);
            }
        } else {
            showErrorName.set(R.string.Toast_Write_NickName);
        }
    }

    @Override
    public void onAvatarAdd(final ProtoGlobal.Avatar avatar) {
        existAvatar = true;
        avatarHandler.avatarAdd(AccountManager.getInstance().getCurrentUser().getId(), pathImageUser, avatar, avatarPath -> G.handler.post(() -> {
            existAvatar = true;
            prgVisibility.set(View.GONE);
            showCameraImage.set(View.GONE);
            avatarImagePath.set(new BindingAdapter.AvatarImage(avatarPath, false, null));
        }));
    }

    @Override
    public void onAvatarAddTimeOut() {
        existAvatar = true;
        prgVisibility.set(View.GONE);
    }

    @Override
    public void onAvatarError() {
        existAvatar = true;
        prgVisibility.set(View.GONE);
    }

    public void uploadAvatar(String path) {
        existAvatar = false;
        pathImageUser = path;
        int lastUploadedAvatarId = idAvatar + 1;
        prgVisibility.set(View.VISIBLE);
        Uploader.getInstance().upload(lastUploadedAvatarId + "", new File(pathImageUser), ProtoGlobal.RoomMessageType.IMAGE, new OnUploadListener() {
            @Override
            public void onProgress(String id, int progress) {
                progressValue.postValue(progress);
            }

            @Override
            public void onFinish(String id, String token) {
                Log.wtf(this.getClass().getName(), "onFinish: id: " + id);
                existAvatar = true;
                new RequestUserAvatarAdd().userAddAvatar(token);
            }

            @Override
            public void onError(String id) {
                Log.wtf(this.getClass().getName(), "onError: id: " + id);
                existAvatar = true;
                prgVisibility.set(View.GONE);
            }
        });
    }
}
