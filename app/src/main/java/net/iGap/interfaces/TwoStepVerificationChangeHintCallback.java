package net.iGap.interfaces;

public interface TwoStepVerificationChangeHintCallback {
    void changeHint();

    void errorChangeHint(int major, int minor);
}
