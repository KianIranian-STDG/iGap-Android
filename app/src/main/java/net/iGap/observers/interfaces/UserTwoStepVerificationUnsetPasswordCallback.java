package net.iGap.observers.interfaces;

public interface UserTwoStepVerificationUnsetPasswordCallback {
    void unSetPassword();

    void onUnsetError(int major, int minor);
}
