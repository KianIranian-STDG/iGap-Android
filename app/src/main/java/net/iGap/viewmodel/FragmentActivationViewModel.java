package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.CountDownTimer;
import android.text.format.DateUtils;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperString;
import net.iGap.model.repository.ErrorWithWaitTime;
import net.iGap.model.repository.RegisterRepository;

import java.util.Locale;

public class FragmentActivationViewModel extends ViewModel {

    public ObservableField<String> timerValue = new ObservableField<>();
    public MutableLiveData<String> verifyCode = new MutableLiveData<>();
    public ObservableBoolean enabledResendCodeButton = new ObservableBoolean(false);
    public MutableLiveData<Boolean> showEnteredCodeError = new MutableLiveData<>();
    public MutableLiveData<Integer> currentTimePosition = new MutableLiveData<>();
    public MutableLiveData<WaitTimeModel> showWaitDialog = new MutableLiveData<>();
    public MutableLiveData<Integer> showEnteredCodeErrorServer = new MutableLiveData<>();
    public MutableLiveData<Boolean> showLoading = new MutableLiveData<>();
    public MutableLiveData<Boolean> closeKeyword = new MutableLiveData<>();
    public MutableLiveData<Boolean> clearActivationCode = new MutableLiveData<>();
    public MutableLiveData<Long> goToTwoStepVerificationPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogUserBlocked = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogVerificationCodeExpired = new MutableLiveData<>();

    private RegisterRepository repository;
    private CountDownTimer countDownTimer;
    private int sendRequestRegister = 0;

    public FragmentActivationViewModel() {
        repository = RegisterRepository.getInstance();
        timerValue.set("1:00");
        counterTimer();
    }

    private void counterTimer() {
        countDownTimer = new CountDownTimer(60 * DateUtils.SECOND_IN_MILLIS, Config.COUNTER_TIMER_DELAY) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                timerValue.set(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                currentTimePosition.setValue(seconds * 6);
            }

            public void onFinish() {
                timerValue.set(String.format(Locale.getDefault(), "%02d:%02d", 0, 0));
                currentTimePosition.setValue(60 * 6);
                enabledResendCodeButton.set(true);
                cancelTimer();
            }
        };
        countDownTimer.start();
    }

    public void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    public void resentActivationCodeClick() {
        enabledResendCodeButton.set(false);
        cancelTimer();
        requestRegister();
        counterTimer();
    }

    public void loginButtonOnClick(String enteredCode) {
        closeKeyword.setValue(true);
        if (enteredCode.length() == 5) {
            if (G.socketConnection) {
                showLoading.setValue(true);
                userVerification(enteredCode);
            } else {
                requestRegister();
            }
        } else {
            showEnteredCodeError.setValue(true);
        }
    }

    private void userVerification(String verificationCode) {
        repository.userVerify(verificationCode, new RegisterRepository.RepositoryCallbackWithError<ErrorWithWaitTime>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(ErrorWithWaitTime error) {
                showLoading.setValue(false);
                if (error.getMajorCode() == 184 && error.getMinorCode() == 1) {
                    goToTwoStepVerificationPage.setValue(repository.getUserId());
                } else if (error.getMajorCode() == 102 && error.getMinorCode() == 1) {
                    errorVerifySms(Reason.INVALID_CODE);
                } else if (error.getMajorCode() == 102 && error.getMinorCode() == 2) {
                    //empty
                } else if (error.getMajorCode() == 103) {
                    //empty
                } else if (error.getMajorCode() == 104) {
                    showDialogUserBlocked.setValue(true);
                } else if (error.getMajorCode() == 105) {
                    showDialogUserBlocked.setValue(true);
                } else if (error.getMajorCode() == 106) {
                    errorVerifySms(Reason.INVALID_CODE);
                } else if (error.getMajorCode() == 107) {
                    showDialogVerificationCodeExpired.setValue(true);
                } else if (error.getMajorCode() == 108) {
                    showWaitDialog.setValue(new WaitTimeModel(R.string.USER_VERIFY_MANY_TRIES, error.getWaitTime(), error.getMajorCode()));
                } else if (error.getMajorCode() == 5 && error.getMinorCode() == 1) {
                    userVerification(verifyCode.getValue());
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
                cancelTimer();
                counterTimer();
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
            message = R.string.verify_socket_message;
            isNeedTimer = false;
            enabledResendCodeButton.set(false);
        } else if (reason == Reason.TIME_OUT) {
            message = R.string.verify_time_out_message;
            isNeedTimer = true;
            enabledResendCodeButton.set(false);
        } else if (reason == Reason.INVALID_CODE) {
            message = R.string.verify_invalid_code_message;
            isNeedTimer = false;
            enabledResendCodeButton.set(true);
        }

        showEnteredCodeErrorServer.setValue(message);
        clearActivationCode.setValue(true);
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
