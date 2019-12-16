package net.iGap.viewmodel;

import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.WebSocketClient;
import net.iGap.helper.HelperString;
import net.iGap.model.repository.ErrorWithWaitTime;
import net.iGap.model.repository.RegisterRepository;
import net.iGap.module.SingleLiveEvent;
import net.iGap.proto.ProtoUserRegister;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class FragmentActivationViewModel extends ViewModel {

    public ObservableField<String> timerValue = new ObservableField<>();
    public MutableLiveData<String> verifyCode = new MutableLiveData<>();
    public ObservableBoolean enabledResendCodeButton = new ObservableBoolean(false);
    public SingleLiveEvent<Boolean> showEnteredCodeError = new SingleLiveEvent<>();
    public MutableLiveData<Integer> currentTimePosition = new MutableLiveData<>();
    public MutableLiveData<WaitTimeModel> showWaitDialog = new MutableLiveData<>();
    public SingleLiveEvent<Integer> showEnteredCodeErrorServer = new SingleLiveEvent<>();
    public ObservableInt showLoading = new ObservableInt(View.GONE);
    public ObservableInt sendActivationStatus = new ObservableInt(R.string.empty_error_message);
    public MutableLiveData<Boolean> closeKeyword = new MutableLiveData<>();
    public MutableLiveData<Boolean> clearActivationCode = new MutableLiveData<>();
    public MutableLiveData<Long> goToTwoStepVerificationPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogUserBlocked = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogVerificationCodeExpired = new MutableLiveData<>();
    public ObservableBoolean isActive = new ObservableBoolean(true);
    public MutableLiveData<Boolean> showConnectionError = new MutableLiveData<>();

    private RegisterRepository repository;
    private CountDownTimer countDownTimer;
    private int sendRequestRegister = 0;

    public FragmentActivationViewModel() {
        repository = RegisterRepository.getInstance();
        timerValue.set("60");
        counterTimer();
        if (repository.getMethod() == ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SMS) {
            sendActivationStatus.set(R.string.verify_sms_message);
        } else if (repository.getMethod() == ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SOCKET) {
            sendActivationStatus.set(R.string.verify_socket_message);
        } else if (repository.getMethod() == ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SMS_SOCKET) {
            sendActivationStatus.set(R.string.verify_sms_socket_message);
        } else if (repository.getMethod() == ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_CALL) {
            sendActivationStatus.set(R.string.verify_call_message);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cancelTimer();
    }

    private void counterTimer() {
        countDownTimer = new CountDownTimer(60 * DateUtils.SECOND_IN_MILLIS, Config.COUNTER_TIMER_DELAY) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
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
        requestRegister();
    }

    public void loginButtonOnClick(@NotNull String enteredCode) {
        closeKeyword.setValue(true);
        if (enteredCode.length() == 5) {
            if (WebSocketClient.getInstance().isConnect()) {
                showLoading.set(View.VISIBLE);
                userVerification(enteredCode);
            } else {
                showConnectionError.setValue(true);
            }
        } else {
            showEnteredCodeError.setValue(true);
        }
    }

    private void userVerification(String verificationCode) {
        isActive.set(false);
        repository.userVerify(verificationCode, new RegisterRepository.RepositoryCallbackWithError<ErrorWithWaitTime>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(ErrorWithWaitTime error) {
                showLoading.set(View.GONE);
                isActive.set(true);
                if (error.getMajorCode() == 184 && error.getMinorCode() == 1) {
                    goToTwoStepVerificationPage.postValue(repository.getUserId());
                } else if (error.getMajorCode() == 102 && error.getMinorCode() == 1) {
                    errorVerifySms(Reason.INVALID_CODE);
                } else if (error.getMajorCode() == 102 && error.getMinorCode() == 2) {
                    //empty
                } else if (error.getMajorCode() == 103) {
                    //empty
                } else if (error.getMajorCode() == 104) {
                    showDialogUserBlocked.postValue(true);
                } else if (error.getMajorCode() == 105) {
                    showDialogUserBlocked.setValue(true);
                } else if (error.getMajorCode() == 106) {
                    errorVerifySms(Reason.INVALID_CODE);
                } else if (error.getMajorCode() == 107) {
                    showDialogVerificationCodeExpired.postValue(true);
                } else if (error.getMajorCode() == 108) {
                    showWaitDialog.postValue(new WaitTimeModel(R.string.USER_VERIFY_MANY_TRIES, error.getWaitTime(), error.getMajorCode()));
                } else if (error.getMajorCode() == 5 && error.getMinorCode() == 1) {
                    userVerification(verificationCode);
                }
            }
        });
    }

    public String receiveVerifySms(String message) {
        return HelperString.regexExtractValue(message, repository.getRegexFetchCodeVerification());
    }

    private void requestRegister() {
        repository.registration(new RegisterRepository.RepositoryCallbackWithError<ErrorWithWaitTime>() {
            @Override
            public void onSuccess() {
                G.handler.post(() -> {
                    cancelTimer();
                    counterTimer();
                });
            }

            @Override
            public void onError(ErrorWithWaitTime error) {
                if (error.getMajorCode() == 100 && error.getMinorCode() == 1) {
                    //Invalid countryCode
                } else if (error.getMajorCode() == 100 && error.getMinorCode() == 2) {
                    //Invalid phoneNumber
                } else if (error.getMajorCode() == 101) {
                    //Invalid phoneNumber
                } else if (error.getMajorCode() == 135) {
                    showDialogUserBlocked.setValue(true);
                } else if (error.getMajorCode() == 136) {
                    showWaitDialog.setValue(new WaitTimeModel(R.string.USER_VERIFY_MANY_TRIES, error.getWaitTime(), error.getMajorCode()));
                } else if (error.getMajorCode() == 137) {
                    showWaitDialog.setValue(new WaitTimeModel(R.string.USER_VERIFY_MANY_TRIES_SEND, error.getWaitTime(), error.getMajorCode()));
                } else if (error.getMajorCode() == 5 && error.getMinorCode() == 1) { // timeout
                    if (sendRequestRegister <= 2) {
                        requestRegister();
                        sendRequestRegister++;
                    }
                } else if (error.getMajorCode() == 10) {
                    showWaitDialog.setValue(new WaitTimeModel(R.string.IP_blocked, error.getWaitTime(), error.getMajorCode()));
                }
            }
        });
    }

    private void errorVerifySms(Reason reason) { //when don't receive sms and open rg_dialog for enter code

        boolean isNeedTimer = true;
        int message = 0;

        if (reason == Reason.SOCKET) {
            isNeedTimer = false;
            enabledResendCodeButton.set(false);
        } else if (reason == Reason.TIME_OUT) {
            isNeedTimer = true;
            enabledResendCodeButton.set(false);
        } else if (reason == Reason.INVALID_CODE) {
            message = R.string.verify_invalid_code_message;
            isNeedTimer = false;
            enabledResendCodeButton.set(true);
        }

        showEnteredCodeErrorServer.postValue(message);
        clearActivationCode.postValue(true);
        if (isNeedTimer) {
            counterTimer();
        }
    }

    public void timerFinished() {
        enabledResendCodeButton.set(true);
    }

    public enum Reason {
        SOCKET, TIME_OUT, INVALID_CODE
    }
}
