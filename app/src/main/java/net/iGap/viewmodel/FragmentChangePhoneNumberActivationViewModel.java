package net.iGap.viewmodel;

import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.Config;
import net.iGap.WebSocketClient;
import net.iGap.helper.HelperString;
import net.iGap.model.repository.ChangePhoneNumberRepository;
import net.iGap.module.SingleLiveEvent;
import net.iGap.network.AbstractObject;
import net.iGap.network.IG_RPC;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.realm.RealmUserInfo;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class FragmentChangePhoneNumberActivationViewModel extends BaseViewModel {

    public ObservableField<String> timerValue = new ObservableField<>();
    public MutableLiveData<String> verifyCode = new MutableLiveData<>();
    public ObservableBoolean enabledResendCodeButton = new ObservableBoolean(false);
    public SingleLiveEvent<Boolean> showEnteredCodeError = new SingleLiveEvent<>();
    public MutableLiveData<Integer> currentTimePosition = new MutableLiveData<>();
    public MutableLiveData<WaitTimeModel> showWaitDialog = new MutableLiveData<>();
    public SingleLiveEvent<Integer> showEnteredCodeErrorServer = new SingleLiveEvent<>();
    public ObservableInt showLoading = new ObservableInt(View.GONE);
    public ObservableField<String> sendActivationStatus = new ObservableField<>();
    public ObservableField<String> sendActivationPhoneNumber = new ObservableField<>();
    public MutableLiveData<Boolean> closeKeyword = new MutableLiveData<>();
    public MutableLiveData<Boolean> clearActivationCode = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogUserBlocked = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogVerificationCodeExpired = new MutableLiveData<>();
    public ObservableBoolean isActive = new ObservableBoolean(true);
    public MutableLiveData<Boolean> showConnectionError = new MutableLiveData<>();
    public MutableLiveData<Boolean> closeFragment = new MutableLiveData<>();

    private ChangePhoneNumberRepository repository;
    private CountDownTimer countDownTimer;
    private String phoneNumber;
    private String countryCode;


    public FragmentChangePhoneNumberActivationViewModel() {
        repository = ChangePhoneNumberRepository.getInstance();
        timerValue.set(String.format(Locale.getDefault(), "%02d", repository.getResendDelayTime()));
        counterTimer(repository.getResendDelayTime());

        phoneNumber = repository.getPhoneNumber();
        countryCode = repository.getIsoCode();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cancelTimer();
    }

    private void counterTimer(long resendCodeDelay) {
        countDownTimer = new CountDownTimer(resendCodeDelay * DateUtils.SECOND_IN_MILLIS, Config.COUNTER_TIMER_DELAY) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                timerValue.set(String.format(Locale.getDefault(), "%02d", seconds));
                currentTimePosition.setValue(seconds * 6);
            }

            public void onFinish() {
                timerValue.set(String.format(Locale.getDefault(), "%02d", 0));
                currentTimePosition.setValue(60 * 6);
                enabledResendCodeButton.set(true);
                cancelTimer();
            }
        };
        countDownTimer.start();
    }

    private void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    public void resentActivationCodeClick() {
        enabledResendCodeButton.set(false);
        AbstractObject req = null;
        IG_RPC.Change_Phone_Number change_phone_number = new IG_RPC.Change_Phone_Number();
        change_phone_number.countryCode = countryCode;
        change_phone_number.phoneNumber = Long.parseLong(phoneNumber);
        req = change_phone_number;
        getRequestManager().sendRequest(req, (response, error) -> {
            if (error == null) {
                IG_RPC.Res_Change_Phone_Number res = (IG_RPC.Res_Change_Phone_Number) response;
            } else {

            }
        });
    }

    public void loginButtonOnClick(@NotNull String enteredCode) {
        closeKeyword.setValue(true);
        if (enteredCode.length() == 5) {
            if (WebSocketClient.getInstance().isConnect()) {
                showLoading.set(View.VISIBLE);
                isActive.set(false);
                AbstractObject req = null;
                IG_RPC.Verify_New_Phone_Number verify_new_phone_number = new IG_RPC.Verify_New_Phone_Number();
                verify_new_phone_number.verifyCode = enteredCode;
                req = verify_new_phone_number;
                getRequestManager().sendRequest(req, (response, error) -> {
                    if (error == null) {
                        IG_RPC.Res_Verify_New_Phone_Number res = (IG_RPC.Res_Verify_New_Phone_Number) response;
                        RealmUserInfo.updatePhoneNumber(repository.getPhoneNumber());
                    }
                    closeFragment.postValue(true);
                    showLoading.set(View.GONE);
                });
            } else {
                showConnectionError.setValue(true);
            }
        } else {
            showEnteredCodeError.setValue(true);
        }
    }

    public String receiveVerifySms(String message) {
        return HelperString.regexExtractValue(message, repository.getRegexFetchCodeVerification());
    }

    public void timerFinished() {
        enabledResendCodeButton.set(true);
    }

    public enum Reason {
        SOCKET, TIME_OUT, INVALID_CODE
    }
}
