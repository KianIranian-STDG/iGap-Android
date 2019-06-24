package net.iGap.interfaces;

public interface TwoStepVerificationChangeRecoveryEmailCallback {
    void changeEmail(String unConfirmEmailPattern);

    void errorChangeEmail(int major, int minor);
}
