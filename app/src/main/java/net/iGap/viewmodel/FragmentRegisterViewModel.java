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
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.fragments.FragmentRegister;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperLogout;
import net.iGap.helper.HelperString;
import net.iGap.interfaces.OnCountryCode;
import net.iGap.interfaces.OnInfoCountryResponse;
import net.iGap.interfaces.OnReceiveInfoLocation;
import net.iGap.interfaces.OnReceivePageInfoTOS;
import net.iGap.interfaces.OnRecoverySecurityPassword;
import net.iGap.interfaces.OnSecurityCheckPassword;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserLogin;
import net.iGap.interfaces.OnUserRegistration;
import net.iGap.interfaces.OnUserVerification;
import net.iGap.module.CountryListComparator;
import net.iGap.module.CountryReader;
import net.iGap.module.structs.StructCountry;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoRequest;
import net.iGap.proto.ProtoUserRegister;
import net.iGap.proto.ProtoUserVerify;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestInfoCountry;
import net.iGap.request.RequestInfoLocation;
import net.iGap.request.RequestInfoPage;
import net.iGap.request.RequestQueue;
import net.iGap.request.RequestUserInfo;
import net.iGap.request.RequestUserLogin;
import net.iGap.request.RequestUserTwoStepVerificationGetPasswordDetail;
import net.iGap.request.RequestWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;

public class FragmentRegisterViewModel extends ViewModel implements OnSecurityCheckPassword, OnRecoverySecurityPassword {

    //ui
    public MutableLiveData<Boolean> closeKeyword = new MutableLiveData<>();
    public MutableLiveData<Boolean> showConditionErrorDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> goNextStep = new MutableLiveData<>();
    public MutableLiveData<Boolean> showEnteredPhoneNumberStartWithZeroError = new MutableLiveData<>();
    public MutableLiveData<Boolean> showEnteredPhoneNumberError = new MutableLiveData<>();
    public MutableLiveData<Boolean> showChooseCountryDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> showConfirmPhoneNumberDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> showConnectionErrorDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToMainPage = new MutableLiveData<>();
    public MutableLiveData<WaitTimeModel> showDialogWaitTime = new MutableLiveData<>();


    public static String isoCode = "IR";

    public String regex;
    public boolean isVerify = false;
    public ObservableField<String> callbackTxtAgreement = new ObservableField<>(G.context.getResources().getString(R.string.rg_agreement_text_register));
    public ObservableField<String> callbackBtnChoseCountry = new ObservableField<>("Iran");
    public ObservableField<String> callbackEdtCodeNumber = new ObservableField<>("+98");
    public ObservableField<String> callBackEdtPhoneNumber = new ObservableField<>("");
    public ObservableField<String> edtPhoneNumberMask = new ObservableField<>("###-###-####");
    public ObservableInt edtPhoneNumberMaskMaxCount = new ObservableInt(11);
    public ObservableInt isShowLoading = new ObservableInt(View.GONE);
    public ObservableField<String> callBackEdtCheckPassword = new ObservableField<>("");
    public ObservableField<String> edtCheckPasswordHint = new ObservableField<>("");
    public ObservableField<String> callBackTxtVerifySms = new ObservableField<>(G.context.getResources().getString(R.string.rg_verify_register2));
    public ObservableBoolean termsAndConditionIsChecked = new ObservableBoolean(false);
    public ObservableInt prgVerifyConnectVisibility = new ObservableInt(View.INVISIBLE);
    public ObservableInt txtVerifyTimerVisibility = new ObservableInt(View.INVISIBLE);
    public ObservableInt prgVerifySmsVisibility = new ObservableInt(View.INVISIBLE);
    public ObservableInt prgVerifyServerVisibility = new ObservableInt(View.INVISIBLE);
    public ObservableInt imgVerifySmsVisibility = new ObservableInt(View.GONE);
    public ObservableInt imgVerifyServerVisibility = new ObservableInt(View.GONE);
    public ObservableInt txtIconVerifyConnectVisibility = new ObservableInt(View.GONE);
    public ObservableInt layoutVerifyAgreement = new ObservableInt(View.GONE);
    public ObservableInt txtOkVisibility = new ObservableInt(View.GONE);
    public ObservableInt qrCodeVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt rootMainLayoutVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt prgVerifyKeyVisibility = new ObservableInt(View.INVISIBLE);
    public ObservableInt imgVerifyKeyVisibility = new ObservableInt(View.GONE);
    public ObservableInt prgWaitingVisibility = new ObservableInt(View.GONE);
    public ObservableInt rootCheckPasswordVisibility = new ObservableInt(View.GONE);
    public ObservableInt txtAgreementVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt edtCodeNumberColor = new ObservableInt(G.context.getResources().getColor(R.color.rg_black_register));
    public ObservableInt imgVerifySmsColor = new ObservableInt(G.context.getResources().getColor(R.color.rg_text_verify));
    public ObservableInt txtVerifySmsColor = new ObservableInt(G.context.getResources().getColor(R.color.rg_text_verify));
    public ObservableInt btnChoseCountryColor = new ObservableInt(G.context.getResources().getColor(R.color.rg_black_register));
    public ObservableInt edtPhoneNumberColor = new ObservableInt(G.context.getResources().getColor(R.color.rg_black_register));
    public ObservableInt txtVerifyKeColor = new ObservableInt(G.context.getResources().getColor(R.color.rg_text_verify));
    public ObservableInt txtVerifyServerColor = new ObservableInt(G.context.getResources().getColor(R.color.rg_text_verify));
    public ObservableInt btnStartColor = new ObservableInt(G.context.getResources().getColor(R.color.rg_whit_background));
    public ObservableInt btnStartBackgroundColor = new ObservableInt(Color.parseColor(G.appBarColor));
    public ObservableBoolean edtCodeNumberEnable = new ObservableBoolean(false);
    public ObservableBoolean btnChoseCountryEnable = new ObservableBoolean(true);
    public ObservableBoolean edtPhoneNumberEnable = new ObservableBoolean(true);
    public ObservableBoolean btnStartEnable = new ObservableBoolean(true);
    ProtoUserRegister.UserRegisterResponse.Method methodForReceiveCode = ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SMS;
    private boolean isCallMethodSupported;
    public ArrayList structCountryArrayList = new ArrayList();
    public String phoneNumber;
    public String userName;
    public String authorHash;
    private String token;
    private String regexFetchCodeVerification;
    public long userId;
    private boolean newUser;
    private int digitCount;
    private String verifyCode;
    private boolean isRecoveryByEmail = false;
    private String securityPasswordQuestionOne = "";
    private String securityPasswordQuestionTwo = "";
    private String securityPaternEmail = "";
    private boolean isConfirmedRecoveryEmail;
    private int sendRequestRegister = 0;
    public int ONETIME = 1;
    private String countryName = "", pattern = "";
    private boolean locationFound;
    private int callingCode;

    public FragmentRegisterViewModel() {
        showConditionErrorDialog.setValue(false);
    }

    public void onTextChanged(String s) {
        if (s.startsWith("0")) {
            callBackEdtPhoneNumber.set("");
            showEnteredPhoneNumberStartWithZeroError.setValue(true);
        }
    }

    public void onClickChoseCountry() {
        showChooseCountryDialog.setValue(true);
    }

    public void termsOnCheckChange(boolean isChecked) {
        termsAndConditionIsChecked.set(isChecked);
    }

    public void onClicksStart() {
        phoneNumber = callBackEdtPhoneNumber.get();
        if (callBackEdtPhoneNumber.get().length() > 0 && (regex.equals("") || (!regex.equals("") && callBackEdtPhoneNumber.get().replace("-", "").matches(regex)))) {
            if (termsAndConditionIsChecked.get()) {
                showConfirmPhoneNumberDialog.setValue(true);
            } else {
                showConditionErrorDialog.setValue(true);
            }
        } else {
            showEnteredPhoneNumberStartWithZeroError.setValue(callBackEdtPhoneNumber.get().replace("-", "").matches(regex));
        }
    }

    public void confirmPhoneNumber() {
        isVerify = true;
        checkVerify();
    }

    public void saveInstance(String codeNumber, String phoneNumberMask, String phoneNumber, String countryName, String agreement, String regex, int oneTime) {
        callbackEdtCodeNumber.set(codeNumber);
        edtPhoneNumberMask.set(phoneNumberMask);
        callBackEdtPhoneNumber.set(phoneNumber);
        callbackBtnChoseCountry.set(countryName);
        callbackTxtAgreement.set(agreement);
        this.regex = regex;
        ONETIME = oneTime;
        if (ONETIME != 1) {
            getInfo();
        }
    }

    private void checkVerify() {
        if (G.socketConnection) { //connection ok
            btnStartEnable.set(false);
            userRegister();
        } else { // connection error
            edtPhoneNumberEnable.set(true);
            showConnectionErrorDialog.setValue(true);
        }
    }

    private void userRegister() {
        isShowLoading.set(View.VISIBLE);
        requestRegister();
    }

    //Todo:Move to repository
    private void getTermsOfServiceBody() {
        G.onReceivePageInfoTOS = new OnReceivePageInfoTOS() {
            @Override
            public void onReceivePageInfo(final String bodyR) {
                G.handler.post(() -> {
                    if (bodyR != null) {
                        callbackTxtAgreement.set(Html.fromHtml(bodyR).toString());
                    }
                });
                getInfoLocation();
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                getTermsOfServiceBody();
            }
        };
        new RequestInfoPage().infoPage("TOS");
    }

    //Todo:Move To repository
    private void getInfoLocation() {
        new RequestInfoLocation().infoLocation(new OnReceiveInfoLocation() {
            @Override
            public void onReceive(String isoCodeR, final int callingCodeR, final String countryNameR, String patternR, String regexR) {
                locationFound = true;
                isoCode = isoCodeR;
                callingCode = callingCodeR;
                countryName = countryNameR;
                pattern = patternR;
                regex = regexR;
                G.handler.post(() -> {
                    callbackEdtCodeNumber.set("+" + callingCode);
                    callbackBtnChoseCountry.set(countryName);
                    if (pattern != null && !pattern.equals("")) {
                        edtPhoneNumberMask.set(pattern.replace("X", "#").replace(" ", "-"));
                    } else {
                        edtPhoneNumberMask.set("##################");
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                if (majorCode == 500 && minorCode == 1) {
                    G.handler.post(() -> locationFound = false);
                }
            }
        });
    }

    public void setCountry(StructCountry country) {
        isShowLoading.set(View.VISIBLE);
        new RequestInfoCountry().infoCountry(country.getAbbreviation(), new OnInfoCountryResponse() {
            @Override
            public void onInfoCountryResponse(final int callingCode, final String name, final String pattern, final String regexR) {
                Log.wtf("register view model", "onInfoCountryResponse");
                G.handler.post(() -> {
                    isShowLoading.set(View.GONE);
                    callbackEdtCodeNumber.set("+" + callingCode);
                    if (pattern.equals("")) {
                        edtPhoneNumberMask.set("##################");
                    } else {
                        edtPhoneNumberMask.set(pattern.replace("X", "#").replace(" ", "-"));
                    }
                    regex = regexR;
                    callbackBtnChoseCountry.set(name);
                    btnStartEnable.set(true);
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                //empty
                isShowLoading.set(View.GONE);
            }
        });
        callBackEdtPhoneNumber.set("");

        /*isoCode = structCountry.getAbbreviation();

        FragmentRegister.positionRadioButton = structCountry.getId();

        callbackEdtCodeNumber.set("+ " + structCountry.getCountryCode());

        if (structCountry.getPhonePattern() != null || structCountry.getPhonePattern().equals(" ")) {
            edtPhoneNumberMask.set((structCountry.getPhonePattern().replace("X", "#").replace(" ", "-")));
        } else {
            edtPhoneNumberMaskMaxCount.set(18);
            edtPhoneNumberMask.set("##################");
        }

        callbackBtnChoseCountry.set(structCountry.getName());*/
    }


    //for qr code
    /*private void shareQr() {
        if (_resultQrCode == null) {
            return;
        }
        try {
            File file = new File(_resultQrCode);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                G.fragmentActivity.startActivity(Intent.createChooser(intent, G.fragmentActivity.getResources().getString(R.string.share_image_from_igap)));
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }*/

    //for qr code
    /*public void onClickQrCode(View v) {

        dialogQrCode = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.Login_with_QrCode)).customView(R.layout.dialog_qrcode, true).positiveText(R.string.share_item_dialog).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                shareQr();
            }
        }).negativeText(R.string.save).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (_resultQrCode == null) {
                    return;
                }
                File file = new File(_resultQrCode);
                if (file.exists()) {
                    HelperSaveFile.savePicToGallery(_resultQrCode, true);
                }
            }
        }).neutralText(R.string.cancel).onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                dialog.dismiss();
            }
        }).build();

        imgQrCodeNewDevice = (ImageView) dialogQrCode.findViewById(R.id.imgQrCodeNewDevice);
        prgQrCodeNewDevice = (ProgressBar) dialogQrCode.findViewById(R.id.prgWaitQrCode);
        prgQrCodeNewDevice.setVisibility(View.VISIBLE);
        if (!(G.fragmentActivity).isFinishing()) {
            dialogQrCode.show();
        }

        dialogQrCode.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (CountDownTimerQrCode != null) {
                    CountDownTimerQrCode.cancel();
                }
            }
        });

        new RequestQrCodeNewDevice().qrCodeNewDevice();

        G.onQrCodeNewDevice = new OnQrCodeNewDevice() {
            @Override
            public void getQrCode(ByteString codeImage, final int expireTime) {

                _resultQrCode = G.DIR_TEMP + "/" + "QrCode" + ".jpg";

                File f = new File(_resultQrCode);
                if (f.exists()) {
                    f.delete();
                }
                AndroidUtils.writeBytesToFile(_resultQrCode, codeImage.toByteArray());
                image_uriQrCode = Uri.parse("file://" + _resultQrCode);
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        checkExpireTime(expireTime);
                        if (prgQrCodeNewDevice != null) {
                            prgQrCodeNewDevice.setVisibility(View.GONE);
                        }

                        if (imgQrCodeNewDevice != null) {
                            G.imageLoader.clearMemoryCache();
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(_resultQrCode), imgQrCodeNewDevice);
                        }
                    }
                });
            }
        };


    }*/

    /*public void onClickTxtForgotPassword(View v) {

        int item;
        if (isConfirmedRecoveryEmail) {
            item = R.array.securityRecoveryPassword;
        } else {
            item = R.array.securityRecoveryPasswordWithoutEmail;
        }

        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.set_recovery_dialog_title).items(item).itemsCallback(new MaterialDialog.ListCallback() {

            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                if (text.equals(G.fragmentActivity.getResources().getString(R.string.recovery_by_email_dialog))) {
                    isRecoveryByEmail = true;
                } else {
                    isRecoveryByEmail = false;
                }

                FragmentSecurityRecovery fragmentSecurityRecovery = new FragmentSecurityRecovery();
                Bundle bundle = new Bundle();
                bundle.putSerializable("PAGE", Security.REGISTER);
                bundle.putString("QUESTION_ONE", securityPasswordQuestionOne);
                bundle.putString("QUESTION_TWO", securityPasswordQuestionTwo);
                bundle.putString("PATERN_EMAIL", securityPaternEmail);
                bundle.putBoolean("IS_EMAIL", isRecoveryByEmail);
                bundle.putBoolean("IS_CONFIRM_EMAIL", isConfirmedRecoveryEmail);
                fragmentSecurityRecovery.setArguments(bundle);

                G.fragmentActivity.getSupportFragmentManager().beginTransaction().addToBackStack(null).
                        setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).
                        replace(R.id.ar_layout_root, fragmentSecurityRecovery).commit();


            }
        }).show();


    }*/

    /*public void onClickTxtOk(View v) {
        if (callBackEdtCheckPassword.get().length() > 0) {

            prgWaitingVisibility.set(View.VISIBLE);

            G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            new RequestUserTwoStepVerificationVerifyPassword().verifyPassword(callBackEdtCheckPassword.get());
        } else {
            error(G.fragmentActivity.getResources().getString(R.string.please_enter_code));
        }
    }*/

    public void getInfo() {
        G.onSecurityCheckPassword = this;
        G.onRecoverySecurityPassword = this;
        G.onPushLoginToken = (tokenQrCode, userNameR, userIdR, authorHashR) -> {
            token = tokenQrCode;
            G.displayName = userName = userNameR;
            G.userId = userId = userIdR;
            G.authorHash = authorHash = authorHashR;
            G.handler.post(() -> {
                /*if (dialogQrCode != null && dialogQrCode.isShowing())
                    dialogQrCode.dismiss();*/
                userLogin(token);
            });
        };

        G.onPushTwoStepVerification = (userNameR, userIdR, authorHashR) -> {
            userName = userNameR;
            userId = userIdR;
            authorHash = authorHashR;
            G.handler.post(() -> {
                /*if (dialogQrCode != null && dialogQrCode.isShowing())
                    dialogQrCode.dismiss();*/
            });
            checkPassword("", true);
        };

        CountryReader countryReade = new CountryReader();
        StringBuilder fileListBuilder = countryReade.readFromAssetsTextFile("country.txt", G.context);

        String list = fileListBuilder.toString();
        // Split line by line Into array
        String[] listArray = list.split("\\r?\\n");
        final String[] countryNameList = new String[listArray.length];
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

        /*for (int i = 0; i < structCountryArrayList.size(); i++) {
            if (i < countryNameList.length) {
                countryNameList[i] = structCountryArrayList.get(i).getName();
                StructCountry item = new StructCountry();
                item.setId(i);
                item.setName(structCountryArrayList.get(i).getName());
                item.setCountryCode(structCountryArrayList.get(i).getCountryCode());
                item.setPhonePattern(structCountryArrayList.get(i).getPhonePattern());
                item.setAbbreviation(structCountryArrayList.get(i).getAbbreviation());
                items.add(item);
            }
        }*/

        if (G.isSecure) {
            getTermsOfServiceBody();
            ONETIME = 1;
        } else {
            getInfo();
        }
    }

    private void requestRegister() {
        if (G.socketConnection) {
            phoneNumber = phoneNumber.replace("-", "");
            ProtoUserRegister.UserRegister.Builder builder = ProtoUserRegister.UserRegister.newBuilder();
            builder.setCountryCode(isoCode);
            builder.setPhoneNumber(Long.parseLong(phoneNumber));
            builder.setPreferenceMethodValue(methodForReceiveCode.getNumber());
            builder.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
            RequestWrapper requestWrapper = new RequestWrapper(100, builder, new OnUserRegistration() {
                @Override
                public void onRegister(final String userNameR, final long userIdR, final ProtoUserRegister.UserRegisterResponse.Method methodValue, final List<Long> smsNumbersR, String regex, int verifyCodeDigitCount, final String authorHashR, boolean callMethodSupported) {
                    isCallMethodSupported = callMethodSupported;
                    digitCount = verifyCodeDigitCount;
                    regexFetchCodeVerification = regex;
                    G.handler.post(() -> {
                        isShowLoading.set(View.GONE);
                        txtVerifyTimerVisibility.set(View.VISIBLE);
                        userName = userNameR;
                        userId = userIdR;
                        authorHash = authorHashR;
                        G.smsNumbers = smsNumbersR;
                    /*if (methodValue == ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SOCKET) {
                        errorVerifySms(FragmentRegister.Reason.SOCKET);
                    } else {
                        errorVerifySms(FragmentRegister.Reason.TIME_OUT); // open rg_dialog for enter sms code
                    }*/
                        goNextStep.setValue(true);
                    /*prgVerifyConnectVisibility.set(View.GONE);
                    txtIconVerifyConnectVisibility.set(View.VISIBLE);
                    imgVerifySmsVisibility.set(View.GONE);
                    txtVerifyConnectColor.set(G.context.getResources().getColor(R.color.rg_text_verify));
                    prgVerifySmsVisibility.set(View.VISIBLE);*/
                    });
                }

                @Override
                public void onRegisterError(final int majorCode, int minorCode, int getWait) {
                    try {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                isShowLoading.set(View.GONE);
                            }
                        });
                        final long time = getWait;
                        if (majorCode == 100 && minorCode == 1) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //Invalid countryCode
                                }
                            });
                        } else if (majorCode == 100 && minorCode == 2) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //Invalid phoneNumber
                                }
                            });
                        } else if (majorCode == 101) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //Invalid phoneNumber
                                }
                            });
                        } else if (majorCode == 135) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    new DefaultRoundDialog(G.fragmentActivity).setTitle(R.string.USER_VERIFY_BLOCKED_USER).setMessage(R.string.Toast_Number_Block).setPositiveButton(R.string.B_ok, null).show();
                                    /*new MaterialDialog.Builder(G.fragmentActivity).title(R.string.USER_VERIFY_BLOCKED_USER).content(R.string.Toast_Number_Block).positiveText(R.string.B_ok).show();*/
                                }
                            });
                        } else if (majorCode == 136) {
                            G.handler.post(() -> showDialogWaitTime.setValue(new WaitTimeModel(R.string.USER_VERIFY_MANY_TRIES, time, majorCode)));
                        } else if (majorCode == 137) {
                            G.handler.post(() -> showDialogWaitTime.setValue(new WaitTimeModel(R.string.USER_VERIFY_MANY_TRIES_SEND, time, majorCode)));
                        } else if (majorCode == 5 && minorCode == 1) { // timeout
                            if (sendRequestRegister <= 2) {
                                requestRegister();
                                sendRequestRegister++;
                            }
                        } else if (majorCode == 10) {
                            G.handler.post(() -> showDialogWaitTime.setValue(new WaitTimeModel(R.string.IP_blocked, time, majorCode)));
                        }
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });

            try {
                RequestQueue.sendRequest(requestWrapper);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            G.handler.postDelayed(this::requestRegister, 1000);
        }
    }

    /**
     * if the connection is established do verify otherwise start registration(step one) again
     */
    private void userVerify(final String userName, final String verificationCode) {
        if (G.socketConnection) {
            prgVerifyKeyVisibility.set(View.VISIBLE);
            try {
                ProtoUserVerify.UserVerify.Builder userVerify = ProtoUserVerify.UserVerify.newBuilder();
                userVerify.setCode(Integer.parseInt(verificationCode));
                userVerify.setUsername(userName);

                RequestWrapper requestWrapper = new RequestWrapper(101, userVerify, new OnUserVerification() {
                    @Override
                    public void onUserVerify(final String tokenR, final boolean newUserR) {
                        G.handler.post(() -> {
                            callBackTxtVerifySms.set(G.fragmentActivity.getResources().getString(R.string.rg_verify_register2));
                            prgVerifySmsVisibility.set(View.GONE);
                            imgVerifySmsVisibility.set(View.VISIBLE);
                            //imgVerifySmsColor.set(R.mipmap.check);
                            imgVerifySmsColor.set(G.context.getResources().getColor(R.color.rg_text_verify));
                            txtVerifySmsColor.set(G.context.getResources().getColor(R.color.rg_text_verify));

                            newUser = newUserR;
                            token = tokenR;

                            prgVerifyKeyVisibility.set(View.GONE);
                            imgVerifyKeyVisibility.set(View.VISIBLE);
                            txtVerifyKeColor.set(G.context.getResources().getColor(R.color.rg_text_verify));

                            userLogin(token);
                        });
                    }

                    @Override
                    public void onUserVerifyError(final int majorCode, int minorCode, final int time) {
                        try {
                            if (majorCode == 184 && minorCode == 1) {
                                checkPassword(verificationCode, false);
                            } else if (majorCode == 102 && minorCode == 1) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        /*errorVerifySms(FragmentRegister.Reason.INVALID_CODE);*/
                                    }
                                });
                            } else if (majorCode == 102 && minorCode == 2) {
                                //empty
                            } else if (majorCode == 103) {
                                //empty
                            } else if (majorCode == 104) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // There is no registered user with given username
                                        if (!G.fragmentActivity.isFinishing()) {
                                            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.USER_VERIFY_GIVEN_USERNAME).content(R.string.Toast_Number_Block).positiveText(R.string.B_ok).show();
                                        }
                                    }
                                });
                            } else if (majorCode == 105) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // User is blocked , You cannot verify the user
                                        if (!G.fragmentActivity.isFinishing()) {
                                            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.USER_VERIFY_BLOCKED_USER).content(R.string.Toast_Number_Block).positiveText(R.string.B_ok).show();
                                        }
                                    }
                                });
                            } else if (majorCode == 106) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Verification code is invalid
                                        /*errorVerifySms(FragmentRegister.Reason.INVALID_CODE);*/
                                    }
                                });
                            } else if (majorCode == 107) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Verification code is expired
                                        if (!G.fragmentActivity.isFinishing()) {
                                            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.USER_VERIFY_EXPIRED)
                                                    .content(R.string.Toast_Number_Block)
                                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        }
                                                    })
                                                    .positiveText(R.string.B_ok)
                                                    .show();
                                        }
                                    }
                                });
                            } else if (majorCode == 108) {
                                G.handler.post(() -> {
                                    // Verification code is locked for a while due to too many tries
                                    showDialogWaitTime.setValue(new WaitTimeModel(R.string.USER_VERIFY_MANY_TRIES, time, majorCode));
                                });
                            } else if (majorCode == 5 && minorCode == 1) {
                                userVerify(userName, verifyCode);
                            }
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });

                RequestQueue.sendRequest(requestWrapper);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            /**
             * return view for step one and two because now start registration again from step one
             */

            // return step one
            prgVerifyConnectVisibility.set(View.VISIBLE);
            txtIconVerifyConnectVisibility.set(View.GONE);
            // clear step two
            prgVerifySmsVisibility.set(View.GONE);
            imgVerifySmsVisibility.set(View.INVISIBLE);
            txtVerifySmsColor.set(G.context.getResources().getColor(R.color.rg_text_verify));

            //rg_txt_verify_sms.setTextAppearance(G.context, Typeface.NORMAL);

            requestRegister();
        }
    }

    private void checkPassword(final String verificationCode, final boolean isQrCode) {
        new RequestUserTwoStepVerificationGetPasswordDetail().getPasswordDetail();
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                if (!isQrCode) {

                    callBackTxtVerifySms.set((G.fragmentActivity.getResources().getString(R.string.rg_verify_register2)));
                    prgVerifySmsVisibility.set(View.GONE);
                    imgVerifySmsVisibility.set(View.VISIBLE);
                    //imgVerifySmsColor.set(R.mipmap.check);
                    imgVerifySmsColor.set(G.context.getResources().getColor(R.color.rg_text_verify));
                    txtVerifySmsColor.set(G.context.getResources().getColor(R.color.rg_text_verify));


                    //newUser = newUserR;
                    //token = tokenR;

                    prgVerifyKeyVisibility.set(View.GONE);
                    imgVerifyKeyVisibility.set(View.VISIBLE);
                    txtVerifyKeColor.set(G.context.getResources().getColor(R.color.rg_text_verify));
                    //userLogin(token);

                }
                rootMainLayoutVisibility.set(View.GONE);
                rootCheckPasswordVisibility.set(View.VISIBLE);
                txtOkVisibility.set(View.VISIBLE);
                qrCodeVisibility.set(View.GONE);

            }
        });
    }

    private void userLogin(final String token) {
        prgVerifyServerVisibility.set(View.VISIBLE);

        G.onUserLogin = new OnUserLogin() {
            @Override
            public void onLogin() {
                G.handler.post(() -> {
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(realm1 -> RealmUserInfo.putOrUpdate(realm1, userId, userName, phoneNumber, token, authorHash));

                    prgVerifyServerVisibility.set(View.GONE);
                    imgVerifyServerVisibility.set(View.VISIBLE);

                    txtVerifyServerColor.set(G.context.getResources().getColor(R.color.rg_text_verify));

                    if (newUser) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //TODO: change this part
                                /*FragmentRegistrationNickname fragment = new FragmentRegistrationNickname();
                                Bundle bundle = new Bundle();
                                bundle.putLong(FragmentRegistrationNickname.ARG_USER_ID, userId);
                                fragment.setArguments(bundle);
                                G.fragmentActivity.getSupportFragmentManager().beginTransaction().add(R.id.ar_layout_root, fragment).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
                                G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(fragmentRegister).commitAllowingStateLoss();*/
                            }
                        });
                    } else {
                        // get user info for set nick name and after from that go to ActivityMain
                        getUserInfo();
                        requestUserInfo();
                    }
                    realm.close();
                });
            }

            @Override
            public void onLoginError(int majorCode, int minorCode) {
                if (majorCode == 111 && minorCode == 4) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            HelperLogout.logout();
                        }
                    });
                } else if (majorCode == 111) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //requestLogin();
                        }
                    });
                } else if (majorCode == 5 && minorCode == 1) {
                    requestLogin();
                }
            }
        };

        requestLogin();
    }

    private void getUserInfo() {
        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(realm1 -> {
                    G.displayName = user.getDisplayName();
                    RealmUserInfo.putOrUpdate(realm1, user);
                    G.handler.post(() -> {
                        G.onUserInfoResponse = null;
                        goToMainPage.setValue(true);
                    });
                });
                realm.close();
            }

            @Override
            public void onUserInfoTimeOut() {
                requestUserInfo();
            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {

            }
        };
    }

    private void requestUserInfo() {
        if (G.socketConnection) {
            if (userId == 0) {
                Realm realm = Realm.getDefaultInstance();
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo == null) {
                    //finish();
                } else {
                    userId = realmUserInfo.getUserId();
                }
                realm.close();
            }
            new RequestUserInfo().userInfo(userId);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestUserInfo();
                }
            }, 1000);
        }
    }

    private void requestLogin() {
        if (G.socketConnection) {
            if (token == null) {
                Realm realm = Realm.getDefaultInstance();
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo == null) {
                    //finish();
                } else {
                    token = realmUserInfo.getToken();
                }
                realm.close();
            }
            new RequestUserLogin().userLogin(token);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestLogin();
                }
            }, 1000);
        }
    }

    private void dialogWaitTimeVerifyPassword(long time) {
        boolean wrapInScrollView = true;
        final MaterialDialog dialogWait = new MaterialDialog.Builder(G.fragmentActivity).title(R.string.error_check_password).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(true).canceledOnTouchOutside(true).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                dialog.dismiss();
            }
        }).show();

        View v = dialogWait.getCustomView();

        final TextView remindTime = (TextView) v.findViewById(R.id.remindTime);
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                remindTime.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                remindTime.setText("00:00");
            }
        };
        countWaitTimer.start();
    }

    private void error(String error) {
        HelperError.showSnackMessage(error, true);
    }

    /**
     * ****************************** Callbacks ******************************
     */

    @Override
    public void getDetailPassword(final String questionOne, final String questionTwo, final String hint, boolean hasConfirmedRecoveryEmail, String unconfirmedEmailPattern) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                edtCheckPasswordHint.set(hint);
            }
        });

        securityPasswordQuestionOne = questionOne;
        securityPasswordQuestionTwo = questionTwo;
        isConfirmedRecoveryEmail = hasConfirmedRecoveryEmail;
        String securityPasswordHint = hint;
        boolean hasConfirmedRecoveryEmail1 = hasConfirmedRecoveryEmail;
        String unconfirmedEmailPattern1 = unconfirmedEmailPattern;
    }

    @Override
    public void verifyPassword(final String tokenR) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                prgWaitingVisibility.set(View.GONE);
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                token = tokenR;
                closeKeyword.setValue(true);
                userLogin(token);
            }
        });
    }

    @Override
    public void errorVerifyPassword(final int wait) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                prgWaitingVisibility.set(View.GONE);
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                dialogWaitTimeVerifyPassword(wait);
            }
        });
    }

    @Override
    public void errorInvalidPassword() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                closeKeyword.setValue(true);
                prgWaitingVisibility.set(View.GONE);
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    @Override
    public void recoveryByEmail(final String tokenR) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                prgWaitingVisibility.set(View.GONE);

                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                token = tokenR;
                rootCheckPasswordVisibility.set(View.GONE);
                txtOkVisibility.set(View.GONE);
                rootMainLayoutVisibility.set(View.VISIBLE);
                userLogin(token);
            }
        });

    }

    @Override
    public void getEmailPatern(final String patern) {

    }

    @Override
    public void errorRecoveryByEmail() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                prgWaitingVisibility.set(View.GONE);
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                closeKeyword.setValue(true);
            }
        });
    }

    @Override
    public void recoveryByQuestion(final String tokenR) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {

                prgWaitingVisibility.set(View.GONE);

                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                token = tokenR;
                rootCheckPasswordVisibility.set(View.GONE);
                txtOkVisibility.set(View.GONE);
                rootMainLayoutVisibility.set(View.VISIBLE);
                userLogin(token);
            }
        });
    }

    @Override
    public void errorRecoveryByQuestion() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                prgWaitingVisibility.set(View.GONE);
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                closeKeyword.setValue(true);
            }
        });
    }

    public void timerFinished() {
        btnStartEnable.set(true);
        btnChoseCountryEnable.set(true);
        edtPhoneNumberEnable.set(true);
        txtAgreementVisibility.set(View.VISIBLE);
        layoutVerifyAgreement.set(View.GONE);
    }

    public enum Reason {
        SOCKET, TIME_OUT, INVALID_CODE
    }
}



