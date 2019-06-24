package net.iGap.interfaces;

public interface UserTwoStepVerificationUnsetPasswordCallback {
    void unSetPassword();

    void onUnsetError(int major, int minor);
}
